package com.radiantcat1.course2ics.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CoursePropertiesTest {

    @Autowired
    private CourseProperties props;

    @Test
    void shouldBindValuesFromApplicationYml() {
        assertAll(
                () -> assertEquals("Asia/Shanghai", props.getTimezone()),
                () -> assertEquals("My Timetable", props.getCalendarName()),
                () -> assertEquals(10, props.getPeriodStart().size()),
                () -> assertEquals("08:00", props.getPeriodStart().get(0)),
                () -> assertEquals("08:45", props.getPeriodEnd().get(0)),
                () -> assertEquals("20:40", props.getPeriodEnd().get(9))
        );
    }
}
