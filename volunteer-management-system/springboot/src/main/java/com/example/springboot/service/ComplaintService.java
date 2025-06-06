package com.example.springboot.service;

import com.example.springboot.entity.Complaint;
import com.example.springboot.exception.CustomException;
import com.example.springboot.mapper.ComplaintMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils; // For StringUtils.hasText

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class ComplaintService {

    @Resource
    private ComplaintMapper complaintMapper;

    private static final List<String> VALID_COMPLAINT_TYPES = Arrays.asList(
            "服务质量", "行为不当", "信息虚假", "活动违规", "其他"
    );
    private static final List<String> VALID_PROCESSING_STATUSES = Arrays.asList(
            "未处理", "转应诉", "处理中", "已应诉", "仲裁中", "已仲裁", "已退回"
    );
    private static final List<String> VALID_VISIT_RESULTS = Arrays.asList(
            "满意", "不满意"
    );

    /**
     * 提交新的投诉
     * @param complaint 投诉对象
     * @throws CustomException if validation fails
     */
    @Transactional
    public void submitComplaint(Complaint complaint) throws CustomException {
        if (complaint == null || !StringUtils.hasText(complaint.getComplaintId())) {
            throw new CustomException("投诉ID不能为空", "400");
        }
        if (!StringUtils.hasText(complaint.getComplainantId())) {
            throw new CustomException("投诉人ID不能为空", "400");
        }
        if (!StringUtils.hasText(complaint.getComplaintTargetId())) {
            throw new CustomException("投诉对象ID不能为空", "400");
        }
        if (!StringUtils.hasText(complaint.getComplaintContent())) {
            throw new CustomException("投诉内容不能为空", "400");
        }
        if (complaint.getComplaintType() == null || !VALID_COMPLAINT_TYPES.contains(complaint.getComplaintType())) {
            complaint.setComplaintType("其他"); // Default if invalid or not provided
        }
        // ComplaintTime and ProcessingStatus have DB defaults
        if (complaint.getVisitResult() != null && !VALID_VISIT_RESULTS.contains(complaint.getVisitResult())){
            complaint.setVisitResult("满意"); // Default if invalid or not provided
        } else if (complaint.getVisitResult() == null) {
            complaint.setVisitResult("满意"); // Default if not provided
        }


        complaintMapper.insert(complaint);
    }

    /**
     * 更新投诉信息 (通常由管理员操作，如更新处理状态、结果等)
     * @param complaint 投诉对象
     * @throws CustomException if validation fails or complaint not found
     */
    @Transactional
    public void updateComplaintDetails(Complaint complaint) throws CustomException {
        if (complaint == null || !StringUtils.hasText(complaint.getComplaintId())) {
            throw new CustomException("投诉ID不能为空以进行更新", "400");
        }
        Complaint existingComplaint = complaintMapper.selectById(complaint.getComplaintId());
        if (existingComplaint == null) {
            throw new CustomException("未找到要更新的投诉，ID: " + complaint.getComplaintId(), "404");
        }

        // Validate specific fields if they are being updated
        if (StringUtils.hasText(complaint.getProcessingStatus()) && !VALID_PROCESSING_STATUSES.contains(complaint.getProcessingStatus())) {
            throw new CustomException("无效的处理状态: " + complaint.getProcessingStatus(), "400");
        }
        if (StringUtils.hasText(complaint.getVisitResult()) && !VALID_VISIT_RESULTS.contains(complaint.getVisitResult())) {
            throw new CustomException("无效的回访结果: " + complaint.getVisitResult(), "400");
        }
        if (complaint.getArbitrationRound() != null && (complaint.getArbitrationRound() < 1 || complaint.getArbitrationRound() > 2)) {
            throw new CustomException("无效的仲裁轮次", "400");
        }

        // Ensure latest processing time is set if status or result changes
        if (StringUtils.hasText(complaint.getProcessingStatus()) || StringUtils.hasText(complaint.getProcessingResult())) {
            complaint.setLatestProcessingTime(new Date());
        }

        complaintMapper.updateById(complaint); // Or a more specific update method like updateProcessingDetails
    }


    /**
     * 管理员处理投诉（更新状态，结果，处理人等）
     * @param complaintId 投诉ID
     * @param processingStatus 新的处理状态
     * @param processingResult 处理结果
     * @param handlerAdminId 处理管理员ID
     * @throws CustomException
     */
    @Transactional
    public void processComplaint(String complaintId, String processingStatus, String processingResult, String handlerAdminId) throws CustomException {
        if (!StringUtils.hasText(complaintId)) {
            throw new CustomException("投诉ID不能为空", "400");
        }
        Complaint complaint = complaintMapper.selectById(complaintId);
        if (complaint == null) {
            throw new CustomException("未找到投诉: " + complaintId, "404");
        }
        if (!StringUtils.hasText(processingStatus) || !VALID_PROCESSING_STATUSES.contains(processingStatus)) {
            throw new CustomException("无效的处理状态", "400");
        }
        if (!StringUtils.hasText(handlerAdminId)) {
            throw new CustomException("处理人ID不能为空", "400");
        }

        complaint.setProcessingStatus(processingStatus);
        complaint.setProcessingResult(processingResult); // Can be null or empty
        complaint.setHandlerAdminId(handlerAdminId);
        complaint.setLatestProcessingTime(new Date());

        complaintMapper.updateProcessingDetails(complaint);
    }


    /**
     * 根据投诉ID删除投诉
     * @param complaintId 投诉ID
     * @throws CustomException if validation fails or complaint not found
     */
    @Transactional
    public void deleteComplaintById(String complaintId) throws CustomException {
        if (!StringUtils.hasText(complaintId)) {
            throw new CustomException("投诉ID不能为空", "400");
        }
        Complaint existingComplaint = complaintMapper.selectById(complaintId);
        if (existingComplaint == null) {
            throw new CustomException("未找到要删除的投诉，ID: " + complaintId, "404");
        }
        complaintMapper.deleteById(complaintId);
    }

    /**
     * 根据投诉ID查询投诉信息
     * @param complaintId 投诉ID
     * @return 投诉对象
     * @throws CustomException if validation fails
     */
    public Complaint getComplaintById(String complaintId) throws CustomException {
        if (!StringUtils.hasText(complaintId)) {
            throw new CustomException("投诉ID不能为空", "400");
        }
        return complaintMapper.selectById(complaintId);
    }

    /**
     * 查询所有投诉 (可带条件过滤)
     * @param complaint 包含过滤条件的投诉对象
     * @return 投诉列表
     */
    public List<Complaint> getAllComplaints(Complaint complaint) {
        return complaintMapper.selectAll(complaint);
    }

    /**
     * 分页查询投诉信息
     * @param complaint 包含过滤条件的投诉对象
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页后的投诉列表
     */
    public PageInfo<Complaint> getComplaintPage(Complaint complaint, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Complaint> list = complaintMapper.selectAll(complaint);
        return PageInfo.of(list);
    }

    /**
     * 根据发起人ID查询投诉
     * @param complainantId 发起人ID
     * @return 投诉列表
     * @throws CustomException if validation fails
     */
    public List<Complaint> getComplaintsByComplainantId(String complainantId) throws CustomException {
        if (!StringUtils.hasText(complainantId)) {
            throw new CustomException("发起人ID不能为空", "400");
        }
        return complaintMapper.selectByComplainantId(complainantId);
    }

    /**
     * 根据投诉对象ID查询投诉
     * @param complaintTargetId 投诉对象ID
     * @return 投诉列表
     * @throws CustomException if validation fails
     */
    public List<Complaint> getComplaintsByComplaintTargetId(String complaintTargetId) throws CustomException {
        if (!StringUtils.hasText(complaintTargetId)) {
            throw new CustomException("投诉对象ID不能为空", "400");
        }
        return complaintMapper.selectByComplaintTargetId(complaintTargetId);
    }

    /**
     * 根据处理状态查询投诉
     * @param processingStatus 处理状态
     * @return 投诉列表
     * @throws CustomException if validation fails
     */
    public List<Complaint> getComplaintsByProcessingStatus(String processingStatus) throws CustomException {
        if (!StringUtils.hasText(processingStatus) || !VALID_PROCESSING_STATUSES.contains(processingStatus)) {
            throw new CustomException("无效或空的处理状态", "400");
        }
        return complaintMapper.selectByProcessingStatus(processingStatus);
    }

    /**
     * 根据投诉类型查询投诉
     * @param complaintType 投诉类型
     * @return 投诉列表
     * @throws CustomException if validation fails
     */
    public List<Complaint> getComplaintsByComplaintType(String complaintType) throws CustomException {
        if (!StringUtils.hasText(complaintType) || !VALID_COMPLAINT_TYPES.contains(complaintType)) {
            throw new CustomException("无效或空的投诉类型", "400");
        }
        return complaintMapper.selectByComplaintType(complaintType);
    }
}
