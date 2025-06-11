// 文件路径: com/example/springboot/service/VolunteerActivityApplicationService.java (新建或更新此文件)

package com.example.springboot.service;

import com.example.springboot.entity.VolunteerActivityApplication;
import com.example.springboot.exception.CustomException;
import com.example.springboot.mapper.VolunteerActivityApplicationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class VolunteerActivityApplicationService {

    @Autowired
    private VolunteerActivityApplicationMapper applicationMapper;

    /**
     * 直接调用Mapper的JOIN查询方法
     */
    public List<Map<String, Object>> findMyApplicationsWithDetails(String volunteerId) {
        return applicationMapper.selectMyApplicationDetails(volunteerId);
    }

    /**
     * 志愿者撤回报名申请
     */
    public int withdrawApplication(String applicationId) {
        // 根据您的数据库结构，撤回状态为 '取消报名'
        return applicationMapper.updateStatus(applicationId, "取消报名");
    }

    /**
     * 【重要】补全缺失的创建报名申请方法
     * @param volunteerId 志愿者ID
     * @param activityId 活动ID
     * @param intendedPositionId 意向岗位ID
     */
    public void createApplication(String volunteerId, String activityId, String intendedPositionId) {
        // 1. 参数校验
        if (!StringUtils.hasText(volunteerId) || !StringUtils.hasText(activityId) || !StringUtils.hasText(intendedPositionId)) {
            throw new CustomException("400", "请求参数不完整（志愿者ID、活动ID、岗位ID均为必填）");
        }

        // 2. 检查是否已存在有效的申请（防止重复提交）
        // 该方法依赖于 VolunteerActivityApplicationMapper.java 中的 checkExistingApplicationByPositionAndStatus 方法
        int existingCount = applicationMapper.checkExistingApplicationByPositionAndStatus(
                volunteerId,
                activityId,
                null, // 检查活动级别，不限制于特定岗位
                Arrays.asList("待审核", "已通过") // 检查的状态列表
        );

        if (existingCount > 0) {
            throw new CustomException("409", "您已报名该活动，请勿重复提交。");
        }

        // 3. 创建并保存申请实体
        VolunteerActivityApplication application = new VolunteerActivityApplication();
        application.setApplicationId(UUID.randomUUID().toString().replace("-", "").substring(0, 15)); // 生成唯一ID
        application.setVolunteerId(volunteerId);
        application.setActivityId(activityId);
        application.setIntendedPositionId(intendedPositionId);
        application.setApplicationStatus("待审核"); // 初始状态

        // 调用Mapper将报名信息插入数据库
        applicationMapper.insert(application);
    }
}