package com.example.springboot.service;

import com.example.springboot.entity.ActivityTimeslot;
import com.example.springboot.entity.VolunteerActivity;
import com.example.springboot.entity.VolunteerTraining;
import com.example.springboot.exception.CustomException;
import com.example.springboot.mapper.ActivityTimeslotMapper;
import com.example.springboot.mapper.VolunteerActivityMapper;
import com.example.springboot.mapper.VolunteerTrainingMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class ActivityTimeslotService {

    @Resource
    private ActivityTimeslotMapper activityTimeslotMapper;

    @Resource
    private VolunteerActivityMapper volunteerActivityMapper;

    @Resource
    private VolunteerTrainingMapper volunteerTrainingMapper;
    
    // 定义统一的日期格式
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 添加一个新的时段，并自动更新活动/培训主表的起止时间
     * @param timeslot 要添加的时段信息
     * @return 添加后的时段信息（包含生成的ID）
     */
    @Transactional(rollbackFor = Exception.class)
    public ActivityTimeslot addTimeslot(ActivityTimeslot timeslot) throws CustomException {
        // 1. 数据校验
        if (timeslot == null || timeslot.getEventId() == null || timeslot.getStartTime() == null || timeslot.getEndTime() == null) {
            throw new CustomException("400", "活动/培训ID、开始时间和结束时间不能为空");
        }
        if (timeslot.getStartTime().after(timeslot.getEndTime())) {
            throw new CustomException("400", "开始时间不能晚于结束时间");
        }

        // 2. 验证事件是否存在
        String eventType = timeslot.getEventId().substring(0, 3).toLowerCase();
        if ("act".equals(eventType)) {
            VolunteerActivity activity = volunteerActivityMapper.getById(timeslot.getEventId());
            if (activity == null) {
                throw new CustomException("404", "未找到活动：" + timeslot.getEventId());
            }
        } else if ("trn".equals(eventType)) {
            VolunteerTraining training = volunteerTrainingMapper.selectById(timeslot.getEventId());
            if (training == null) {
                throw new CustomException("404", "未找到培训：" + timeslot.getEventId());
            }
        } else {
            throw new CustomException("400", "无效的事件ID格式：" + timeslot.getEventId());
        }

        // 3. 插入新的时段记录（让数据库触发器生成ID）
        try {
            int inserted = activityTimeslotMapper.insert(timeslot);
            if (inserted == 0) {
                throw new CustomException("500", "添加时段失败");
            }
            return timeslot;
        } catch (Exception e) {
            throw new CustomException("500", "添加时段时发生错误：" + e.getMessage());
        }
    }

    /**
     * 删除一个时段，并自动更新活动/培训主表的起止时间
     * @param timeslotId 要删除的时段ID
     * @param eventId 关联的活动/培训ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteTimeslot(String timeslotId, String eventId) throws CustomException {
        // 1. 验证事件是否存在
        String eventType = eventId.substring(0, 3).toLowerCase();
        if ("act".equals(eventType)) {
            VolunteerActivity activity = volunteerActivityMapper.getById(eventId);
            if (activity == null) {
                throw new CustomException("404", "未找到活动：" + eventId);
            }
        } else if ("trn".equals(eventType)) {
            VolunteerTraining training = volunteerTrainingMapper.selectById(eventId);
            if (training == null) {
                throw new CustomException("404", "未找到培训：" + eventId);
            }
        } else {
            throw new CustomException("400", "无效的事件ID格式：" + eventId);
        }

        // 2. 删除时段
        try {
            int deleted = activityTimeslotMapper.deleteById(timeslotId);
            if (deleted == 0) {
                throw new CustomException("404", "未找到要删除的时段");
            }
        } catch (Exception e) {
            throw new CustomException("500", "删除时段时发生错误：" + e.getMessage());
        }
    }
    
    public List<ActivityTimeslot> getTimeslotsByEventId(String eventId) throws CustomException {
        if (eventId == null || eventId.trim().isEmpty()) {
            throw new CustomException("400", "查询时段列表必须提供事件ID");
        }
        return activityTimeslotMapper.selectByEventId(eventId);
    }
}