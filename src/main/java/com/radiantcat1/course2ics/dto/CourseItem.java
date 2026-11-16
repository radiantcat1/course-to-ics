package com.radiantcat1.course2ics.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "单门课程")
public record CourseItem(

        @Schema(description = "课程名", example = "Data Structures")
        @NotBlank(message = "课程名不能为空")
        String title,

        @Schema(description = "星期几：1=周一 … 7=周日", example = "1")
        @Min(value = 1, message = "星期需要 1-7")
        @Max(value = 7, message = "星期需要 1-7")
        int dayOfWeek,

        @Schema(description = "起始节次，1-10", example = "1")
        @Min(value = 1, message = "起始节次需要 1-10")
        @Max(value = 10, message = "起始节次需要 1-10")
        int startPeriod,

        @Schema(description = "结束节次，1-10", example = "2")
        @Min(value = 1, message = "结束节次需要 1-10")
        @Max(value = 10, message = "结束节次需要 1-10")
        int endPeriod,

        @Schema(description = "连续上课周数，1-30", example = "16")
        @Min(value = 1, message = "周数需要 1-30")
        @Max(value = 30, message = "周数需要 1-30")
        int weeks,

        @Schema(description = "上课地点", example = "Building A-301")
        String location,

        @Schema(description = "教师", example = "Prof. Zhang")
        String teacher
) {}
