package com.capstone2024.scss.infrastructure.configuration.openai;

import com.capstone2024.scss.domain.counselor.entities.*;
import com.capstone2024.scss.domain.student.entities.Major;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAiPromptGenerator {
    public String generateMostSuitableCounselorPrompt(List<Counselor> counselors, String studentQuery) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("Student seeks advice on: ").append(studentQuery).append("\n");
        prompt.append("Below is the information about available counselors:\n");

        for (Counselor counselor : counselors) {
            prompt.append("\nCounselor ID: ").append(counselor.getId()).append("\n")
//                    .append("Rating: ").append(counselor.getRating() != null ? counselor.getRating() : "N/A").append("\n")
//                    .append("Status: ").append(counselor.getStatus()).append("\n")
                    .append("Specialized Skills: ").append(counselor.getSpecializedSkills() != null ? counselor.getSpecializedSkills() : "N/A").append("\n")
                    .append("Other Skills: ").append(counselor.getOtherSkills() != null ? counselor.getOtherSkills() : "N/A").append("\n")
                    .append("Work History: ").append(counselor.getWorkHistory() != null ? counselor.getWorkHistory() : "N/A").append("\n")
                    .append("Achievements: ").append(counselor.getAchievements() != null ? counselor.getAchievements() : "N/A").append("\n");

            // Qualifications
            if (counselor.getQualifications() != null && !counselor.getQualifications().isEmpty()) {
                prompt.append("Qualifications:\n");
                for (Qualification qualification : counselor.getQualifications()) {
                    prompt.append("- Field of Study: ").append(qualification.getFieldOfStudy()).append("\n")
                            .append("  Degree: ").append(qualification.getDegree()).append("\n");
                }
            } else {
                prompt.append("Qualifications: N/A\n");
            }

            // Certifications
            if (counselor.getCertifications() != null && !counselor.getCertifications().isEmpty()) {
                prompt.append("Certifications:\n");
                for (Certification certification : counselor.getCertifications()) {
                    prompt.append("- Name: ").append(certification.getName()).append("\n")
                            .append("  Organization: ").append(certification.getOrganization()).append("\n");
                }
            } else {
                prompt.append("Certifications: N/A\n");
            }

            // Type-specific Information
            if (counselor instanceof AcademicCounselor) {
                AcademicCounselor academic = (AcademicCounselor) counselor;
                prompt.append("Type: Academic Counselor\n")
                        .append("Department: ").append(academic.getDepartment().getName()).append("\n")
                        .append("Major: ").append(academic.getMajor().getName()).append("\n")
                        .append("Academic Degree: ").append(academic.getAcademicDegree() != null ? academic.getAcademicDegree() : "N/A").append("\n");
            } else if (counselor instanceof NonAcademicCounselor) {
                NonAcademicCounselor nonAcademic = (NonAcademicCounselor) counselor;
                prompt.append("Type: Non-Academic Counselor\n")
                        .append("Expertise: ").append(nonAcademic.getExpertise().getName()).append("\n")
                        .append("Industry Experience: ").append(nonAcademic.getIndustryExperience() != null ? nonAcademic.getIndustryExperience() + " years" : "N/A").append("\n");
            }
        }

        prompt.append("\nRank the counselors by relevance to the student's query in descending order of suitability, returning their IDs in the format: 'ID1, ID2, ID3, ...'.\n");

        return prompt.toString();
    }

    public String generatePromptForFindingCounselingField(List<Major> majors, List<Expertise> expertises, String studentQuery) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("Student seeks advice on: ").append(studentQuery).append("\n");
        prompt.append("Available academic majors and non-academic expertise are listed below:\n");

        prompt.append("\nACADEMIC MAJORS:\n");
        for (Major major : majors) {
            prompt.append("- ").append(major.getName()).append("\n");
        }

        prompt.append("\nNON-ACADEMIC EXPERTISE:\n");
        for (Expertise expertise : expertises) {
            prompt.append("- ").append(expertise.getName()).append("\n");
        }

        prompt.append("\nFrom the provided list and student's query, determine the most suitable option. If there is no suitable option or student seeking is not clear or nonsense, return 'NONE, none'");
        prompt.append(" Respond in the format: 'ACADEMIC, major name'.\n");
        prompt.append(" or Respond in the format: 'NON_ACADEMIC, expertise name)'.\n");
        prompt.append(" or Respond in the format: 'NONE, none'.");

        return prompt.toString();
    }
}
