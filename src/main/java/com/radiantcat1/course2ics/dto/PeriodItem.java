package com.radiantcat1.course2ics.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "一节课的起止时间")
public record PeriodItem(

        @Schema(description = "上课时间，格式 HH:mm", example = "08:00")
        @NotBlank(message = "上课时间不能为空")
        @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "上课时间格式应为 HH:mm")
        String start,

        @Schema(description = "下课时间，格式 HH:mm", example = "08:45")
        @NotBlank(message = "下课时间不能为空")
        @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "下课时间格式应为 HH:mm")
        String end
) {
    @JsonIgnore
    @AssertTrue(message = "下课时间必须晚于上课时间")
    public boolean isEndAfterStart() {
        if (start == null || end == null) {
            return true; // 空值交给 @NotBlank 报错，这里不重复判断
        }
        return end.compareTo(start) > 0; // "08:45" > "08:00" 字符串按字典序比较即可
    }
}
