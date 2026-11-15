package com.radiantcat1.course2ics.service;

import com.radiantcat1.course2ics.common.BizException;
import com.radiantcat1.course2ics.common.ErrorCode;
import com.radiantcat1.course2ics.dto.CourseItem;
import com.radiantcat1.course2ics.dto.CourseRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;

@Service
public class IcsService {

    private static final String[] PERIOD_START = {
            "08:00", "08:55", "10:00", "10:55", "14:00",
            "14:55", "16:00", "16:55", "19:00", "19:55"
    };
    private static final String[] PERIOD_END = {
            "08:45", "09:40", "10:45", "11:40", "14:45",
            "15:40", "16:45", "17:40", "19:45", "20:40"
    };
    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
    private static final DateTimeFormatter STAMP_FMT =
            DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
    private static final String TZID = "Asia/Shanghai";

    public String generate(CourseRequest request) {
        StringBuilder sb = new StringBuilder();
        line(sb, "BEGIN:VCALENDAR");
        line(sb, "VERSION:2.0");
        line(sb, "PRODID:-//radiantcat1//Course2ICS//CN");
        line(sb, "CALSCALE:GREGORIAN");
        line(sb, "METHOD:PUBLISH");
        line(sb, "X-WR-CALNAME:My Timetable");

        String stamp = LocalDateTime.now(ZoneId.of("UTC")).format(STAMP_FMT);
        String dayTag = request.semesterStart().format(DateTimeFormatter.BASIC_ISO_DATE);

        int i = 0;
        for (CourseItem c : request.courses()) {
            check(c);
            i++;
            LocalDate date = request.semesterStart().plusDays(c.dayOfWeek() - 1);
            LocalDateTime start = LocalDateTime.of(
                    date, LocalTime.parse(PERIOD_START[c.startPeriod() - 1]));
            LocalDateTime end = LocalDateTime.of(
                    date, LocalTime.parse(PERIOD_END[c.endPeriod() - 1]));

            line(sb, "BEGIN:VEVENT");
            line(sb, "UID:course-" + dayTag + "-" + i + "@course2ics");
            line(sb, "DTSTAMP:" + stamp);
            line(sb, "DTSTART;TZID=" + TZID + ":" + start.format(TIME_FMT));
            line(sb, "DTEND;TZID=" + TZID + ":" + end.format(TIME_FMT));
            line(sb, "RRULE:FREQ=WEEKLY;COUNT=" + c.weeks());
            line(sb, "SUMMARY:" + esc(c.title()));
            line(sb, "LOCATION:" + esc(c.location()));
            line(sb, "DESCRIPTION:" + esc("Teacher: " + nullToEmpty(c.teacher())));
            line(sb, "END:VEVENT");
        }
        line(sb, "END:VCALENDAR");
        return sb.toString();
    }

    private void check(CourseItem c) {
        if (c.endPeriod() < c.startPeriod()) {
            throw new BizException(ErrorCode.PERIOD_INVALID, "结束节次不能小于起始节次");
        }
    }

    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private String esc(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\\", "\\\\")
                .replace(";", "\\;")
                .replace(",", "\\,")
                .replace("\r\n", "\\n")
                .replace("\n", "\\n");
    }

    private void line(StringBuilder sb, String content) {
        sb.append(fold(content)).append("\r\n");
    }

    private String fold(String s) {
        if (s.getBytes(StandardCharsets.UTF_8).length <= 75) {
            return s;
        }
        StringBuilder out = new StringBuilder();
        int width = 0;
        int limit = 75;
        for (int i = 0; i < s.length(); i++) {
            String ch = s.substring(i, i + 1);
            int size = ch.getBytes(StandardCharsets.UTF_8).length;
            if (width + size > limit) {
                out.append("\r\n ");
                width = 1;
                limit = 74;
            }
            out.append(ch);
            width += size;
        }
        return out.toString();
    }
}

