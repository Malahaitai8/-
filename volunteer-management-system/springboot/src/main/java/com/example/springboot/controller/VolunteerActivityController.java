package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.VolunteerActivity;
import com.example.springboot.exception.CustomException;
import com.example.springboot.service.VolunteerActivityService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils; // 引入 StringUtils

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/volunteerActivity")
public class VolunteerActivityController {

    @Resource
    private VolunteerActivityService volunteerActivityService;

    /**
     * 管理员审核/更新活动状态。
     * 前端发送的请求体 payload 包含: activityId, activityStatus, reviewerAdminId, 和可选的 rejectionReason
     * API端点: PUT /volunteerActivity/review
     */
    @PutMapping("/review")
    public Result reviewActivity(@RequestBody Map<String, String> payload) {
        try {
            String activityId = payload.get("activityId");
            String newStatus = payload.get("activityStatus");
            String reviewerAdminId = payload.get("reviewerAdminId");
            String remarks = payload.get("rejectionReason"); // 对应前端发送的驳回理由

            if (!StringUtils.hasText(activityId)) { // 使用 StringUtils.hasText 进行更安全的检查
                return Result.error("400", "活动ID不能为空");
            }
            if (!StringUtils.hasText(newStatus)) {
                return Result.error("400", "新的活动状态不能为空");
            }
            // reviewerAdminId 可以根据业务逻辑判断是否强制要求，这里假设它可以为null（如果系统自动更新等情况）

            volunteerActivityService.reviewActivity(activityId, newStatus, reviewerAdminId, remarks);
            return Result.success("活动状态更新成功");
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            System.err.println("Review Activity Error for payload " + payload + ": " + e.getMessage());
            e.printStackTrace(); // 打印完整堆栈，便于调试
            return Result.error("500", "更新活动状态失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有志愿活动列表 (供管理员使用)
     * API端点: GET /volunteerActivity/selectAll
     * 前端 Vue 组件的 fetchAllActivities 方法会调用此接口。
     */
    @GetMapping("/selectAll")
    public Result selectAllActivitiesForAdmin(VolunteerActivity filter) {
        try {
            List<VolunteerActivity> activities = volunteerActivityService.getAllActivitiesForAdmin(filter);
            return Result.success(activities);
        } catch (Exception e) {
            // ... error handling ...
            return Result.error("500", "获取活动列表失败");
        }
    }

    // 您可能已经有的其他 Controller 方法，例如：
    // @GetMapping("/{activityId}")
    // public Result getActivityById(@PathVariable String activityId) { ... }

    // @PostMapping("/create") // 组织申请创建活动
    // public Result createActivity(@RequestBody VolunteerActivity activity) { ... }
}
