package com.capstone2024.scss.application.demand.dto;

import com.capstone2024.scss.application.account.dto.CounselorProfileDTO;
import com.capstone2024.scss.application.account.dto.StudentProfileDTO;
import com.capstone2024.scss.application.booking_counseling.dto.CounselingAppointmentDTO;
import com.capstone2024.scss.application.common.dto.SemesterDTO;
import com.capstone2024.scss.domain.demand.entities.CounselingDemand;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CounselingDemandDTO {

    private Long id;
    private CounselingDemand.Status status;
    private StudentProfileDTO student;
    private SupportStaffDTO supportStaff;
    private String contactNote;
    private String summarizeNote;
    private CounselorProfileDTO counselor;
//    private SemesterDTO semester;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private List<CounselingAppointmentDTO> appointments;
    private CounselingDemand.PriorityLevel priorityLevel;
    private String additionalInformation;
    private String issueDescription;
    private String causeDescription;
    private CounselingDemand.DemandType demandType;
}

