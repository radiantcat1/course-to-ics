package com.radiantcat1.course2ics.controller;

import com.radiantcat1.course2ics.common.Result;
import com.radiantcat1.course2ics.dto.CourseRequest;
import com.radiantcat1.course2ics.service.IcsService;
import jakarta.validation.Valid;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ics")
public class IcsController {

    private final IcsService icsService;

    public IcsController(IcsService icsService) {
        this.icsService = icsService;
    }

    @PostMapping("/preview")
    public Result<String> preview(@Valid @RequestBody CourseRequest request) {
        return Result.ok(icsService.generate(request));
    }

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

