package com.example.springboot.mapper;

import com.example.springboot.entity.Position;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface PositionMapper {

    /**
     * 插入新的岗位信息
     * @param position 岗位对象
     * @return 影响行数
     */
    @Insert("INSERT INTO tbl_Position (PositionID, PositionName, ActivityID, PositionServiceHours, RequiredVolunteers, RecruitedVolunteers) " +
            "VALUES (#{positionId,jdbcType=CHAR}, #{positionName,jdbcType=NVARCHAR}, #{activityId,jdbcType=CHAR}, " +
            "#{positionServiceHours,jdbcType=INTEGER}, #{requiredVolunteers,jdbcType=INTEGER}, #{recruitedVolunteers,jdbcType=INTEGER})")
    int insert(Position position);

    /**
     * 根据岗位ID更新岗位信息
     * (SQL defined in XML for potentially more complex updates)
     * @param position 岗位对象
     * @return 影响行数
     */
    int updateById(Position position);

    /**
     * 根据岗位ID删除岗位信息
     * @param positionId 岗位ID
     * @return 影响行数
     */
    @Delete("DELETE FROM tbl_Position WHERE PositionID = #{positionId,jdbcType=CHAR}")
    int deleteById(@Param("positionId") String positionId);

    /**
     * 根据岗位ID查询岗位信息
     * @param positionId 岗位ID
     * @return 岗位对象
     */
    @Select("SELECT PositionID as positionId, PositionName as positionName, ActivityID as activityId, " +
            "PositionServiceHours as positionServiceHours, RequiredVolunteers as requiredVolunteers, RecruitedVolunteers as recruitedVolunteers " +
            "FROM tbl_Position WHERE PositionID = #{positionId,jdbcType=CHAR}")
    Position selectById(@Param("positionId") String positionId);

    /**
     * 根据活动ID查询该活动下的所有岗位
     * @param activityId 活动ID
     * @return 岗位列表
     */
    @Select("SELECT PositionID as positionId, PositionName as positionName, ActivityID as activityId, " +
            "PositionServiceHours as positionServiceHours, RequiredVolunteers as requiredVolunteers, RecruitedVolunteers as recruitedVolunteers " +
            "FROM tbl_Position WHERE ActivityID = #{activityId,jdbcType=CHAR} ORDER BY PositionName")
    List<Position> selectByActivityId(@Param("activityId") String activityId);

    /**
     * 查询所有岗位信息 (可带条件过滤，SQL defined in XML)
     * @param position 包含过滤条件的岗位对象
     * @return 岗位列表
     */
    List<Position> selectAll(Position position);

    /**
     * 更新指定岗位的已招募人数
     * @param positionId 岗位ID
     * @param countChange 已招募人数的变化量 (正数表示增加，负数表示减少)
     * @return 影响行数
     */
    @Update("UPDATE tbl_Position SET RecruitedVolunteers = RecruitedVolunteers + #{countChange,jdbcType=INTEGER} " +
            "WHERE PositionID = #{positionId,jdbcType=CHAR}")
    int updateRecruitedVolunteers(@Param("positionId") String positionId, @Param("countChange") int countChange);

    /**
     * 根据活动ID删除该活动下的所有岗位
     * @param activityId 活动ID
     * @return 影响行数
     */
    @Delete("DELETE FROM tbl_Position WHERE ActivityID = #{activityId,jdbcType=CHAR}")
    int deleteByActivityId(@Param("activityId") String activityId);
}
