package com.capstone2024.scss.domain.common.helpers;

import com.capstone2024.scss.application.student.dto.StudentAttendanceAsCountFilterDTO;
import com.capstone2024.scss.application.student.dto.StudentAttendanceAsPercentFilterDTO;
import com.capstone2024.scss.application.student.dto.StudentGPAFilterDTO;

public class StudentFilterHelper {
    public static String buildAttendanceCountFilterURL(String baseURL, String semesterName, StudentAttendanceAsCountFilterDTO filterRequest) {
        StringBuilder url = new StringBuilder(baseURL + "/api/students/attendance/" + semesterName + "/count/filter?");

        if (filterRequest.getFrom() != null) {
            url.append("absenceSlotFrom=").append(filterRequest.getFrom());
        }

        if (filterRequest.getTo() != null) {
            url.append("&absenceSlotTo=").append(filterRequest.getTo()).append("&");
        }

        if (filterRequest.getMinSubject() != null) {
            url.append("subjectcountFrom=").append(filterRequest.getMinSubject());
        }

        return url.toString().endsWith("&") ? url.substring(0, url.length() - 1) : url.toString(); // Loại bỏ dấu "&" thừa nếu có
    }

    public static String buildAttendancePercentageFilterURL(String baseURL, String semesterName, StudentAttendanceAsPercentFilterDTO filterRequest) {
        StringBuilder url = new StringBuilder(baseURL + "/api/students/attendance/" + semesterName + "/percentage/filter?");

        if (filterRequest.getFrom() != null) {
            url.append("absenceSlotFrom=").append(filterRequest.getFrom());
        }

        if (filterRequest.getTo() != null) {
            url.append("&absenceSlotTo=").append(filterRequest.getTo()).append("&");
        }

        if (filterRequest.getMinSubject() != null) {
            url.append("subjectcountFrom=").append(filterRequest.getMinSubject());
        }

        return url.toString().endsWith("&") ? url.substring(0, url.length() - 1) : url.toString(); // Loại bỏ dấu "&" thừa nếu có
    }

    public static String buildGPAFilterURL(String baseURL, String semesterName, StudentGPAFilterDTO filterRequest) {
        StringBuilder url = new StringBuilder(baseURL + "/api/students/gpa/" + semesterName + "/filter?");

        if (filterRequest.getMin() != null) {
            url.append("from=").append(filterRequest.getMin());
        }

        if (filterRequest.getMax() != null) {
            url.append("&to=").append(filterRequest.getMax()).append("&");
        }

        return url.toString().endsWith("&") ? url.substring(0, url.length() - 1) : url.toString(); // Loại bỏ dấu "&" thừa nếu có
    }
}
