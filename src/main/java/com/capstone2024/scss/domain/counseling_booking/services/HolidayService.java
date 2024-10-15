package com.capstone2024.scss.domain.counseling_booking.services;

import com.capstone2024.scss.application.holiday.dto.HolidayResponseDTO;
import com.capstone2024.scss.application.holiday.dto.request.HolidayCreateRequestDTO;
import com.capstone2024.scss.application.holiday.dto.request.HolidayUpdateRequestDTO;

import java.util.List;

public interface HolidayService {
    List<HolidayResponseDTO> getAllHolidays();
    HolidayResponseDTO getHolidayById(Long id);
    HolidayResponseDTO createHoliday(HolidayCreateRequestDTO createDTO);
    HolidayResponseDTO updateHoliday(Long id, HolidayUpdateRequestDTO updateDTO);
    void deleteHoliday(Long id);
}
