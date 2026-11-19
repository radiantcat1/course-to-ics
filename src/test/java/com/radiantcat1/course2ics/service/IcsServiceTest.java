package com.radiantcat1.course2ics.service;

import com.radiantcat1.course2ics.common.BizException;
import com.radiantcat1.course2ics.common.ErrorCode;
import com.radiantcat1.course2ics.dto.CourseItem;
import com.radiantcat1.course2ics.dto.CourseRequest;
import com.radiantcat1.course2ics.dto.PeriodItem;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class IcsServiceTest {

    private final IcsService service =
            new IcsService(new com.radiantcat1.course2ics.config.CourseProperties());

    /** 默认作息表：模拟客户没改时的 10 节 */
    private List<PeriodItem> defaultPeriods() {
        List<PeriodItem> p = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            p.add(new PeriodItem(String.format("%02d:00", 8 + i), String.format("%02d:45", 8 + i)));
        }
        return p;
    }

    private CourseRequest req(CourseItem... items) {
        return new CourseRequest(LocalDate.of(2026, 9, 7), defaultPeriods(), List.of(items));
    }

    private CourseRequest reqWithPeriods(List<PeriodItem> periods, CourseItem... items) {
        return new CourseRequest(LocalDate.of(2026, 9, 7), periods, List.of(items));
    }

    private CourseItem course(String title, int day, int start, int end, int weeks) {
        return new CourseItem(title, day, start, end, weeks, "A-301", "Zhang");
    }

    @Test
    void generate_shouldProduceValidCalendarEnvelope() {
        String ics = service.generate(req(course("Data Structures", 1, 1, 2, 16)));
        assertAll(
                () -> assertTrue(ics.startsWith("BEGIN:VCALENDAR\r\n")),
                () -> assertTrue(ics.contains("VERSION:2.0\r\n")),
                () -> assertTrue(ics.contains("CALSCALE:GREGORIAN\r\n")),
                () -> assertTrue(ics.contains("BEGIN:VEVENT\r\n")),
                () -> assertTrue(ics.contains("RRULE:FREQ=WEEKLY;COUNT=16\r\n")),
                () -> assertTrue(ics.trim().endsWith("END:VCALENDAR"))
        );
    }

    @Test
    void generate_shouldUseUtcTimestampEndingWithZ() {
        String ics = service.generate(req(course("X", 1, 1, 1, 1)));
        assertTrue(ics.matches("(?s).*DTSTAMP:\\d{8}T\\d{6}Z\\r\\n.*"),
                "DTSTAMP 必须是 UTC 且以 Z 结尾");
    }

    @ParameterizedTest
    @CsvSource({
            "1, 20260907",
            "2, 20260908",
            "3, 20260909",
            "7, 20260913"
    })
    void generate_shouldOffsetDateByDayOfWeek(int dayOfWeek, String expectedDate) {
        String ics = service.generate(req(course("X", dayOfWeek, 1, 1, 1)));
        assertTrue(ics.contains("DTSTART;TZID=Asia/Shanghai:" + expectedDate + "T080000"));
    }

    @Test
    void generate_shouldEscapeSpecialCharacters() {
        String ics = service.generate(req(course("A,B;C\\D\nE", 1, 1, 1, 1)));
        assertTrue(ics.contains("SUMMARY:A\\,B\\;C\\\\D\\nE"),
                "逗号、分号、反斜杠、换行必须按 ICS 规范转义");
    }

    @Test
    void generate_shouldFoldLongChineseTitleByUtf8Bytes() {
        String longTitle = "数据结构与算法分析（含课程设计、上机实验与小组项目答辩环节）A班";
        String ics = service.generate(req(course(longTitle, 1, 1, 1, 1)));

        for (String line : ics.split("\r\n")) {
            assertTrue(line.getBytes(StandardCharsets.UTF_8).length <= 75,
                    "单行超过 75 字节: " + line);
        }
        assertTrue(ics.contains("\r\n "), "续行必须以一个空格开头");
        assertTrue(ics.replace("\r\n ", "").contains(longTitle),
                "折行后去掉续行空格必须能无损还原完整标题");
    }

    @Test
    void generate_shouldUseCrLfOnly() {
        String ics = service.generate(req(course("X", 1, 1, 1, 1)));
        assertFalse(ics.replace("\r\n", "").contains("\n"), "不能出现裸 \\n 行尾");
    }

    @Test
    void generate_shouldEmitOneVeventPerCourse() {
        String ics = service.generate(req(
                course("A", 1, 1, 1, 8),
                course("B", 3, 3, 4, 16)));
        assertEquals(2, ics.split("BEGIN:VEVENT", -1).length - 1);
        assertTrue(ics.contains("RRULE:FREQ=WEEKLY;COUNT=8"));
        assertTrue(ics.contains("RRULE:FREQ=WEEKLY;COUNT=16"));
    }

    @Test
    void generate_shouldRejectEndPeriodBeforeStart() {
        BizException ex = assertThrows(BizException.class,
                () -> service.generate(req(course("X", 1, 5, 2, 1))));
        assertEquals(ErrorCode.PERIOD_INVALID, ex.getErrorCode());
    }

    // ===== 动态作息表相关的新用例 =====

    @Test
    void generate_shouldUseTimeFromRequestNotYml() {
        // 客户自定义：第1节 09:30-10:15，覆盖 yml 里的默认 08:00-08:45
        List<PeriodItem> custom = List.of(new PeriodItem("09:30", "10:15"));
        String ics = service.generate(reqWithPeriods(custom, course("X", 1, 1, 1, 1)));
        assertAll(
                () -> assertTrue(ics.contains("DTSTART;TZID=Asia/Shanghai:20260907T093000"),
                        "上课时间应来自请求里的作息表"),
                () -> assertTrue(ics.contains("DTEND;TZID=Asia/Shanghai:20260907T101500"),
                        "下课时间应来自请求里的作息表")
        );
    }

    @Test
    void generate_shouldSupportMoreThan10Periods() {
        // 客户有 12 节课，第 12 节 20:30-21:15
        List<PeriodItem> p = new ArrayList<>(defaultPeriods());
        p.add(new PeriodItem("19:55", "20:40"));
        p.add(new PeriodItem("20:30", "21:15"));
        String ics = service.generate(reqWithPeriods(p, course("晚课", 1, 12, 12, 16)));
        assertTrue(ics.contains("DTSTART;TZID=Asia/Shanghai:20260907T203000"),
                "应支持超过 10 节，并使用第 12 节的时间");
    }

    @Test
    void generate_shouldRejectPeriodBeyondSchedule() {
        // 作息表只有 3 节，课程却排到第 5 节
        List<PeriodItem> three = List.of(
                new PeriodItem("08:00", "08:45"),
                new PeriodItem("08:55", "09:40"),
                new PeriodItem("10:00", "10:45"));
        BizException ex = assertThrows(BizException.class,
                () -> service.generate(reqWithPeriods(three, course("X", 1, 5, 5, 16))));
        assertEquals(ErrorCode.PERIOD_INVALID, ex.getErrorCode());
    }
}
