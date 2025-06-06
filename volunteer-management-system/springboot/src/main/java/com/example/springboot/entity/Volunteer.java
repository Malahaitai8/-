package com.example.springboot.entity;

public class Volunteer {
    private String volunteerId; // 志愿者ID
    private String username; // 用户名
    private String password; // 登录密码
    private String name; // 志愿者真实姓名
    private String phone; // 手机号
    private String idCard; // 身份证号 (Matches IDCardNumber in table, consider renaming for consistency)
    private String registrationTime; // 注册时间 (Table does not have this column explicitly, maybe generated or part of another logic)
    private String country; // 国籍
    private String gender; // 性别
    private String ethnicity; // 民族
    private String politicalStatus; // 政治面貌
    private String highestEducation; // 最高学历 (Was educationLevel)
    private String employmentStatus; // 从业情况 (Was occupation)
    private String serviceCategory; // 服务类别
    private String serviceArea; // 服务区域
    private Double totalServiceHours; // 志愿总时长 (Matches TotalVolunteerHours)
    private Double volunteerComprehensiveScore; // 志愿者综合评分 (Matches VolunteerRating)
    // totalTrainingHours is in the old select query but not in your new CREATE TABLE statement.
    // I will remove it from the entity for now unless you confirm it should be there
    // private Double totalTrainingHours;
    private String accountStatus; // 账户状态

    // Getter 和 Setter 方法
    public String getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(String volunteerId) {
        this.volunteerId = volunteerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIdCard() { // Corresponds to IDCardNumber in the table
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(String registrationTime) {
        this.registrationTime = registrationTime;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }

    public String getPoliticalStatus() {
        return politicalStatus;
    }

    public void setPoliticalStatus(String politicalStatus) {
        this.politicalStatus = politicalStatus;
    }

    public String getHighestEducation() {
        return highestEducation;
    }

    public void setHighestEducation(String highestEducation) {
        this.highestEducation = highestEducation;
    }

    public String getEmploymentStatus() {
        return employmentStatus;
    }

    public void setEmploymentStatus(String employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    public String getServiceCategory() {
        return serviceCategory;
    }

    public void setServiceCategory(String serviceCategory) {
        this.serviceCategory = serviceCategory;
    }

    public String getServiceArea() {
        return serviceArea;
    }

    public void setServiceArea(String serviceArea) {
        this.serviceArea = serviceArea;
    }

    public Double getTotalServiceHours() {
        return totalServiceHours;
    }

    public void setTotalServiceHours(Double totalServiceHours) {
        this.totalServiceHours = totalServiceHours;
    }

    public Double getVolunteerComprehensiveScore() {
        return volunteerComprehensiveScore;
    }

    public void setVolunteerComprehensiveScore(Double volunteerComprehensiveScore) {
        this.volunteerComprehensiveScore = volunteerComprehensiveScore;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }
}