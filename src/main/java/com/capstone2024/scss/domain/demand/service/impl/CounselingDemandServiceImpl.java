package com.capstone2024.scss.domain.demand.service.impl;

import com.capstone2024.scss.application.advice.exeptions.ForbiddenException;
import com.capstone2024.scss.application.advice.exeptions.NotFoundException;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.demand.dto.CounselingDemandDTO;
import com.capstone2024.scss.application.demand.dto.request.CounselingDemandFilterRequestDTO;
import com.capstone2024.scss.application.demand.dto.request.CounselingDemandUpdateRequestDTO;
import com.capstone2024.scss.domain.common.mapper.demand.DemandMapper;
import com.capstone2024.scss.domain.counselor.entities.Counselor;
import com.capstone2024.scss.domain.demand.entities.CounselingDemand;
import com.capstone2024.scss.domain.demand.service.CounselingDemandService;
import com.capstone2024.scss.domain.student.entities.Student;
import com.capstone2024.scss.domain.support_staff.entity.SupportStaff;
import com.capstone2024.scss.infrastructure.repositories.counselor.CounselorRepository;
import com.capstone2024.scss.infrastructure.repositories.demand.CounselingDemandRepository;
import com.capstone2024.scss.infrastructure.repositories.demand.SupportStaffRepository;
import com.capstone2024.scss.infrastructure.repositories.student.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CounselingDemandServiceImpl implements CounselingDemandService {

    private final StudentRepository studentRepository;
    private final SupportStaffRepository supportStaffRepository;
    private final CounselingDemandRepository counselingDemandRepository;
    private final CounselorRepository counselorRepository;

    public CounselingDemandDTO createCounselingDemand(Long studentId, Long supportStaffId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found"));

        SupportStaff supportStaff = supportStaffRepository.findById(supportStaffId)
                .orElseThrow(() -> new NotFoundException("Support staff not found"));

        CounselingDemand counselingDemand = CounselingDemand.builder()
                .status(CounselingDemand.Status.WAITING)
                .student(student)
                .supportStaff(supportStaff)
                .build();

        CounselingDemand savedDemand = counselingDemandRepository.save(counselingDemand);
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

        if (updateRequestDTO.getCounselorId() != null) {
            Counselor counselor = counselorRepository.findById(updateRequestDTO.getCounselorId())
                    .orElseThrow(() -> new NotFoundException("Counselor not found"));
            counselingDemand.setCounselor(counselor);
            counselingDemand.setStatus(CounselingDemand.Status.PROCESSING);
            if(counselingDemand.getStartDateTime() == null) {
                counselingDemand.setStartDateTime(LocalDateTime.now());
            }
        }

        counselingDemand.setSummarizeNote(updateRequestDTO.getSummarizeNote());
        counselingDemand.setContactNote(updateRequestDTO.getContactNote());

        counselingDemand = counselingDemandRepository.save(counselingDemand);

        return DemandMapper.toCounselingDemandDTO(counselingDemand);
    }

    @Override
    public void deleteCounselingDemandIfWaiting(Long counselingDemandId) {
        CounselingDemand counselingDemand = counselingDemandRepository.findById(counselingDemandId)
                .orElseThrow(() -> new NotFoundException("Counseling Demand not found"));

        if (counselingDemand.getStatus() == CounselingDemand.Status.WAITING) {
            counselingDemandRepository.delete(counselingDemand);
        } else {
            throw new ForbiddenException("Counseling Demand can only be deleted if its status is WAITING");
        }
    }

    @Override
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

        counselingDemand.setEndDateTime(LocalDateTime.now());
        counselingDemand.setStatus(CounselingDemand.Status.SOLVE);
        counselingDemand.setSummarizeNote(summarizeNote);
        CounselingDemand updatedDemand = counselingDemandRepository.save(counselingDemand);

        return DemandMapper.toCounselingDemandDTO(updatedDemand);
    }

}
