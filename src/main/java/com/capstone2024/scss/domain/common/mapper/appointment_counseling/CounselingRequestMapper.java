package com.capstone2024.scss.domain.common.mapper.appointment_counseling;

import com.capstone2024.scss.application.account.dto.CounselorProfileDTO;
import com.capstone2024.scss.application.account.dto.StudentProfileDTO;
import com.capstone2024.scss.application.booking_counseling.dto.counseling_appointment_request.AppointmentDetailsDTO;
import com.capstone2024.scss.application.booking_counseling.dto.counseling_appointment_request.CounselingAppointmentRequestDTO;
import com.capstone2024.scss.domain.common.mapper.account.CounselorProfileMapper;
import com.capstone2024.scss.domain.common.mapper.student.StudentMapper;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.CounselingAppointment;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.OfflineAppointment;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.OnlineAppointment;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.CounselingAppointmentRequest;
import com.capstone2024.scss.domain.counselor.entities.AcademicCounselor;
import com.capstone2024.scss.domain.counselor.entities.NonAcademicCounselor;

import java.util.List;

public class CounselingRequestMapper {

    public static CounselingAppointmentRequestDTO convertToDTO(CounselingAppointmentRequest request) {
        if (request == null) {
            return null;
        }

        List<CounselingAppointment> appointments = request.getCounselingAppointments();
        AppointmentDetailsDTO appointmentDetails = null;
        if(appointments != null && !appointments.isEmpty()) {
            CounselingAppointment appointment = appointments.getLast();

            if (appointment instanceof OnlineAppointment onlineAppointment) {
                appointmentDetails = AppointmentDetailsDTO.builder()
                        .meetUrl(onlineAppointment.getMeetUrl())
                        .build();
            } else if (appointment instanceof OfflineAppointment offlineAppointment) {
                appointmentDetails = AppointmentDetailsDTO.builder()
                        .address(offlineAppointment.getAddress())
                        .build();
            }
        }

        CounselorProfileDTO counselorDTO = (request.getCounselor() != null) ? (
                (request.getCounselor() instanceof NonAcademicCounselor) ?
                        CounselorProfileMapper.toNonAcademicCounselorProfileDTO((NonAcademicCounselor) request.getCounselor()) : CounselorProfileMapper.toAcademicCounselorProfileDTO((AcademicCounselor) request.getCounselor())
        ) : null;

        StudentProfileDTO studentDTO = request.getStudent() != null
                ?
                StudentMapper.toStudentProfileDTO(request.getStudent())
                : null;

        return CounselingAppointmentRequestDTO.builder()
                .id(request.getId())
                .requireDate(request.getRequireDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(request.getStatus().name())
                .meetingType(request.getMeetingType())
                .reason(request.getReason())
                .counselor(counselorDTO)
                .student(studentDTO)
                .appointmentDetails(appointmentDetails)
                .build();
    }
}
