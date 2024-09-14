package com.capstone2024.scss.domain.counseling_booking.services.impl;

import com.capstone2024.scss.application.advice.exeptions.NotFoundException;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.counselor.dto.CounselorDTO;
import com.capstone2024.scss.application.counselor.dto.request.CounselorFilterRequestDTO;
import com.capstone2024.scss.domain.counseling_booking.entities.counselor.Counselor;
import com.capstone2024.scss.domain.counseling_booking.services.CounselorService;
import com.capstone2024.scss.infrastructure.repositories.CounselorRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CounselorServiceImpl implements CounselorService {

    private final CounselorRepository counselorRepository;

    public CounselorServiceImpl(CounselorRepository counselorRepository) {
        this.counselorRepository = counselorRepository;
    }

    @Override
    public PaginationDTO<List<CounselorDTO>> getCounselorsWithFilter(CounselorFilterRequestDTO filterRequest) {
        Page<Counselor> counselorsPage = counselorRepository.findByKeywordAndRatingRange(
                filterRequest.getSearch(),
                filterRequest.getRatingFrom(),
                filterRequest.getRatingTo(),
                filterRequest.getPagination()
        );

        List<CounselorDTO> counselorDTOs = counselorsPage.getContent().stream()
                .map(counselor -> CounselorDTO.builder()
                        .id(counselor.getId())
                        .rating(counselor.getRating())
                        .fullName(counselor.getFullName())
                        .phoneNumber(counselor.getPhoneNumber())
                        .dateOfBirth(counselor.getDateOfBirth())
                        .email(counselor.getAccount().getEmail())
                        .avatarLink(counselor.getAvatarLink())
                        .build())
                .collect(Collectors.toList());

        return PaginationDTO.<List<CounselorDTO>>builder()
                .data(counselorDTOs)
                .totalPages(counselorsPage.getTotalPages())
                .totalElements((int) counselorsPage.getTotalElements())
                .build();
    }

    @Override
    public CounselorDTO getOneCounselor(Long counselorId) {
        // Find the Counselor entity by ID
        Optional<Counselor> optionalCounselor = counselorRepository.findById(counselorId);

        if (optionalCounselor.isEmpty()) {
            throw new NotFoundException("Counselor not found");
        }

        Counselor counselor = optionalCounselor.get();

        // Convert to CounselorDTO
        return CounselorDTO.builder()
                .id(counselor.getId())
                .rating(counselor.getRating())
                .fullName(counselor.getFullName())
                .phoneNumber(counselor.getPhoneNumber())
                .dateOfBirth(counselor.getDateOfBirth())
                .email(counselor.getAccount().getEmail())
                .avatarLink(counselor.getAvatarLink())
                .build();
    }
}
