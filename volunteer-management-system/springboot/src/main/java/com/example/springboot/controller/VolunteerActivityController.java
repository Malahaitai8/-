package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.VolunteerActivity;
import com.example.springboot.entity.ActivityTimeslot;
import com.example.springboot.entity.Position;
import com.example.springboot.exception.CustomException;
import com.example.springboot.service.VolunteerActivityService;
import com.example.springboot.mapper.ActivityTimeslotMapper;
import com.example.springboot.mapper.PositionMapper;
import com.example.springboot.mapper.VolunteerActivityParticipationMapper;
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
    
    @Resource
    private ActivityTimeslotMapper activityTimeslotMapper;
    
    @Resource
    private PositionMapper positionMapper;
    
    @Resource
    private VolunteerActivityParticipationMapper participationMapper;

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

    /**
     * 根据活动ID查询单个活动详情
     * API端点: GET /volunteerActivity/{activityId}
     * @param activityId 活动ID，从路径中获取
     * @return 志愿活动详情
     */
    @GetMapping("/{activityId}")
    public Result getActivityById(@PathVariable String activityId) {
        try {
            VolunteerActivity activity = volunteerActivityService.getActivityById(activityId);
            if (activity != null) {
                return Result.success(activity);
            } else {
                return Result.error("404", "未找到指定的志愿活动");
            }
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            System.err.println("Error fetching activity by ID: " + activityId + "; Error: " + e.getMessage());
            return Result.error("500", "查询活动详情失败");
        }
    }

    /**
     * 根据活动ID删除活动
     * API端点: DELETE /volunteerActivity/delete/{activityId}
     * @param activityId 活动ID，从路径中获取
     * @return 操作结果
     */
    @DeleteMapping("/delete/{activityId}")
    public Result deleteActivity(@PathVariable String activityId) {
        try {
            volunteerActivityService.deleteActivityById(activityId);
            return Result.success("志愿活动删除成功");
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            System.err.println("Error deleting activity by ID: " + activityId + "; Error: " + e.getMessage());
            return Result.error("500", "删除活动失败");
        }
    }

    /**
     * 根据活动ID获取时段列表
     * API端点: GET /volunteerActivity/{activityId}/timeslots
     * @param activityId 活动ID，从路径中获取
     * @return 时段列表
     */
    @GetMapping("/{activityId}/timeslots")
    public Result getTimeslotsByActivityId(@PathVariable String activityId) {
        try {
            List<ActivityTimeslot> timeslots = activityTimeslotMapper.selectByEventId(activityId);
            return Result.success(timeslots);
        } catch (Exception e) {
            System.err.println("Error fetching timeslots by activity ID: " + activityId + "; Error: " + e.getMessage());
            return Result.error("500", "查询活动时段失败");
        }
    }

    /**
     * 根据活动ID获取岗位列表
     * API端点: GET /volunteerActivity/{activityId}/positions
     * @param activityId 活动ID，从路径中获取
     * @return 岗位列表
     */
    @GetMapping("/{activityId}/positions")
    public Result getPositionsByActivityId(@PathVariable String activityId) {
        try {
            List<Position> positions = positionMapper.selectByActivityId(activityId);
            return Result.success(positions);
        } catch (Exception e) {
            System.err.println("Error fetching positions by activity ID: " + activityId + "; Error: " + e.getMessage());
            return Result.error("500", "查询活动岗位失败");
        }
    }

    /**
     * 根据活动ID获取参与志愿者列表
     * API端点: GET /volunteerActivity/{activityId}/participants
     * @param activityId 活动ID，从路径中获取
     * @return 参与志愿者列表
     */
    @GetMapping("/{activityId}/participants")
    public Result getParticipantsByActivityId(@PathVariable String activityId) {
        try {
            List<Map<String, Object>> participants = participationMapper.selectParticipantsWithDetailsByActivityId(activityId);
            return Result.success(participants);
        } catch (Exception e) {
            System.err.println("Error fetching participants by activity ID: " + activityId + "; Error: " + e.getMessage());
            return Result.error("500", "查询活动参与者失败");
        }
    }

    /**
     * 更新活动信息
     * API端点: PUT /volunteerActivity/{activityId}
     * @param activityId 活动ID，从路径中获取
     * @param updatedActivity 更新的活动信息
     * @return 操作结果
     */
    @PutMapping("/{activityId}")
    public Result updateActivity(@PathVariable String activityId, @RequestBody VolunteerActivity updatedActivity) {
        try {
            // 确保活动ID正确
            updatedActivity.setActivityId(activityId);
            
            // 调用service更新活动
            volunteerActivityService.updateActivity(updatedActivity);
            return Result.success("活动信息更新成功");
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            System.err.println("Error updating activity by ID: " + activityId + "; Error: " + e.getMessage());
            return Result.error("500", "更新活动信息失败");
        }
    }

    /**
     * 为活动添加新时段
     * API端点: POST /volunteerActivity/{activityId}/timeslots
     * @param activityId 活动ID，从路径中获取
     * @param timeslot 时段信息
     * @return 操作结果
     */
    @PostMapping("/{activityId}/timeslots")
    public Result addTimeslot(@PathVariable String activityId, @RequestBody ActivityTimeslot timeslot) {
        try {
            // 设置活动ID
            timeslot.setEventId(activityId);
            
            // 基本验证
            if (timeslot.getStartTime() == null || timeslot.getEndTime() == null) {
                return Result.error("400", "开始时间和结束时间不能为空");
            }
            
            if (timeslot.getStartTime().after(timeslot.getEndTime())) {
                return Result.error("400", "开始时间不能晚于结束时间");
            }
            
            // 生成时段ID (可以使用UUID或其他策略)
            String timeslotId = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            timeslot.setTimeslotId(timeslotId);
            
            // 插入时段
            int result = activityTimeslotMapper.insert(timeslot);
            if (result > 0) {
                return Result.success("时段添加成功");
            } else {
                return Result.error("500", "时段添加失败");
            }
        } catch (Exception e) {
            System.err.println("Error adding timeslot for activity ID: " + activityId + "; Error: " + e.getMessage());
            return Result.error("500", "添加时段失败");
        }
    }

    /**
     * 删除指定时段
     * API端点: DELETE /volunteerActivity/{activityId}/timeslots/{timeslotId}
     * @param activityId 活动ID，从路径中获取
     * @param timeslotId 时段ID，从路径中获取
     * @return 操作结果
     */
    @DeleteMapping("/{activityId}/timeslots/{timeslotId}")
    public Result deleteTimeslot(@PathVariable String activityId, @PathVariable String timeslotId) {
        try {
            // 先检查时段是否存在并属于该活动
            ActivityTimeslot timeslot = activityTimeslotMapper.selectById(timeslotId);
            if (timeslot == null) {
                return Result.error("404", "时段不存在");
            }
            
            if (!activityId.equals(timeslot.getEventId())) {
                return Result.error("400", "时段不属于该活动");
            }
            
            // 删除时段
            int result = activityTimeslotMapper.deleteById(timeslotId);
            if (result > 0) {
                return Result.success("时段删除成功");
            } else {
                return Result.error("500", "时段删除失败");
            }
        } catch (org.springframework.dao.DataAccessException e) {
            // 处理数据库访问异常
            String errorMessage = e.getMessage();
            System.err.println("Error deleting timeslot ID: " + timeslotId + " for activity ID: " + activityId + "; Error: " + errorMessage);
            
            // 递归获取所有异常信息
            String allErrorMessages = collectAllErrorMessages(e);
            System.err.println("All error messages: " + allErrorMessages);
            
            // 检查是否是业务规则约束错误（至少需要一个时段）
            // 数据库触发器错误消息：操作失败：事件 {ID} 至少需要一个时段。不能删除其最后一个时段
            if (allErrorMessages.contains("操作失败") && allErrorMessages.contains("至少需要一个时段") ||
                allErrorMessages.contains("不能删除其最后一个时段") ||
                allErrorMessages.contains("至少需要一个时段") ||
                allErrorMessages.contains("需要一个时段")) {
                return Result.error("400", "无法删除：每个活动至少需要保留一个时段");
            }
            
            return Result.error("500", "删除时段失败");
        } catch (Exception e) {
            System.err.println("Error deleting timeslot ID: " + timeslotId + " for activity ID: " + activityId + "; Error: " + e.getMessage());
            return Result.error("500", "删除时段失败");
        }
    }

    /**
     * 为活动添加新岗位
     * API端点: POST /volunteerActivity/{activityId}/positions
     * @param activityId 活动ID，从路径中获取
     * @param position 岗位信息
     * @return 操作结果
     */
    @PostMapping("/{activityId}/positions")
    public Result addPosition(@PathVariable String activityId, @RequestBody Position position) {
        try {
            // 设置活动ID
            position.setActivityId(activityId);
            
            // 基本验证
            if (position.getPositionName() == null || position.getPositionName().trim().isEmpty()) {
                return Result.error("400", "岗位名称不能为空");
            }
            
            if (position.getPositionServiceHours() == null || position.getPositionServiceHours() <= 0) {
                return Result.error("400", "服务时长必须大于0");
            }
            
            if (position.getRequiredVolunteers() == null || position.getRequiredVolunteers() <= 0) {
                return Result.error("400", "岗位需求人数必须大于0");
            }
            
            // 生成岗位ID
            String positionId = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            position.setPositionId(positionId);
            
            // 设置已招募人数为0
            position.setRecruitedVolunteers(0);
            
            // 插入岗位
            int result = positionMapper.insert(position);
            if (result > 0) {
                return Result.success("岗位添加成功");
            } else {
                return Result.error("500", "岗位添加失败");
            }
        } catch (org.springframework.dao.DataAccessException e) {
            // 处理数据库访问异常
            String errorMessage = e.getMessage();
            System.err.println("Error adding position for activity ID: " + activityId + "; Error: " + errorMessage);
            
            // 递归获取所有异常信息
            String allErrorMessages = collectAllErrorMessages(e);
            System.err.println("All error messages: " + allErrorMessages);
            
            // 检查是否是约束违反或其他数据库错误
            if (allErrorMessages.contains("UNIQUE") || allErrorMessages.contains("重复")) {
                return Result.error("400", "岗位名称已存在，请使用不同的名称");
            }
            
            return Result.error("500", "添加岗位失败：数据库错误");
        } catch (Exception e) {
            System.err.println("Error adding position for activity ID: " + activityId + "; Error: " + e.getMessage());
            return Result.error("500", "添加岗位失败");
        }
    }

    /**
     * 删除指定岗位
     * API端点: DELETE /volunteerActivity/{activityId}/positions/{positionId}
     * @param activityId 活动ID，从路径中获取
     * @param positionId 岗位ID，从路径中获取
     * @return 操作结果
     */
    @DeleteMapping("/{activityId}/positions/{positionId}")
    public Result deletePosition(@PathVariable String activityId, @PathVariable String positionId) {
        try {
            // 先检查岗位是否存在并属于该活动
            Position position = positionMapper.selectById(positionId);
            if (position == null) {
                return Result.error("404", "岗位不存在");
            }
            
            if (!activityId.equals(position.getActivityId())) {
                return Result.error("400", "岗位不属于该活动");
            }
            
            // 检查是否有已招募的志愿者
            if (position.getRecruitedVolunteers() != null && position.getRecruitedVolunteers() > 0) {
                return Result.error("400", "该岗位已有志愿者，无法删除");
            }
            
            // 删除岗位
            int result = positionMapper.deleteById(positionId);
            if (result > 0) {
                return Result.success("岗位删除成功");
            } else {
                return Result.error("500", "岗位删除失败");
            }
        } catch (org.springframework.dao.DataAccessException e) {
            // 处理数据库访问异常
            String errorMessage = e.getMessage();
            System.err.println("Error deleting position ID: " + positionId + " for activity ID: " + activityId + "; Error: " + errorMessage);
            
            // 递归获取所有异常信息
            String allErrorMessages = collectAllErrorMessages(e);
            System.err.println("All error messages: " + allErrorMessages);
            
            // 检查是否是外键约束错误
            if (allErrorMessages.contains("FOREIGN KEY") || allErrorMessages.contains("外键") || 
                allErrorMessages.contains("REFERENCE") || allErrorMessages.contains("引用")) {
                return Result.error("400", "该岗位已有相关数据，无法删除");
            }
            
            return Result.error("500", "删除岗位失败");
        } catch (Exception e) {
            System.err.println("Error deleting position ID: " + positionId + " for activity ID: " + activityId + "; Error: " + e.getMessage());
            return Result.error("500", "删除岗位失败");
        }
    }

    /**
     * 递归收集异常链中的所有错误消息
     * @param throwable 异常对象
     * @return 所有错误消息的连接字符串
     */
    private String collectAllErrorMessages(Throwable throwable) {
        StringBuilder allMessages = new StringBuilder();
        Throwable current = throwable;
        
        while (current != null) {
            if (current.getMessage() != null) {
                allMessages.append(current.getMessage()).append(" ");
            }
            current = current.getCause();
        }
        
        return allMessages.toString();
    }
    /**
     * 【新增接口】为志愿者端获取可报名活动列表
     * API端点: GET /api/volunteer-activity/available-for-volunteer?volunteerId=xxx&activityName=爱心
     * 路径名详细，避免与您已有的 /list 或 /selectAll 冲突。
     */
    @GetMapping("/available-for-volunteer")
    public Result listAvailableActivitiesForVolunteer(VolunteerActivity filter, @RequestParam String volunteerId) {
        List<VolunteerActivity> activities = volunteerActivityService.findAvailableActivitiesForVolunteer(filter, volunteerId);
        return Result.success(activities);
    }


 /**
     * 更新志愿者签到状态
     */
    @PutMapping("/participation/check-in")
    public Result updateCheckInStatus(@RequestBody Map<String, String> payload) {
        // [修复] 从请求体中获取 actualPositionId
        String activityId = payload.get("activityId");
        String volunteerId = payload.get("volunteerId");
        String isCheckedIn = payload.get("isCheckedIn");
        String actualPositionId = payload.get("actualPositionId"); // 新增获取

        // [修复] 调用服务时传入 actualPositionId
        volunteerActivityService.updateCheckInStatus(activityId, volunteerId, actualPositionId, isCheckedIn);
        return Result.success("更新签到状态成功");
    }

    /**
     * 组织为志愿者评分
     */
    @PutMapping("/rate-participant")
    public Result rateParticipant(@RequestBody Map<String, Object> payload) {
        // [修复] 从请求体中获取 activityId, volunteerId, actualPositionId, 和 rating
        String activityId = (String) payload.get("activityId");
        String volunteerId = (String) payload.get("volunteerId");
        //String actualPositionId = (String) payload.get("actualPositionId"); // 新增获取
        Integer rating = (Integer) payload.get("rating");

        // [修复] 调用服务时传入 actualPositionId
        volunteerActivityService.rateParticipantByOrg(activityId, volunteerId, rating);
        return Result.success("评价成功");
    }
}