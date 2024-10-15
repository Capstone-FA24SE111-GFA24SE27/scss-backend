package com.capstone2024.scss.application.student.dto;

import com.capstone2024.scss.application.account.dto.StudentProfileDTO;
import com.capstone2024.scss.application.booking_counseling.dto.CounselingAppointmentDTO;
import com.capstone2024.scss.application.booking_counseling.dto.counseling_appointment_request.StudentAppointmentDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDocumentDTO {
    private StudentProfileDTO studentProfile;
    private List<CounselingAppointmentDTO> counselingAppointment;
}
