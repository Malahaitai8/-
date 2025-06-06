package com.example.springboot.service;

import com.example.springboot.entity.VolunteerTraining;
import com.example.springboot.exception.CustomException;
import com.example.springboot.mapper.VolunteerTrainingMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class VolunteerTrainingService {

    @Resource
    private VolunteerTrainingMapper volunteerTrainingMapper;

    // Valid training statuses from your table definition
    private static final List<String> VALID_TRAINING_STATUSES = Arrays.asList(
            "待审核", "审核通过", "审核不通过", "进行中", "已结束", "已停用"
    );

    /**
     * 添加新的志愿培训
     * @param volunteerTraining 志愿培训对象
     * @throws CustomException if validation fails
     */
    @Transactional
    public void addTraining(VolunteerTraining volunteerTraining) throws CustomException {
        if (volunteerTraining == null || volunteerTraining.getTrainingId() == null || volunteerTraining.getOrgId() == null) {
            throw new CustomException("培训ID和组织ID不能为空", "400");
        }
        if (volunteerTraining.getTrainingName() == null || volunteerTraining.getTrainingName().trim().isEmpty()) {
            throw new CustomException("培训名称不能为空", "400");
        }
        if (volunteerTraining.getTheme() == null || volunteerTraining.getTheme().trim().isEmpty()) {
            throw new CustomException("培训主题不能为空", "400");
        }
        if (volunteerTraining.getStartTime() == null || volunteerTraining.getEndTime() == null) {
            throw new CustomException("培训开始和结束时间不能为空", "400");
        }
        if (volunteerTraining.getStartTime().after(volunteerTraining.getEndTime())) {
            throw new CustomException("培训开始时间不能晚于结束时间", "400");
        }
        if (volunteerTraining.getRecruitmentCount() == null || volunteerTraining.getRecruitmentCount() <= 0) {
            throw new CustomException("招募人数必须大于0", "400");
        }
        if (volunteerTraining.getTrainingStatus() == null || !VALID_TRAINING_STATUSES.contains(volunteerTraining.getTrainingStatus())) {
            volunteerTraining.setTrainingStatus("待审核"); // Defaulting
        }
        if (volunteerTraining.getContactPersonPhone() == null || volunteerTraining.getContactPersonPhone().trim().isEmpty()){
            throw new CustomException("负责人联系方式不能为空", "400");
        }
        // CreationTime defaults to GETDATE() in DB.
        volunteerTrainingMapper.insert(volunteerTraining);
    }

    /**
     * 根据培训ID更新志愿培训信息
     * @param volunteerTraining 志愿培训对象
     * @throws CustomException if validation fails or training not found
     */
    @Transactional
    public void updateTraining(VolunteerTraining volunteerTraining) throws CustomException {
        if (volunteerTraining == null || volunteerTraining.getTrainingId() == null) {
            throw new CustomException("培训ID不能为空以进行更新", "400");
        }
        VolunteerTraining existingTraining = volunteerTrainingMapper.selectById(volunteerTraining.getTrainingId());
        if (existingTraining == null) {
            throw new CustomException("未找到要更新的培训，ID: " + volunteerTraining.getTrainingId(), "404");
        }

        if (volunteerTraining.getStartTime() != null && volunteerTraining.getEndTime() != null &&
            volunteerTraining.getStartTime().after(volunteerTraining.getEndTime())) {
            throw new CustomException("培训开始时间不能晚于结束时间", "400");
        }
        if (volunteerTraining.getRecruitmentCount() != null && volunteerTraining.getRecruitmentCount() <= 0) {
            throw new CustomException("招募人数必须大于0", "400");
        }
        if (volunteerTraining.getTrainingStatus() != null && !VALID_TRAINING_STATUSES.contains(volunteerTraining.getTrainingStatus())) {
            throw new CustomException("无效的培训状态: " + volunteerTraining.getTrainingStatus(), "400");
        }

        volunteerTrainingMapper.updateById(volunteerTraining);
    }

    /**
     * 根据培训ID删除志愿培训
     * @param trainingId 培训ID
     * @throws CustomException if validation fails or training not found
     */
    @Transactional
    public void deleteTrainingById(String trainingId) throws CustomException {
        if (trainingId == null || trainingId.trim().isEmpty()) {
            throw new CustomException("培训ID不能为空", "400");
        }
        VolunteerTraining existingTraining = volunteerTrainingMapper.selectById(trainingId);
        if (existingTraining == null) {
            throw new CustomException("未找到要删除的培训，ID: " + trainingId, "404");
        }
        volunteerTrainingMapper.deleteById(trainingId);
    }

    /**
     * 根据培训ID查询志愿培训信息
     * @param trainingId 培训ID
     * @return 志愿培训对象
     * @throws CustomException if validation fails
     */
    public VolunteerTraining getTrainingById(String trainingId) throws CustomException {
        if (trainingId == null || trainingId.trim().isEmpty()) {
            throw new CustomException("培训ID不能为空", "400");
        }
        return volunteerTrainingMapper.selectById(trainingId);
    }

    /**
     * 查询所有志愿培训 (可带条件过滤)
     * @param volunteerTraining 包含过滤条件的志愿培训对象
     * @return 志愿培训列表
     */
    public List<VolunteerTraining> getAllTrainings(VolunteerTraining volunteerTraining) {
        return volunteerTrainingMapper.selectAll(volunteerTraining);
    }

    /**
     * 分页查询志愿培训信息
     * @param volunteerTraining 包含过滤条件的志愿培训对象
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页后的志愿培训列表
     */
    public PageInfo<VolunteerTraining> getTrainingPage(VolunteerTraining volunteerTraining, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<VolunteerTraining> list = volunteerTrainingMapper.selectAll(volunteerTraining);
        return PageInfo.of(list);
    }

    /**
     * 根据组织ID查询培训
     * @param orgId 组织ID
     * @return 志愿培训列表
     * @throws CustomException if validation fails
     */
    public List<VolunteerTraining> getTrainingsByOrgId(String orgId) throws CustomException {
        if (orgId == null || orgId.trim().isEmpty()) {
            throw new CustomException("组织ID不能为空", "400");
        }
        return volunteerTrainingMapper.selectByOrgId(orgId);
    }

    /**
     * 根据培训状态查询培训
     * @param status 培训状态
     * @return 志愿培训列表
     * @throws CustomException if validation fails
     */
    public List<VolunteerTraining> getTrainingsByStatus(String status) throws CustomException {
        if (status == null || status.trim().isEmpty() || !VALID_TRAINING_STATUSES.contains(status)) {
            throw new CustomException("无效或空的培训状态", "400");
        }
        return volunteerTrainingMapper.selectByStatus(status);
    }

    /**
     * 根据培训主题查询培训
     * @param theme 培训主题
     * @return 志愿培训列表
     * @throws CustomException if validation fails
     */
    public List<VolunteerTraining> getTrainingsByTheme(String theme) throws CustomException {
        if (theme == null || theme.trim().isEmpty()) {
            throw new CustomException("培训主题不能为空", "400");
        }
        return volunteerTrainingMapper.selectByTheme(theme);
    }

    /**
     * 审核/更新培训状态
     * @param trainingId 培训ID
     * @param newStatus 新的状态
     * @param reviewerAdminId 审核员ID (可以为null)
     * @throws CustomException if validation fails or training not found
     */
    @Transactional
    public void reviewTraining(String trainingId, String newStatus, String reviewerAdminId) throws CustomException {
        if (trainingId == null || trainingId.trim().isEmpty()) {
            throw new CustomException("培训ID不能为空", "400");
        }
        if (newStatus == null || !VALID_TRAINING_STATUSES.contains(newStatus)) {
            throw new CustomException("无效的培训状态: " + newStatus, "400");
        }
        VolunteerTraining training = volunteerTrainingMapper.selectById(trainingId);
        if (training == null) {
            throw new CustomException("未找到培训: " + trainingId, "404");
        }
        volunteerTrainingMapper.updateTrainingStatus(trainingId, newStatus, reviewerAdminId);
    }
}
