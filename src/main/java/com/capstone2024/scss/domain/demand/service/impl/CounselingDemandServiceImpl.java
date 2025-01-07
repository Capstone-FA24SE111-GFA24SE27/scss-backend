package com.capstone2024.scss.domain.demand.service.impl;

import com.capstone2024.scss.application.advice.exeptions.NotFoundException;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.demand.dto.CounselingDemandDTO;
import com.capstone2024.scss.application.demand.dto.ProblemTagCountResponse;
import com.capstone2024.scss.application.demand.dto.request.CounselingDemandCreateRequestDTO;
import com.capstone2024.scss.application.demand.dto.request.CounselingDemandFilterRequestDTO;
import com.capstone2024.scss.application.demand.dto.request.CounselingDemandUpdateRequestDTO;
import com.capstone2024.scss.domain.common.mapper.demand.DemandMapper;
import com.capstone2024.scss.domain.counselor.entities.Counselor;
import com.capstone2024.scss.domain.demand.entities.CounselingDemand;
import com.capstone2024.scss.domain.demand.service.CounselingDemandService;
import com.capstone2024.scss.domain.student.entities.Student;
import com.capstone2024.scss.domain.student.services.StudentService;
import com.capstone2024.scss.domain.support_staff.entity.SupportStaff;
import com.capstone2024.scss.infrastructure.repositories.counselor.CounselorRepository;
import com.capstone2024.scss.infrastructure.repositories.demand.CounselingDemandRepository;
import com.capstone2024.scss.infrastructure.repositories.demand.SupportStaffRepository;
import com.capstone2024.scss.infrastructure.repositories.student.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CounselingDemandServiceImpl implements CounselingDemandService {

    private final StudentRepository studentRepository;
    private final SupportStaffRepository supportStaffRepository;
    private final CounselingDemandRepository counselingDemandRepository;
    private final CounselorRepository counselorRepository;
    private final StudentService studentService;

    public CounselingDemandDTO createCounselingDemand(Long studentId, Long supportStaffId, CounselingDemandCreateRequestDTO counselingDemandDTO) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found"));

        SupportStaff supportStaff = supportStaffRepository.findById(supportStaffId)
                .orElseThrow(() -> new NotFoundException("Support staff not found"));

        CounselingDemand counselingDemand = CounselingDemand.builder()
                .student(student)
                .supportStaff(supportStaff)
                .issueDescription(counselingDemandDTO.getIssueDescription())
                .causeDescription(counselingDemandDTO.getCauseDescription())
                .priorityLevel(counselingDemandDTO.getPriorityLevel())
                .additionalInformation(counselingDemandDTO.getAdditionalInformation())
                .contactNote(counselingDemandDTO.getContactNote())
                .demandType(counselingDemandDTO.getDemandType())
                .build();

        Counselor counselor = counselorRepository.findById(counselingDemandDTO.getCounselorId())
                .orElseThrow(() -> new NotFoundException("Counselor not found"));
        counselingDemand.setCounselor(counselor);
        counselingDemand.setStatus(CounselingDemand.Status.PROCESSING);
        counselingDemand.setStartDateTime(LocalDateTime.now());

        CounselingDemand savedDemand = counselingDemandRepository.save(counselingDemand);
        studentService.excludeAllDemandProblemTagsByStudentId(counselingDemand.getStudent().getId());
        return DemandMapper.toCounselingDemandDTO(savedDemand);
    }

    @Override
    public CounselingDemandDTO getOne(Long id) {
        CounselingDemand counselingDemand = counselingDemandRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Counseling demand not found with ID: " + id));

        return DemandMapper.toCounselingDemandDTO(counselingDemand);
    }

    @Override
    public PaginationDTO<List<CounselingDemandDTO>> filterCounselingDemandsForSupportStaff(CounselingDemandFilterRequestDTO filterRequest, Long supportStaffId) {
        Page<CounselingDemand> counselingDemandsPage = counselingDemandRepository.findCounselingDemandsWithFilterForSupportStaff(
                filterRequest.getKeyword(),
                filterRequest.getStatus(),
                supportStaffId,
                filterRequest.getPageRequest()
        );

        List<CounselingDemandDTO> counselingDemandDTOs = counselingDemandsPage.getContent().stream()
                .map(DemandMapper::toCounselingDemandDTO)
                .collect(Collectors.toList());

        return PaginationDTO.<List<CounselingDemandDTO>>builder()
                .data(counselingDemandDTOs)
                .totalPages(counselingDemandsPage.getTotalPages())
                .totalElements((int) counselingDemandsPage.getTotalElements())
                .build();
    }

    @Override
    public CounselingDemandDTO updateCounselingDemand(Long counselingDemandId, CounselingDemandUpdateRequestDTO updateRequestDTO) {
        CounselingDemand counselingDemand = counselingDemandRepository.findById(counselingDemandId)
                .orElseThrow(() -> new NotFoundException("Counseling Demand not found"));

        counselingDemand.setSummarizeNote(updateRequestDTO.getSummarizeNote());
        counselingDemand.setContactNote(updateRequestDTO.getContactNote());
        counselingDemand.setPriorityLevel(updateRequestDTO.getPriorityLevel());
        counselingDemand.setIssueDescription(updateRequestDTO.getIssueDescription());
        counselingDemand.setCauseDescription(updateRequestDTO.getCauseDescription());
        counselingDemand.setAdditionalInformation(updateRequestDTO.getAdditionalInformation());

        if(!updateRequestDTO.getCounselorId().equals(counselingDemand.getCounselor().getId())) {
            Counselor newCounselor = counselorRepository.findById(updateRequestDTO.getCounselorId()).orElseThrow(() -> new NotFoundException("Not found counselor"));
            counselingDemand.setCounselor(newCounselor);
        }

        counselingDemand = counselingDemandRepository.save(counselingDemand);

        return DemandMapper.toCounselingDemandDTO(counselingDemand);
    }

    @Override
    public void deleteCounselingDemandIfWaiting(Long counselingDemandId) {
        CounselingDemand counselingDemand = counselingDemandRepository.findById(counselingDemandId)
                .orElseThrow(() -> new NotFoundException("Counseling Demand not found"));
    }

    @Override
    @Transactional
    public PaginationDTO<List<CounselingDemandDTO>> filterCounselingDemandsForCounselor(CounselingDemandFilterRequestDTO filterRequest, Long counselorId) {
        Page<CounselingDemand> counselingDemandsPage = counselingDemandRepository.findCounselingDemandsWithFilterForCounselor(
                filterRequest.getKeyword(),
                filterRequest.getStatus(),
                counselorId,
                filterRequest.getPageRequest()
        );

        List<CounselingDemandDTO> counselingDemandDTOs = counselingDemandsPage.getContent().stream()
                .map(DemandMapper::toCounselingDemandDTO)
                .collect(Collectors.toList());

        return PaginationDTO.<List<CounselingDemandDTO>>builder()
                .data(counselingDemandDTOs)
                .totalPages(counselingDemandsPage.getTotalPages())
                .totalElements((int) counselingDemandsPage.getTotalElements())
                .build();
    }

    @Override
    public CounselingDemandDTO solveCounselingDemand(Long counselingDemandId, String summarizeNote) {
        CounselingDemand counselingDemand = counselingDemandRepository.findById(counselingDemandId)
                .orElseThrow(() -> new NotFoundException("Counseling demand not found with ID: " + counselingDemandId));

//        studentService.excludeAllDemandProblemTagsByStudentId(counselingDemand.getStudent().getId());
        ZoneId vietnam = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime nowVietNam = now.withZoneSameInstant(vietnam);
        LocalDateTime vietNamDateTime = nowVietNam.toLocalDateTime();

        counselingDemand.setEndDateTime(vietNamDateTime);
        counselingDemand.setStatus(CounselingDemand.Status.DONE);
        counselingDemand.setSummarizeNote(summarizeNote);
        CounselingDemand updatedDemand = counselingDemandRepository.save(counselingDemand);

        return DemandMapper.toCounselingDemandDTO(updatedDemand);
    }

    @Override
    public List<CounselingDemandDTO> getAll(LocalDate from, LocalDate to) {
        LocalDateTime fromDateTime = (from != null) ? from.atStartOfDay() : null;
        LocalDateTime toDateTime = (to != null) ? to.atTime(LocalTime.MAX) : null;

        List<CounselingDemand> counselingDemands = counselingDemandRepository.findAllByStartDateTimeBetween(fromDateTime, toDateTime);

        return counselingDemands.stream()
                .map(DemandMapper::toCounselingDemandDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProblemTagCountResponse> getProblemTagsAndCountBySemester(String semesterName) {
        List<Object[]> results = counselingDemandRepository.findProblemTagsWithCountBySemester(semesterName);
        return results.stream()
                .map(result -> new ProblemTagCountResponse((String) result[0], (Long) result[1]))
                .collect(Collectors.toList());
    }

}
