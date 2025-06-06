package com.example.springboot.entity;

public class Position {

    private String positionId;          // 岗位ID (PositionID)
    private String positionName;        // 岗位名称 (PositionName)
    private String activityId;          // 活动ID (ActivityID)
    private Integer positionServiceHours; // 岗位服务时长 (PositionServiceHours)
    private Integer requiredVolunteers;   // 需求人数 (RequiredVolunteers)
    private Integer recruitedVolunteers;  // 已招募人数 (RecruitedVolunteers)

    // Getters and Setters
    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public Integer getPositionServiceHours() {
        return positionServiceHours;
    }

    public void setPositionServiceHours(Integer positionServiceHours) {
        this.positionServiceHours = positionServiceHours;
    }

    public Integer getRequiredVolunteers() {
        return requiredVolunteers;
    }

    public void setRequiredVolunteers(Integer requiredVolunteers) {
        this.requiredVolunteers = requiredVolunteers;
    }

    public Integer getRecruitedVolunteers() {
        return recruitedVolunteers;
    }

    public void setRecruitedVolunteers(Integer recruitedVolunteers) {
        this.recruitedVolunteers = recruitedVolunteers;
    }
}
