package com.radiantcat1.course2ics.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "整个课表转换请求")
public record CourseRequest(

        @Schema(description = "学期第一周周一的日期，格式 yyyy-MM-dd", example = "2026-09-07")
        @NotNull(message = "学期第一周周一不能为空")
        LocalDate semesterStart,

        @Schema(description = "作息表：第 1 个元素对应第 1 节，客户可自定义节数与每节时间",
                example = "[{\"start\":\"08:00\",\"end\":\"08:45\"},{\"start\":\"08:55\",\"end\":\"09:40\"}]")
        @NotEmpty(message = "作息表不能为空")
        @Size(max = 30, message = "作息表最多 30 节")
        @Valid
        List<PeriodItem> periods,

        @Schema(description = "课程列表，至少一门")
        @NotEmpty(message = "课程列表不能为空")
        @Valid
        List<CourseItem> courses
) {}
