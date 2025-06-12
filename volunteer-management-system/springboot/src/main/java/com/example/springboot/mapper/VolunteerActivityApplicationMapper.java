// 这是 VolunteerActivityApplicationMapper.java 的最终正确版本

package com.example.springboot.mapper;

import com.example.springboot.entity.VolunteerActivityApplication;
import com.example.springboot.exception.CustomException;
import org.apache.ibatis.annotations.*;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface VolunteerActivityApplicationMapper {

    // =================================================================
    //  这些是用注解实现的方法 (简单的SQL)
    // =================================================================

    @Insert("INSERT INTO tbl_VolunteerActivityApplication (ApplicationID, VolunteerID, ActivityID, IntendedPositionID, ApplicationTime, ApplicationStatus) " +
            "VALUES (#{applicationId,jdbcType=CHAR}, #{volunteerId,jdbcType=CHAR}, #{activityId,jdbcType=CHAR}, #{intendedPositionId,jdbcType=CHAR}, " +
            "GETDATE(), #{applicationStatus,jdbcType=NVARCHAR})")
    int insert(VolunteerActivityApplication application);

    @Update("UPDATE tbl_VolunteerActivityApplication SET ApplicationStatus = #{applicationStatus,jdbcType=NVARCHAR} " +
            "WHERE ApplicationID = #{applicationId,jdbcType=CHAR}")
    int updateStatus(@Param("applicationId") String applicationId, @Param("applicationStatus") String applicationStatus);

    @Delete("DELETE FROM tbl_VolunteerActivityApplication WHERE ApplicationID = #{applicationId,jdbcType=CHAR}")
    int deleteById(@Param("applicationId") String applicationId);

    @Select("SELECT ApplicationID as applicationId, VolunteerID as volunteerId, ActivityID as activityId, " +
            "IntendedPositionID as intendedPositionId, ApplicationTime as applicationTime, ApplicationStatus as applicationStatus " +
            "FROM tbl_VolunteerActivityApplication WHERE ApplicationID = #{applicationId,jdbcType=CHAR}")
    VolunteerActivityApplication selectById(@Param("applicationId") String applicationId);

    @Select("SELECT ApplicationID as applicationId, VolunteerID as volunteerId, ActivityID as activityId, " +
            "IntendedPositionID as intendedPositionId, ApplicationTime as applicationTime, ApplicationStatus as applicationStatus " +
            "FROM tbl_VolunteerActivityApplication WHERE VolunteerID = #{volunteerId,jdbcType=CHAR} ORDER BY ApplicationTime DESC")
    List<VolunteerActivityApplication> selectByVolunteerId(@Param("volunteerId") String volunteerId);


    // =================================================================
    //  这些是在 XML 文件中实现的方法 (复杂的SQL)
    //  (在Java接口中只保留方法声明)
    // =================================================================

    /**
     * 查询所有报名申请 (可带条件过滤，SQL在XML中)
     */
    List<VolunteerActivityApplication> selectAll(VolunteerActivityApplication filterCriteria);

     /**
     * 检查志愿者是否已对某活动下的某岗位提交了特定状态（如待审核、已通过）的申请
     * @param volunteerId 志愿者ID
     * @param activityId 活动ID
     * @param intendedPositionId 意向岗位ID (可以为null，表示检查活动级别申请)
     * @param statuses 检查的状态列表
     * @return 匹配的申请数量
     */
    int checkExistingApplicationByPositionAndStatus(@Param("volunteerId") String volunteerId,
                                           @Param("activityId") String activityId,
                                           @Param("intendedPositionId") String intendedPositionId,
                                           @Param("statuses") List<String> statuses);

        /**
     * 【最重要的修改】
     * 我们将之前在XML中的JOIN查询，直接放到了下面的 @Select 注解里。
     * SQL语句被写成了一个字符串数组，这样更清晰易读。
     */
    @Select({
        "SELECT",
        "    app.ApplicationID       AS applicationId,",
        "    app.ApplicationStatus   AS applicationStatus,",
        "    app.ApplicationTime     AS applicationTime,",
        "    act.ActivityID          AS activityId,",
        "    act.ActivityName        AS activityName,",
        "    act.Location            AS location,",
        "    act.StartTime           AS startTime,",
        "    pos.PositionName        AS intendedPositionName",
        "FROM",
        "    tbl_VolunteerActivityApplication app",
        "INNER JOIN",
        "    tbl_VolunteerActivity act ON app.ActivityID = act.ActivityID",
        "LEFT JOIN",
        "    tbl_Position pos ON app.IntendedPositionID = pos.PositionID",
        "WHERE",
        "    app.VolunteerID = #{volunteerId,jdbcType=CHAR}",
        "ORDER BY",
        "    app.ApplicationTime DESC"
    })
    List<Map<String, Object>> selectMyApplicationDetails(@Param("volunteerId") String volunteerId);

    //修改的代码都在下面


    @Select({
            "SELECT ",
            "    v.VolunteerID           AS id, ",
            "    v.Name                  AS name, ",
            "    v.Gender                AS gender, ",
            "    v.PhoneNumber           AS telephone, ",
            "    v.PoliticalStatus       AS zzmm, ",
            "    v.HighestEducation      AS study, ",
            "    v.VolunteerRating       AS rating, ",
            "    act.ActivityName        AS activityName, ",
            "    pos.PositionName        AS desiredPosition, ",
            "    app.ApplicationTime     AS applicationDate, ",
            "    app.ApplicationID       AS applicationId, ",
            // --- NEWLY ADDED LINE ---
            // Formats and concatenates the start and end times into a single string.
            // LEFT(CONVERT(..., 120), 16) formats the datetime to 'YYYY-MM-DD HH:MI'.
            "    LEFT(CONVERT(varchar, ts.StartTime, 120), 16) + ' to ' + LEFT(CONVERT(varchar, ts.EndTime, 120), 16) AS activityPeriod ",
            "FROM ",
            "    tbl_VolunteerActivityApplication app ",
            "INNER JOIN ",
            "    tbl_VolunteerActivity act ON app.ActivityID = act.ActivityID ",
            "INNER JOIN ",
            "    tbl_Volunteer v ON app.VolunteerID = v.VolunteerID ",
            "LEFT JOIN ",
            "    tbl_Position pos ON app.IntendedPositionID = pos.PositionID ",
            // --- NEWLY ADDED JOIN ---
            "LEFT JOIN ",
            "    tbl_ActivityTimeslot ts ON act.ActivityID = ts.EventID ",
            "WHERE ",
            "    act.OrgID = #{orgId,jdbcType=CHAR} AND app.ApplicationStatus = N'待审核' ",
            "ORDER BY ",
            "    app.ApplicationTime DESC"
    })
    List<Map<String, Object>> selectPendingApplicationsForOrg(@Param("orgId") String orgId);
}