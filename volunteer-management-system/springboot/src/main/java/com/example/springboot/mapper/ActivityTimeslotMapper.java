package com.example.springboot.mapper;

import com.example.springboot.entity.ActivityTimeslot;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface ActivityTimeslotMapper {

    /**
     * 插入新的活动时段信息
     * @param timeslot 活动时段对象
     * @return 影响行数
     */
    @Insert("INSERT INTO tbl_ActivityTimeslot (EventID, StartTime, EndTime) " +
            "VALUES (#{eventId,jdbcType=CHAR}, #{startTime,jdbcType=TIMESTAMP}, #{endTime,jdbcType=TIMESTAMP})")
    int insert(ActivityTimeslot timeslot);

    /**
     * 根据时段ID更新活动时段信息
     * @param timeslot 活动时段对象
     * @return 影响行数
     */
    @Update("UPDATE tbl_ActivityTimeslot SET EventID = #{eventId,jdbcType=CHAR}, StartTime = #{startTime,jdbcType=TIMESTAMP}, EndTime = #{endTime,jdbcType=TIMESTAMP} " +
            "WHERE TimeslotID = #{timeslotId,jdbcType=CHAR}")
    int updateById(ActivityTimeslot timeslot);

    /**
     * 根据时段ID删除活动时段信息
     * @param timeslotId 时段ID
     * @return 影响行数
     */
    @Delete("DELETE FROM tbl_ActivityTimeslot WHERE TimeslotID = #{timeslotId,jdbcType=CHAR}")
    int deleteById(@Param("timeslotId") String timeslotId);

    /**
     * 根据时段ID查询活动时段信息
     * @param timeslotId 时段ID
     * @return 活动时段对象
     */
    @Select("SELECT TimeslotID as timeslotId, EventID as eventId, StartTime as startTime, EndTime as endTime " +
            "FROM tbl_ActivityTimeslot WHERE TimeslotID = #{timeslotId,jdbcType=CHAR}")
    ActivityTimeslot selectById(@Param("timeslotId") String timeslotId);

    /**
     * 根据事件ID查询该事件的所有时段
     * @param eventId 事件ID (例如活动ID或培训ID)
     * @return 时段列表
     */
    @Select("SELECT TimeslotID as timeslotId, EventID as eventId, StartTime as startTime, EndTime as endTime " +
            "FROM tbl_ActivityTimeslot WHERE EventID = #{eventId,jdbcType=CHAR} ORDER BY StartTime")
    List<ActivityTimeslot> selectByEventId(@Param("eventId") String eventId);

    /**
     * 删除指定事件ID下的所有时段
     * @param eventId 事件ID
     * @return 影响行数
     */
    @Delete("DELETE FROM tbl_ActivityTimeslot WHERE EventID = #{eventId,jdbcType=CHAR}")
    int deleteByEventId(@Param("eventId") String eventId);

    /**
     * 查询所有活动时段 (可用于后台管理，谨慎使用，数据量可能较大)
     * (SQL defined in XML for potential dynamic filtering)
     * @param filterCriteria 过滤条件
     * @return 时段列表
     */
    List<ActivityTimeslot> selectAll(ActivityTimeslot filterCriteria);
}
