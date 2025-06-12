package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Volunteer;
import com.example.springboot.entity.VolunteerTraining;
import com.example.springboot.exception.CustomException;
import com.example.springboot.service.VolunteerTrainingService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/volunteerTraining")
public class VolunteerTrainingController {

    @Resource
    private VolunteerTrainingService volunteerTrainingService;


    /**
     * 获取指定志愿者参与的所有培训列表
     * API: GET /volunteerTraining/my-participations/{volunteerId}
     */
    @GetMapping("/my-participations/{volunteerId}")
    public Result getMyParticipatedTrainings(@PathVariable String volunteerId) {
        // 【核心诊断点】在方法入口处立即添加日志
        System.out.println("[DIAGNOSTIC] ==> Request received for getMyParticipatedTrainings with volunteerId: " + volunteerId);

        try {
            List<Map<String, Object>> trainings = volunteerTrainingService.getParticipatedTrainings(volunteerId);
            System.out.println("[DIAGNOSTIC] ==> Service method executed. Found " + (trainings != null ? trainings.size() : 0) + " trainings.");
            return Result.success(trainings);
        } catch (Exception e) {
            // 如果请求能进来，但服务层出错，这里会打印日志
            System.err.println("[DIAGNOSTIC] ==> Error caught in getMyParticipatedTrainings: " + e.getMessage());
            e.printStackTrace(); // 打印完整的堆栈信息
            return Result.error("500", "获取我的培训列表失败：" + e.getMessage());
        }
    }

    /**
     * 志愿者对培训进行评价
     * API: POST /volunteerTraining/rate
     */
    @PostMapping("/rate")
    public Result rateTraining(@RequestBody Map<String, Object> payload) {
        try {
            String volunteerId = (String) payload.get("volunteerId");
            String trainingId = (String) payload.get("trainingId");
            Number ratingNum = (Number) payload.get("rating");
            Integer rating = ratingNum != null ? ratingNum.intValue() : null;

            // 接收 Service 返回的最新对象
            volunteerTrainingService.rateTraining(volunteerId, trainingId, rating);

            // 将最新对象返回给前端
            return Result.success("评价成功");

        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            return Result.error("500", "评价失败：" + e.getMessage());
        }
    }

    /**
     * 添加新的志愿培训
     * API: POST /volunteerTraining/add
     */
    @PostMapping("/add")
    public Result addTraining(@RequestBody VolunteerTraining volunteerTraining) {
        try {
            volunteerTrainingService.addTraining(volunteerTraining);
            return Result.success("志愿培训添加成功");
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            // Log the exception e
            return Result.error("500", "添加志愿培训失败: " + e.getMessage());
        }
    }

    /**
     * 更新志愿培训信息
     * API: PUT /volunteerTraining/update
     */
    @PutMapping("/update")
    public Result updateTraining(@RequestBody VolunteerTraining volunteerTraining) {
        try {
            volunteerTrainingService.updateTraining(volunteerTraining);
            return Result.success("志愿培训更新成功");
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            return Result.error("500", "更新志愿培训失败: " + e.getMessage());
        }
    }

    /**
     * 根据培训ID删除志愿培训
     * API: DELETE /volunteerTraining/delete/{trainingId}
     */
    @DeleteMapping("/delete/{trainingId}")
    public Result deleteTraining(@PathVariable String trainingId) {
        try {
            volunteerTrainingService.deleteTrainingById(trainingId);
            return Result.success("志愿培训删除成功");
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            return Result.error("500", "删除志愿培训失败: " + e.getMessage());
        }
    }

    /**
     * 根据培训ID查询志愿培训详情
     * API: GET /volunteerTraining/get/{trainingId}
     */
    @GetMapping("/get/{trainingId}")
    public Result getTrainingById(@PathVariable String trainingId) {
        try {
            VolunteerTraining training = volunteerTrainingService.getTrainingById(trainingId);
            if (training != null) {
                return Result.success(training);
            } else {
                return Result.error("404", "未找到指定的志愿培训");
            }
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            return Result.error("500", "查询志愿培训详情失败: " + e.getMessage());
        }
    }

