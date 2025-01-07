package com.capstone2024.scss.domain.counselor.services.impl;

import com.capstone2024.scss.application.account.dto.AcademicCounselorProfileDTO;
import com.capstone2024.scss.application.account.dto.CounselorProfileDTO;
import com.capstone2024.scss.application.account.dto.NonAcademicCounselorProfileDTO;
import com.capstone2024.scss.application.advice.exeptions.NotFoundException;
import com.capstone2024.scss.application.booking_counseling.dto.SlotDTO;
import com.capstone2024.scss.application.booking_counseling.dto.enums.SlotStatus;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.common.dto.SpecializationDTO;
import com.capstone2024.scss.application.counselor.dto.request.AcademicCounselorFilterRequestDTO;
import com.capstone2024.scss.application.counselor.dto.request.CounselorFilterRequestDTO;
import com.capstone2024.scss.application.counselor.dto.request.NonAcademicCounselorFilterRequestDTO;
import com.capstone2024.scss.domain.counselor.entities.Specialization;
import com.capstone2024.scss.domain.counselor.entities.Expertise;
import com.capstone2024.scss.domain.counselor.entities.enums.Gender;
import com.capstone2024.scss.domain.counselor.services.CounselorService;
import com.capstone2024.scss.domain.common.mapper.account.CounselorProfileMapper;
import com.capstone2024.scss.application.counselor.dto.ExpertiseDTO;
import com.capstone2024.scss.domain.common.mapper.account.ExpertiseMapper;
import com.capstone2024.scss.domain.common.mapper.account.AcademicDepartmentDetailMapper;
import com.capstone2024.scss.domain.counseling_booking.entities.CounselingSlot;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.CounselingAppointmentRequest;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.enums.CounselingAppointmentRequestStatus;
import com.capstone2024.scss.domain.counselor.entities.*;
import com.capstone2024.scss.domain.student.entities.Major;
import com.capstone2024.scss.domain.student.entities.Student;
import com.capstone2024.scss.infrastructure.configuration.openai.OpenAIService;
import com.capstone2024.scss.infrastructure.configuration.openai.OpenAiPromptGenerator;
import com.capstone2024.scss.infrastructure.repositories.MajorRepository;
import com.capstone2024.scss.infrastructure.repositories.booking_counseling.CounselingAppointmentRequestRepository;
import com.capstone2024.scss.infrastructure.repositories.booking_counseling.CounselingSlotRepository;
import com.capstone2024.scss.infrastructure.repositories.counselor.CounselorRepository;
import com.capstone2024.scss.infrastructure.repositories.counselor.ExpertiseRepository;
import com.capstone2024.scss.infrastructure.repositories.counselor.SpecializationRepository;
import com.capstone2024.scss.infrastructure.repositories.student.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.Collections;
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
    private final SpecializationRepository specializationRepository;
    private final StudentRepository studentRepository;
    private final OpenAIService openAIService;

    @Override
    public PaginationDTO<List<CounselorProfileDTO>> getCounselorsWithFilter(CounselorFilterRequestDTO filterRequest) {
        Page<Counselor> counselorsPage = counselorRepository.findByKeywordAndRatingRange(
                filterRequest.getSearch(),
//                filterRequest.getRatingFrom(),
//                filterRequest.getRatingTo(),
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

    private final OpenAiPromptGenerator openAiPromptGenerator;

    @Override
    public List<CounselorProfileDTO> findBestAvailableCounselorForNonAcademic(Long slotId, LocalDate date, Gender gender, String reason, String expertiseName) {
        if(expertiseName == null) {
            String prompt = openAIService.generatePromptToOpenAIForBestExpertiseMatching(reason);
            expertiseName = openAIService.callOpenAPIForBestExpertiseMatching(prompt);
            if(expertiseName.equals("none")) {
                throw new NotFoundException("There is no counselor can solve this case");
            }
        }

        Expertise expertise = expertiseRepository.findByName(expertiseName)
                    .orElseThrow(() -> new NotFoundException("Expertise not found"));

        // Tạo Pageable để giới hạn kết quả trả về chỉ 1 bản ghi
        PageRequest pageable = PageRequest.of(0, 10000);

        List<NonAcademicCounselor> counselors = new ArrayList<>();

        if(slotId == null || date == null) {
            counselors = counselorRepository.findAvailableCounselorsByGenderAndExpertiseOrderedForNonAcademicWithoutDate(
                    gender, expertise, pageable);
        } else {
            CounselingSlot slot = counselingSlotRepository.findById(slotId)
                    .orElseThrow(() -> new NotFoundException("Slot not found with ID: " + slotId));
            counselors = counselorRepository.findAvailableCounselorsByGenderAndExpertiseOrderedForNonAcademic(
                    gender, expertise, date, slot.getStartTime(), slot.getEndTime(), pageable);

            counselors = counselors
                    .stream()
                    .filter((counselor) ->
                            counselor.getSlotOfCounselors()
                                    .stream()
                                    .filter(slotOfCounselor -> slotOfCounselor.getDayOfWeek().equals(date.getDayOfWeek()))
                                    .anyMatch((slotOfCounselor) ->
                                            slotOfCounselor
                                                    .getCounselingSlot()
                                                    .getId().equals(slot.getId())))
                    .collect(Collectors.toList());
        }

        if (counselors.isEmpty()) {
            throw new NotFoundException("Không tìm thấy counselor nào khả dụng vào thời gian này với yêu cầu giới tính và chuyên môn.");
        }

//        HashMap<String, Object> re = new HashMap<>();
//        re.put("counselor", counselors.isEmpty() ? null : counselors.getFirst());
//        re.put("recommend", counselorRepository.findAvailableCounselors(date));

        List<Counselor> counselorList = new ArrayList<>(counselors);

        String sortSuitableCounselorPrompt = openAiPromptGenerator.generateMostSuitableCounselorPrompt(counselorList, reason);

        System.out.println(sortSuitableCounselorPrompt);

        List<Long> longs = openAIService.callOpenAPIForSortSuitableCounselor(sortSuitableCounselorPrompt);

        List<NonAcademicCounselor> returnCounselors = new ArrayList<>();

        for(Long i : longs) {
            Optional<NonAcademicCounselor> counselorOptional = counselors.stream().filter(counselor -> counselor.getId().equals(i)).findFirst();
            counselorOptional.ifPresent(returnCounselors::add);
        }

        return counselors.stream().map(counselor -> CounselorProfileMapper.toNonAcademicCounselorProfileDTO(counselor)).collect(Collectors.toList());
    }

    private final MajorRepository majorRepository;

    @Override
    public List<CounselorProfileDTO> findBestAvailableCounselor(Long slotId, LocalDate date, Gender gender, String reason) {
        List<Expertise> expertises = expertiseRepository.findAll();
        List<Major> majors = majorRepository.findAll();

        String prompt = openAiPromptGenerator.generatePromptForFindingCounselingField(majors, expertises, reason);

        System.out.println(prompt);

        ArrayList<String> suitableField = openAIService.callOpenAPIForSuitableCounselingField(prompt);
        if(suitableField.get(0).equals("NON_ACADEMIC")) {
            return findBestAvailableCounselorForNonAcademic(slotId, date, gender, reason, suitableField.get(1));
        } else if(suitableField.get(0).equals("ACADEMIC")) {
            return findBestAvailableCounselorForAcademic(slotId, date, gender, null, reason, null, null, suitableField.get(1));
        } else if(suitableField.get(0).equals("NONE")) {
            throw new NotFoundException("Không tìm thấy counselor nào khả dụng vào thời gian này với yêu cầu giới tính và chuyên môn.");
        } else {
            throw new NotFoundException("Không tìm thấy counselor nào khả dụng vào thời gian này với yêu cầu giới tính và chuyên môn.");
        }
    }

    @Override
    public CounselorProfileDTO findBestAvailableCounselorForNonAcademicWithLowestDemandInMonth(Gender gender, String reason) {
        String prompt = openAIService.generatePromptToOpenAIForBestExpertiseMatching(reason);
        String expertiseName = openAIService.callOpenAPIForBestExpertiseMatching(prompt);

        Expertise expertise = expertiseRepository.findByName(expertiseName)
                .orElseThrow(() -> new NotFoundException("Expertise not found with name: " + expertiseName));

        // Tạo Pageable để giới hạn kết quả trả về chỉ 1 bản ghi
        PageRequest pageable = PageRequest.of(0, 10);

        // Thực hiện truy vấn
        List<NonAcademicCounselor> counselors = counselorRepository.findBestAvailableCounselorForNonAcademicWithLowestDemandInMonth(
                gender, expertise, pageable);

        if (counselors.isEmpty()) {
            throw new NotFoundException("Không tìm thấy counselor nào khả dụng vào thời gian này với yêu cầu giới tính và chuyên môn.");
        }

        return CounselorProfileMapper.toNonAcademicCounselorProfileDTO(counselors.getFirst());
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

    @Override
    public PaginationDTO<List<NonAcademicCounselorProfileDTO>> getNonAcademicCounselorsWithFilter(NonAcademicCounselorFilterRequestDTO filterRequest) {
        Page<NonAcademicCounselor> counselorsPage = counselorRepository.findNonAcademicCounselorsWithFilter(
                filterRequest.getSearch(),
//                filterRequest.getRatingFrom(),
//                filterRequest.getRatingTo(),
                filterRequest.getAvailableFrom(),
                filterRequest.getAvailableTo(),
                filterRequest.getExpertiseId(),
                filterRequest.getGender(),
                filterRequest.getPagination());

        List<NonAcademicCounselorProfileDTO> counselorDTOs = counselorsPage.getContent().stream()
                .map(CounselorProfileMapper::toNonAcademicCounselorProfileDTO)
                .collect(Collectors.toList());

        return PaginationDTO.<List<NonAcademicCounselorProfileDTO>>builder()
                .data(counselorDTOs)
                .totalPages(counselorsPage.getTotalPages())
                .totalElements((int) counselorsPage.getTotalElements())
                .build();
    }

    @Override
    public PaginationDTO<List<AcademicCounselorProfileDTO>> getAcademicCounselorsWithFilter(AcademicCounselorFilterRequestDTO filterRequest) {
        Page<AcademicCounselor> counselorsPage = counselorRepository.findAcademicCounselorsWithFilter(
                filterRequest.getSearch(),
//                filterRequest.getRatingFrom(),
//                filterRequest.getRatingTo(),
                filterRequest.getAvailableFrom(),
                filterRequest.getAvailableTo(),
//                null,
                filterRequest.getDepartmentId(),
                filterRequest.getMajorId(),
                filterRequest.getGender(),
                filterRequest.getPagination());

        List<AcademicCounselorProfileDTO> counselorDTOs = counselorsPage.getContent().stream()
                .map(CounselorProfileMapper::toAcademicCounselorProfileDTO)
                .collect(Collectors.toList());

        return PaginationDTO.<List<AcademicCounselorProfileDTO>>builder()
                .data(counselorDTOs)
                .totalPages(counselorsPage.getTotalPages())
                .totalElements((int) counselorsPage.getTotalElements())
                .build();
    }

    @Override
    public List<SpecializationDTO> getAllSpecialization() {
        List<Specialization> expertiseList = specializationRepository.findAll();
        return expertiseList.stream()
                .map(AcademicDepartmentDetailMapper::toSpecializationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public NonAcademicCounselorProfileDTO getNonAcademicCounselorById(Long id) {
        Counselor counselor = counselorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Non-Academic Counselor not found with id: " + id));

        if(counselor instanceof NonAcademicCounselor nonAcademicCounselor) {
            return CounselorProfileMapper.toNonAcademicCounselorProfileDTO(nonAcademicCounselor);
        } else {
            throw new NotFoundException("No non-academic counselor match this id: " + id);
        }
    }

    @Override
    public AcademicCounselorProfileDTO getAcademicCounselorById(Long id) {
        Counselor counselor = counselorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Academic Counselor not found with id: " + id));
        if(counselor instanceof AcademicCounselor academicCounselor) {
            return CounselorProfileMapper.toAcademicCounselorProfileDTO(academicCounselor);
        } else {
            throw new NotFoundException("No academic counselor match this id: " + id);
        }
    }

    @Override
    public List<CounselorProfileDTO> findBestAvailableCounselorForAcademic(Long slotId, LocalDate date, Gender gender, Long studentId, String reason, Long departmentId, Long majorId, String majorName) {

        Major major = null;

        if(majorName == null) {
//            Student student = studentRepository.findById(studentId)
//                    .orElseThrow(() -> new NotFoundException("Student not found"));

            major = majorRepository.findById(majorId).orElseThrow(() -> new NotFoundException("Not found major"));
        } else {
            major = majorRepository.findByName(majorName).orElseThrow(() -> new NotFoundException("Not found major"));
        }

        // Tạo Pageable để giới hạn kết quả trả về chỉ 1 bản ghi
        PageRequest pageable = PageRequest.of(0, 10000);

        List<AcademicCounselor> counselors = new ArrayList<>();

        if(slotId == null || date == null) {
            // Thực hiện truy vấn
            counselors = counselorRepository.findAvailableCounselorsByGenderAndExpertiseOrderedForAcademicWithoutDate(
                    gender,
                    major.getDepartment().getId(),
                    major.getId(),
                    pageable);
        } else {
            CounselingSlot slot = counselingSlotRepository.findById(slotId)
                    .orElseThrow(() -> new NotFoundException("Slot not found with ID: " + slotId));
            // Thực hiện truy vấn
            counselors = counselorRepository.findAvailableCounselorsByGenderAndExpertiseOrderedForAcademic(
                    gender,
//                null,
                    major.getDepartment().getId(),
                    major.getId(),
                    date,
                    slot.getStartTime(),
                    slot.getEndTime(), pageable);

            counselors = counselors
                    .stream()
                    .filter((counselor) ->
                            counselor.getSlotOfCounselors()
                                    .stream()
                                    .filter(slotOfCounselor -> slotOfCounselor.getDayOfWeek().equals(date.getDayOfWeek()))
                                    .anyMatch((slotOfCounselor) ->
                                            slotOfCounselor
                                                    .getCounselingSlot()
                                                    .getId().equals(slot.getId())))
                    .collect(Collectors.toList());
        }

        if (counselors.isEmpty()) {
            throw new NotFoundException("Không tìm thấy counselor nào khả dụng vào thời gian này với yêu cầu giới tính và chuyên môn.");
        }

        List<Counselor> counselorList = new ArrayList<>(counselors);

        String sortSuitableCounselorPrompt = openAiPromptGenerator.generateMostSuitableCounselorPrompt(counselorList, reason);

        System.out.println(sortSuitableCounselorPrompt);

        List<Long> longs = openAIService.callOpenAPIForSortSuitableCounselor(sortSuitableCounselorPrompt);

        List<AcademicCounselor> returnCounselors = new ArrayList<>();

        for(Long i : longs) {
            Optional<AcademicCounselor> counselorOptional = counselors.stream().filter(counselor -> counselor.getId().equals(i)).findFirst();
            counselorOptional.ifPresent(returnCounselors::add);
        }

        return counselors.stream().map(counselor -> CounselorProfileMapper.toAcademicCounselorProfileDTO(counselor)).collect(Collectors.toList());
    }

    @Override
    public CounselorProfileDTO findBestAvailableCounselorForAcademicWithLowestDemand(Gender gender, Long studentId, String reason) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found"));

        // Tạo Pageable để giới hạn kết quả trả về chỉ 1 bản ghi
        PageRequest pageable = PageRequest.of(0, 1);

        // Thực hiện truy vấn
        List<AcademicCounselor> counselors = counselorRepository.findBestAvailableCounselorForAcademicWithLowestDemand(
                gender,
//                null,
                student.getDepartment().getId(),
                student.getMajor().getId(),
                pageable);

        if (counselors.isEmpty()) {
            throw new NotFoundException("Không tìm thấy counselor nào khả dụng vào thời gian này với yêu cầu giới tính và chuyên môn.");
        }

        return CounselorProfileMapper.toAcademicCounselorProfileDTO(counselors.getFirst());
    }

    @Override
    public String getReasonMeaning(String reason, Long studentId) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new NotFoundException("No student match this ID"));
        String prompt = openAIService.generatePromptToOpenAIForDefineReasonMeaning(reason, student);

        return openAIService.callOpenAPIForDefineReasonMeaning(prompt);
    }
}
