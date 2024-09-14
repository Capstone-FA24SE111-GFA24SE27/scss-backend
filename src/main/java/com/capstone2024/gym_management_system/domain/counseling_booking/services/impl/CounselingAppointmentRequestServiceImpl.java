package com.capstone2024.gym_management_system.domain.counseling_booking.services.impl;

import com.capstone2024.gym_management_system.application.advice.exeptions.BadRequestException;
import com.capstone2024.gym_management_system.application.advice.exeptions.NotFoundException;
import com.capstone2024.gym_management_system.application.booking_counseling.dto.SlotDTO;
import com.capstone2024.gym_management_system.application.booking_counseling.dto.enums.SlotStatus;
import com.capstone2024.gym_management_system.domain.counseling_booking.entities.CounselingSlot;
import com.capstone2024.gym_management_system.domain.counseling_booking.entities.counseling_appointment_request.CounselingAppointmentRequest;
import com.capstone2024.gym_management_system.domain.counseling_booking.entities.counseling_appointment_request.OfflineAppointment;
import com.capstone2024.gym_management_system.domain.counseling_booking.entities.counseling_appointment_request.OnlineAppointment;
import com.capstone2024.gym_management_system.domain.counseling_booking.entities.counseling_appointment_request.enums.CounselingAppointmentRequestStatus;
import com.capstone2024.gym_management_system.domain.counseling_booking.entities.counseling_appointment_request.enums.MeetingType;
import com.capstone2024.gym_management_system.domain.counseling_booking.entities.counselor.Counselor;
import com.capstone2024.gym_management_system.domain.counseling_booking.entities.student.Student;
import com.capstone2024.gym_management_system.domain.counseling_booking.services.CounselingAppointmentRequestService;
import com.capstone2024.gym_management_system.infrastructure.repositories.CounselingAppointmentRequestRepository;
import com.capstone2024.gym_management_system.infrastructure.repositories.CounselingSlotRepository;
import com.capstone2024.gym_management_system.infrastructure.repositories.CounselorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CounselingAppointmentRequestServiceImpl implements CounselingAppointmentRequestService {

    private static final Logger logger = LoggerFactory.getLogger(CounselingAppointmentRequestService.class);
    private final CounselingAppointmentRequestRepository requestRepository;
    private final CounselingSlotRepository slotRepository;
    private final CounselorRepository counselorRepository;

    public CounselingAppointmentRequestServiceImpl(CounselingAppointmentRequestRepository requestRepository, CounselingSlotRepository slotRepository, CounselorRepository counselorRepository) {
        this.requestRepository = requestRepository;
        this.slotRepository = slotRepository;
        this.counselorRepository = counselorRepository;
    }

    @Override
    public Map<LocalDate, List<SlotDTO>> getDailySlots(Long counselorId, LocalDate from, LocalDate to, Long studentId) {
        logger.info("Fetching daily slots for counselorId: {}, from: {}, to: {}", counselorId, from, to);
        // Find the counselor
        Counselor counselor = counselorRepository.findById(counselorId)
                .orElseThrow(() -> new NotFoundException("Counselor not found"));
        List<CounselingAppointmentRequest> requests = requestRepository.findByCounselorIdAndRequireDateBetween(counselorId, from, to);
        List<CounselingSlot> slots = slotRepository.findAllSlots();
        LocalDateTime now = LocalDateTime.now();

        Map<LocalDate, List<SlotDTO>> dailySlots = new LinkedHashMap<>();
        LocalDate currentDate = from;

        while (!currentDate.isAfter(to)) {
            final LocalDate dateToCheck = currentDate;
            List<SlotDTO> slotDTOs = slots.stream()
                    .map(slot -> {
                        boolean isSlotTaken = requests.stream()
                                .anyMatch(r -> r.getRequireDate().equals(dateToCheck) &&
                                        (r.getStatus() == CounselingAppointmentRequestStatus.WAITING ||
                                                r.getStatus() == CounselingAppointmentRequestStatus.APPROVED) &&
                                        (r.getStartTime().isBefore(slot.getEndTime()) &&
                                                r.getEndTime().isAfter(slot.getStartTime())));

                        boolean isMyAppointment = requests.stream()
                                .anyMatch(r -> r.getRequireDate().equals(dateToCheck) &&
                                        (r.getStatus() == CounselingAppointmentRequestStatus.WAITING ||
                                                r.getStatus() == CounselingAppointmentRequestStatus.APPROVED) &&
                                        r.getStudent() != null &&
                                        r.getStudent().getId().equals(studentId) &&
                                        (r.getStartTime().isBefore(slot.getEndTime()) &&
                                                r.getEndTime().isAfter(slot.getStartTime())));

                        SlotStatus status;
                        if (isSlotTaken) {
                            status = SlotStatus.UNAVAILABLE;
                        } else if (LocalDateTime.of(dateToCheck, slot.getEndTime()).isBefore(now)) {
                            status = SlotStatus.EXPIRED;
                        } else {
                            status = SlotStatus.AVAILABLE;
                        }

                        return SlotDTO.builder()
                                .slotCode(slot.getSlotCode())
                                .startTime(slot.getStartTime())
                                .endTime(slot.getEndTime())
                                .status(status)
                                .isMyAppointment(isMyAppointment)
                                .build();
                    })
                    .collect(Collectors.toList());

            dailySlots.put(currentDate, slotDTOs);
            currentDate = currentDate.plusDays(1);
        }

        logger.info("Daily slots fetched successfully.");
        return dailySlots;
    }

    @Override
    public CounselingAppointmentRequest createAppointmentRequest(String slotCode, LocalDate date, Long counselorId, boolean isOnline, String reason, Student student) {
        logger.info("Creating appointment request for slotCode: {}, date: {}, counselorId: {}, isOnline: {}, reason: {}",
                slotCode, date, counselorId, isOnline, reason);

        // Find the slot
        CounselingSlot slot = slotRepository.findBySlotCode(slotCode)
                .orElseThrow(() -> new NotFoundException("Slot not found"));

        // Validate slot status
        SlotStatus slotStatus = getSlotStatus(slot, date);
        if (slotStatus != SlotStatus.AVAILABLE) {
            throw new BadRequestException("Slot is not available");
        }

        // Find the counselor
        Counselor counselor = counselorRepository.findById(counselorId)
                .orElseThrow(() -> new NotFoundException("Counselor not found"));

        // Create the appointment request
        CounselingAppointmentRequest appointmentRequest = isOnline
                ? OnlineAppointment.builder()
                .requireDate(date)
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .status(CounselingAppointmentRequestStatus.WAITING)
                .meetingType(MeetingType.ONLINE)
                .reason(reason)
                .counselor(counselor)
                .student(student)
                .meetUrl("defaultUrl") // Set a default URL or manage accordingly
                .build()
                : OfflineAppointment.builder()
                .requireDate(date)
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .status(CounselingAppointmentRequestStatus.WAITING)
                .meetingType(MeetingType.OFFLINE)
                .reason(reason)
                .counselor(counselor)
                .student(student)
                .address("defaultAddress") // Set a default address or manage accordingly
                .build();

        // Save the appointment request
        return requestRepository.save(appointmentRequest);
    }

    private SlotStatus getSlotStatus(CounselingSlot slot, LocalDate date) {
        // Assume we have a method to get appointment requests for a slot on a specific date
        boolean isSlotTaken = requestRepository.findByRequireDateAndStartTimeAndEndTime(date, slot.getStartTime(), slot.getEndTime()).stream()
                .anyMatch(r -> r.getStatus() == CounselingAppointmentRequestStatus.WAITING ||
                        r.getStatus() == CounselingAppointmentRequestStatus.APPROVED);

        LocalDateTime now = LocalDateTime.now();
        if (isSlotTaken) {
            return SlotStatus.UNAVAILABLE;
        } else if (LocalDateTime.of(date, slot.getEndTime()).isBefore(now)) {
            return SlotStatus.EXPIRED;
        } else {
            return SlotStatus.AVAILABLE;
        }
    }
}
