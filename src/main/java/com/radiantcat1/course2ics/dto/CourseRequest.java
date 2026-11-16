package com.radiantcat1.course2ics.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "整个课表转换请求")
public record CourseRequest(

        @Schema(description = "学期第一周周一的日期，格式 yyyy-MM-dd", example = "2026-09-07")
        @NotNull(message = "学期第一周周一不能为空")
        LocalDate semesterStart,

        @Schema(description = "课程列表，至少一门")
        @NotEmpty(message = "课程列表不能为空")
        @Valid
        List<CourseItem> courses
) {}
