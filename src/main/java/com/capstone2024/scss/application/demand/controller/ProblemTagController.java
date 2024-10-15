package com.capstone2024.scss.application.demand.controller;

import com.capstone2024.scss.application.account.dto.enums.SortDirection;
import com.capstone2024.scss.application.advice.exeptions.BadRequestException;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.common.utils.ResponseUtil;
import com.capstone2024.scss.application.demand.dto.ProblemCategoryResponseDTO;
import com.capstone2024.scss.application.demand.dto.request.ProblemCategoryRequestDTO;
import com.capstone2024.scss.application.demand.dto.request.ProblemTagFilterRequestDTO;
import com.capstone2024.scss.application.demand.dto.ProblemTagResponseDTO;
import com.capstone2024.scss.application.demand.dto.request.ProblemTagRequestDTO;
import com.capstone2024.scss.domain.demand.service.ProblemTagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/problem-tags")
@RequiredArgsConstructor// Đường dẫn cho API
public class ProblemTagController {

    private static final Logger logger = LoggerFactory.getLogger(ProblemTagController.class);

    private final ProblemTagService problemTagService;  // Inject service để xử lý logic

    @GetMapping("/filter")  // Định nghĩa endpoint cho filter
    public ResponseEntity<Object> filterProblemTags(
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(name = "problemCategoryId", required = false) Long problemCategoryId,
            @RequestParam(name = "sortBy", defaultValue = "createdDate") String sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "DESC") SortDirection sortDirection,
            @RequestParam(name = "page", defaultValue = "1") Integer page) {

        // Kiểm tra số trang hợp lệ
        if (page < 1) {
            logger.error("Invalid page number: {}", page);
            throw new IllegalArgumentException("Page must be positive (page > 0)");
        }

        // Tạo đối tượng filter request
        ProblemTagFilterRequestDTO filterRequest = ProblemTagFilterRequestDTO.builder()
                .keyword(keyword.isEmpty() ? null : keyword.trim())  // Trim keyword nếu không rỗng
                .problemCategoryId(problemCategoryId)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .pagination(PageRequest.of(page - 1, 10, Sort.by(sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy)))
                .build();

        // Lấy danh sách ProblemTags từ service
        PaginationDTO<List<ProblemTagResponseDTO>> responseDTO = problemTagService.filterProblemTags(filterRequest);
        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);  // Trả về phản hồi
    }

    @PostMapping("/problem-tag")
    public ResponseEntity<Object> createProblemTag(
            @Valid @RequestBody ProblemTagRequestDTO request,
            BindingResult errors) {
        if (errors.hasErrors()) {
            logger.warn("validation errors: {}", errors.getAllErrors());
            throw new BadRequestException("Invalid login request", errors, HttpStatus.BAD_REQUEST);
        }

        ProblemTagResponseDTO createdTag = problemTagService.createProblemTag(request);
        return ResponseUtil.getResponse(createdTag, HttpStatus.OK);
    }

    @PostMapping("/problem-category")
    public ResponseEntity<Object> createProblemCategory(
            @Valid @RequestBody ProblemCategoryRequestDTO request,
            BindingResult errors) {
        if (errors.hasErrors()) {
            logger.warn("validation errors: {}", errors.getAllErrors());
            throw new BadRequestException("Invalid login request", errors, HttpStatus.BAD_REQUEST);
        }

        ProblemCategoryResponseDTO createdCategory = problemTagService.createProblemCategory(request);
        return ResponseUtil.getResponse(createdCategory, HttpStatus.OK);
    }

    @PutMapping("/problem-tag/{id}")
    public ResponseEntity<Object> updateProblemTag(
            @PathVariable Long id,
            @RequestBody ProblemTagRequestDTO request) {
        ProblemTagResponseDTO updatedTag = problemTagService.updateProblemTag(id, request);
        return ResponseUtil.getResponse(updatedTag, HttpStatus.OK);
    }

    @PutMapping("/problem-category/{id}")
    public ResponseEntity<Object> updateProblemCategory(
            @PathVariable Long id,
            @RequestBody ProblemCategoryRequestDTO request) {
        ProblemCategoryResponseDTO updatedCategory = problemTagService.updateProblemCategory(id, request);
        return ResponseUtil.getResponse(updatedCategory, HttpStatus.OK);
    }

    @GetMapping("/problem-category")
    public ResponseEntity<List<ProblemCategoryResponseDTO>> getAllProblemCategories() {
        List<ProblemCategoryResponseDTO> categories = problemTagService.getAllProblemCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @DeleteMapping("/problem-tag/{id}")
    public ResponseEntity<Void> deleteProblemTag(@PathVariable Long id) {
        problemTagService.deleteProblemTag(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/problem-category/{id}")
    public ResponseEntity<Void> deleteProblemCategory(@PathVariable Long id) {
        problemTagService.deleteProblemCategory(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
