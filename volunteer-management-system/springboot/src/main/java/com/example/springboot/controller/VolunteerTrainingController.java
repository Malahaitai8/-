package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.VolunteerTraining;
import com.example.springboot.exception.CustomException;
import com.example.springboot.service.VolunteerTrainingService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/volunteerTraining")
public class VolunteerTrainingController {

    @Resource
    private VolunteerTrainingService volunteerTrainingService;

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
}
