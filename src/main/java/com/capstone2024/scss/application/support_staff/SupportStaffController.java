package com.capstone2024.scss.application.support_staff;

import com.capstone2024.scss.application.account.dto.CounselorProfileDTO;
import com.capstone2024.scss.application.account.dto.enums.SortDirection;
import com.capstone2024.scss.application.advice.exeptions.BadRequestException;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.common.utils.ResponseUtil;
import com.capstone2024.scss.application.demand.dto.CounselingDemandDTO;
import com.capstone2024.scss.application.demand.dto.FollowStatusDTO;
import com.capstone2024.scss.application.demand.dto.StudentFollowingDTO;
import com.capstone2024.scss.application.demand.dto.SupportStaffDTO;
import com.capstone2024.scss.application.demand.dto.request.CounselingDemandFilterRequestDTO;
import com.capstone2024.scss.application.support_staff.dto.SupportStaffFilterRequestDTO;
import com.capstone2024.scss.application.support_staff.dto.UpdateFollowNoteRequest;
import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.counselor.entities.enums.Gender;
import com.capstone2024.scss.domain.counselor.services.CounselorService;
import com.capstone2024.scss.domain.demand.entities.CounselingDemand;
import com.capstone2024.scss.domain.demand.service.CounselingDemandService;
import com.capstone2024.scss.domain.support_staff.service.SupportStaffService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/support-staff")
@RequiredArgsConstructor
public class SupportStaffController {

    private final SupportStaffService supportStaffService;
    private final CounselorService counselorService;
    private final CounselingDemandService counselingDemandService;

    @GetMapping("/filter")
    public ResponseEntity<Object> getSupportStaffWithFilter(
            @RequestParam(name = "search", required = false, defaultValue = "") String search,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "ASC") SortDirection sortDirection,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        if (page < 1 || size < 1) {
            throw new BadRequestException("Page and size must be positive integers.");
        }

        Sort sort = Sort.by(sortBy);
        sort = sortDirection == SortDirection.ASC ? sort.ascending() : sort.descending();

        PageRequest pagination = PageRequest.of(page - 1, size, sort);
        SupportStaffFilterRequestDTO filterRequest = SupportStaffFilterRequestDTO.builder()
                .search(search.trim().isEmpty() ? null : search.trim())
                .pagination(pagination)
                .build();

        PaginationDTO<List<SupportStaffDTO>> responseDTO = supportStaffService.getSupportStaffWithFilter(filterRequest);

        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/non-academic/match")
    public ResponseEntity<Object> findBestCounselorNonAcademic(
            @RequestParam(name = "gender", required = false) Gender gender,
            @RequestParam(name = "reason", required = false) String reason) {

        CounselorProfileDTO counselor = counselorService.findBestAvailableCounselorForNonAcademicWithLowestDemandInMonth(gender, reason);
        return ResponseUtil.getResponse(counselor, HttpStatus.OK);
    }

    @GetMapping("/academic/match/{studentId}")
    public ResponseEntity<Object> findBestCounselorAcademic(
            @RequestParam(name = "gender", required = false) Gender gender,
            @PathVariable Long studentId,
            @RequestParam(name = "reason", required = true) String reason) {

        CounselorProfileDTO counselor = counselorService.findBestAvailableCounselorForAcademicWithLowestDemand(gender, studentId, reason);
        return ResponseUtil.getResponse(counselor, HttpStatus.OK);
    }

    @GetMapping("/counseling-demand/filter/{supportStaffId}")
    public ResponseEntity<Object> filterCounselingDemandsForSupportStaff(
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(name = "status", required = false) CounselingDemand.Status status,
            @RequestParam(name = "sortBy", defaultValue = "createdDate") String sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "DESC") String sortDirection,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @PathVariable Long supportStaffId) {

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

        PaginationDTO<List<CounselingDemandDTO>> counselingDemands = counselingDemandService.filterCounselingDemandsForSupportStaff(filterRequest, supportStaffId);
        return ResponseUtil.getResponse(counselingDemands, HttpStatus.OK);
    }

    @PostMapping("/follow/{studentId}")
    public ResponseEntity<String> followStudent(
            @PathVariable Long studentId,
            @AuthenticationPrincipal @NotNull Account principle) {

        supportStaffService.followStudent(principle.getProfile().getId(), studentId);
        return ResponseEntity.ok("Successfully followed the student.");
    }

    @DeleteMapping("/unfollow/{studentId}")
    public ResponseEntity<String> unfollowStudent(
            @AuthenticationPrincipal @NotNull Account principle,
            @PathVariable @NotNull Long studentId) {

        supportStaffService.unfollowStudent(principle.getProfile().getId(), studentId);
        return ResponseEntity.ok("Successfully unfollowed the student.");
    }

    @PutMapping("/update-follow-note/{studentId}")
    public ResponseEntity<String> updateFollowNote(
            @AuthenticationPrincipal @NotNull Account principle,
            @PathVariable @NotNull Long studentId,
            @RequestBody @NotNull UpdateFollowNoteRequest request) {

        supportStaffService.updateFollowNote(principle.getProfile().getId(), studentId, request.getFollowNote());
        return ResponseEntity.ok("Follow note updated successfully.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getOneSupportStaff(@PathVariable @NotNull Long id) {
        SupportStaffDTO supportStaff = supportStaffService.getOneSupportStaff(id);
        return ResponseUtil.getResponse(supportStaff, HttpStatus.OK);
    }

    @GetMapping("/following")
    public ResponseEntity<Object> getAllFollowingStudents(
            @AuthenticationPrincipal @NotNull Account principle,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (page < 1 || size < 1) {
            throw new BadRequestException("Page and size must be positive integers.");
        }

        PaginationDTO<List<StudentFollowingDTO>> result =
                supportStaffService.getAllFollowingStudents(principle.getProfile().getId(), page, size);
        return ResponseUtil.getResponse(result, HttpStatus.OK);
    }

    @GetMapping("/following/{staffId}")
    public ResponseEntity<Object> getAllFollowingStudents(
            @PathVariable Long staffId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (page < 1 || size < 1) {
            throw new BadRequestException("Page and size must be positive integers.");
        }

        PaginationDTO<List<StudentFollowingDTO>> result =
                supportStaffService.getAllFollowingStudents(staffId, page, size);
        return ResponseUtil.getResponse(result, HttpStatus.OK);
    }

    @GetMapping("/check-follow/{studentId}")
    public ResponseEntity<Object> checkIfStudentIsFollowed(
            @AuthenticationPrincipal @NotNull Account principle,
            @PathVariable @NotNull Long studentId) {

        Long staffId = principle.getProfile().getId();

        FollowStatusDTO followStatus = supportStaffService.checkIfStudentIsFollowed(staffId, studentId);
        return ResponseUtil.getResponse(followStatus, HttpStatus.OK);
    }
}

