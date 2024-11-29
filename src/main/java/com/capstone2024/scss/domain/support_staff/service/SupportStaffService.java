package com.capstone2024.scss.domain.support_staff.service;

import com.capstone2024.scss.application.advice.exeptions.NotFoundException;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.demand.dto.FollowStatusDTO;
import com.capstone2024.scss.application.demand.dto.StudentFollowingDTO;
import com.capstone2024.scss.application.demand.dto.SupportStaffDTO;
import com.capstone2024.scss.application.support_staff.dto.SupportStaffFilterRequestDTO;
import com.capstone2024.scss.domain.common.mapper.demand.StudentFollowingMapper;
import com.capstone2024.scss.domain.common.support_staff.SupportStaffMapper;
import com.capstone2024.scss.domain.demand.entities.StudentFollowing;
import com.capstone2024.scss.domain.student.entities.Student;
import com.capstone2024.scss.domain.support_staff.entity.SupportStaff;
import com.capstone2024.scss.infrastructure.repositories.demand.StudentFollowingRepository;
import com.capstone2024.scss.infrastructure.repositories.demand.SupportStaffRepository;
import com.capstone2024.scss.infrastructure.repositories.student.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupportStaffService {

    private final SupportStaffRepository supportStaffRepository;
    private final StudentRepository studentRepository;
    private final StudentFollowingRepository studentFollowingRepository;

    public PaginationDTO<List<SupportStaffDTO>> getSupportStaffWithFilter(SupportStaffFilterRequestDTO filterRequest) {
        Page<SupportStaff> staffPage = supportStaffRepository.findByFilters(
                filterRequest.getSearch(),
                filterRequest.getPagination()
        );

        List<SupportStaffDTO> staffDTOs = staffPage.getContent().stream()
                .map(SupportStaffMapper::toSupportStaffDTO)
                .collect(Collectors.toList());

        return PaginationDTO.<List<SupportStaffDTO>>builder()
                .data(staffDTOs)
                .totalPages(staffPage.getTotalPages())
                .totalElements((int) staffPage.getTotalElements())
                .build();
    }

    @Transactional
    public void followStudent(Long staffId, Long studentId) {
        // Fetch the student
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found with id: " + studentId));

        // Fetch the staff
        SupportStaff supportStaff = supportStaffRepository.findById(staffId)
                .orElseThrow(() -> new NotFoundException("Support staff not found with id: " + staffId));

        // Check if the student is already followed by the staff
        boolean alreadyFollowed = studentFollowingRepository.existsByStudentIdAndSupportStaffId(studentId, staffId);
        if (alreadyFollowed) {
            throw new IllegalStateException("This student is already followed by the staff.");
        }

        // Create the follow relationship
        StudentFollowing studentFollowing = StudentFollowing.builder()
                .student(student)
                .supportStaff(supportStaff)
                .followDate(ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDateTime())
                .followNote("Assigned for support")
                .build();

        studentFollowingRepository.save(studentFollowing);
    }

    @Transactional
    public void unfollowStudent(Long staffId, Long studentId) {
        // Check if the follow relationship exists
        StudentFollowing studentFollowing = studentFollowingRepository
                .findByStudentIdAndSupportStaffId(studentId, staffId)
                .orElseThrow(() -> new NotFoundException("Follow relationship not found"));

        // Remove the follow relationship
        studentFollowingRepository.delete(studentFollowing);
    }

    @Transactional
    public void updateFollowNote(Long staffId, Long studentId, String followNote) {
        // Fetch the existing follow relationship
        StudentFollowing studentFollowing = studentFollowingRepository
                .findByStudentIdAndSupportStaffId(studentId, staffId)
                .orElseThrow(() -> new NotFoundException("Follow relationship not found"));

        // Update the follow note
        studentFollowing.setFollowNote(followNote);
        studentFollowingRepository.save(studentFollowing);
    }

    public SupportStaffDTO getOneSupportStaff(Long id) {
        SupportStaff supportStaff = supportStaffRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Support staff not found with id: " + id));

        return SupportStaffMapper.toSupportStaffDTO(supportStaff);
    }

    public PaginationDTO<List<StudentFollowingDTO>> getAllFollowingStudents(Long staffId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        // Fetch the paginated data
        Page<StudentFollowing> followingPage = studentFollowingRepository.findAllBySupportStaffId(staffId, pageable);

        // Convert entities to DTOs
        List<StudentFollowingDTO> followingDTOs = followingPage.getContent().stream()
                .map(StudentFollowingMapper::toDTO)
                .collect(Collectors.toList());

        // Build and return the PaginationDTO
        return PaginationDTO.<List<StudentFollowingDTO>>builder()
                .data(followingDTOs)
                .totalPages(followingPage.getTotalPages())
                .totalElements((int) followingPage.getTotalElements())
                .build();
    }

    public FollowStatusDTO checkIfStudentIsFollowed(Long staffId, Long studentId) {
        // Check if the student is followed
        Optional<StudentFollowing> optionalFollowing = studentFollowingRepository.findByStudentId(studentId);

        if (optionalFollowing.isEmpty()) {
            return FollowStatusDTO.builder()
                    .isFollowed(false)
                    .studentFollowingDTO(null)
                    .isYour(false)
                    .supportStaffDTO(null)
                    .build();
        }

        // Extract following information
        StudentFollowing studentFollowing = optionalFollowing.get();
        boolean isYour = studentFollowing.getSupportStaff().getId().equals(staffId);

        return FollowStatusDTO.builder()
                .isFollowed(true)
                .studentFollowingDTO(StudentFollowingMapper.toDTO(studentFollowing))
                .isYour(isYour)
                .supportStaffDTO(SupportStaffMapper.toSupportStaffDTO(studentFollowing.getSupportStaff()))
                .build();
    }
}

