package com.example.springboot.mapper;

import com.example.springboot.entity.Complaint;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface ComplaintMapper {

    /**
     * 插入新的投诉信息 (SQL defined in XML)
     * @param complaint 投诉对象
     * @return 影响行数
     */
    int insert(Complaint complaint);

    /**
     * 根据投诉ID更新投诉信息 (SQL defined in XML)
     * @param complaint 投诉对象
     * @return 影响行数
     */
    int updateById(Complaint complaint);

    /**
     * 根据投诉ID删除投诉信息
     * @param complaintId 投诉ID
     * @return 影响行数
     */
    @Delete("DELETE FROM tbl_Complaint WHERE ComplaintID = #{complaintId}")
    int deleteById(@Param("complaintId") String complaintId);

    /**
     * 根据投诉ID查询投诉信息
     * @param complaintId 投诉ID
     * @return 投诉对象
     */
    @Select("SELECT * FROM tbl_Complaint WHERE ComplaintID = #{complaintId}")
    Complaint selectById(@Param("complaintId") String complaintId);

    /**
     * 查询所有投诉信息 (可带条件过滤，SQL defined in XML)
     * @param complaint 包含过滤条件的投诉对象
     * @return 投诉列表
     */
    List<Complaint> selectAll(Complaint complaint);

    /**
     * 根据发起人ID查询投诉
     * @param complainantId 发起人ID
     * @return 投诉列表
     */
    List<Complaint> selectByComplainantId(@Param("complainantId") String complainantId);

    /**
     * 根据投诉对象ID查询投诉
     * @param complaintTargetId 投诉对象ID
     * @return 投诉列表
     */
    List<Complaint> selectByComplaintTargetId(@Param("complaintTargetId") String complaintTargetId);

    /**
     * 根据处理状态查询投诉
     * @param processingStatus 处理状态
     * @return 投诉列表
     */
    List<Complaint> selectByProcessingStatus(@Param("processingStatus") String processingStatus);

    /**
     * 根据投诉类型查询投诉
     * @param complaintType 投诉类型
     * @return 投诉列表
     */
    List<Complaint> selectByComplaintType(@Param("complaintType") String complaintType);

    /**
     * 更新投诉处理状态和处理结果等信息 (SQL defined in XML)
     * @param complaint 包含更新信息的投诉对象
     * @return 影响行数
     */
    int updateProcessingDetails(Complaint complaint);
}
