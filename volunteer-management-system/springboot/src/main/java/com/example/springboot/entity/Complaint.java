package com.example.springboot.entity;

import java.util.Date;

public class Complaint {

    private String complaintId; // 投诉ID
    private Date complaintTime; // 投诉时间
    private String complainantId; // 发起人对象ID
    private String complaintTargetId; // 投诉对象ID
    private String complaintType; // 投诉类型
    private String complaintContent; // 投诉内容
    private String evidenceLink; // 证据链接
    private String processingStatus; // 处理状态
    private String processingResult; // 处理结果
    private Date latestProcessingTime; // 最新处理时间
    private String handlerAdminId; // 处理人ID
    private Date visitTime; // 回访时间
    private String visitResult; // 回访结果
    private Integer arbitrationRound; // 仲裁轮次
    private String reviewAdminId; // 复审管理员ID

    // Getters and Setters
    public String getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }

    public Date getComplaintTime() {
        return complaintTime;
    }

    public void setComplaintTime(Date complaintTime) {
        this.complaintTime = complaintTime;
    }

    public String getComplainantId() {
        return complainantId;
    }

    public void setComplainantId(String complainantId) {
        this.complainantId = complainantId;
    }

    public String getComplaintTargetId() {
        return complaintTargetId;
    }

    public void setComplaintTargetId(String complaintTargetId) {
        this.complaintTargetId = complaintTargetId;
    }

    public String getComplaintType() {
        return complaintType;
    }

    public void setComplaintType(String complaintType) {
        this.complaintType = complaintType;
    }

    public String getComplaintContent() {
        return complaintContent;
    }

    public void setComplaintContent(String complaintContent) {
        this.complaintContent = complaintContent;
    }

    public String getEvidenceLink() {
        return evidenceLink;
    }

    public void setEvidenceLink(String evidenceLink) {
        this.evidenceLink = evidenceLink;
    }

    public String getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(String processingStatus) {
        this.processingStatus = processingStatus;
    }

    public String getProcessingResult() {
        return processingResult;
    }

    public void setProcessingResult(String processingResult) {
        this.processingResult = processingResult;
    }

    public Date getLatestProcessingTime() {
        return latestProcessingTime;
    }

    public void setLatestProcessingTime(Date latestProcessingTime) {
        this.latestProcessingTime = latestProcessingTime;
    }

    public String getHandlerAdminId() {
        return handlerAdminId;
    }

    public void setHandlerAdminId(String handlerAdminId) {
        this.handlerAdminId = handlerAdminId;
    }

    public Date getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(Date visitTime) {
        this.visitTime = visitTime;
    }

    public String getVisitResult() {
        return visitResult;
    }

    public void setVisitResult(String visitResult) {
        this.visitResult = visitResult;
    }

    public Integer getArbitrationRound() {
        return arbitrationRound;
    }

    public void setArbitrationRound(Integer arbitrationRound) {
        this.arbitrationRound = arbitrationRound;
    }

    public String getReviewAdminId() {
        return reviewAdminId;
    }

    public void setReviewAdminId(String reviewAdminId) {
        this.reviewAdminId = reviewAdminId;
    }
}
