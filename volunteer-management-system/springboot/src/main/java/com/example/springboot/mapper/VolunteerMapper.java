package com.example.springboot.mapper;

import com.example.springboot.entity.Volunteer;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

public interface VolunteerMapper {
    // 查询所有志愿者
    List<Volunteer> selectAll(Volunteer volunteer); // Assumes XML mapping

    /**
     * 根据志愿者ID查询其所有已评价的志愿活动
     *
     * @param volunteerId 志愿者ID
     * @return Map列表，每个Map包含 orgName, projectName, rating, reviewTime
     */
    @Select("SELECT " +
            "    o.OrgName AS orgName, " +
            "    a.ActivityName AS projectName, " +
            "    p.OrgToVolunteerRating AS rating, " +
            "    a.EndTime AS reviewTime " +
            "FROM tbl_VolunteerActivityParticipation p " +
            "JOIN tbl_VolunteerActivity a ON p.ActivityID = a.ActivityID " +
            "JOIN tbl_Organization o ON a.OrgID = o.OrgID " +
            "WHERE p.VolunteerID = #{volunteerId} " +
            "  AND a.ActivityStatus = N'已结束' " +
            "  AND p.OrgToVolunteerRating IS NOT NULL " +
            "ORDER BY a.EndTime DESC")
    List<Map<String, Object>> findActivityReviewsByVolunteerId(@Param("volunteerId") String volunteerId);

    /**
     * 根据志愿者ID查询其所有已评价的培训
     *
     * @param volunteerId 志愿者ID
     * @return Map列表，每个Map包含 orgName, projectName, rating, reviewTime
     */
    @Select("SELECT " +
            "    o.OrgName AS orgName, " +
            "    t.TrainingName AS projectName, " +
            "    p.OrgToVolunteerRating AS rating, " +
            "    t.EndTime AS reviewTime " +
            "FROM tbl_VolunteerTrainingParticipation p " +
            "JOIN tbl_VolunteerTraining t ON p.TrainingID = t.TrainingID " +
            "JOIN tbl_Organization o ON t.OrgID = o.OrgID " +
            "WHERE p.VolunteerID = #{volunteerId} " +
            "  AND t.TrainingStatus = N'已结束' " +
            "  AND p.OrgToVolunteerRating IS NOT NULL " +
            "ORDER BY t.EndTime DESC")
    List<Map<String, Object>> findTrainingReviewsByVolunteerId(@Param("volunteerId") String volunteerId);

    int updateAccountStatus(@Param("volunteerId") String volunteerId, @Param("accountStatus") String accountStatus);

    /**
     * 根据 VolunteerID 更新志愿者密码
     *
     * @param volunteerId 志愿者的ID
     * @param newPassword 新密码
     */
    void updatePassword(@Param("volunteerId") String volunteerId, @Param("newPassword") String newPassword);

    // 根据志愿者ID查询志愿者信息
    // Matching columns from your CREATE TABLE statement
    @Select("SELECT VolunteerID as volunteerId, Username as username, Name as name, PhoneNumber as phone, " +
            "IDCardNumber as idCard, Password as password, Country as country, Gender as gender, " +
            "ServiceArea as serviceArea, Ethnicity as ethnicity, PoliticalStatus as politicalStatus, " +
            "HighestEducation as highestEducation, EmploymentStatus as employmentStatus, ServiceCategory as serviceCategory, " +
            "TotalVolunteerHours as totalServiceHours, VolunteerRating as volunteerComprehensiveScore, " +
            "AccountStatus as accountStatus " +
            "FROM tbl_Volunteer WHERE VolunteerID = #{volunteerId}")
    Volunteer selectByID(String volunteerId);

    // 插入新的志愿者信息 (SQL defined in XML)
    void insert(Volunteer volunteer);

    // 根据志愿者ID更新志愿者信息 (SQL defined in XML)
    void updateById(Volunteer volunteer);

    // 根据志愿者ID删除志愿者信息
    @Delete("DELETE FROM tbl_Volunteer WHERE VolunteerID = #{volunteerId}")
    void deleteById(String volunteerId);

    // 根据用户名查询志愿者信息
    // Matching columns from your CREATE TABLE statement
    @Select("SELECT VolunteerID as volunteerId, Username as username, Name as name, PhoneNumber as phone, " +
            "IDCardNumber as idCard, Password as password, Country as country, Gender as gender, " +
            "ServiceArea as serviceArea, Ethnicity as ethnicity, PoliticalStatus as politicalStatus, " +
            "HighestEducation as highestEducation, EmploymentStatus as employmentStatus, ServiceCategory as serviceCategory, " +
            "TotalVolunteerHours as totalServiceHours, VolunteerRating as volunteerComprehensiveScore, " +
            "AccountStatus as accountStatus " +
            "FROM tbl_Volunteer WHERE Username = #{username}")
    Volunteer selectByUsername(String username);

    /**
     * 根据志愿者ID从视图 Volunteer_Stars 中查询星级。
     *
     * @param volunteerId 志愿者的ID
     * @return 志愿者的星级 (整数)，如果未找到则返回 null。
     */
    @Select("SELECT StarLevel FROM Volunteer_Stars WHERE VolunteerID = #{volunteerId}")
    Integer findStarLevelByVolunteerID(String volunteerId);

    /**
     * 【新方法-已重命名】根据姓名模糊搜索所有志愿者
     * @param name 志愿者姓名关键词
     * @return 志愿者列表
     */
    @Select("<script>" +
            "SELECT * FROM tbl_Volunteer" +
            "<where>" +
            "  <if test='name != null and name != \"\"'>" +
            "    AND Name LIKE CONCAT('%', #{name}, '%')" +
            "  </if>" +
            "</where>" +
            "</script>")
    List<Volunteer> searchVolunteersByName(@Param("name") String name);
}