// 文件路径: com/example/springboot/controller/VolunteerActivityApplicationController.java (新建或更新此文件)

package com.example.springboot.controller;

import com.example.springboot.common.Result; // 假设: 你有统一的返回结果类
import com.example.springboot.entity.VolunteerActivityApplication;
import com.example.springboot.exception.CustomException;
import com.example.springboot.service.VolunteerActivityApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    /**
     * 【新增接口】志愿者提交报名申请
     * @param payload 包含 volunteerId, activityId, intendedPositionId 的请求体
     * @return 操作结果
     */
    @PostMapping("/apply")
    public Result applyForActivity(@RequestBody Map<String, String> payload) {
        try {
            String volunteerId = payload.get("volunteerId");
            String activityId = payload.get("activityId");
            String intendedPositionId = payload.get("intendedPositionId");

            applicationService.createApplication(volunteerId, activityId, intendedPositionId);
            return Result.success("报名成功！请等待组织审核。");
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            return Result.error("500", "报名时发生未知错误: " + e.getMessage());
        }
    }



    @PutMapping("/update-status")
    public Result updateApplicationStatus(@RequestBody Map<String, String> payload) {
        String applicationId = payload.get("applicationId");
        String applicationStatus = payload.get("applicationStatus");
        int result = applicationService.updateApplicationStatus(applicationId, applicationStatus);
        if (result > 0) {
            return Result.success("状态更新成功");
        } else {
            return Result.error("400", "状态更新失败");
        }
    }

    @GetMapping("/pending-applications/{orgId}")
    public Result getPendingApplications(@PathVariable String orgId) {
        System.out.println("后台接收到审核请求，组织ID为: " + orgId); // 用于调试
        // 【同步修改】变量类型现在是 List<Map<String, Object>>
        List<Map<String, Object>> pendingApplications = applicationService.getPendingApplicationsForOrg(orgId);
        return Result.success(pendingApplications);
    }
