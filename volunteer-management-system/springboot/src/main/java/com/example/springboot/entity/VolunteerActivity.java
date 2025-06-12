package com.example.springboot.entity;

import com.fasterxml.jackson.annotation.JsonFormat; // 导入JsonFormat
import java.util.Date;

public class VolunteerActivity {

    private String activityId; // 唯一标识活动 (ActivityID)
    private String orgId; // 关联志愿组织机构表 (OrgID)
    private String activityName; // 志愿活动名称 (ActivityName)

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime; // 志愿活动开始时间 (StartTime)

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime; // 志愿活动结束时间 (EndTime)

    private String location; // 活动地点 (Location)
    private Integer recruitmentCount; // 招募人数 (RecruitmentCount)
    private Integer acceptedCount; // 录取人数 (AcceptedCount)
    private String activityStatus; // 志愿活动状态 (ActivityStatus)

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date creationTime; // 创建时间 (CreationTime)

    private String reviewerAdminId; // 审核管理员ID (ReviewerAdminID)
    private String contactPersonPhone; // 负责人联系方式 (ContactPersonPhone)
    private Integer activityDurationHours; // 志愿活动总时长 (小时) (ActivityDurationHours)
    private Integer activityRating; // 志愿活动评分 (ActivityRating)
    private String isRatingAggregated; // 标记一个活动或培训的评分是否已经被处理过 (IsRatingAggregated)

    // 默认构造函数
    public VolunteerActivity() {}

    // Getter 和 Setter 方法
    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
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

    public Integer getAcceptedCount() {
        return acceptedCount;
    }

    public void setAcceptedCount(Integer acceptedCount) {
        this.acceptedCount = acceptedCount;
    }

    public String getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(String activityStatus) {
        this.activityStatus = activityStatus;
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

    public Integer getActivityDurationHours() {
        return activityDurationHours;
    }

    public void setActivityDurationHours(Integer activityDurationHours) {
        this.activityDurationHours = activityDurationHours;
    }

    public Integer getActivityRating() {
        return activityRating;
    }

    public void setActivityRating(Integer activityRating) {
        this.activityRating = activityRating;
    }

    public String getIsRatingAggregated() {
        return isRatingAggregated;
    }

    public void setIsRatingAggregated(String isRatingAggregated) {
        this.isRatingAggregated = isRatingAggregated;
    }
}
