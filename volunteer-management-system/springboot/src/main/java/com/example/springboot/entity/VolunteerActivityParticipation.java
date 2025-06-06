package com.example.springboot.entity;

public class VolunteerActivityParticipation {

    private String volunteerId;        // 关联志愿者表 (VolunteerID)
    private String activityId;         // 关联志愿活动表 (ActivityID)
    private String actualPositionId;   // 实际岗位ID (ActualPositionID)
    private String isCheckedIn;        // 志愿者是否进行签到 (IsCheckedIn) - NCHAR(1) mapped to String
    private Integer volunteerToOrgRating; // 志愿者给组织评分 (VolunteerToOrgRating)
    private Integer orgToVolunteerRating; // 组织给志愿者评分 (OrgToVolunteerRating)

    // Getters and Setters
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

    public String getActualPositionId() {
        return actualPositionId;
    }

    public void setActualPositionId(String actualPositionId) {
        this.actualPositionId = actualPositionId;
    }

    public String getIsCheckedIn() {
        return isCheckedIn;
    }

    public void setIsCheckedIn(String isCheckedIn) {
        this.isCheckedIn = isCheckedIn;
    }

    public Integer getVolunteerToOrgRating() {
        return volunteerToOrgRating;
    }

    public void setVolunteerToOrgRating(Integer volunteerToOrgRating) {
        this.volunteerToOrgRating = volunteerToOrgRating;
    }

    public Integer getOrgToVolunteerRating() {
        return orgToVolunteerRating;
    }

    public void setOrgToVolunteerRating(Integer orgToVolunteerRating) {
        this.orgToVolunteerRating = orgToVolunteerRating;
    }
}
