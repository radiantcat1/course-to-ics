package com.radiantcat1.course2ics.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class IcsControllerTest {

    @Autowired
    private MockMvc mvc;

    private static final String BODY = """
            {
              "semesterStart": "2026-09-07",
              "courses": [
                {
                  "title": "Data Structures",
                  "dayOfWeek": 1,
                  "startPeriod": 1,
                  "endPeriod": 2,
                  "weeks": 16,
                  "location": "Building A-301",
                  "teacher": "Prof. Zhang"
                }
              ]
            }
            """;

    @Test
    void health_shouldReturnOk() throws Exception {
        mvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    void preview_shouldReturnIcsText() throws Exception {
        mvc.perform(post("/api/v1/ics/preview")
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value(containsString("BEGIN:VCALENDAR")));
    }

    @Test
    void download_shouldReturnCalendarAttachment() throws Exception {
        mvc.perform(post("/api/v1/ics/download")
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                        "attachment; filename=\"timetable.ics\""))
                .andExpect(content().contentTypeCompatibleWith("text/calendar"))
                .andExpect(content().string(containsString("BEGIN:VEVENT")));
    }

    @Test
    void preview_shouldReturn400OnInvalidParam() throws Exception {
        String bad = """
                {"semesterStart":"2026-09-07","courses":[{"title":"","dayOfWeek":9,"startPeriod":1,"endPeriod":2,"weeks":16}]}
                """;
        mvc.perform(post("/api/v1/ics/preview")
                        .contentType(MediaType.APPLICATION_JSON).content(bad))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(40001));
    }

    @Test
    void preview_shouldReturnBizCodeOnReversedPeriod() throws Exception {
        String bad = """
                {"semesterStart":"2026-09-07","courses":[{"title":"X","dayOfWeek":1,"startPeriod":5,"endPeriod":2,"weeks":16}]}
                """;
        mvc.perform(post("/api/v1/ics/preview")
                        .contentType(MediaType.APPLICATION_JSON).content(bad))
                .andExpect(jsonPath("$.code").value(40003));
    }
}
