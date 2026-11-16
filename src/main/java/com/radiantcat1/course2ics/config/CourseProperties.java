package com.radiantcat1.course2ics.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "course")
public class CourseProperties {

    private String timezone = "Asia/Shanghai";
    private String calendarName = "My Timetable";
    private List<String> periodStart = new ArrayList<>(List.of(
            "08:00", "08:55", "10:00", "10:55", "14:00",
            "14:55", "16:00", "16:55", "19:00", "19:55"));
    private List<String> periodEnd = new ArrayList<>(List.of(
            "08:45", "09:40", "10:45", "11:40", "14:45",
            "15:40", "16:45", "17:40", "19:45", "20:40"));

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    public String getCalendarName() { return calendarName; }
    public void setCalendarName(String calendarName) { this.calendarName = calendarName; }

    public List<String> getPeriodStart() { return periodStart; }
    public void setPeriodStart(List<String> periodStart) { this.periodStart = periodStart; }

    public List<String> getPeriodEnd() { return periodEnd; }
    public void setPeriodEnd(List<String> periodEnd) { this.periodEnd = periodEnd; }
}
