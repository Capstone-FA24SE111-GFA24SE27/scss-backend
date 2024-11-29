package com.capstone2024.scss.application.demand.controller;

import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.common.utils.ResponseUtil;
import com.capstone2024.scss.application.demand.dto.CounselingDemandDTO;
import com.capstone2024.scss.application.demand.dto.request.CounselingDemandCreateRequestDTO;
import com.capstone2024.scss.application.demand.dto.request.CounselingDemandFilterRequestDTO;
import com.capstone2024.scss.application.demand.dto.request.CounselingDemandSolveRequestDTO;
import com.capstone2024.scss.application.demand.dto.request.CounselingDemandUpdateRequestDTO;
import com.capstone2024.scss.application.q_and_a.dto.QuestionCardResponseDTO;
import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.demand.entities.CounselingDemand;
import com.capstone2024.scss.domain.demand.service.CounselingDemandService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/counseling-demand")
@Tag(name = "Counseling Demand", description = "API endpoints for managing counseling demands")
@RequiredArgsConstructor
public class CounselingDemandController {

    private final CounselingDemandService counselingDemandService;

    @PostMapping("/create/{studentId}")
    public ResponseEntity<Object> createCounselingDemand(
            @PathVariable Long studentId,
            @RequestBody CounselingDemandCreateRequestDTO counselingDemandDTO,
            @NotNull @AuthenticationPrincipal Account principle) {
        Long supportStaffId = principle.getProfile().getId();
        CounselingDemandDTO counselingDemand = counselingDemandService.createCounselingDemand(studentId, supportStaffId, counselingDemandDTO);
        return ResponseUtil.getResponse(counselingDemand ,HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CounselingDemandDTO> getOne(@PathVariable Long id) {
        CounselingDemandDTO counselingDemand = counselingDemandService.getOne(id);
        return new ResponseEntity<>(counselingDemand, HttpStatus.OK);
    }

    @GetMapping("/support-staff/filter")
    public ResponseEntity<Object> filterCounselingDemandsForSupportStaff(
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(name = "status", required = false) CounselingDemand.Status status,
            @RequestParam(name = "sortBy", defaultValue = "createdDate") String sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "DESC") String sortDirection,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @NotNull @AuthenticationPrincipal Account principle) {

        if (page < 1) {
            throw new IllegalArgumentException("Page must be positive (page > 0)");
        }

        // Tạo một PageRequest để phân trang và sắp xếp
        PageRequest pageRequest = PageRequest.of(
                page - 1,
                10, // Giả sử mỗi trang có 10 mục
                Sort.by(sortDirection.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy)
        );

        // Gọi service để lấy dữ liệu đã được lọc
        CounselingDemandFilterRequestDTO filterRequest = CounselingDemandFilterRequestDTO.builder()
                .keyword(keyword.isEmpty() ? null : keyword.trim())
                .status(status)
                .pageRequest(pageRequest)
                .build();

        Long supportStaffId = principle.getProfile().getId();

        PaginationDTO<List<CounselingDemandDTO>> counselingDemands = counselingDemandService.filterCounselingDemandsForSupportStaff(filterRequest, supportStaffId);
        return ResponseUtil.getResponse(counselingDemands, HttpStatus.OK);
    }

    @GetMapping("/counselor/filter")
    public ResponseEntity<Object> filterCounselingDemandsForCounselor(
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(name = "status", required = false) CounselingDemand.Status status,
            @RequestParam(name = "sortBy", defaultValue = "createdDate") String sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "DESC") String sortDirection,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @NotNull @AuthenticationPrincipal Account principle) {

        if (page < 1) {
            throw new IllegalArgumentException("Page must be positive (page > 0)");
        }

        // Tạo một PageRequest để phân trang và sắp xếp
        PageRequest pageRequest = PageRequest.of(
                page - 1,
                10, // Giả sử mỗi trang có 10 mục
                Sort.by(sortDirection.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy)
        );

        // Gọi service để lấy dữ liệu đã được lọc
        CounselingDemandFilterRequestDTO filterRequest = CounselingDemandFilterRequestDTO.builder()
                .keyword(keyword.isEmpty() ? null : keyword.trim())
                .status(status)
                .pageRequest(pageRequest)
                .build();

        Long counselorId = principle.getProfile().getId();

        PaginationDTO<List<CounselingDemandDTO>> counselingDemands = counselingDemandService.filterCounselingDemandsForCounselor(filterRequest, counselorId);
        return ResponseUtil.getResponse(counselingDemands, HttpStatus.OK);
    }

    @PutMapping("/update/{counselingDemandId}")
    public ResponseEntity<Object> updateCounselingDemand(
            @PathVariable Long counselingDemandId,
            @RequestBody CounselingDemandUpdateRequestDTO updateRequestDTO) {
        CounselingDemandDTO counselingDemand = counselingDemandService.updateCounselingDemand(counselingDemandId, updateRequestDTO);
        return ResponseUtil.getResponse(counselingDemand ,HttpStatus.OK);
    }

    @DeleteMapping("/delete/{counselingDemandId}")
    public ResponseEntity<String> deleteCounselingDemand(@PathVariable Long counselingDemandId) {
        counselingDemandService.deleteCounselingDemandIfWaiting(counselingDemandId);
        return ResponseEntity.ok("Counseling Demand deleted successfully.");
    }

    @PutMapping("/{counselingDemandId}/solve")
    public ResponseEntity<Object> solveCounselingDemand(
            @PathVariable Long counselingDemandId,
            @RequestBody CounselingDemandSolveRequestDTO solveRequestDTO
    ) {
        CounselingDemandDTO solvedDemand = counselingDemandService.solveCounselingDemand(counselingDemandId, solveRequestDTO.getSummarizeNote());
        return ResponseUtil.getResponse(solvedDemand ,HttpStatus.OK);
    }

    @GetMapping("/manage/find-all")
    public ResponseEntity<Object> getAllDemand(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        List<CounselingDemandDTO> responseDTO = counselingDemandService.getAll(from, to);
        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }
}
