package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.ActivityTimeslot;
import com.example.springboot.exception.CustomException;
import com.example.springboot.service.ActivityTimeslotService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activityTimeslot")
public class ActivityTimeslotController {

    @Resource
    private ActivityTimeslotService activityTimeslotService;

    /**
     * 根据活动ID获取其所有时段列表
     * @param eventId 活动ID
     * @return 时段列表
     */
    @GetMapping("/byEvent/{eventId}")
    public Result getTimeslotsByEventId(@PathVariable String eventId) throws CustomException {
        List<ActivityTimeslot> timeslots = activityTimeslotService.getTimeslotsByEventId(eventId);
        return Result.success(timeslots);
    }

    /**
     * 添加一个新的时段
     * @param timeslot 包含 eventId, startTime, endTime 的时段信息
     * @return 成功信息和创建好的时段对象
     */
    @PostMapping
    public Result addTimeslot(@RequestBody ActivityTimeslot timeslot) throws CustomException {
        ActivityTimeslot newTimeslot = activityTimeslotService.addTimeslot(timeslot);
        return Result.success(newTimeslot);
    }

    /**
     * 删除一个时段
     * @param timeslotId 要删除的时段ID
     * @param eventId 关联的活动ID，用于后续更新
     * @return 成功或失败信息
     */
    @DeleteMapping("/{timeslotId}/{eventId}")
    public Result deleteTimeslot(@PathVariable String timeslotId, @PathVariable String eventId) throws CustomException {
        activityTimeslotService.deleteTimeslot(timeslotId, eventId);
        return Result.success();
    }
} 