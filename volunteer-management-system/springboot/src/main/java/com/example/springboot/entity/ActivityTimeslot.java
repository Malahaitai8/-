package com.example.springboot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

public class ActivityTimeslot {

    private String timeslotId; // 时段ID (TimeslotID)
    private String eventId;    // 事件ID (EventID)
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;  // 时段开始时间 (StartTime)
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;    // 时段结束时间 (EndTime)

    // Getters and Setters
    public String getTimeslotId() {
        return timeslotId;
    }

    public void setTimeslotId(String timeslotId) {
        this.timeslotId = timeslotId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
