package com.capstone2024.scss.domain.common.mapper.appointment_counseling;

import com.capstone2024.scss.application.booking_counseling.dto.CounselingAppointmentDTO;
import com.capstone2024.scss.application.counseling_appointment.dto.CounselingAppointmentForReportResponse;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.AppointmentFeedback;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.CounselingAppointment;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.OfflineAppointment;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.OnlineAppointment;
import com.capstone2024.scss.domain.counselor.entities.Counselor;
import com.capstone2024.scss.domain.student.entities.Student;

public class CounselingAppointmentMapper {
    public static CounselingAppointmentDTO toCounselingAppointmentDTO(CounselingAppointment appointment) {
        CounselingAppointmentDTO.CounselingAppointmentDTOBuilder dtoBuilder = CounselingAppointmentDTO.builder()
                .id(appointment.getId())
                .startDateTime(appointment.getStartDateTime())
                .endDateTime(appointment.getEndDateTime())
                .status(appointment.getStatus())
                .meetingType(appointment.getAppointmentRequest().getMeetingType())
                .isHavingReport(appointment.getReport() != null ? true : false);

        // Xử lý theo kiểu họp mặt
        if (appointment instanceof OnlineAppointment) {
            dtoBuilder.meetUrl(((OnlineAppointment) appointment).getMeetUrl());
        } else if (appointment instanceof OfflineAppointment) {
            dtoBuilder.address(((OfflineAppointment) appointment).getAddress());
        }

        // Mapping feedback
        AppointmentFeedback appointmentFeedback = appointment.getFeedback();
        if (appointmentFeedback != null) {
            dtoBuilder.appointmentFeedback(AppointmentFeedbackMapper.toDTO(appointmentFeedback));
        }

        // Thêm thông tin của counselor và student
        Counselor counselor = appointment.getAppointmentRequest().getCounselor();
        Student student = appointment.getAppointmentRequest().getStudent();
        dtoBuilder.counselorInfo(CounselorProfileMapper.toCounselorProfileDTO(counselor));
        dtoBuilder.studentInfo(StudentProfileMapper.toStudentProfileDTO(student));

        return dtoBuilder.build();
    }

    public static CounselingAppointmentForReportResponse toCounselingAppointmentForReportDTO(CounselingAppointment appointment) {
        CounselingAppointmentForReportResponse.CounselingAppointmentForReportResponseBuilder dtoBuilder = CounselingAppointmentForReportResponse.builder()
                .id(appointment.getId())
                .startDateTime(appointment.getStartDateTime())
                .endDateTime(appointment.getEndDateTime())
                .status(appointment.getStatus())
                .meetingType(appointment.getAppointmentRequest().getMeetingType());

        // Xử lý theo kiểu họp mặt
        if (appointment instanceof OnlineAppointment) {
            dtoBuilder.meetUrl(((OnlineAppointment) appointment).getMeetUrl());
        } else if (appointment instanceof OfflineAppointment) {
            dtoBuilder.address(((OfflineAppointment) appointment).getAddress());
        }

        return dtoBuilder.build();
    }
}
