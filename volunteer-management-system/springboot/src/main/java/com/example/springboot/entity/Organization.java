package com.example.springboot.entity;

public class Organization {
    private String orgId;               // 组织ID (对应 OrgID)
    private String orgName;             // 组织名称 (对应 OrgName)
    private String orgLoginUserName;    // 登录用户名 (对应 OrgLoginUserName)
    private String orgLoginPassword;    // 登录密码 (对应 OrgLoginPassword)
    private String contactPersonPhone;  // 负责人联系方式 (对应 ContactPersonPhone)
    private String serviceRegion;       // 服务区域 (对应 ServiceRegion)
    private Integer orgScale;           // 组织规模 (对应 OrgScale)
    private Double orgRating;           // 组织评分 (对应 OrgRating)
    private String orgAccountStatus;    // 组织账户状态 (对应 OrgAccountStatus)
    private Integer totalServiceHours;  // 服务总时长 (对应 TotalServiceHours)
    private Integer activityCount;      // 活动举办次数 (对应 ActivityCount)
    private Integer trainingCount;      // 培训举办次数 (对应 TrainingCount)

    // Getter 和 Setter 方法
    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgLoginUserName() {
        return orgLoginUserName;
    }

    public void setOrgLoginUserName(String orgLoginUserName) {
        this.orgLoginUserName = orgLoginUserName;
    }

    public String getOrgLoginPassword() {
        return orgLoginPassword;
    }

    public void setOrgLoginPassword(String orgLoginPassword) {
        this.orgLoginPassword = orgLoginPassword;
    }

    public String getContactPersonPhone() {
        return contactPersonPhone;
    }

    public void setContactPersonPhone(String contactPersonPhone) {
        this.contactPersonPhone = contactPersonPhone;
    }

    public String getServiceRegion() {
        return serviceRegion;
    }

    public void setServiceRegion(String serviceRegion) {
        this.serviceRegion = serviceRegion;
    }

    public Integer getOrgScale() {
        return orgScale;
    }

    public void setOrgScale(Integer orgScale) {
        this.orgScale = orgScale;
    }

    public Double getOrgRating() {
        return orgRating;
    }

    public void setOrgRating(Double orgRating) {
        this.orgRating = orgRating;
    }

    public String getOrgAccountStatus() {
        return orgAccountStatus;
    }

    public void setOrgAccountStatus(String orgAccountStatus) {
        this.orgAccountStatus = orgAccountStatus;
    }

    public Integer getTotalServiceHours() {
        return totalServiceHours;
    }

    public void setTotalServiceHours(Integer totalServiceHours) {
        this.totalServiceHours = totalServiceHours;
    }

    public Integer getActivityCount() {
        return activityCount;
    }

    public void setActivityCount(Integer activityCount) {
        this.activityCount = activityCount;
    }

    public Integer getTrainingCount() {
        return trainingCount;
    }

    public void setTrainingCount(Integer trainingCount) {
        this.trainingCount = trainingCount;
    }
}
