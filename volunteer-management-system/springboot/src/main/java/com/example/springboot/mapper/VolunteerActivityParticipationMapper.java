package com.example.springboot.mapper;

import com.example.springboot.entity.VolunteerActivityParticipation;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

public interface VolunteerActivityParticipationMapper {

    /**
     * 插入新的活动参与记录
     * @param participation 参与记录对象
     * @return 影响行数
     */
    @Insert("INSERT INTO tbl_VolunteerActivityParticipation (VolunteerID, ActivityID, ActualPositionID, IsCheckedIn, VolunteerToOrgRating, OrgToVolunteerRating) " +
            "VALUES (#{volunteerId,jdbcType=CHAR}, #{activityId,jdbcType=CHAR}, #{actualPositionId,jdbcType=CHAR}, " +
            "#{isCheckedIn,jdbcType=NCHAR}, #{volunteerToOrgRating,jdbcType=INTEGER}, #{orgToVolunteerRating,jdbcType=INTEGER})")
    int insert(VolunteerActivityParticipation participation);

    /**
     * 更新活动参与记录 (例如：签到状态，评分)
     * (SQL defined in XML for flexibility)
     * @param participation 参与记录对象
     * @return 影响行数
     */
    int updateByPrimaryKey(VolunteerActivityParticipation participation);

    /**
     * 根据主键删除活动参与记录 (物理删除，谨慎使用)
     * @param volunteerId 志愿者ID
     * @param actualPositionId 实际岗位ID - 注意：主键是 VolunteerID, ActualPositionID
     * @return 影响行数
     */
    @Delete("DELETE FROM tbl_VolunteerActivityParticipation WHERE VolunteerID = #{volunteerId,jdbcType=CHAR} AND ActualPositionID = #{actualPositionId,jdbcType=CHAR}")
    int deleteByPrimaryKey(@Param("volunteerId") String volunteerId, @Param("actualPositionId") String actualPositionId);

    /**
     * 根据主键查询活动参与记录
     * @param volunteerId 志愿者ID
     * @param actualPositionId 实际岗位ID
     * @return 参与记录对象
     */
    @Select("SELECT VolunteerID as volunteerId, ActivityID as activityId, ActualPositionID as actualPositionId, " +
            "IsCheckedIn as isCheckedIn, VolunteerToOrgRating as volunteerToOrgRating, OrgToVolunteerRating as orgToVolunteerRating " +
            "FROM tbl_VolunteerActivityParticipation " +
            "WHERE VolunteerID = #{volunteerId,jdbcType=CHAR} AND ActualPositionID = #{actualPositionId,jdbcType=CHAR}")
    VolunteerActivityParticipation selectByPrimaryKey(@Param("volunteerId") String volunteerId, @Param("actualPositionId") String actualPositionId);

    /**
     * 根据志愿者ID查询其所有活动参与记录
     * @param volunteerId 志愿者ID
     * @return 参与记录列表
     */
    List<VolunteerActivityParticipation> selectByVolunteerId(@Param("volunteerId") String volunteerId);

    /**
     * 根据活动ID查询该活动的所有参与记录
     * @param activityId 活动ID
     * @return 参与记录列表
     */
    List<VolunteerActivityParticipation> selectByActivityId(@Param("activityId") String activityId);

    /**
     * 根据活动ID和岗位ID查询参与记录
     * @param activityId 活动ID
     * @param actualPositionId 实际岗位ID
     * @return 参与记录列表
     */
    List<VolunteerActivityParticipation> selectByActivityAndPosition(@Param("activityId") String activityId, @Param("actualPositionId") String actualPositionId);

    /**
     * 查询所有活动参与记录 (可带条件过滤，SQL defined in XML)
     * @param filterCriteria 过滤条件
     * @return 参与记录列表
     */
    List<VolunteerActivityParticipation> selectAll(VolunteerActivityParticipation filterCriteria);

    /**
     * 更新签到状态
     * @param volunteerId 志愿者ID
     * @param actualPositionId 实际岗位ID
     * @param isCheckedIn 签到状态 ('是' 或 '否')
     * @return 影响行数
     */
    @Update("UPDATE tbl_VolunteerActivityParticipation SET IsCheckedIn = #{isCheckedIn,jdbcType=NCHAR} " +
            "WHERE VolunteerID = #{volunteerId,jdbcType=CHAR} AND ActualPositionID = #{actualPositionId,jdbcType=CHAR}")
    int updateCheckInStatus(@Param("volunteerId") String volunteerId,
                            @Param("actualPositionId") String actualPositionId,
                            @Param("isCheckedIn") String isCheckedIn);

    /**
     * 更新志愿者对组织的评分
     * @param volunteerId 志愿者ID
     * @param actualPositionId 实际岗位ID
     * @param volunteerToOrgRating 志愿者给组织的评分
     * @return 影响行数
     */
    @Update("UPDATE tbl_VolunteerActivityParticipation SET VolunteerToOrgRating = #{volunteerToOrgRating,jdbcType=INTEGER} " +
            "WHERE VolunteerID = #{volunteerId,jdbcType=CHAR} AND ActualPositionID = #{actualPositionId,jdbcType=CHAR}")
    int updateVolunteerToOrgRating(@Param("volunteerId") String volunteerId,
                                   @Param("actualPositionId") String actualPositionId,
                                   @Param("volunteerToOrgRating") Integer volunteerToOrgRating);

    /**
     * 更新组织对志愿者的评分
     * @param volunteerId 志愿者ID
     * @param actualPositionId 实际岗位ID
     * @param orgToVolunteerRating 组织给志愿者的评分
     * @return 影响行数
     */
    @Update("UPDATE tbl_VolunteerActivityParticipation SET OrgToVolunteerRating = #{orgToVolunteerRating,jdbcType=INTEGER} " +
            "WHERE VolunteerID = #{volunteerId,jdbcType=CHAR} AND ActualPositionID = #{actualPositionId,jdbcType=CHAR}")
    int updateOrgToVolunteerRating(@Param("volunteerId") String volunteerId,
                                   @Param("actualPositionId") String actualPositionId,
                                   @Param("orgToVolunteerRating") Integer orgToVolunteerRating);

    /**
     * 获取活动的参与志愿者详细信息
     * @param activityId 活动ID
     * @return 包含志愿者信息的Map列表
     */
    @Select("SELECT " +
            "    p.VolunteerID as volunteerId, " +
            "    p.ActivityID as activityId, " +
            "    p.ActualPositionID as actualPositionId, " +
            "    p.IsCheckedIn as isCheckedIn, " +
            "    p.VolunteerToOrgRating as volunteerToOrgRating, " +
            "    p.OrgToVolunteerRating as orgToVolunteerRating, " +
            "    v.VolunteerName as volunteerName, " +
            "    v.Phone as volunteerPhone, " +
            "    v.Email as volunteerEmail, " +
            "    pos.PositionName as positionName " +
            "FROM tbl_VolunteerActivityParticipation p " +
            "INNER JOIN tbl_Volunteer v ON p.VolunteerID = v.VolunteerID " +
            "INNER JOIN tbl_Position pos ON p.ActualPositionID = pos.PositionID " +
            "WHERE p.ActivityID = #{activityId,jdbcType=CHAR} " +
            "ORDER BY v.VolunteerName")
    List<Map<String, Object>> selectParticipantsWithDetailsByActivityId(@Param("activityId") String activityId);
}
