package com.capstone2024.scss.domain.common.mapper.appointment_counseling;

import com.capstone2024.scss.application.booking_counseling.dto.CounselingAppointmentDTO;
import com.capstone2024.scss.application.counseling_appointment.dto.CounselingAppointmentForReportResponse;
import com.capstone2024.scss.domain.common.mapper.account.CounselorProfileMapper;
import com.capstone2024.scss.domain.common.mapper.student.StudentMapper;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.AppointmentFeedback;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.CounselingAppointment;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.OfflineAppointment;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.OnlineAppointment;
import com.capstone2024.scss.domain.counselor.entities.AcademicCounselor;
import com.capstone2024.scss.domain.counselor.entities.Counselor;
import com.capstone2024.scss.domain.counselor.entities.NonAcademicCounselor;
import com.capstone2024.scss.domain.student.entities.Student;

public class CounselingAppointmentMapper {
    public static CounselingAppointmentDTO toCounselingAppointmentDTO(CounselingAppointment appointment) {
        CounselingAppointmentDTO.CounselingAppointmentDTOBuilder dtoBuilder = CounselingAppointmentDTO.builder()
                .id(appointment.getId())
                .startDateTime(appointment.getStartDateTime())
                .endDateTime(appointment.getEndDateTime())
                .status(appointment.getStatus())
                .meetingType(appointment.getMeetingType())
                .reason(appointment.getReason())
                .cancelReason(appointment.getCancelReason())
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
            dtoBuilder.appointmentFeedback(AppointmentFeedbackMapper.toAppointmentDTO(appointmentFeedback));
        }

        // Thêm thông tin của counselor và student
        Counselor counselor = appointment.getCounselor();
        Student student = appointment.getStudent();
        dtoBuilder.counselorInfo((counselor instanceof NonAcademicCounselor) ? CounselorProfileMapper.toNonAcademicCounselorProfileDTO((NonAcademicCounselor) counselor) : CounselorProfileMapper.toAcademicCounselorProfileDTO((AcademicCounselor) counselor));
        dtoBuilder.studentInfo(StudentMapper.toStudentProfileDTO(student));

        return dtoBuilder.build();
    }

    public static CounselingAppointmentForReportResponse toCounselingAppointmentForReportDTO(CounselingAppointment appointment) {
        CounselingAppointmentForReportResponse.CounselingAppointmentForReportResponseBuilder dtoBuilder = CounselingAppointmentForReportResponse.builder()
                .id(appointment.getId())
                .startDateTime(appointment.getStartDateTime())
                .endDateTime(appointment.getEndDateTime())
                .status(appointment.getStatus())
                .meetingType(appointment.getMeetingType());

        // Xử lý theo kiểu họp mặt
        if (appointment instanceof OnlineAppointment) {
            dtoBuilder.meetUrl(((OnlineAppointment) appointment).getMeetUrl());
        } else if (appointment instanceof OfflineAppointment) {
            dtoBuilder.address(((OfflineAppointment) appointment).getAddress());
        }

        return dtoBuilder.build();
    }
}
