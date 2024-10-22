package com.capstone2024.scss.application.q_and_a.controller;

import com.capstone2024.scss.application.common.utils.ResponseUtil;
import com.capstone2024.scss.application.q_and_a.dto.TopicDTO;
import com.capstone2024.scss.domain.common.mapper.q_and_a.TopicMapper;
import com.capstone2024.scss.domain.q_and_a.entities.Topic;
import com.capstone2024.scss.domain.q_and_a.enums.TopicType;
import com.capstone2024.scss.infrastructure.repositories._and_a.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicRepository topicRepository;

    @GetMapping("/academic")
    public ResponseEntity<Object> getAcademicTopics() {
        List<Topic> academicTopics = topicRepository.findByType(TopicType.ACADEMIC);
        List<TopicDTO> response = academicTopics.stream().map(TopicMapper::toDTO).toList();
        return ResponseUtil.getResponse(response, HttpStatus.OK);
    }

    // API lấy các topic non-academic
    @GetMapping("/non-academic")
    public ResponseEntity<Object> getNonAcademicTopics() {
        List<Topic> nonAcademicTopics = topicRepository.findByType(TopicType.NON_ACADEMIC);
        List<TopicDTO> response = nonAcademicTopics.stream().map(TopicMapper::toDTO).toList();
        return ResponseUtil.getResponse(response, HttpStatus.OK);
    }
}
