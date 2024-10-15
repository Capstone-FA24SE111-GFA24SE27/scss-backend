package com.capstone2024.scss.domain.counseling_booking.services.impl;

import com.capstone2024.scss.application.advice.exeptions.NotFoundException;
import com.capstone2024.scss.application.holiday.dto.HolidayResponseDTO;
import com.capstone2024.scss.application.holiday.dto.request.HolidayCreateRequestDTO;
import com.capstone2024.scss.application.holiday.dto.request.HolidayUpdateRequestDTO;
import com.capstone2024.scss.domain.common.mapper.holiday.HolidayMapper;
import com.capstone2024.scss.domain.counseling_booking.entities.Holiday;
import com.capstone2024.scss.domain.counseling_booking.services.HolidayService;
import com.capstone2024.scss.infrastructure.repositories.HolidayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HolidayServiceImpl implements HolidayService {

    private final HolidayRepository holidayRepository;

    @Override
    @Transactional(readOnly = true)
    public List<HolidayResponseDTO> getAllHolidays() {
        List<Holiday> holidays = holidayRepository.findAll();
        return holidays.stream()
                .map(HolidayMapper::toHolidayResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public HolidayResponseDTO getHolidayById(Long id) {
        Holiday holiday = holidayRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Holiday not found with id " + id));
        return HolidayMapper.toHolidayResponseDTO(holiday);
    }

    @Override
    @Transactional
    public HolidayResponseDTO createHoliday(HolidayCreateRequestDTO createDTO) {
        Holiday holiday = HolidayMapper.toHoliday(createDTO);
        Holiday savedHoliday = holidayRepository.save(holiday);
        return HolidayMapper.toHolidayResponseDTO(savedHoliday);
    }

    @Override
    @Transactional
    public HolidayResponseDTO updateHoliday(Long id, HolidayUpdateRequestDTO updateDTO) {
        Holiday holiday = holidayRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Holiday not found with id " + id));
        HolidayMapper.updateHolidayFromDTO(updateDTO, holiday);
        Holiday updatedHoliday = holidayRepository.save(holiday);
        return HolidayMapper.toHolidayResponseDTO(updatedHoliday);
    }

    @Override
    @Transactional
    public void deleteHoliday(Long id) {
        Holiday holiday = holidayRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Holiday not found with id " + id));
        holidayRepository.delete(holiday);
    }
}
