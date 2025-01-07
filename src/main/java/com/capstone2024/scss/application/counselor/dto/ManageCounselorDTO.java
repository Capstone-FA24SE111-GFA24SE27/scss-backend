package com.capstone2024.scss.application.counselor.dto;

import com.capstone2024.scss.application.account.dto.CounselorProfileDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ManageCounselorDTO {

    private CounselorProfileDTO profile;

    private String password;

    private AvailableDateRangeDTO availableDateRange;

    private List<CounselingSlotDTO> counselingSlot;
}
