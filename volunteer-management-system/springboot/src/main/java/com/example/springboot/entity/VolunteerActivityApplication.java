package com.example.springboot.entity;

import java.util.Date;

public class VolunteerActivityApplication {

    private String applicationId;     // 报名申请的唯一标识 (ApplicationID)
    private String volunteerId;       // 关联申请的志愿者 (VolunteerID)
    private String activityId;        // 志愿活动ID (ActivityID)
    private String intendedPositionId; // 意向岗位ID (IntendedPositionID)
    private Date applicationTime;     // 志愿者提交申请的具体时间 (ApplicationTime)
    private String applicationStatus; // 申请的当前状态 (ApplicationStatus)

    // Getters and Setters
    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(String volunteerId) {
        this.volunteerId = volunteerId;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getIntendedPositionId() {
        return intendedPositionId;
    }

    public void setIntendedPositionId(String intendedPositionId) {
        this.intendedPositionId = intendedPositionId;
    }

    public Date getApplicationTime() {
        return applicationTime;
    }

    public void setApplicationTime(Date applicationTime) {
        this.applicationTime = applicationTime;
    }

    public String getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(String applicationStatus) {
        this.applicationStatus = applicationStatus;
    }
}
