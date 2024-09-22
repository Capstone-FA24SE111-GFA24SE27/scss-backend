package com.capstone2024.scss.domain.event.service;

import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.event.dto.EventDTO;
import com.capstone2024.scss.application.event.dto.request.EventFilterDTO;

import java.util.List;

public interface EventService {
    PaginationDTO<List<EventDTO>> getAllEvents(EventFilterDTO filterRequest);
}
