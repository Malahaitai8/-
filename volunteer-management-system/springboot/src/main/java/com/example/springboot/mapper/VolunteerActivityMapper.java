package com.example.springboot.mapper;

import com.example.springboot.entity.VolunteerActivity;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface VolunteerActivityMapper {

    /**
     * 插入新的志愿活动信息 (SQL defined in XML)
     * @param volunteerActivity 志愿活动对象
     * @return 影响行数
     */
    int insert(VolunteerActivity volunteerActivity);

    /**
     * 根据活动ID更新志愿活动信息 (SQL defined in XML)
     * @param volunteerActivity 志愿活动对象
     * @return 影响行数
     */
    int updateById(VolunteerActivity volunteerActivity);

    /**
     * 根据活动ID删除志愿活动信息
     * @param activityId 活动ID
     * @return 影响行数
     */
    @Delete("DELETE FROM tbl_VolunteerActivity WHERE ActivityID = #{activityId}")
    int deleteById(@Param("activityId") String activityId);

    /**
     * 根据活动ID查询志愿活动信息
     * @param activityId 活动ID
     * @return 志愿活动对象
     */
    @Select("SELECT " +
            "ActivityID as activityId, OrgID as orgId, ActivityName as activityName, " +
            "StartTime as startTime, EndTime as endTime, Location as location, " +
            "RecruitmentCount as recruitmentCount, AcceptedCount as acceptedCount, " +
            "ActivityStatus as activityStatus, CreationTime as creationTime, " +
            "ReviewerAdminID as reviewerAdminId, ContactPersonPhone as contactPersonPhone, " +
            "ActivityDurationHours as activityDurationHours, ActivityRating as activityRating, " +
            "IsRatingAggregated as isRatingAggregated " +
            "FROM tbl_VolunteerActivity WHERE ActivityID = #{activityId}")
    VolunteerActivity selectById(@Param("activityId") String activityId);

    /**
     * 查询所有志愿活动信息 (可带条件过滤，SQL defined in XML)
     * @param volunteerActivity 包含过滤条件的志愿活动对象
     * @return 志愿活动列表
     */
    List<VolunteerActivity> selectAll(VolunteerActivity volunteerActivity);

    /**
     * 根据组织ID查询该组织发布的所有志愿活动 (SQL defined in XML)
     * @param orgId 组织ID
     * @return 志愿活动列表
     */
    List<VolunteerActivity> selectByOrgId(@Param("orgId") String orgId);

    /**
     * 根据活动状态查询志愿活动 (SQL defined in XML)
     * @param activityStatus 活动状态
     * @return 志愿活动列表
     */
    List<VolunteerActivity> selectByStatus(@Param("activityStatus") String activityStatus);

    /**
     * 更新活动状态
     * @param activityId 活动ID
     * @param activityStatus 新的活动状态
     * @param reviewerAdminId 审核管理员ID (如果适用)
     * @return 影响行数
     */
    int updateActivityStatus(@Param("activityId") String activityId,
                             @Param("activityStatus") String activityStatus,
                             @Param("reviewerAdminId") String reviewerAdminId);

    /**
     * 增减活动录取人数
     * @param activityId 活动ID
     * @param countChange 录取人数的变化量 (正数表示增加，负数表示减少)
     * @return 影响行数
     */
    @Update("UPDATE tbl_VolunteerActivity SET AcceptedCount = AcceptedCount + #{countChange} WHERE ActivityID = #{activityId}")
    int updateAcceptedCount(@Param("activityId") String activityId, @Param("countChange") int countChange);
}