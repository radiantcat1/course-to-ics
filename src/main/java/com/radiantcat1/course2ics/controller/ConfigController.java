package com.radiantcat1.course2ics.controller;

import com.radiantcat1.course2ics.common.Result;
import com.radiantcat1.course2ics.config.CourseProperties;
import com.radiantcat1.course2ics.dto.PeriodItem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "配置", description = "供页面初始化使用的默认配置")
@RestController
@RequestMapping("/api/v1")
public class ConfigController {

    private final CourseProperties props;

    public ConfigController(CourseProperties props) {
        this.props = props;
    }

    @Operation(summary = "获取默认作息表",
            description = "返回 application.yml 里的时区、日历名和默认节次时间，页面据此初始化，之后客户可自行修改")
    @GetMapping("/config")
    public Result<ConfigVO> config() {
        List<String> starts = props.getPeriodStart();
        List<String> ends = props.getPeriodEnd();
        int count = Math.min(starts.size(), ends.size());
        List<PeriodItem> periods = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            periods.add(new PeriodItem(starts.get(i), ends.get(i)));
        }
        return Result.ok(new ConfigVO(props.getTimezone(), props.getCalendarName(), periods));
    }

    public record ConfigVO(String timezone, String calendarName, List<PeriodItem> periods) {}
}
