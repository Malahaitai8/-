package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.VolunteerActivity;
import com.example.springboot.exception.CustomException;
import com.example.springboot.service.VolunteerActivityService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/volunteerActivity") // <-- 确保这里是 "/volunteerActivity"，没有多余的空格，大小写完全一致
public class VolunteerActivityController {

    @Resource
    private VolunteerActivityService volunteerActivityService;

    /**
     * 管理员审核/更新活动状态。
     * API端点: PUT /volunteerActivity/review
     */
    @PutMapping("/review")
    public Result reviewActivity(@RequestBody Map<String, String> payload) {
        try {
            String activityId = payload.get("activityId");
            String newStatus = payload.get("activityStatus");
            String reviewerAdminId = payload.get("reviewerAdminId");
            String remarks = payload.get("rejectionReason");

            if (!StringUtils.hasText(activityId)) {
                return Result.error("400", "活动ID不能为空");
            }
            if (!StringUtils.hasText(newStatus)) {
                return Result.error("400", "新的活动状态不能为空");
            }

            volunteerActivityService.reviewActivity(activityId, newStatus, reviewerAdminId, remarks);
            return Result.success("活动状态更新成功");
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            System.err.println("Review Activity Error for payload " + payload + ": " + e.getMessage());
            e.printStackTrace();
            return Result.error("500", "更新活动状态失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有志愿活动列表 (供管理员使用)
     * API端点: GET /volunteerActivity/selectAll
     */
    @GetMapping("/selectAll")
    public Result selectAllActivitiesForAdmin(VolunteerActivity filter) {
        try {
            List<VolunteerActivity> activities = volunteerActivityService.getAllActivitiesForAdmin(filter);
            return Result.success(activities);
        } catch (Exception e) {
            return Result.error("500", "获取活动列表失败");
        }
    }

    /**
     * 新增方法：组织申请创建活动
     * API端点: POST /volunteerActivity/apply
     * 请求体: VolunteerActivity 实体
     */
    @PostMapping("/apply") // <-- 确保这里是 "/apply"，没有多余的空格，并且方法是 POST
    public Result applyForActivity(@RequestBody VolunteerActivity volunteerActivity) {
        try {
            volunteerActivityService.addActivity(volunteerActivity);
            return Result.success("志愿活动申请已提交，请等待审核");
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            System.err.println("Apply for Activity Error: " + e.getMessage());
            e.printStackTrace();
            return Result.error("500", "提交活动申请失败，系统内部错误");
        }
    }
    /**
     * 新增方法：根据组织ID查询该组织发布的所有活动
     * API端点: GET /volunteerActivity/byOrg/{orgId}
     * @param orgId 组织ID，从路径中获取
     * @return 志愿活动列表
     */
    @GetMapping("/byOrg/{orgId}")
    public Result getActivitiesByOrg(@PathVariable String orgId) {
        try {
            List<VolunteerActivity> activities = volunteerActivityService.getActivitiesByOrgId(orgId);
            return Result.success(activities);
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            System.err.println("Error fetching activities by org ID: " + orgId + "; Error: " + e.getMessage());
            return Result.error("500", "查询组织活动列表失败");
        }
    }
}