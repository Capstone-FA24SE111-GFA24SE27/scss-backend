package com.capstone2024.scss.domain.counselor.services.impl;

import com.capstone2024.scss.application.account.dto.CounselorProfileDTO;
import com.capstone2024.scss.application.advice.exeptions.NotFoundException;
import com.capstone2024.scss.application.booking_counseling.dto.SlotDTO;
import com.capstone2024.scss.application.booking_counseling.dto.enums.SlotStatus;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.counselor.dto.CounselingSlotDTO;
import com.capstone2024.scss.application.counselor.dto.CounselorDTO;
import com.capstone2024.scss.application.counselor.dto.request.CounselorFilterRequestDTO;
import com.capstone2024.scss.domain.common.mapper.appointment_counseling.CounselingSlotMapper;
import com.capstone2024.scss.domain.common.mapper.appointment_counseling.CounselorProfileMapper;
import com.capstone2024.scss.domain.common.mapper.appointment_counseling.ExpertiseDTO;
import com.capstone2024.scss.domain.common.mapper.appointment_counseling.ExpertiseMapper;
import com.capstone2024.scss.domain.counseling_booking.entities.CounselingSlot;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.CounselingAppointmentRequest;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.enums.CounselingAppointmentRequestStatus;
import com.capstone2024.scss.domain.counselor.entities.Counselor;
import com.capstone2024.scss.domain.counselor.entities.Expertise;
import com.capstone2024.scss.domain.counselor.entities.enums.Gender;
import com.capstone2024.scss.domain.counselor.services.CounselorService;
import com.capstone2024.scss.infrastructure.repositories.CounselingAppointmentRequestRepository;
import com.capstone2024.scss.infrastructure.repositories.CounselingSlotRepository;
import com.capstone2024.scss.infrastructure.repositories.counselor.CounselorRepository;
import com.capstone2024.scss.infrastructure.repositories.counselor.ExpertiseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CounselorServiceImpl implements CounselorService {

    private final CounselorRepository counselorRepository;
    private final ExpertiseRepository expertiseRepository;
    private final CounselingSlotRepository counselingSlotRepository;
    private final CounselingSlotRepository slotRepository;
    private final CounselingAppointmentRequestRepository requestRepository;

    @Override
    public PaginationDTO<List<CounselorProfileDTO>> getCounselorsWithFilter(CounselorFilterRequestDTO filterRequest) {
        Page<Counselor> counselorsPage = counselorRepository.findByKeywordAndRatingRange(
                filterRequest.getSearch(),
                filterRequest.getRatingFrom(),
                filterRequest.getRatingTo(),
                filterRequest.getPagination()
        );

        List<CounselorProfileDTO> counselorDTOs = counselorsPage.getContent().stream()
                .map(CounselorProfileMapper::toCounselorProfileDTO)
                .collect(Collectors.toList());

        return PaginationDTO.<List<CounselorProfileDTO>>builder()
                .data(counselorDTOs)
                .totalPages(counselorsPage.getTotalPages())
                .totalElements((int) counselorsPage.getTotalElements())
                .build();
    }

    @Override
    public CounselorProfileDTO getOneCounselor(Long counselorId) {
        // Find the Counselor entity by ID
        Optional<Counselor> optionalCounselor = counselorRepository.findById(counselorId);

        if (optionalCounselor.isEmpty()) {
            throw new NotFoundException("Counselor not found");
        }

        Counselor counselor = optionalCounselor.get();

        // Convert to CounselorDTO
        return CounselorProfileMapper.toCounselorProfileDTO(counselor);
    }

    @Override
    public List<ExpertiseDTO> getAllExpertises() {
        List<Expertise> expertiseList = expertiseRepository.findAll();
        return expertiseList.stream()
                .map(ExpertiseMapper::toExpertiseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CounselorProfileDTO findBestAvailableCounselor(Long slotId, LocalDate date, Gender gender, Long expertiseId) {
        CounselingSlot slot = counselingSlotRepository.findById(slotId)
                .orElseThrow(() -> new NotFoundException("Slot not found with ID: " + slotId));

        Expertise expertise = null;
        if(expertiseId != null) {
            expertise = expertiseRepository.findById(expertiseId)
                    .orElseThrow(() -> new NotFoundException("Expertise not found with ID: " + expertiseId));
        }

        // Kết hợp date với startTime và endTime từ slot
//        LocalDateTime startDateTime = LocalDateTime.of(date, slot.getStartTime());
//        LocalDateTime endDateTime = LocalDateTime.of(date, slot.getEndTime());

        // Tạo Pageable để giới hạn kết quả trả về chỉ 1 bản ghi
        PageRequest pageable = PageRequest.of(0, 1);

        // Thực hiện truy vấn
        List<Counselor> counselors = counselorRepository.findAvailableCounselorsByGenderAndExpertiseOrdered(
                gender, expertise, date, slot.getStartTime(), slot.getEndTime(), pageable);

        if (counselors.isEmpty()) {
            throw new NotFoundException("Không tìm thấy counselor nào khả dụng vào thời gian này với yêu cầu giới tính và chuyên môn.");
        }

//        HashMap<String, Object> re = new HashMap<>();
//        re.put("counselor", counselors.isEmpty() ? null : counselors.getFirst());
//        re.put("recommend", counselorRepository.findAvailableCounselors(date));


        return CounselorProfileMapper.toCounselorProfileDTO(counselors.getFirst());
    }

    @Override
    public List<SlotDTO> getAllCounselingSlots(LocalDate date, Long studentId) {
        List<CounselingSlot> slots = slotRepository.findAllSlots();
        LocalDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDateTime();
        LocalDate currentDate = date;
        List<CounselingAppointmentRequest> requests = requestRepository.findByRequireDate(date);
        if (currentDate.getDayOfWeek() == DayOfWeek.SATURDAY || currentDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return Collections.emptyList();
        } else {
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
                                .slotId(slot.getId())
                                .slotCode(slot.getSlotCode())
                                .startTime(slot.getStartTime())
                                .endTime(slot.getEndTime())
                                .status(status)
                                .isMyAppointment(isMyAppointment)
                                .build();
                    })
                    .collect(Collectors.toList());
            return slotDTOs;
        }
    }
}
