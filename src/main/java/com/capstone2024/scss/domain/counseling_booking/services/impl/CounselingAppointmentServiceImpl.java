package com.capstone2024.scss.domain.counseling_booking.services.impl;

import com.capstone2024.scss.application.advice.exeptions.BadRequestException;
import com.capstone2024.scss.application.advice.exeptions.NotFoundException;
import com.capstone2024.scss.application.booking_counseling.dto.CounselingAppointmentDTO;
import com.capstone2024.scss.application.booking_counseling.dto.counseling_appointment_request.CounselorAppointmentDTO;
import com.capstone2024.scss.application.booking_counseling.dto.counseling_appointment_request.StudentAppointmentDTO;
import com.capstone2024.scss.application.booking_counseling.dto.request.counceling_appointment.OfflineAppointmentRequestDTO;
import com.capstone2024.scss.application.booking_counseling.dto.request.counceling_appointment.OnlineAppointmentRequestDTO;
import com.capstone2024.scss.domain.common.mapper.account.ProfileMapper;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.CounselingAppointment;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.OfflineAppointment;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.OnlineAppointment;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.enums.CounselingAppointmentStatus;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.CounselingAppointmentRequest;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.enums.CounselingAppointmentRequestStatus;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.enums.MeetingType;
import com.capstone2024.scss.domain.counselor.entities.Counselor;
import com.capstone2024.scss.domain.student.entities.Student;
import com.capstone2024.scss.domain.counseling_booking.services.CounselingAppointmentService;
import com.capstone2024.scss.infrastructure.repositories.CounselingAppointmentRepository;
import com.capstone2024.scss.infrastructure.repositories.CounselingAppointmentRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CounselingAppointmentServiceImpl implements CounselingAppointmentService {
    private static final Logger logger = LoggerFactory.getLogger(CounselingAppointmentServiceImpl.class);
    private final CounselingAppointmentRequestRepository requestRepository;
    private final CounselingAppointmentRepository appointmentRepository;

    public CounselingAppointmentServiceImpl(CounselingAppointmentRequestRepository requestRepository,
                                        CounselingAppointmentRepository appointmentRepository) {
        this.requestRepository = requestRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional
    public void approveOnlineAppointment(Long requestId, Long counselorId, OnlineAppointmentRequestDTO dto) {
        logger.info("Approving online appointment for requestId: {} and counselorId: {}", requestId, counselorId);

        // Tìm yêu cầu từ requestId
        CounselingAppointmentRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> {
                    logger.error("Appointment request with id {} not found", requestId);
                    return new NotFoundException("Appointment request not found");
                });

        // Kiểm tra counselor có quyền phê duyệt yêu cầu này
        if (!request.getCounselor().getId().equals(counselorId)) {
            logger.warn("Counselor with id {} does not have permission to approve request {}", counselorId, requestId);
            throw new BadRequestException("You do not have permission to approve this appointment request");
        }

        // Kiểm tra trạng thái yêu cầu là WAITING
        if (request.getStatus() != CounselingAppointmentRequestStatus.WAITING) {
            logger.warn("Appointment request with id {} is not in WAITING status (current status: {})", requestId, request.getStatus());
            throw new BadRequestException("Appointment request is not in WAITING status and cannot be approved");
        }

        // Kiểm tra loại cuộc gặp là ONLINE
        if (request.getMeetingType() != MeetingType.ONLINE) {
            logger.warn("Appointment request with id {} is not of type ONLINE", requestId);
            throw new BadRequestException("Appointment is not of type ONLINE");
        }

        // Kiểm tra meetUrl hợp lệ
        if (dto.getMeetUrl() == null || dto.getMeetUrl().isEmpty()) {
            logger.warn("Meet URL is missing for requestId: {}", requestId);
            throw new BadRequestException("Meet URL is required for online appointments");
        }

        // Tạo CounselingAppointment Online
        OnlineAppointment appointment = OnlineAppointment.builder()
                .startDateTime(LocalDateTime.of(request.getRequireDate(), request.getStartTime()))
                .endDateTime(LocalDateTime.of(request.getRequireDate(), request.getEndTime()))
                .status(CounselingAppointmentStatus.WAITING)
                .appointmentRequest(request)
                .meetUrl(dto.getMeetUrl())
                .build();

        logger.info("Creating online appointment for requestId: {} with meet URL: {}", requestId, dto.getMeetUrl());

        // Cập nhật trạng thái yêu cầu
        request.setStatus(CounselingAppointmentRequestStatus.APPROVED);
        logger.info("Appointment request with id {} has been approved", requestId);

        // Lưu yêu cầu và cuộc hẹn
        appointmentRepository.save(appointment);
        logger.info("Online appointment for requestId: {} has been saved", requestId);

        requestRepository.save(request);
        logger.info("Appointment request with id {} has been updated and saved", requestId);
    }

    @Transactional
    public void approveOfflineAppointment(Long requestId, Long counselorId, OfflineAppointmentRequestDTO dto) {
        logger.info("Approving offline appointment for requestId: {} by counselorId: {}", requestId, counselorId);

        CounselingAppointmentRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> {
                    logger.error("Appointment request not found for id: {}", requestId);
                    return new NotFoundException("Appointment request not found");
                });

        if (!request.getCounselor().getId().equals(counselorId)) {
            logger.warn("Unauthorized attempt to approve appointment request by counselorId: {}", counselorId);
            throw new BadRequestException("You do not have permission to approve this appointment request");
        }

        if (request.getStatus() != CounselingAppointmentRequestStatus.WAITING) {
            logger.warn("Appointment request id: {} is not in WAITING status", requestId);
            throw new BadRequestException("Appointment request is not in WAITING status and cannot be approved");
        }

        if (request.getMeetingType() != MeetingType.OFFLINE) {
            logger.warn("Appointment request id: {} is not of type OFFLINE", requestId);
            throw new BadRequestException("Appointment is not of type OFFLINE");
        }

        if (dto.getAddress() == null || dto.getAddress().isEmpty()) {
            logger.warn("Invalid address provided for appointment request id: {}", requestId);
            throw new BadRequestException("Address is required for offline appointments");
        }

        logger.debug("Creating offline appointment for requestId: {}", requestId);
        OfflineAppointment appointment = OfflineAppointment.builder()
                .startDateTime(LocalDateTime.of(request.getRequireDate(), request.getStartTime()))
                .endDateTime(LocalDateTime.of(request.getRequireDate(), request.getEndTime()))
                .status(CounselingAppointmentStatus.WAITING)
                .appointmentRequest(request)
                .address(dto.getAddress())
                .build();

        request.setStatus(CounselingAppointmentRequestStatus.APPROVED);

        appointmentRepository.save(appointment);
        requestRepository.save(request);

        logger.info("Successfully approved offline appointment for requestId: {}", requestId);
    }

    @Transactional
    public void denyAppointmentRequest(Long requestId, Long counselorId) {
        logger.info("Denying appointment request for requestId: {} by counselorId: {}", requestId, counselorId);

        CounselingAppointmentRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> {
                    logger.error("Appointment request not found for id: {}", requestId);
                    return new NotFoundException("Appointment request not found");
                });

        if (!request.getCounselor().getId().equals(counselorId)) {
            logger.warn("Unauthorized attempt to deny appointment request by counselorId: {}", counselorId);
            throw new BadRequestException("Counselor not authorized to deny this request");
        }

        if (request.getStatus() != CounselingAppointmentRequestStatus.WAITING) {
            logger.warn("Appointment request id: {} is not in WAITING status", requestId);
            throw new BadRequestException("Only requests with status WAITING can be denied");
        }

        request.setStatus(CounselingAppointmentRequestStatus.DENIED);
        requestRepository.save(request);

        logger.info("Successfully denied appointment request for requestId: {}", requestId);
    }

    ///////////////////////////////////////////////////////////////////////////

    public List<CounselingAppointmentDTO> getAppointmentsForCounselor(LocalDate fromDate, LocalDate toDate, Long counselorId) {
        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        LocalDateTime toDateTime = toDate.atTime(LocalTime.MAX);

        List<CounselingAppointment> appointments = appointmentRepository.findAllByCounselorIdAndDateRange(counselorId, fromDateTime, toDateTime);

        return appointments.stream()
                .map(this::convertToCounselorAppointmentDTO)
                .collect(Collectors.toList());
    }

    public List<CounselingAppointmentDTO> getAppointmentsForStudent(LocalDate fromDate, LocalDate toDate, Long studentId) {
        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        LocalDateTime toDateTime = toDate.atTime(LocalTime.MAX);

        List<CounselingAppointment> appointments = appointmentRepository.findAllByStudentIdAndDateRange(studentId, fromDateTime, toDateTime);

        return appointments.stream()
                .map(this::convertToStudentAppointmentDTO)
                .collect(Collectors.toList());
    }

    private CounselingAppointmentDTO convertToCounselorAppointmentDTO(CounselingAppointment appointment) {
        CounselingAppointmentDTO.CounselingAppointmentDTOBuilder dtoBuilder = CounselingAppointmentDTO.builder()
                .id(appointment.getId())
                .startDateTime(appointment.getStartDateTime())
                .endDateTime(appointment.getEndDateTime())
                .status(appointment.getStatus())
                .meetingType(appointment.getAppointmentRequest().getMeetingType());

        // Thêm meetUrl hoặc address dựa trên meetingType
        if (appointment instanceof OnlineAppointment) {
            dtoBuilder.meetUrl(((OnlineAppointment) appointment).getMeetUrl());
        } else if (appointment instanceof OfflineAppointment) {
            dtoBuilder.address(((OfflineAppointment) appointment).getAddress());
        }

        // Thêm thông tin counselor vào DTO
        Student student = appointment.getAppointmentRequest().getStudent();
        dtoBuilder.studentInfo(StudentAppointmentDTO.builder()
                .fullName(student.getFullName())
                .phoneNumber(student.getPhoneNumber())
                .dateOfBirth(student.getDateOfBirth())
                .avatarLink(student.getAvatarLink())
                .studentCode(student.getStudentCode())
                .profile(ProfileMapper.toProfileDTO(student))
                .build());

        return dtoBuilder.build();
    }

    private CounselingAppointmentDTO convertToStudentAppointmentDTO(CounselingAppointment appointment) {
        CounselingAppointmentDTO.CounselingAppointmentDTOBuilder dtoBuilder = CounselingAppointmentDTO.builder()
                .id(appointment.getId())
                .startDateTime(appointment.getStartDateTime())
                .endDateTime(appointment.getEndDateTime())
                .status(appointment.getStatus())
                .meetingType(appointment.getAppointmentRequest().getMeetingType());

        // Thêm meetUrl hoặc address dựa trên meetingType
        if (appointment instanceof OnlineAppointment) {
            dtoBuilder.meetUrl(((OnlineAppointment) appointment).getMeetUrl());
        } else if (appointment instanceof OfflineAppointment) {
            dtoBuilder.address(((OfflineAppointment) appointment).getAddress());
        }

        // Thêm thông tin student vào DTO
        Counselor counselor = appointment.getAppointmentRequest().getCounselor();
        dtoBuilder.counselorInfo(CounselorAppointmentDTO.builder()
                .fullName(counselor.getFullName())
                .phoneNumber(counselor.getPhoneNumber())
                .dateOfBirth(counselor.getDateOfBirth())
                .avatarLink(counselor.getAvatarLink())
                .rating(counselor.getRating())
                .profile(ProfileMapper.toProfileDTO(counselor))
                .build());

        return dtoBuilder.build();
    }
}
