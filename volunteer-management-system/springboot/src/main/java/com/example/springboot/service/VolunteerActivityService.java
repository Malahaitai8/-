package com.example.springboot.service;

import com.example.springboot.entity.VolunteerActivity;
import com.example.springboot.exception.CustomException;
import com.example.springboot.mapper.VolunteerActivityMapper;
// import com.github.pagehelper.PageHelper; // 如果将来需要分页
// import com.github.pagehelper.PageInfo; // 如果将来需要分页
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
// import java.util.Date; // 如果需要处理日期逻辑
import java.util.List;

@Service
public class VolunteerActivityService {

    @Resource
    private VolunteerActivityMapper volunteerActivityMapper;

    // 定义合法的活动状态常量 (与前端和数据库表定义一致)
    private static final List<String> VALID_ACTIVITY_STATUSES = Arrays.asList(
            "待审核", "审核通过", "审核不通过", "进行中", "已结束", "已停用"
    );

    /**
     * 管理员审核/更新活动状态。
     *
     * @param activityId      活动ID
     * @param newStatus       新的活动状态
     * @param reviewerAdminId 操作的管理员ID
     * @param remarks         (可选) 备注或驳回理由
     * @throws CustomException 如果参数无效、活动不存在或状态转换不允许
     */
    @Transactional // 建议将写操作声明为事务性
    public void reviewActivity(String activityId, String newStatus, String reviewerAdminId, String remarks) throws CustomException {
        if (!StringUtils.hasText(activityId) || !StringUtils.hasText(newStatus)) {
            throw new CustomException("400", "活动ID和新状态不能为空");
        }
        if (!VALID_ACTIVITY_STATUSES.contains(newStatus)) {
            throw new CustomException("400", "无效的活动状态: " + newStatus);
        }
        // 根据您的业务逻辑，reviewerAdminId 是否必须可以在这里校验
        // if (!StringUtils.hasText(reviewerAdminId)) {
        //     throw new CustomException("400", "审核管理员ID不能为空");
        // }

        VolunteerActivity activity = volunteerActivityMapper.selectById(activityId);
        if (activity == null) {
            throw new CustomException("404", "活动 " + activityId + " 不存在");
        }

        String currentStatus = activity.getActivityStatus();
        System.out.println("Activity " + activityId + ": current status = " + currentStatus + ", attempting to change to " + newStatus + " by admin " + reviewerAdminId);

        // 如果您的 tbl_VolunteerActivity 表有字段存储驳回理由 (例如 RejectionReason)
        // 并且您希望在驳回时记录它，可以在这里处理：
        // if ("审核不通过".equals(newStatus) && StringUtils.hasText(remarks)) {
        //    // activity.setRejectionReason(remarks); // 假设实体 VolunteerActivity 有 setRejectionReason 方法
        //    // 然后确保 updateActivityStatus mapper方法或一个新的mapper方法会更新这个字段
        //    System.out.println("Rejection reason for activity " + activityId + ": " + remarks);
        // }

        // 调用Mapper更新状态和审核员ID
        int updatedRows = volunteerActivityMapper.updateActivityStatus(activityId, newStatus, reviewerAdminId);
        if (updatedRows == 0) {
            // 如果前面 selectById 找到了活动，这里不应该为0，除非并发删除了
            throw new CustomException("500", "数据库更新活动状态失败");
        }
        System.out.println("Admin " + (reviewerAdminId != null ? reviewerAdminId : "SYSTEM") +
                           " reviewed activity " + activityId + ", new status: " + newStatus);
    }

    /**
     * 获取所有志愿活动列表 (供管理员使用)
     * @param filter (可选) 过滤条件，目前前端未使用特定过滤条件调用此方法。
     * 如果 VolunteerActivityMapper.selectAll 方法需要一个非null的参数，
     * 即使没有过滤条件，也应传入一个空的 VolunteerActivity 对象。
     * @return 活动列表
     */
    public List<VolunteerActivity> getAllActivitiesForAdmin(VolunteerActivity filter) {
        // 如果 VolunteerActivityMapper.selectAll 不能接受 null 参数，则确保 filter 不是 null
        if (filter == null) {
            filter = new VolunteerActivity(); // 创建一个空对象作为默认过滤器
        }
        List<VolunteerActivity> activities = volunteerActivityMapper.selectAll(filter);
        // 可以在这里进行数据转换或补充，例如根据 orgId 获取组织名称填充到活动对象中（如果需要）
        return activities;
    }

    // 您可能已经有的其他 Service 方法，例如：
    // public VolunteerActivity getActivityDetails(String activityId) throws CustomException { ... }
    // public PageInfo<VolunteerActivity> getActivityPage(VolunteerActivity filter, int pageNum, int pageSize) { ... }
}
