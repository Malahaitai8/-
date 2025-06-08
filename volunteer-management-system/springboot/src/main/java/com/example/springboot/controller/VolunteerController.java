package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Employee;
import com.example.springboot.entity.Volunteer;
import com.example.springboot.exception.CustomException;
import com.example.springboot.service.VolunteerService;
import io.micrometer.common.util.internal.logging.InternalLogger;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap; // 用于构建响应数据
import java.util.List;    // 如果其他端点需要
import java.util.Map;     // 用于构建响应数据


@RestController
@RequestMapping("/volunteer")
public class VolunteerController {
    @Resource
    private VolunteerService volunteerService;

    /**
     * 获取指定志愿者的所有活动评价
     * API: GET /volunteer/{volunteerId}/reviews/activities
     *
     * @param volunteerId 志愿者ID
     * @return 活动评价列表
     */
    @GetMapping("/{volunteerId}/reviews/activities")
    public Result getActivityReviews(@PathVariable String volunteerId) {
        try {
            List<Map<String, Object>> reviews = volunteerService.getActivityReviews(volunteerId);
            return Result.success(reviews);
        } catch (Exception e) {
            return Result.error("500", "获取活动评价失败: " + e.getMessage());
        }
    }

    /**
     * 获取指定志愿者的所有培训评价
     * API: GET /volunteer/{volunteerId}/reviews/trainings
     *
     * @param volunteerId 志愿者ID
     * @return 培训评价列表
     */
    @GetMapping("/{volunteerId}/reviews/trainings")
    public Result getTrainingReviews(@PathVariable String volunteerId) {
        try {
            List<Map<String, Object>> reviews = volunteerService.getTrainingReviews(volunteerId);
            return Result.success(reviews);
        } catch (Exception e) {
            return Result.error("500", "获取培训评价失败: " + e.getMessage());
        }
    }

    @PutMapping("/updateStatus/{volunteerId}") // 或者 @PostMapping("/updateAccountStatus")
    public Result updateAccountStatus(@PathVariable String volunteerId, @RequestBody Map<String, Object> payload) {
        try {
            String newStatus = (String) payload.get("accountStatus");
            String rejectionReason = (String) payload.get("rejectionReason"); // 可选
            // String adminId = ... // 如果需要记录操作员，可以从token或会话中获取，或由前端传递

            volunteerService.updateVolunteerAccountStatus(volunteerId, newStatus, rejectionReason /*, adminId */);
            return Result.success("志愿者状态更新成功");
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            // Log error
            return Result.error("500", "更新志愿者状态失败：" + e.getMessage());
        }
    }

    /**
     * 修改志愿者密码
     * API端点示例: POST /volunteer/changePassword
     * 请求体示例: { "volunteerId": "V001", "oldPassword": "oldPwd", "newPassword": "newPwd" }
     *
     * @param requestBodyMap 包含 volunteerId, oldPassword, newPassword 的 Map
     * @return Result 对象，表示操作成功或失败
     */
    @PostMapping("/changePassword")
    public Result changePassword(@RequestBody Map<String, String> requestBodyMap) { // 接收 Map
        try {
            // 从 Map 中提取参数
            String volunteerId = requestBodyMap.get("volunteerId");
            String oldPassword = requestBodyMap.get("oldPassword");
            String newPassword = requestBodyMap.get("newPassword");

            // 在 Controller 层面进行最基本的非空检查
            if (volunteerId == null || volunteerId.trim().isEmpty() ||
                    oldPassword == null || oldPassword.trim().isEmpty() ||
                    newPassword == null || newPassword.trim().isEmpty()) {
                return Result.error("400", "志愿者ID、原密码和新密码不能为空");
            }

            // 将参数直接传递给 Service
            volunteerService.changePassword(volunteerId, oldPassword, newPassword);
            return Result.success("密码修改成功");
        } catch (CustomException e) {
            // 捕获业务异常，例如旧密码不匹配，账号不存在
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            // 记录其他未知错误
            System.err.println("修改密码时发生未知错误: " + e.getMessage());
            e.printStackTrace();
            return Result.error("500", "修改密码时发生系统错误");
        }
    }

    /**
     * 根据志愿者ID从视图获取志愿者星级。
     * API端点示例: GET /volunteer/{volunteerId}/stars
     *
     * @param volunteerId 志愿者ID (作为路径参数)
     * @return Result 对象，其 data 字段包含星级 (整数)。
     */
    @GetMapping("/{volunteerId}/stars")
    public Result getStarRating(@PathVariable String volunteerId) {
        InternalLogger log = null;
        try {
            int starLevel = volunteerService.getVolunteerStarLevel(volunteerId);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("starLevel", starLevel);

            return Result.success(responseData);
        } catch (IllegalArgumentException e) {
            log.warn("无效的志愿者ID: {}", volunteerId, e); // 使用 warn 级别记录非法参数
            return Result.error("400", e.getMessage()); // 返回更具体的错误信息
        } catch (Exception e) {
            log.error("获取志愿者星级时发生未知错误: volunteerId={}", volunteerId, e); // 记录错误日志
            return Result.error("500", "获取志愿者星级时发生未知错误");
        }
    }


    /*
     * 志愿者登录
     * */
    @PostMapping("/login")
    public Result volunteerLogin(@RequestBody Volunteer volunteer) throws CustomException {
        Volunteer dbvolunteer = volunteerService.volunteerLogin(volunteer);
        return Result.success(dbvolunteer);

    }

    /*
     * 志愿者注册
     * */
    @PostMapping("/register")
    public Result volunteerRegister(@RequestBody Volunteer volunteer) throws CustomException {
        // --- 添加这行调试代码 ---
        System.out.println("接收到的后端志愿者对象: " + volunteer);
        // --- 调试代码结束 ---
        volunteerService.register(volunteer);
        return Result.success("注册成功");

    }

    /**
     * 添加志愿者
     */
    @PostMapping("/insert")
    public Result insert(@RequestBody Volunteer volunteer) {
        volunteerService.insert(volunteer);
        return Result.success();
    }

    /**
     * 更新志愿者信息
     */
    @PutMapping("/updateById")
    public Result updateById(@RequestBody Volunteer volunteer) {
        volunteerService.updateById(volunteer);
        return Result.success();
    }

    /**
     * 根据志愿者ID删除志愿者信息
     */
    @DeleteMapping("/deleteById/{volunteerId}")
    public Result deleteById(@PathVariable String volunteerId) {
        volunteerService.deleteById(volunteerId);
        return Result.success();
    }

    /**
     * 查询所有志愿者
     */
    @GetMapping("/selectAll")
    public Result selectAll(Volunteer volunteer) {
        List<Volunteer> list = volunteerService.selectAll(volunteer);
        return Result.success(list);
    }

    /**
     * 根据志愿者ID查询志愿者信息
     */
    @GetMapping("/selectByID/{volunteerId}")
    public Result selectByID(@PathVariable String volunteerId) {
        Volunteer volunteer = volunteerService.selectByID(volunteerId);
        return Result.success(volunteer);
    }

    /**
     * 根据用户名查询志愿者信息
     */
    @GetMapping("/selectByUsername")
    public Result selectByUsername(@RequestParam String username) {
        Volunteer volunteer = volunteerService.selectByUsername(username);
        return Result.success(volunteer);
    }
}