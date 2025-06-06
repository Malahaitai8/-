package com.example.springboot.mapper;

import com.example.springboot.entity.VolunteerTraining;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface VolunteerTrainingMapper {

    /**
     * 插入新的志愿培训信息 (SQL defined in XML)
     * @param volunteerTraining 志愿培训对象
     * @return 影响行数
     */
    int insert(VolunteerTraining volunteerTraining);

    /**
     * 根据培训ID更新志愿培训信息 (SQL defined in XML)
     * @param volunteerTraining 志愿培训对象
     * @return 影响行数
     */
    int updateById(VolunteerTraining volunteerTraining);

    /**
     * 根据培训ID删除志愿培训信息
     * @param trainingId 培训ID
     * @return 影响行数
     */
    @Delete("DELETE FROM tbl_VolunteerTraining WHERE TrainingID = #{trainingId}")
    int deleteById(@Param("trainingId") String trainingId);

    /**
     * 根据培训ID查询志愿培训信息
     * @param trainingId 培训ID
     * @return 志愿培训对象
     */
    @Select("SELECT " +
            "TrainingID as trainingId, OrgID as orgId, TrainingName as trainingName, Theme as theme, " +
            "StartTime as startTime, EndTime as endTime, Location as location, " +
            "RecruitmentCount as recruitmentCount, TrainingStatus as trainingStatus, CreationTime as creationTime, " +
            "ReviewerAdminID as reviewerAdminId, ContactPersonPhone as contactPersonPhone, " +
            "TrainingRating as trainingRating, IsRatingAggregated as isRatingAggregated " +
            "FROM tbl_VolunteerTraining WHERE TrainingID = #{trainingId}")
    VolunteerTraining selectById(@Param("trainingId") String trainingId);

    /**
     * 查询所有志愿培训信息 (可带条件过滤，SQL defined in XML)
     * @param volunteerTraining 包含过滤条件的志愿培训对象
     * @return 志愿培训列表
     */
    List<VolunteerTraining> selectAll(VolunteerTraining volunteerTraining);

    /**
     * 根据组织ID查询该组织发布的所有志愿培训 (SQL defined in XML)
     * @param orgId 组织ID
     * @return 志愿培训列表
     */
    List<VolunteerTraining> selectByOrgId(@Param("orgId") String orgId);

    /**
     * 根据培训状态查询志愿培训 (SQL defined in XML)
     * @param trainingStatus 培训状态
     * @return 志愿培训列表
     */
    List<VolunteerTraining> selectByStatus(@Param("trainingStatus") String trainingStatus);

    /**
     * 根据培训主题查询志愿培训 (SQL defined in XML)
     * @param theme 培训主题
     * @return 志愿培训列表
     */
    List<VolunteerTraining> selectByTheme(@Param("theme") String theme);


    /**
     * 更新培训状态
     * @param trainingId 培训ID
     * @param trainingStatus 新的培训状态
     * @param reviewerAdminId 审核管理员ID (如果适用)
     * @return 影响行数
     */
    int updateTrainingStatus(@Param("trainingId") String trainingId,
                             @Param("trainingStatus") String trainingStatus,
                             @Param("reviewerAdminId") String reviewerAdminId);
}