    /**
     * 查询所有志愿培训 (可带过滤条件)
     * API: GET /volunteerTraining/all
     * Query Params: trainingName, orgId, theme, location, trainingStatus, contactPersonPhone
     */
    @GetMapping("/all")
    public Result getAllTrainings(VolunteerTraining volunteerTraining) {
        try {
            List<VolunteerTraining> trainings = volunteerTrainingService.getAllTrainings(volunteerTraining);
            return Result.success(trainings);
        } catch (Exception e) {
            return Result.error("500", "查询所有志愿培训失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询志愿培训
     * API: GET /volunteerTraining/page
     * Query Params: pageNum, pageSize, and fields from VolunteerTraining for filtering
     */
    @GetMapping("/page")
    public Result getTrainingPage(@RequestParam(defaultValue = "1") Integer pageNum,
                                  @RequestParam(defaultValue = "10") Integer pageSize,
                                  VolunteerTraining volunteerTraining) {
        try {
            PageInfo<VolunteerTraining> pageInfo = volunteerTrainingService.getTrainingPage(volunteerTraining, pageNum, pageSize);
            return Result.success(pageInfo);
        } catch (Exception e) {
            return Result.error("500", "分页查询志愿培训失败: " + e.getMessage());
        }
    }

    /**
     * 根据组织ID查询培训
     * API: GET /volunteerTraining/byOrg/{orgId}
     */
    @GetMapping("/byOrg/{orgId}")
    public Result getTrainingsByOrgId(@PathVariable String orgId) {
        try {
            List<VolunteerTraining> trainings = volunteerTrainingService.getTrainingsByOrgId(orgId);
            return Result.success(trainings);
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            return Result.error("500", "按组织查询培训失败: " + e.getMessage());
        }
    }

    /**
     * 根据培训状态查询培训
     * API: GET /volunteerTraining/byStatus
     * Query Param: status
     */
    @GetMapping("/byStatus")
    public Result getTrainingsByStatus(@RequestParam String status) {
        try {
            List<VolunteerTraining> trainings = volunteerTrainingService.getTrainingsByStatus(status);
            return Result.success(trainings);
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            return Result.error("500", "按状态查询培训失败: " + e.getMessage());
        }
    }

    /**
     * 根据培训主题查询培训
     * API: GET /volunteerTraining/byTheme
     * Query Param: theme
     */
    @GetMapping("/byTheme")
    public Result getTrainingsByTheme(@RequestParam String theme) {
        try {
            List<VolunteerTraining> trainings = volunteerTrainingService.getTrainingsByTheme(theme);
            return Result.success(trainings);
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            return Result.error("500", "按主题查询培训失败: " + e.getMessage());
        }
    }

    /**
     * 审核/更新培训状态
     * API: PUT /volunteerTraining/review
     * Request Body: { "trainingId": "T001", "newStatus": "审核通过", "reviewerAdminId": "ADM001" }
     */
    @PutMapping("/review")
    public Result reviewTraining(@RequestBody Map<String, String> payload) {
        try {
            String trainingId = payload.get("trainingId");
            String newStatus = payload.get("newStatus");
            String reviewerAdminId = payload.get("reviewerAdminId"); // Can be null

            volunteerTrainingService.reviewTraining(trainingId, newStatus, reviewerAdminId);
            return Result.success("培训状态更新成功");
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            return Result.error("500", "更新培训状态失败: " + e.getMessage());
        }
    }

    /**
     * 申请志愿培训
     * API: POST /volunteerTraining/apply
     */
    @PostMapping("/apply")
    public Result applyTraining(@RequestBody Map<String, Object> payload) {
        try {
            // 创建VolunteerTraining对象
            VolunteerTraining training = new VolunteerTraining();

            // 映射前端字段到实体字段
            training.setTrainingName((String) payload.get("trainingName"));
            training.setTheme((String) payload.get("trainingType")); // 前端传trainingType，实体用theme
            training.setLocation((String) payload.get("location"));
            training.setOrgId((String) payload.get("orgId"));
            training.setContactPersonPhone((String) payload.get("contactPersonPhone"));
            training.setTrainingStatus((String) payload.get("trainingStatus"));

            // 处理参与人数字段映射
            Object participantCount = payload.get("participantCount");
            if (participantCount instanceof Number) {
                training.setRecruitmentCount(((Number) participantCount).intValue());
            }

            // 处理时间字段转换
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String startTimeStr = (String) payload.get("startTime");
            String endTimeStr = (String) payload.get("endTime");

            if (startTimeStr != null) {
                training.setStartTime(sdf.parse(startTimeStr));
            }
            if (endTimeStr != null) {
                training.setEndTime(sdf.parse(endTimeStr));
            }

            // 设置创建时间
            training.setCreationTime(new Date());

            System.out.println("申请培训数据: " + payload);
            System.out.println("转换后的培训实体: " + training.getTrainingName() + ", " + training.getTheme());

            volunteerTrainingService.addTraining(training);
            return Result.success("志愿培训申请提交成功");
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("500", "申请志愿培训失败: " + e.getMessage());
        }
    }

    /**
     * 根据组织ID查询培训，并包含组织名称
     * API: GET /volunteerTraining/detailedByOrg/{orgId}
     *
     * @param orgId 组织ID
     * @return 包含组织名称的培训列表
     */
    @GetMapping("/detailedByOrg/{orgId}")
    public Result getDetailedTrainingsByOrganization(@PathVariable String orgId) {
        try {
            List<VolunteerTraining> trainings = volunteerTrainingService.getTrainingsWithOrgNameByOrgId(orgId);
            return Result.success(trainings);
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            return Result.error("500", "按组织查询详细培训失败: " + e.getMessage());
        }
    }

    /**
     * 根据培训状态查询培训，并包含组织名称
     * API: GET /volunteerTraining/detailedByStatus
     * Query Param: status
     *
     * @param status 培训状态
     * @return 包含组织名称的培训列表
     */
    @GetMapping("/detailedByStatus")
    public Result getDetailedTrainingsByTrainingStatus(@RequestParam String status) {
        try {
            List<VolunteerTraining> trainings = volunteerTrainingService.getTrainingsWithOrgNameByStatus(status);
            return Result.success(trainings);
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            return Result.error("500", "按状态查询详细培训失败: " + e.getMessage());
        }
    }

    /**
     * 根据培训主题查询培训，并包含组织名称
     * API: GET /volunteerTraining/detailedByTheme
     * Query Param: theme
     *
     * @param theme 培训主题
     * @return 包含组织名称的培训列表
     */
    @GetMapping("/detailedByTheme")
    public Result getDetailedTrainingsByTrainingTheme(@RequestParam String theme) {
        try {
            List<VolunteerTraining> trainings = volunteerTrainingService.getTrainingsWithOrgNameByTheme(theme);
            return Result.success(trainings);
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            return Result.error("500", "按主题查询详细培训失败: " + e.getMessage());
        }
    }
















 /**
     * 【核心】获取指定培训的参与者列表（包含签到状态）
     * API: GET /volunteerTraining/{trainingId}/participants
     */
    @GetMapping("/{trainingId}/participants")
    public Result getTrainingParticipants(@PathVariable String trainingId) {
        try {
            // 正确调用返回 List<Map> 的方法
            List<Map<String, Object>> participants = volunteerTrainingService.getParticipantsByTrainingId(trainingId);
            return Result.success(participants);
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            return Result.error("500", "获取培训参与者列表失败：" + e.getMessage());
        }
    }

    /**
     * 更新培训中某个志愿者的签到状态
     * API: PUT /volunteerTraining/participation/status
     */
    @PutMapping("/participation/status")
    public Result updateParticipationStatus(@RequestBody Map<String, String> payload) {
        try {
            String trainingId = payload.get("trainingId");
            String volunteerId = payload.get("volunteerId");
            String isCheckedIn = payload.get("isCheckedIn");

            volunteerTrainingService.updateParticipationStatus(trainingId, volunteerId, isCheckedIn);
            return Result.success("签到状态更新成功");
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("500", "更新签到状态失败，服务器内部错误");
        }
    }

    /**
     * 获取可添加到某培训的志愿者列表
     * API: GET /volunteerTraining/{trainingId}/potential-participants
     */
    @GetMapping("/{trainingId}/potential-participants")
    public Result getPotentialParticipants(@PathVariable String trainingId, @RequestParam(required = false) String name) {
        try {
            List<Volunteer> volunteers = volunteerTrainingService.getPotentialParticipantsForTraining(trainingId, name);
            return Result.success(volunteers);
        } catch (Exception e) {
            return Result.error("500", "获取可添加志愿者列表失败：" + e.getMessage());
        }
    }

    /**
     * 将志愿者添加到培训中
     * API: POST /volunteerTraining/enroll-participant
     */
    @PostMapping("/enroll-participant")
    public Result enrollParticipant(@RequestBody Map<String, String> payload) {
        try {
            String trainingId = payload.get("trainingId");
            String volunteerId = payload.get("volunteerId");
            volunteerTrainingService.enrollVolunteerInTraining(trainingId, volunteerId);
            return Result.success("添加成功");
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            return Result.error("500", "添加失败：" + e.getMessage());
        }
    }

}
