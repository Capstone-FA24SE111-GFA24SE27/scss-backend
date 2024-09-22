package com.capstone2024.scss.domain.event.service.impl;

import com.capstone2024.scss.application.account.dto.enums.SortDirection;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.event.dto.EventDTO;
import com.capstone2024.scss.application.event.dto.request.EventFilterDTO;
import com.capstone2024.scss.domain.common.mapper.event.EventMapper;
import com.capstone2024.scss.domain.event.entities.Event;
import com.capstone2024.scss.domain.event.service.EventService;
import com.capstone2024.scss.infrastructure.repositories.event.EventRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);

    @Override
    public PaginationDTO<List<EventDTO>> getAllEvents(EventFilterDTO filterRequest) {
        Sort sort = Sort.by(filterRequest.getSortBy());
        sort = filterRequest.getSortDirection() == SortDirection.ASC ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(
                filterRequest.getPagination().getPageNumber(),
                filterRequest.getPagination().getPageSize(),
                sort
        );

        logger.info("Fetching events with filters - DateFrom: {}, DateTo: {}, SemesterId: {}, Keyword: {}, CategoryId: {}",
                filterRequest.getDateFrom(), filterRequest.getDateTo(), filterRequest.getSemesterId(), filterRequest.getKeyword(), filterRequest.getCategoryId());

        Page<Event> page = eventRepository.findEventsByFilters(
//                filterRequest.getDateFrom(),
//                filterRequest.getDateTo(),
                filterRequest.getSemesterId(),
                filterRequest.getKeyword(),
                filterRequest.getCategoryId(),
                pageable
        );

        List<EventDTO> eventDTOs = page.getContent().stream()
                .map(EventMapper::toDTO)
                .collect(Collectors.toList());

        logger.info("Retrieved {} events", eventDTOs.size());

        return PaginationDTO.<List<EventDTO>>builder()
                .data(eventDTOs)
                .totalPages(page.getTotalPages())
                .totalElements((int) page.getTotalElements())
                .build();
    }
}
