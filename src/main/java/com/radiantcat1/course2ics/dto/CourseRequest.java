package com.radiantcat1.course2ics.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record CourseRequest(

        @NotNull(message = "学期第一周周一不能为空")
        LocalDate semesterStart,

        @NotEmpty(message = "课程列表不能为空")
        @Valid
        List<CourseItem> courses
) {}