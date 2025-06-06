package com.example.springboot.mapper;

import com.example.springboot.entity.VolunteerActivityApplication;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface VolunteerActivityApplicationMapper {

    /**
     * 插入新的活动报名申请
     * @param application 报名申请对象
     * @return 影响行数
     */
    @Insert("INSERT INTO tbl_VolunteerActivityApplication (ApplicationID, VolunteerID, ActivityID, IntendedPositionID, ApplicationTime, ApplicationStatus) " +
            "VALUES (#{applicationId,jdbcType=CHAR}, #{volunteerId,jdbcType=CHAR}, #{activityId,jdbcType=CHAR}, #{intendedPositionId,jdbcType=CHAR}, " +
            "GETDATE(), #{applicationStatus,jdbcType=NVARCHAR})")
    int insert(VolunteerActivityApplication application);

    /**
     * 更新报名申请的状态
     * @param applicationId 申请ID
     * @param applicationStatus 新的申请状态
     * @return 影响行数
     */
    @Update("UPDATE tbl_VolunteerActivityApplication SET ApplicationStatus = #{applicationStatus,jdbcType=NVARCHAR} " +
            "WHERE ApplicationID = #{applicationId,jdbcType=CHAR}")
    int updateStatus(@Param("applicationId") String applicationId, @Param("applicationStatus") String applicationStatus);

    /**
     * 根据申请ID删除报名申请 (物理删除，谨慎使用)
     * @param applicationId 申请ID
     * @return 影响行数
     */
    @Delete("DELETE FROM tbl_VolunteerActivityApplication WHERE ApplicationID = #{applicationId,jdbcType=CHAR}")
    int deleteById(@Param("applicationId") String applicationId);

    /**
     * 根据申请ID查询报名申请信息
     * @param applicationId 申请ID
     * @return 报名申请对象
     */
    @Select("SELECT ApplicationID as applicationId, VolunteerID as volunteerId, ActivityID as activityId, " +
            "IntendedPositionID as intendedPositionId, ApplicationTime as applicationTime, ApplicationStatus as applicationStatus " +
            "FROM tbl_VolunteerActivityApplication WHERE ApplicationID = #{applicationId,jdbcType=CHAR}")
    VolunteerActivityApplication selectById(@Param("applicationId") String applicationId);

    /**
     * 根据志愿者ID查询其所有活动报名申请
     * @param volunteerId 志愿者ID
     * @return 报名申请列表
     */
    @Select("SELECT ApplicationID as applicationId, VolunteerID as volunteerId, ActivityID as activityId, " +
            "IntendedPositionID as intendedPositionId, ApplicationTime as applicationTime, ApplicationStatus as applicationStatus " +
            "FROM tbl_VolunteerActivityApplication WHERE VolunteerID = #{volunteerId,jdbcType=CHAR} ORDER BY ApplicationTime DESC")
    List<VolunteerActivityApplication> selectByVolunteerId(@Param("volunteerId") String volunteerId);

    /**
     * 根据活动ID查询该活动的所有报名申请
     * @param activityId 活动ID
     * @return 报名申请列表
     */
    @Select("SELECT ApplicationID as applicationId, VolunteerID as volunteerId, ActivityID as activityId, " +
            "IntendedPositionID as intendedPositionId, ApplicationTime as applicationTime, ApplicationStatus as applicationStatus " +
            "FROM tbl_VolunteerActivityApplication WHERE ActivityID = #{activityId,jdbcType=CHAR} ORDER BY ApplicationTime DESC")
    List<VolunteerActivityApplication> selectByActivityId(@Param("activityId") String activityId);

    /**
     * 根据活动ID和申请状态查询报名申请
     * @param activityId 活动ID
     * @param applicationStatus 申请状态
     * @return 报名申请列表
     */
    @Select("SELECT ApplicationID as applicationId, VolunteerID as volunteerId, ActivityID as activityId, " +
            "IntendedPositionID as intendedPositionId, ApplicationTime as applicationTime, ApplicationStatus as applicationStatus " +
            "FROM tbl_VolunteerActivityApplication WHERE ActivityID = #{activityId,jdbcType=CHAR} AND ApplicationStatus = #{applicationStatus,jdbcType=NVARCHAR} " +
            "ORDER BY ApplicationTime DESC")
    List<VolunteerActivityApplication> selectByActivityIdAndStatus(@Param("activityId") String activityId, @Param("applicationStatus") String applicationStatus);

    /**
     * 查询所有报名申请 (可带条件过滤，SQL defined in XML)
     * @param filterCriteria 过滤条件
     * @return 报名申请列表
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
}
