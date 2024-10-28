package com.capstone2024.scss.domain.demand.service.impl;

import com.capstone2024.scss.application.advice.exeptions.NotFoundException;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.demand.dto.ProblemCategoryResponseDTO;
import com.capstone2024.scss.application.demand.dto.request.ProblemCategoryRequestDTO;
import com.capstone2024.scss.application.demand.dto.request.ProblemTagFilterRequestDTO;
import com.capstone2024.scss.application.demand.dto.ProblemTagResponseDTO;
import com.capstone2024.scss.application.demand.dto.request.ProblemTagRequestDTO;
import com.capstone2024.scss.domain.common.mapper.demand.ProblemTagMapper;
import com.capstone2024.scss.domain.demand.entities.ProblemCategory;
import com.capstone2024.scss.domain.demand.entities.ProblemTag;
import com.capstone2024.scss.domain.demand.service.ProblemTagService;
import com.capstone2024.scss.infrastructure.repositories.demand.ProblemCategoryRepository;
import com.capstone2024.scss.infrastructure.repositories.demand.ProblemTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProblemTagServiceImpl implements ProblemTagService {

    private final ProblemTagRepository problemTagRepository;
    private final ProblemCategoryRepository problemCategoryRepository;

    @Override
    public PaginationDTO<List<ProblemTagResponseDTO>> filterProblemTags(ProblemTagFilterRequestDTO filterRequest) {
        Page<ProblemTag> problemTagsPage = problemTagRepository.findProblemTags(
                filterRequest.getKeyword(),
                filterRequest.getProblemCategoryId(),
                filterRequest.getPagination());

        List<ProblemTagResponseDTO> problemTagDTOs = problemTagsPage.getContent().stream()
                .map(ProblemTagMapper::toProblemTagResponseDto)
                .collect(Collectors.toList());

        return PaginationDTO.<List<ProblemTagResponseDTO>>builder()
                .data(problemTagDTOs)
                .totalPages(problemTagsPage.getTotalPages())
                .totalElements((int) problemTagsPage.getTotalElements())
                .build();
    }

    @Transactional
    public ProblemTagResponseDTO createProblemTag(ProblemTagRequestDTO request) {
        // Tìm ProblemCategory theo categoryId
        ProblemCategory category = problemCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + request.getCategoryId()));

        // Tạo mới ProblemTag và gán ProblemCategory
        ProblemTag problemTag = ProblemTag.builder()
                .name(request.getName())
//                .point(request.getPoint())
                .category(category) // Gán category cho problemTag
                .build();

        ProblemTag savedTag = problemTagRepository.save(problemTag);
        return ProblemTagMapper.toProblemTagResponseDto(savedTag);
    }

    @Override
    public ProblemCategoryResponseDTO createProblemCategory(ProblemCategoryRequestDTO request) {
        ProblemCategory problemCategory = ProblemCategory.builder()
                .name(request.getName())
                .build();

        ProblemCategory savedCategory = problemCategoryRepository.save(problemCategory);
        return ProblemTagMapper.toProblemCategoryResponseDto(savedCategory);
    }

    @Override
    @Transactional
    public ProblemTagResponseDTO updateProblemTag(Long id, ProblemTagRequestDTO request) {
        // Tìm ProblemTag theo id
        ProblemTag existingTag = problemTagRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ProblemTag not found with id: " + id));

        // Cập nhật thông tin
        existingTag.setName(request.getName());
//        existingTag.setPoint(request.getPoint());

        // Tìm ProblemCategory mới nếu có
        if (request.getCategoryId() != null) {
            ProblemCategory category = problemCategoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found with id: " + request.getCategoryId()));
            existingTag.setCategory(category); // Gán lại category nếu có
        }

        ProblemTag updatedTag = problemTagRepository.save(existingTag);
        return ProblemTagMapper.toProblemTagResponseDto(updatedTag);
    }

    @Override
    @Transactional
    public ProblemCategoryResponseDTO updateProblemCategory(Long id, ProblemCategoryRequestDTO request) {
        // Tìm ProblemCategory theo id
        ProblemCategory existingCategory = problemCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ProblemCategory not found with id: " + id));

        // Cập nhật thông tin
        existingCategory.setName(request.getName());

        ProblemCategory updatedCategory = problemCategoryRepository.save(existingCategory);
        return ProblemTagMapper.toProblemCategoryResponseDto(updatedCategory);
    }

    @Override
    public List<ProblemCategoryResponseDTO> getAllProblemCategories() {
        List<ProblemCategory> categories = problemCategoryRepository.findAll();
        return categories.stream()
                .map(ProblemTagMapper::toProblemCategoryResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteProblemTag(Long id) {
        ProblemTag problemTag = problemTagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ProblemTag not found with id: " + id));

        problemTagRepository.delete(problemTag);
    }

    @Override
    @Transactional
    public void deleteProblemCategory(Long id) {
        ProblemCategory problemCategory = problemCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ProblemCategory not found with id: " + id));

        problemCategoryRepository.delete(problemCategory);
    }
}
