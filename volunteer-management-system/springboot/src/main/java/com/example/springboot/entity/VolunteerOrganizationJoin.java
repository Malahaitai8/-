package com.example.springboot.entity;

import java.util.Date;

public class VolunteerOrganizationJoin {

    private String volunteerId; // 关联志愿者表 (VolunteerID)
    private String orgId;       // 关联组织机构表 (OrgID)
    private Date joinTime;      // 志愿者加入组织的时间 (JoinTime)
    private String memberStatus;  // 成员状态 (MemberStatus)

    // Getters and Setters
    public String getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(String volunteerId) {
        this.volunteerId = volunteerId;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public Date getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(Date joinTime) {
        this.joinTime = joinTime;
    }

    public String getMemberStatus() {
        return memberStatus;
    }

    public void setMemberStatus(String memberStatus) {
        this.memberStatus = memberStatus;
    }
}