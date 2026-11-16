package com.radiantcat1.course2ics.controller;

import com.radiantcat1.course2ics.common.Result;
import com.radiantcat1.course2ics.dto.CourseRequest;
import com.radiantcat1.course2ics.service.IcsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "课表转 ICS", description = "把结构化课表转换成标准 iCalendar 文件")
@RestController
@RequestMapping("/api/v1/ics")
public class IcsController {

    private final IcsService icsService;

    public IcsController(IcsService icsService) {
        this.icsService = icsService;
    }

    @Operation(summary = "预览生成的 ICS 文本",
            description = "返回 Result<String>，data 为 ICS 全文，方便调试")
    @PostMapping("/preview")
    public Result<String> preview(@Valid @RequestBody CourseRequest request) {
        return Result.ok(icsService.generate(request));
    }

    @Operation(summary = "下载 .ics 文件",
            description = "Content-Type 为 text/calendar，可直接保存导入日历")
    @PostMapping("/download")
    public ResponseEntity<byte[]> download(@Valid @RequestBody CourseRequest request) {
        byte[] body = icsService.generate(request).getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"timetable.ics\"")
                .contentType(MediaType.parseMediaType("text/calendar; charset=UTF-8"))
                .body(body);
    }
}
