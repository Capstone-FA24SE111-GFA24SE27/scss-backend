package com.capstone2024.scss.domain.common.mapper.demand;

import com.capstone2024.scss.application.demand.dto.CounselingDemandDTO;
import com.capstone2024.scss.domain.common.mapper.account.CounselorProfileMapper;
import com.capstone2024.scss.domain.common.mapper.appointment_counseling.CounselingAppointmentMapper;
import com.capstone2024.scss.domain.common.mapper.common.SemesterMapper;
import com.capstone2024.scss.domain.common.mapper.student.StudentMapper;
import com.capstone2024.scss.domain.common.support_staff.SupportStaffMapper;
import com.capstone2024.scss.domain.demand.entities.CounselingDemand;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class DemandMapper {
    public static CounselingDemandDTO toCounselingDemandDTO(CounselingDemand demand) {
        if (demand == null) {
            return null;
        }

        return CounselingDemandDTO.builder()
                .id(demand.getId())
                .status(demand.getStatus())
                .student(demand.getStudent() != null ? StudentMapper.toStudentProfileDTO(demand.getStudent()) : null)
                .supportStaff(demand.getSupportStaff() != null ? SupportStaffMapper.toSupportStaffDTO(demand.getSupportStaff()) : null)
                .contactNote(demand.getContactNote())
                .summarizeNote(demand.getSummarizeNote())
                .counselor(demand.getCounselor() != null ? CounselorProfileMapper.toCounselorProfileDTO(demand.getCounselor()) : null)
                .additionalInformation(demand.getAdditionalInformation())
                .issueDescription(demand.getIssueDescription())
                .causeDescription(demand.getCauseDescription())
                .priorityLevel(demand.getPriorityLevel())
                .demandType(demand.getDemandType())
                .startDateTime(demand.getStartDateTime())
                .endDateTime(demand.getEndDateTime())
                .appointments(demand.getAppointmentsForDemand() != null
                        ? demand.getAppointmentsForDemand().stream().map((appointmentForDemand) ->
                        CounselingAppointmentMapper.toCounselingAppointmentDTO(appointmentForDemand.getCounselingAppointment())
                ).collect(Collectors.toList())
                        : new ArrayList<>())
                .build();
    }
}
