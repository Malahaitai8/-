package com.example.springboot.mapper;

import com.example.springboot.entity.VolunteerTrainingParticipation;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface VolunteerTrainingParticipationMapper {

    /**
     * 插入新的培训参与记录
     *
     * @param participation 参与记录对象
     * @return 影响行数
     */
    @Insert("INSERT INTO tbl_VolunteerTrainingParticipation (VolunteerID, TrainingID, IsCheckedIn, OrgToVolunteerRating, VolunteerToOrgRating) " +
            "VALUES (#{volunteerId,jdbcType=CHAR}, #{trainingId,jdbcType=CHAR}, #{isCheckedIn,jdbcType=NCHAR}, " +
            "#{orgToVolunteerRating,jdbcType=INTEGER}, #{volunteerToOrgRating,jdbcType=INTEGER})")
    int insert(VolunteerTrainingParticipation participation);

    /**
     * 更新培训参与记录 (例如：签到状态，评分)
     * (SQL defined in XML for flexibility)
     *
     * @param participation 参与记录对象
     * @return 影响行数
     */
    int updateByPrimaryKey(VolunteerTrainingParticipation participation);

    /**
     * 根据主键删除培训参与记录 (物理删除，谨慎使用)
     *
     * @param volunteerId 志愿者ID
     * @param trainingId  培训ID
     * @return 影响行数
     */
    @Delete("DELETE FROM tbl_VolunteerTrainingParticipation WHERE VolunteerID = #{volunteerId,jdbcType=CHAR} AND TrainingID = #{trainingId,jdbcType=CHAR}")
    int deleteByPrimaryKey(@Param("volunteerId") String volunteerId, @Param("trainingId") String trainingId);

    /**
     * 根据主键查询培训参与记录
     *
     * @param volunteerId 志愿者ID
     * @param trainingId  培训ID
     * @return 参与记录对象
     */
    @Select("SELECT VolunteerID as volunteerId, TrainingID as trainingId, IsCheckedIn as isCheckedIn, " +
            "OrgToVolunteerRating as orgToVolunteerRating, VolunteerToOrgRating as volunteerToOrgRating " +
            "FROM tbl_VolunteerTrainingParticipation " +
            "WHERE VolunteerID = #{volunteerId,jdbcType=CHAR} AND TrainingID = #{trainingId,jdbcType=CHAR}")
    VolunteerTrainingParticipation selectByPrimaryKey(@Param("volunteerId") String volunteerId, @Param("trainingId") String trainingId);

    /**
     * 根据志愿者ID查询其所有培训参与记录
     *
     * @param volunteerId 志愿者ID
     * @return 参与记录列表
     */
    List<VolunteerTrainingParticipation> selectByVolunteerId(@Param("volunteerId") String volunteerId);

    /**
     * 根据培训ID查询该培训的所有参与记录
     *
     * @param trainingId 培训ID
     * @return 参与记录列表
     */
    List<VolunteerTrainingParticipation> selectByTrainingId(@Param("trainingId") String trainingId);

    /**
     * 查询所有培训参与记录 (可带条件过滤，SQL defined in XML)
     *
     * @param filterCriteria 过滤条件
     * @return 参与记录列表
     */
    List<VolunteerTrainingParticipation> selectAll(VolunteerTrainingParticipation filterCriteria);

    /**
     * 更新签到状态
     *
     * @param volunteerId 志愿者ID
     * @param trainingId  培训ID
     * @param isCheckedIn 签到状态 ('是' 或 '否')
     * @return 影响行数
     */
    @Update("UPDATE tbl_VolunteerTrainingParticipation SET IsCheckedIn = #{isCheckedIn,jdbcType=NCHAR} " +
            "WHERE VolunteerID = #{volunteerId,jdbcType=CHAR} AND TrainingID = #{trainingId,jdbcType=CHAR}")
    int updateCheckInStatus(@Param("volunteerId") String volunteerId,
                            @Param("trainingId") String trainingId,
                            @Param("isCheckedIn") String isCheckedIn);

    /**
     * 更新组织对志愿者的评分
     *
     * @param volunteerId          志愿者ID
     * @param trainingId           培训ID
     * @param orgToVolunteerRating 组织给志愿者的评分
     * @return 影响行数
     */
    @Update("UPDATE tbl_VolunteerTrainingParticipation SET OrgToVolunteerRating = #{orgToVolunteerRating,jdbcType=INTEGER} " +
            "WHERE VolunteerID = #{volunteerId,jdbcType=CHAR} AND TrainingID = #{trainingId,jdbcType=CHAR}")
    int updateOrgToVolunteerRating(@Param("volunteerId") String volunteerId,
                                   @Param("trainingId") String trainingId,
                                   @Param("orgToVolunteerRating") Integer orgToVolunteerRating);

    /**
     * 更新志愿者对组织的评分
     *
     * @param volunteerId          志愿者ID
     * @param trainingId           培训ID
     * @param volunteerToOrgRating 志愿者给组织的评分
     * @return 影响行数
     */
    @Update("UPDATE tbl_VolunteerTrainingParticipation SET VolunteerToOrgRating = #{volunteerToOrgRating,jdbcType=INTEGER} " +
            "WHERE VolunteerID = #{volunteerId,jdbcType=CHAR} AND TrainingID = #{trainingId,jdbcType=CHAR}")
    int updateVolunteerToOrgRating(@Param("volunteerId") String volunteerId,
                                   @Param("trainingId") String trainingId,
                                   @Param("volunteerToOrgRating") Integer volunteerToOrgRating);


    /**
     * 更新志愿者对培训的评分
     *
     * @param volunteerId 志愿者ID
     * @param trainingId  培训ID
     * @param rating      评分
     * @return 影响行数
     */
    @Update("UPDATE tbl_VolunteerTrainingParticipation " +
            "SET VolunteerToOrgRating = #{rating} " +
            "WHERE VolunteerID = #{volunteerId} AND TrainingID = #{trainingId}")
    int updateVolunteerRating(@Param("volunteerId") String volunteerId,
                              @Param("trainingId") String trainingId,
                              @Param("rating") Integer rating);
}
