package com.radiantcat1.course2ics.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CourseItem(

        @NotBlank(message = "课程名不能为空")
        String title,

        @Min(value = 1, message = "星期需要 1-7")
        @Max(value = 7, message = "星期需要 1-7")
        int dayOfWeek,

        @Min(value = 1, message = "起始节次需要 1-10")
        @Max(value = 10, message = "起始节次需要 1-10")
        int startPeriod,

        @Min(value = 1, message = "结束节次需要 1-10")
        @Max(value = 10, message = "结束节次需要 1-10")
        int endPeriod,

        @Min(value = 1, message = "周数需要 1-30")
        @Max(value = 30, message = "周数需要 1-30")
        int weeks,

        String location,
        String teacher
) {}