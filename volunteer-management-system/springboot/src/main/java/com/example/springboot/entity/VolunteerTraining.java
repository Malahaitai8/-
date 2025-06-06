package com.example.springboot.entity;

import java.util.Date; // Or use java.time.LocalDateTime

public class VolunteerTraining {

    private String trainingId; // 唯一标识培训 (TrainingID)
    private String orgId; // 关联组织表 (OrgID)
    private String trainingName; // 培训名称 (TrainingName)
    private String theme; // 主题 (Theme)
    private Date startTime; // 培训开始时间 (StartTime)
    private Date endTime; // 培训结束时间 (EndTime)
    private String location; // 培训地点 (Location)
    private Integer recruitmentCount; // 招募人数 (RecruitmentCount)
    private String trainingStatus; // 培训状态 (TrainingStatus)
    private Date creationTime; // 创建时间 (CreationTime)
    private String reviewerAdminId; // 审核管理员ID (ReviewerAdminID)
    private String contactPersonPhone; // 负责人联系方式 (ContactPersonPhone)
    private Integer trainingRating; // 培训评分 (TrainingRating)
    private String isRatingAggregated; // 标记一个活动或培训的评分是否已经被处理过 (IsRatingAggregated)

    // Getters and Setters

    public String getTrainingId() {
        return trainingId;
    }

    public void setTrainingId(String trainingId) {
        this.trainingId = trainingId;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getTrainingName() {
        return trainingName;
    }

    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getRecruitmentCount() {
        return recruitmentCount;
    }

    public void setRecruitmentCount(Integer recruitmentCount) {
        this.recruitmentCount = recruitmentCount;
    }

    public String getTrainingStatus() {
        return trainingStatus;
    }

    public void setTrainingStatus(String trainingStatus) {
        this.trainingStatus = trainingStatus;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public String getReviewerAdminId() {
        return reviewerAdminId;
    }

    public void setReviewerAdminId(String reviewerAdminId) {
        this.reviewerAdminId = reviewerAdminId;
    }

    public String getContactPersonPhone() {
        return contactPersonPhone;
    }

    public void setContactPersonPhone(String contactPersonPhone) {
        this.contactPersonPhone = contactPersonPhone;
    }

    public Integer getTrainingRating() {
        return trainingRating;
    }

    public void setTrainingRating(Integer trainingRating) {
        this.trainingRating = trainingRating;
    }

    public String getIsRatingAggregated() {
        return isRatingAggregated;
    }

    public void setIsRatingAggregated(String isRatingAggregated) {
        this.isRatingAggregated = isRatingAggregated;
    }
}
