package com.capstone2024.scss.domain.counselor.services.impl;

import com.capstone2024.scss.application.advice.exeptions.BadRequestException;
import com.capstone2024.scss.application.advice.exeptions.NotFoundException;
import com.capstone2024.scss.application.booking_counseling.dto.AppointmentFeedbackDTO;
import com.capstone2024.scss.application.booking_counseling.dto.CounselingAppointmentDTO;
import com.capstone2024.scss.application.booking_counseling.dto.counseling_appointment_request.CounselingAppointmentRequestDTO;
import com.capstone2024.scss.application.booking_counseling.dto.request.AppointmentRequestFilterDTO;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.counseling_appointment.dto.AppointmentReportResponse;
import com.capstone2024.scss.application.counseling_appointment.dto.request.counseling_appointment.AppointmentFilterDTO;
import com.capstone2024.scss.application.counselor.dto.CounselingSlotDTO;
import com.capstone2024.scss.application.counselor.dto.ManageCounselorDTO;
import com.capstone2024.scss.application.counselor.dto.request.*;
import com.capstone2024.scss.application.q_and_a.dto.QuestionCardFeedbackDTO;
import com.capstone2024.scss.domain.common.mapper.q_and_a.QuestionCardFeedbackMapper;
import com.capstone2024.scss.domain.counselor.entities.*;
import com.capstone2024.scss.domain.counselor.services.CounselorService;
import com.capstone2024.scss.domain.counselor.services.ManageCounselorService;
import com.capstone2024.scss.domain.common.mapper.account.CounselorProfileMapper;
import com.capstone2024.scss.domain.common.mapper.appointment_counseling.AppointmentFeedbackMapper;
import com.capstone2024.scss.domain.common.mapper.appointment_counseling.CounselingSlotMapper;
import com.capstone2024.scss.domain.counseling_booking.entities.CounselingSlot;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.AppointmentFeedback;
import com.capstone2024.scss.domain.counseling_booking.services.CounselingAppointmentRequestService;
import com.capstone2024.scss.domain.counseling_booking.services.CounselingAppointmentService;
import com.capstone2024.scss.domain.counselor.entities.enums.CounselorStatus;
import com.capstone2024.scss.domain.q_and_a.entities.QuestionCardFeedback;
import com.capstone2024.scss.infrastructure.repositories._and_a.QuestionCardFeedbackRepository;
import com.capstone2024.scss.infrastructure.repositories.booking_counseling.AppointmentFeedbackRepository;
import com.capstone2024.scss.infrastructure.repositories.booking_counseling.CounselingSlotRepository;
import com.capstone2024.scss.infrastructure.repositories.counselor.AvailableDateRangeRepository;
import com.capstone2024.scss.infrastructure.repositories.counselor.CounselorRepository;
import com.capstone2024.scss.infrastructure.repositories.counselor.SlotOfCounselorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManageCounselorServiceImpl implements ManageCounselorService {
    private final CounselorService counselorService;
    private final CounselingAppointmentRequestService counselingAppointmentRequestService;
    private final CounselorRepository counselorRepository;
    private final CounselingAppointmentService appointmentService;
    private final AvailableDateRangeRepository availableDateRangeRepository;
    private final CounselingSlotRepository counselingSlotRepository;
    private final AppointmentFeedbackRepository appointmentFeedbackRepository;
    private final QuestionCardFeedbackRepository questionCardFeedbackRepository;

    public PaginationDTO<List<CounselingAppointmentRequestDTO>> getAppointmentsRequestOfCounselorForManage(Long counselorId, AppointmentRequestFilterDTO filterDTO) {
        Counselor counselor = checkForCounselor(counselorId);

        return counselingAppointmentRequestService.getAppointmentsRequest(counselor.getAccount(), filterDTO);
    }

    public List<CounselingAppointmentDTO> getAppointmentsForCounselor(LocalDate fromDate, LocalDate toDate, Long counselorId) {
        checkForCounselor(counselorId);

        return appointmentService.getAppointmentsForCounselor(fromDate, toDate, counselorId);
    }

    private Counselor checkForCounselor(Long counselorId) {
        return counselorRepository.findById(counselorId)
                .orElseThrow(() -> new NotFoundException("Counselor not found with id: " + counselorId));
    }

    public PaginationDTO<List<CounselingAppointmentDTO>> getAppointmentsWithFilterForCounselor(AppointmentFilterDTO filterDTO, Long counselorId) {
        Counselor counselor = checkForCounselor(counselorId);

        return appointmentService.getAppointmentsWithFilterForCounselor(filterDTO, counselor);
    }

    public AppointmentReportResponse getAppointmentReportByAppointmentId(Long appointmentId, Long counselorId) {
        Counselor counselor = checkForCounselor(counselorId);

        return appointmentService.getAppointmentReportByAppointmentId(appointmentId);
    }

    @Override
    public void updateCounselorStatus(Long counselorId, CounselorStatus status) {
        Counselor counselor = counselorRepository.findById(counselorId)
                .orElseThrow(() -> new NotFoundException("Counselor not found"));

        counselor.setStatus(status);
        counselorRepository.save(counselor);
    }

    @Override
    @Transactional
    public void updateAvailableDateRange(Long counselorId, LocalDate startDate, LocalDate endDate) {
        // Validate dates
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        // Fetch counselor
        Counselor counselor = counselorRepository.findById(counselorId)
                .orElseThrow(() -> new NotFoundException("Counselor not found"));

        // Fetch or create AvailableDateRange
        AvailableDateRange dateRange = availableDateRangeRepository.findByCounselor(counselor)
                .orElse(AvailableDateRange.builder()
                        .counselor(counselor)
                        .startDate(startDate)
                        .endDate(endDate)
                        .build());

        // Update dates
        dateRange.setStartDate(startDate);
        dateRange.setEndDate(endDate);

        // Save the updated entity
        availableDateRangeRepository.save(dateRange);
    }

    @Override
    public AvailableDateRange getAvailableDateRangeByCounselorId(Long counselorId) {
        Counselor counselor = counselorRepository.findById(counselorId)
                .orElseThrow(() -> new NotFoundException("Counselor not found"));

        return availableDateRangeRepository.findByCounselor(counselor)
                .orElseThrow(() -> new NotFoundException("Available date range not found for counselorId: " + counselorId));
    }

    @Override
    public List<CounselingSlot> getAllCounselingSlots() {
        return counselingSlotRepository.findAll();
    }

    @Override
    public List<SlotOfCounselor> getCounselingSlotsByCounselorId(Long counselorId) {
        Counselor counselor = counselorRepository.findById(counselorId)
                .orElseThrow(() -> new NotFoundException("Counselor not found"));

        return counselor.getSlotOfCounselors();
    }

    @Override
    @Transactional
    public void assignSlotToCounselor(Long counselorId, Long slotId, DayOfWeek dayOfWeek) {
        Counselor counselor = counselorRepository.findById(counselorId)
                .orElseThrow(() -> new NotFoundException("Counselor không tồn tại với ID: " + counselorId));

        CounselingSlot slot = counselingSlotRepository.findById(slotId)
                .orElseThrow(() -> new NotFoundException("CounselingSlot không tồn tại với ID: " + slotId));

//        if (counselor.getCounselingSlots().contains(slot)) {
//            throw new BadRequestException("Counselor đã được gán CounselingSlot này");
//        }

        SlotOfCounselor slotOfCounselor = SlotOfCounselor.builder()
                .counselor(counselor)
                .counselingSlot(slot)
                .dayOfWeek(dayOfWeek)
                .build();

        boolean isHaving = counselor.getSlotOfCounselors().stream().anyMatch(slotOfCounselor1 -> slotOfCounselor1.getCounselingSlot().getId().equals(slotId) && slotOfCounselor1.getDayOfWeek().equals(dayOfWeek));
        if (isHaving) {
            throw new BadRequestException("Counselor already have this slot");
        }
        slotOfCounselorRepository.save(slotOfCounselor);

        counselor.getSlotOfCounselors().add(slotOfCounselor);
        counselorRepository.save(counselor); // Lưu Counselor với danh sách slots cập nhật
    }

    private final SlotOfCounselorRepository slotOfCounselorRepository;

    @Override
    @Transactional
    public void unassignSlotFromCounselor(Long counselorId, Long slotId) {
        Counselor counselor = counselorRepository.findById(counselorId)
                .orElseThrow(() -> new NotFoundException("Counselor không tồn tại với ID: " + counselorId));

//        CounselingSlot slot = counselingSlotRepository.findById(slotId)
//                .orElseThrow(() -> new NotFoundException("CounselingSlot không tồn tại với ID: " + slotId));

        SlotOfCounselor slotOfCounselor = slotOfCounselorRepository.findById(slotId)
                .orElseThrow(() -> new NotFoundException("CounselingSlot không tồn tại với ID: " + slotId));

//        if (!counselor.getCounselingSlots().contains(slot)) {
//            throw new BadRequestException("Counselor không có CounselingSlot này để gỡ gán");
//        }

        counselor.getSlotOfCounselors().remove(slotOfCounselor);
        counselorRepository.save(counselor); // Lưu Counselor với danh sách slots cập nhật
    }

    @Override
    public PaginationDTO<List<AppointmentFeedbackDTO>> getFeedbackWithFilterForCounselor(FeedbackFilterDTO filterDTO, Long counselorId) {
        Pageable pageable = createPageable(filterDTO);

        LocalDateTime fromDateTime = filterDTO.getDateFrom() != null ? filterDTO.getDateFrom().atStartOfDay() : null;
        LocalDateTime toDateTime = filterDTO.getDateTo() != null ? filterDTO.getDateTo().atTime(LocalTime.MAX) : null;

        Page<AppointmentFeedback> feedbackPage = appointmentFeedbackRepository.findFeedbackForCounselorWithFilter(
                filterDTO.getKeyword(),
                fromDateTime,
                toDateTime,
                filterDTO.getRatingFrom(),
                filterDTO.getRatingTo(),
                counselorId,
                pageable);

        List<AppointmentFeedbackDTO> feedbackDTOs = feedbackPage.getContent()
                .stream()
                .map(AppointmentFeedbackMapper::toNormalDTO)
                .collect(Collectors.toList());

        return PaginationDTO.<List<AppointmentFeedbackDTO>>builder()
                .data(feedbackDTOs)
                .totalPages(feedbackPage.getTotalPages())
                .totalElements((int) feedbackPage.getTotalElements())
                .build();
    }

    @Override
    public PaginationDTO<List<ManageCounselorDTO>> getCounselorsWithFilter(CounselorFilterRequestDTO filterRequest) {
        Page<Counselor> counselorsPage = counselorRepository.findByKeywordAndRatingRange(
                filterRequest.getSearch(),
//                filterRequest.getRatingFrom(),
//                filterRequest.getRatingTo(),
                filterRequest.getPagination()
        );

        List<ManageCounselorDTO> counselorDTOs = counselorsPage.getContent().stream()
                .map(CounselorProfileMapper::toManageCounselorDTO)
                .collect(Collectors.toList());

        return PaginationDTO.<List<ManageCounselorDTO>>builder()
                .data(counselorDTOs)
                .totalPages(counselorsPage.getTotalPages())
                .totalElements((int) counselorsPage.getTotalElements())
                .build();
    }

    @Override
    public ManageCounselorDTO getOneCounselor(Long counselorId) {
        // Find the Counselor entity by ID
        Optional<Counselor> optionalCounselor = counselorRepository.findById(counselorId);

        if (optionalCounselor.isEmpty()) {
            throw new NotFoundException("Counselor not found");
        }

        Counselor counselor = optionalCounselor.get();

        // Convert to CounselorDTO
        return CounselorProfileMapper.toManageCounselorDTO(counselor);
    }

    @Override
    public PaginationDTO<List<ManageCounselorDTO>> getAcademicCounselorsWithFilter(AcademicCounselorFilterRequestDTO filterRequest) {
        Page<AcademicCounselor> counselorsPage = counselorRepository.findAcademicCounselorsWithFilterForManaging(
                filterRequest.getSearch(),
//                filterRequest.getRatingFrom(),
//                filterRequest.getRatingTo(),
                filterRequest.getAvailableFrom(),
                filterRequest.getAvailableTo(),
//                filterRequest.getSpecializationId(),
                filterRequest.getDepartmentId(),
                filterRequest.getMajorId(),
                filterRequest.getStatus(),
                filterRequest.getGender(),
                filterRequest.getPagination());

        List<ManageCounselorDTO> counselorDTOs = counselorsPage.getContent().stream()
                .map(CounselorProfileMapper::toManageCounselorDTO)
                .collect(Collectors.toList());

        return PaginationDTO.<List<ManageCounselorDTO>>builder()
                .data(counselorDTOs)
                .totalPages(counselorsPage.getTotalPages())
                .totalElements((int) counselorsPage.getTotalElements())
                .build();
    }

    @Override
    public PaginationDTO<List<ManageCounselorDTO>> getNonAcademicCounselorsWithFilter(NonAcademicCounselorFilterRequestDTO filterRequest) {
        Page<NonAcademicCounselor> counselorsPage = counselorRepository.findNonAcademicCounselorsWithFilterForManaging(
                filterRequest.getSearch(),
//                filterRequest.getRatingFrom(),
//                filterRequest.getRatingTo(),
                filterRequest.getAvailableFrom(),
                filterRequest.getAvailableTo(),
                filterRequest.getExpertiseId(),
                filterRequest.getStatus(),
                filterRequest.getGender(),
                filterRequest.getPagination()
        );

        List<ManageCounselorDTO> counselorDTOs = counselorsPage.getContent().stream()
                .map(CounselorProfileMapper::toManageCounselorDTO)
                .collect(Collectors.toList());

        return PaginationDTO.<List<ManageCounselorDTO>>builder()
                .data(counselorDTOs)
                .totalPages(counselorsPage.getTotalPages())
                .totalElements((int) counselorsPage.getTotalElements())
                .build();
    }

    @Override
    public CounselingSlotDTO createCounselingSlot(CreateCounselingSlotRequestDTO createCounselingSlotDTO) {
        CounselingSlot counselingSlot = new CounselingSlot();
        counselingSlot.setSlotCode(createCounselingSlotDTO.getSlotCode());
        counselingSlot.setName(createCounselingSlotDTO.getName());
        counselingSlot.setStartTime(createCounselingSlotDTO.getStartTime());
        counselingSlot.setEndTime(createCounselingSlotDTO.getEndTime());
        return CounselingSlotMapper.toDTO(counselingSlotRepository.save(counselingSlot));
    }

    private Pageable createPageable(FeedbackFilterDTO filterDTO) {
        Sort.Direction direction = Sort.Direction.fromString(filterDTO.getSortDirection());
        return PageRequest.of(filterDTO.getPage() - 1, 10, Sort.by(direction, filterDTO.getSortBy()));
    }

    @Override
    public PaginationDTO<List<QuestionCardFeedbackDTO>> getQCFeedbackWithFilterForCounselor(FeedbackFilterDTO filterDTO, Long counselorId) {
        Sort.Direction direction = Sort.Direction.fromString(filterDTO.getSortDirection());
        Pageable pageable = PageRequest.of(filterDTO.getPage() - 1, filterDTO.getSize(), Sort.by(direction, filterDTO.getSortBy()));

        LocalDateTime fromDateTime = filterDTO.getDateFrom() != null ? filterDTO.getDateFrom().atStartOfDay() : null;
        LocalDateTime toDateTime = filterDTO.getDateTo() != null ? filterDTO.getDateTo().atTime(LocalTime.MAX) : null;

        Page<QuestionCardFeedback> feedbackPage = questionCardFeedbackRepository.findFeedbackForCounselorWithFilter(
                filterDTO.getKeyword(),
                fromDateTime,
                toDateTime,
                filterDTO.getRatingFrom(),
                filterDTO.getRatingTo(),
                counselorId,
                pageable);

        List<QuestionCardFeedbackDTO> feedbackDTOs = feedbackPage.getContent()
                .stream()
                .map(QuestionCardFeedbackMapper::toNormalDTO)
                .collect(Collectors.toList());

        return PaginationDTO.<List<QuestionCardFeedbackDTO>>builder()
                .data(feedbackDTOs)
                .totalPages(feedbackPage.getTotalPages())
                .totalElements((int) feedbackPage.getTotalElements())
                .build();
    }

}
