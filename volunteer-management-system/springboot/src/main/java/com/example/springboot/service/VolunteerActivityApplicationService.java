// 文件路径: com/example/springboot/service/VolunteerActivityApplicationService.java (新建或更新此文件)

package com.example.springboot.service;

import com.example.springboot.mapper.VolunteerActivityApplicationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
}