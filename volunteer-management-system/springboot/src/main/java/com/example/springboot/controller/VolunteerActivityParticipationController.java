// 文件路径: com/example/springboot/controller/VolunteerActivityParticipationController.java
package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.service.VolunteerActivityParticipationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/participation") // 接口路径清晰地指向“参与”这个概念
public class VolunteerActivityParticipationController {

    @Autowired
    private VolunteerActivityParticipationService participationService;

    /**
     * 获取“我的志愿活动”（已参与）列表
     * 这个接口专门给“我的志愿活动”页面使用。
     */
    @GetMapping("/my-activities/{volunteerId}")
    public Result getMyParticipatedActivities(@PathVariable String volunteerId) {
        List<Map<String, Object>> activities = participationService.findMyParticipations(volunteerId);
        return Result.success(activities);
    }

    /**
     * 志愿者对活动进行评分
     */
    @PutMapping("/rate")
    public Result rateActivity(@RequestBody Map<String, Object> payload) {
        String volunteerId = (String) payload.get("volunteerId");
        String actualPositionId = (String) payload.get("actualPositionId");
        Integer rating = (Integer) payload.get("rating");
        participationService.rateOrganization(volunteerId, actualPositionId, rating);
        return Result.success("评价成功");
    }
}