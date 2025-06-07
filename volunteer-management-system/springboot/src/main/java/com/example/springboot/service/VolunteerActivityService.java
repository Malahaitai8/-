package com.example.springboot.service;

import com.example.springboot.entity.Organization;
import com.example.springboot.entity.VolunteerActivity;
import com.example.springboot.entity.VolunteerTraining; // 保留此行，如果其他非活动相关方法（如 reviewTraining）需要使用
import com.example.springboot.exception.CustomException;
import com.example.springboot.mapper.VolunteerActivityMapper;
import com.example.springboot.mapper.OrganizationMapper;
import com.example.springboot.mapper.VolunteerTrainingMapper; // 保留此行，如果其他非活动相关方法需要使用
import com.github.pagehelper.PageHelper; // 保留此行，如果 selectPage 等方法需要使用
import com.github.pagehelper.PageInfo; // 保留此行，如果 selectPage 等方法需要使用
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class VolunteerActivityService {

    @Resource
    private VolunteerActivityMapper volunteerActivityMapper;

    @Resource
    private OrganizationMapper organizationMapper;

    @Resource // 确保注入了 VolunteerTrainingMapper，如果这个Service确实也处理了Training相关逻辑
    private VolunteerTrainingMapper volunteerTrainingMapper;

    // 定义合法的活动状态常量
    private static final List<String> VALID_ACTIVITY_STATUSES = Arrays.asList(
            "待审核", "审核通过", "审核不通过", "进行中", "已结束", "已停用"
    );
    // 定义合法的培训状态常量 (如果此Service也处理培训状态，则保留)
    private static final List<String> VALID_TRAINING_STATUSES = Arrays.asList(
            "待审核", "审核通过", "审核不通过", "进行中", "已结束", "已停用"
    );


    /**
     * 组织申请新的志愿活动
     * @param volunteerActivity 志愿活动对象
     * @throws CustomException 如果校验失败或组织不符合条件
     */
    @Transactional
    public void addActivity(VolunteerActivity volunteerActivity) throws CustomException {
        // 1. 参数非空校验
        if (volunteerActivity == null || volunteerActivity.getOrgId() == null || volunteerActivity.getOrgId().trim().isEmpty()) {
            throw new CustomException("400", "组织ID不能为空");
        }
        if (volunteerActivity.getActivityName() == null || volunteerActivity.getActivityName().trim().isEmpty()) {
            throw new CustomException("400", "活动名称不能为空");
        }
        if (volunteerActivity.getStartTime() == null || volunteerActivity.getEndTime() == null) {
            throw new CustomException("400", "活动开始和结束时间不能为空");
        }
        if (volunteerActivity.getStartTime().after(volunteerActivity.getEndTime())) {
            throw new CustomException("400", "活动开始时间不能晚于结束时间");
        }
        if (volunteerActivity.getLocation() == null || volunteerActivity.getLocation().trim().isEmpty()) {
            throw new CustomException("400", "活动地点不能为空");
        }
        if (volunteerActivity.getRecruitmentCount() == null || volunteerActivity.getRecruitmentCount() <= 0) {
            throw new CustomException("400", "招募人数必须大于0");
        }
        if (volunteerActivity.getContactPersonPhone() == null || volunteerActivity.getContactPersonPhone().trim().isEmpty()){
            throw new CustomException("400", "负责人联系方式不能为空");
        }

        // 2. 校验组织状态：只有“已认证”的组织才能发布活动
        Organization organization = organizationMapper.selectByOrgId(volunteerActivity.getOrgId());
        if (organization == null) {
            throw new CustomException("404", "发布活动的组织不存在");
        }
        if (!"已认证".equals(organization.getOrgAccountStatus())) {
            throw new CustomException("403", "组织账号未认证，无法发布活动");
        }

        // 3. 设置默认值 (如果前端未提供或提供不完整)
        // ActivityID 由数据库触发器自动生成，此处无需手动设置
        volunteerActivity.setActivityStatus("待审核");
        volunteerActivity.setAcceptedCount(0);
        volunteerActivity.setActivityDurationHours(0); // 明确设置为0
        volunteerActivity.setIsRatingAggregated("NO");
        volunteerActivity.setActivityRating(null); // 初始活动评分可以为NULL
        volunteerActivity.setReviewerAdminId(null); // 初始审核管理员ID为NULL

        // 4. 调用 Mapper 插入数据
        try {
            volunteerActivityMapper.insert(volunteerActivity);
        } catch (Exception e) {
            System.err.println("插入活动失败: " + e.getMessage());
            throw new CustomException("500", "创建活动失败，请检查输入或联系管理员");
        }
    }

    /**
     * 根据培训ID更新志愿培训信息
     * (注意：此方法看起来是用于Training的，如果ActivityService不管理Training，应将其移至VolunteerTrainingService)
     * @param volunteerTraining 志愿培训对象
     * @throws CustomException if validation fails or training not found
     */
    @Transactional
    public void updateTraining(VolunteerTraining volunteerTraining) throws CustomException {
        if (volunteerTraining == null || volunteerTraining.getTrainingId() == null) {
            // 将这里的 CustomException 构造器调用修改为只传递两个参数
            throw new CustomException("培训ID不能为空以进行更新", "400"); // <-- 修改这一行
        }
        VolunteerTraining existingTraining = volunteerTrainingMapper.selectById(volunteerTraining.getTrainingId());
        if (existingTraining == null) {
            throw new CustomException("未找到要更新的培训，ID: " + volunteerTraining.getTrainingId(), "404");
        }

        if (volunteerTraining.getStartTime() != null && volunteerTraining.getEndTime() != null &&
                volunteerTraining.getStartTime().after(volunteerTraining.getEndTime())) {
            throw new CustomException("培训开始时间不能晚于结束时间", "400");
        }
        if (volunteerTraining.getRecruitmentCount() != null && volunteerTraining.getRecruitmentCount() <= 0) {
            throw new CustomException("招募人数必须大于0", "400");
        }
        if (volunteerTraining.getTrainingStatus() != null && !VALID_TRAINING_STATUSES.contains(volunteerTraining.getTrainingStatus())) {
            throw new CustomException("无效的培训状态: " + volunteerTraining.getTrainingStatus(), "400");
        }

        volunteerTrainingMapper.updateById(volunteerTraining);
    }
    /**
     * 根据培训ID删除志愿培训
     * (注意：此方法看起来是用于Training的，如果ActivityService不管理Training，应将其移至VolunteerTrainingService)
     * @param trainingId 培训ID
     * @throws CustomException if validation fails or training not found
     */
    @Transactional
    public void deleteTrainingById(String trainingId) throws CustomException {
        if (trainingId == null || trainingId.trim().isEmpty()) {
            throw new CustomException("培训ID不能为空", "400");
        }
        // 确保这里调用的是 volunteerTrainingMapper
        VolunteerTraining existingTraining = volunteerTrainingMapper.selectById(trainingId);
        if (existingTraining == null) {
            throw new CustomException("未找到要删除的培训，ID: " + trainingId, "404");
        }
        // 确保这里调用的是 volunteerTrainingMapper
        volunteerTrainingMapper.deleteById(trainingId);
    }
    /**
     * 新增方法：根据组织ID查询该组织发布的所有活动
     * @param orgId 组织ID
     * @return 志愿活动列表
     * @throws CustomException 如果组织ID为空或未找到活动
     */
    public List<VolunteerActivity> getActivitiesByOrgId(String orgId) throws CustomException {
        if (orgId == null || orgId.trim().isEmpty()) {
            throw new CustomException("400", "组织ID不能为空");
        }
        List<VolunteerActivity> activities = volunteerActivityMapper.selectByOrgId(orgId);
        // 可以在这里添加额外的业务逻辑，例如，如果列表为空是否抛出异常，或者根据权限过滤
        return activities;
    }
    /**
     * 根据培训ID查询志愿培训信息
     * (注意：此方法看起来是用于Training的，如果ActivityService不管理Training，应将其移至VolunteerTrainingService)
     * @param trainingId 培训ID
     * @return 志愿培训对象
     * @throws CustomException if validation fails
     */
    public VolunteerTraining getTrainingById(String trainingId) throws CustomException {
        if (trainingId == null || trainingId.trim().isEmpty()) {
            throw new CustomException("培训ID不能为空", "400");
        }
        // 确保这里调用的是 volunteerTrainingMapper
        return volunteerTrainingMapper.selectById(trainingId);
    }

    /**
     * 查询所有志愿培训 (可带条件过滤)
     * (注意：此方法看起来是用于Training的，如果ActivityService不管理Training，应将其移至VolunteerTrainingService)
     * @param volunteerTraining 包含过滤条件的志愿培训对象
     * @return 志愿培训列表
     */
    public List<VolunteerTraining> getAllTrainings(VolunteerTraining volunteerTraining) {
        // 确保这里调用的是 volunteerTrainingMapper
        return volunteerTrainingMapper.selectAll(volunteerTraining);
    }

    /**
     * 分页查询志愿培训信息
     * (注意：此方法看起来是用于Training的，如果ActivityService不管理Training，应将其移至VolunteerTrainingService)
     * @param volunteerTraining 包含过滤条件的志愿培训对象
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页后的志愿培训列表
     */
    public PageInfo<VolunteerTraining> getTrainingPage(VolunteerTraining volunteerTraining, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        // 确保这里调用的是 volunteerTrainingMapper
        List<VolunteerTraining> list = volunteerTrainingMapper.selectAll(volunteerTraining);
        return PageInfo.of(list);
    }

    /**
     * 根据组织ID查询培训
     * (注意：此方法看起来是用于Training的，如果ActivityService不管理Training，应将其移至VolunteerTrainingService)
     * @param orgId 组织ID
     * @return 志愿培训列表
     * @throws CustomException if validation fails
     */
    public List<VolunteerTraining> getTrainingsByOrgId(String orgId) throws CustomException {
        if (orgId == null || orgId.trim().isEmpty()) {
            throw new CustomException("组织ID不能为空", "400");
        }
        // 确保这里调用的是 volunteerTrainingMapper
        return volunteerTrainingMapper.selectByOrgId(orgId);
    }

    /**
     * 根据培训状态查询培训
     * (注意：此方法看起来是用于Training的，如果ActivityService不管理Training，应将其移至VolunteerTrainingService)
     * @param status 培训状态
     * @return 志愿培训列表
     * @throws CustomException if validation fails
     */
    public List<VolunteerTraining> getTrainingsByStatus(String status) throws CustomException {
        if (status == null || status.trim().isEmpty() || !VALID_TRAINING_STATUSES.contains(status)) {
            throw new CustomException("无效或空的培训状态", "400");
        }
        // 确保这里调用的是 volunteerTrainingMapper
        return volunteerTrainingMapper.selectByStatus(status);
    }

    /**
     * 根据培训主题查询培训
     * (注意：此方法看起来是用于Training的，如果ActivityService不管理Training，应将其移至VolunteerTrainingService)
     * @param theme 培训主题
     * @return 志愿培训列表
     * @throws CustomException if validation fails
     */
    public List<VolunteerTraining> getTrainingsByTheme(String theme) throws CustomException {
        if (theme == null || theme.trim().isEmpty()) {
            throw new CustomException("培训主题不能为空", "400");
        }
        // 确保这里调用的是 volunteerTrainingMapper
        return volunteerTrainingMapper.selectByTheme(theme);
    }

    /**
     * 审核/更新培训状态
     * (注意：此方法看起来是用于Training的，如果ActivityService不管理Training，应将其移至VolunteerTrainingService)
     * @param trainingId 培训ID
     * @param newStatus 新的状态
     * @param reviewerAdminId 审核员ID (可以为null)
     * @throws CustomException if validation fails or training not found
     */
    @Transactional
    public void reviewTraining(String trainingId, String newStatus, String reviewerAdminId) throws CustomException {
        if (trainingId == null || trainingId.trim().isEmpty()) {
            throw new CustomException("培训ID不能为空", "400");
        }
        if (newStatus == null || !VALID_TRAINING_STATUSES.contains(newStatus)) {
            throw new CustomException("无效的培训状态: " + newStatus, "400");
        }
        // 确保这里调用的是 volunteerTrainingMapper
        VolunteerTraining training = volunteerTrainingMapper.selectById(trainingId);
        if (training == null) {
            throw new CustomException("未找到培训: " + trainingId, "404");
        }
        // 确保这里调用的是 volunteerTrainingMapper
        volunteerTrainingMapper.updateTrainingStatus(trainingId, newStatus, reviewerAdminId);
    }

    /**
     * 管理员审核/更新活动状态。
     *
     * @param activityId      活动ID
     * @param newStatus       新的活动状态
     * @param reviewerAdminId 操作的管理员ID
     * @param remarks         (可选) 备注或驳回理由
     * @throws CustomException 如果参数无效、活动不存在或状态转换不允许
     */
    @Transactional
    public void reviewActivity(String activityId, String newStatus, String reviewerAdminId, String remarks) throws CustomException {
        if (!StringUtils.hasText(activityId) || !StringUtils.hasText(newStatus)) {
            throw new CustomException("400", "活动ID和新状态不能为空");
        }
        if (!VALID_ACTIVITY_STATUSES.contains(newStatus)) {
            throw new CustomException("400", "无效的活动状态: " + newStatus);
        }

        VolunteerActivity activity = volunteerActivityMapper.selectById(activityId);
        if (activity == null) {
            throw new CustomException("404", "活动 " + activityId + " 不存在");
        }

        String currentStatus = activity.getActivityStatus();
        System.out.println("Activity " + activityId + ": current status = " + currentStatus + ", attempting to change to " + newStatus + " by admin " + reviewerAdminId);

        int updatedRows = volunteerActivityMapper.updateActivityStatus(activityId, newStatus, reviewerAdminId);
        if (updatedRows == 0) {
            throw new CustomException("500", "数据库更新活动状态失败");
        }
        System.out.println("Admin " + (reviewerAdminId != null ? reviewerAdminId : "SYSTEM") +
                " reviewed activity " + activityId + ", new status: " + newStatus);
    }

    /**
     * 获取所有志愿活动列表 (供管理员使用)
     * @param filter (可选) 过滤条件
     * @return 活动列表
     */
    public List<VolunteerActivity> getAllActivitiesForAdmin(VolunteerActivity filter) {
        if (filter == null) {
            filter = new VolunteerActivity();
        }
        List<VolunteerActivity> activities = volunteerActivityMapper.selectAll(filter);
        return activities;
    }

    // 可以添加其他 VolunteerActivityService 独有的方法
}