// 文件路径: com/example/springboot/controller/VolunteerActivityApplicationController.java (新建或更新此文件)

package com.example.springboot.controller;

import com.example.springboot.common.Result; // 假设: 你有统一的返回结果类
import com.example.springboot.service.VolunteerActivityApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/application") // 定义接口的基础路径
public class VolunteerActivityApplicationController {

     @Autowired
    private VolunteerActivityApplicationService applicationService;

    /**
     * 获取当前志愿者所有已报名的活动列表
     */
    @GetMapping("/my-activities/{volunteerId}")
    public Result getMyRegisteredActivities(@PathVariable String volunteerId) {
        List<Map<String, Object>> activities = applicationService.findMyApplicationsWithDetails(volunteerId);
        return Result.success(activities);
    }

    /**
     * 志愿者撤回报名申请
     */
    @PutMapping("/withdraw")
    public Result withdrawApplication(@RequestBody Map<String, String> payload) {
        String applicationId = payload.get("applicationId");
        int result = applicationService.withdrawApplication(applicationId);
        if (result > 0) {
            return Result.success();
        } else {
            return Result.error("400", "操作失败或申请状态已无法撤回");
        }
    }
}