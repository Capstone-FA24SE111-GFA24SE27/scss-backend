package com.capstone2024.scss.application.holiday.controller;

import com.capstone2024.scss.application.advice.exeptions.BadRequestException;
import com.capstone2024.scss.application.authentication.controller.AuthenticationController;
import com.capstone2024.scss.application.common.utils.ResponseUtil;
import com.capstone2024.scss.application.holiday.dto.HolidayResponseDTO;
import com.capstone2024.scss.application.holiday.dto.request.HolidayCreateRequestDTO;
import com.capstone2024.scss.application.holiday.dto.request.HolidayUpdateRequestDTO;
import com.capstone2024.scss.domain.counseling_booking.entities.Holiday;
import com.capstone2024.scss.domain.counseling_booking.services.HolidayService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/holidays")
@Tag(name = "Holidays", description = "API endpoints for managing holidays")
@RequiredArgsConstructor
public class HolidayController {

    private final HolidayService holidayService;
    private static final Logger logger = LoggerFactory.getLogger(HolidayController.class);

    // Get All Holidays
    @GetMapping
    public ResponseEntity<Object> getAllHolidays() {
        List<HolidayResponseDTO> holidayDTOs = holidayService.getAllHolidays();
        return ResponseUtil.getResponse(holidayDTOs, HttpStatus.OK);
    }

    // Get Holiday by ID
    @GetMapping("/{id}")
    public ResponseEntity<Object> getHolidayById(@PathVariable Long id) {
        HolidayResponseDTO holidayDTO = holidayService.getHolidayById(id);
        return ResponseUtil.getResponse(holidayDTO, HttpStatus.OK);
    }

    // Create a new Holiday
    @PostMapping
    public ResponseEntity<Object> createHoliday(@Validated @RequestBody HolidayCreateRequestDTO createDTO, BindingResult errors) {
        if (errors.hasErrors()) {
            logger.warn("Login request failed due to validation errors: {}", errors.getAllErrors());
            throw new BadRequestException("Invalid login request", errors, HttpStatus.BAD_REQUEST);
        }

        HolidayResponseDTO responseDTO = holidayService.createHoliday(createDTO);
        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }

    // Update an existing Holiday
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateHoliday(@PathVariable Long id,
                                                            @Validated @RequestBody HolidayUpdateRequestDTO updateDTO,
                                                            BindingResult errors) {
        if (errors.hasErrors()) {
            logger.warn("Login request failed due to validation errors: {}", errors.getAllErrors());
            throw new BadRequestException("Invalid login request", errors, HttpStatus.BAD_REQUEST);
        }
        HolidayResponseDTO responseDTO = holidayService.updateHoliday(id, updateDTO);
        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }

    // Delete a Holiday
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteHoliday(@PathVariable Long id) {
        holidayService.deleteHoliday(id);
        return ResponseUtil.getResponse("Delete successfully", HttpStatus.OK);
    }
}
