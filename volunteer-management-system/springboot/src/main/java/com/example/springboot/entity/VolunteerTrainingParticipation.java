package com.example.springboot.entity;

public class VolunteerTrainingParticipation {

    private String volunteerId;        // 关联志愿者表 (VolunteerID)
    private String trainingId;         // 关联志愿培训表 (TrainingID)
    private String isCheckedIn;        // 志愿者是否进行签到 (IsCheckedIn)
    private Integer orgToVolunteerRating; // 组织给志愿者评分 (OrgToVolunteerRating)
    private Integer volunteerToOrgRating; // 志愿者给组织评分 (VolunteerToOrgRating)

    // Getters and Setters
    public String getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(String volunteerId) {
        this.volunteerId = volunteerId;
    }

    public String getTrainingId() {
        return trainingId;
    }

    public void setTrainingId(String trainingId) {
        this.trainingId = trainingId;
    }

    public String getIsCheckedIn() {
        return isCheckedIn;
    }

    public void setIsCheckedIn(String isCheckedIn) {
        this.isCheckedIn = isCheckedIn;
    }

    public Integer getOrgToVolunteerRating() {
        return orgToVolunteerRating;
    }

    public void setOrgToVolunteerRating(Integer orgToVolunteerRating) {
        this.orgToVolunteerRating = orgToVolunteerRating;
    }

    public Integer getVolunteerToOrgRating() {
        return volunteerToOrgRating;
    }

    public void setVolunteerToOrgRating(Integer volunteerToOrgRating) {
        this.volunteerToOrgRating = volunteerToOrgRating;
    }
}
