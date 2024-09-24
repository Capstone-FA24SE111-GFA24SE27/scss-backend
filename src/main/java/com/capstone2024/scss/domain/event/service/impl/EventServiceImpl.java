package com.capstone2024.scss.domain.event.service.impl;

import com.capstone2024.scss.application.account.dto.enums.SortDirection;
import com.capstone2024.scss.application.advice.exeptions.NotFoundException;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.event.dto.EventDTO;
import com.capstone2024.scss.application.event.dto.request.EventFilterDTO;
import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.account.enums.Role;
import com.capstone2024.scss.domain.common.mapper.event.EventMapper;
import com.capstone2024.scss.domain.event.entities.Event;
import com.capstone2024.scss.domain.event.entities.StudentInteraction;
import com.capstone2024.scss.domain.event.entities.enums.InteractionType;
import com.capstone2024.scss.domain.event.service.EventService;
import com.capstone2024.scss.domain.student.entities.Student;
import com.capstone2024.scss.infrastructure.repositories.StudentRepository;
import com.capstone2024.scss.infrastructure.repositories.account.AccountRepository;
import com.capstone2024.scss.infrastructure.repositories.event.EventRepository;
import com.capstone2024.scss.infrastructure.repositories.event.StudentInteractionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final StudentInteractionRepository studentInteractionRepository;
    private final AccountRepository accountRepository;
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

    public EventDTO getOneEvent(Long eventId, Long accountID, boolean isFilter) {
        logger.info("Fetching event with ID: {}", eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    logger.error("Event not found with ID: {}", eventId);
                    return new NotFoundException("Event not found with id " + eventId);
                });

        Account account = accountRepository.findById(accountID)
                .orElseThrow(() -> {
                    logger.error("Account not found with ID: {}", accountID);
                    return new NotFoundException("Account not found with id " + accountID);
                });

        if (account.getRole().equals(Role.STUDENT)) {
            InteractionType interactionType = isFilter ? InteractionType.FILTER : InteractionType.VIEW;

            if (interactionType == InteractionType.VIEW) {
                event.setView(event.getView() + 1);
                eventRepository.save(event);
                logger.info("Incremented view count for event ID: {}", eventId);
            }

            StudentInteraction interaction = StudentInteraction.builder()
                    .student((Student) account.getProfile())
                    .event(event)
                    .interactionType(interactionType)
                    .interactionTime(LocalDateTime.now())
                    .build();

            studentInteractionRepository.save(interaction);
            logger.info("Saved interaction for student ID: {} on event ID: {} with interaction type: {}", accountID, eventId, interactionType);
        }

        logger.info("Returning event DTO for event ID: {}", eventId);
        return EventMapper.toDTO(event);
    }
}
