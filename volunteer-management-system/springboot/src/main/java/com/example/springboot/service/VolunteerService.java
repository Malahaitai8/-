package com.example.springboot.service;

import com.example.springboot.entity.Employee;
import com.example.springboot.entity.Volunteer;
import com.example.springboot.exception.CustomException;
import com.example.springboot.mapper.VolunteerMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class VolunteerService {
    @Resource
    private VolunteerMapper volunteerMapper;


    public void updateVolunteerAccountStatus(String volunteerId, String newStatus, String rejectionReason /*, String adminId */) throws CustomException {
        if (!StringUtils.hasText(volunteerId) || !StringUtils.hasText(newStatus)) {
            throw new CustomException("400", "志愿者ID和新状态不能为空");
        }
        // 校验 newStatus 是否为有效状态值
        // ...

        Volunteer volunteer = volunteerMapper.selectByID(volunteerId); // 假设 selectByID 能获取到实体
        if (volunteer == null) {
            throw new CustomException("404", "志愿者不存在");
        }

        // 可以在这里添加业务逻辑，例如：
        // if (VOLUNTEER_STATUS.PENDING.equals(volunteer.getAccountStatus()) &&
        //     (VOLUNTEER_STATUS.APPROVED.equals(newStatus) || VOLUNTEER_STATUS.REJECTED.equals(newStatus))) {
        //   // 允许操作
        // } else if (...) { ... }
        // else {
        //   throw new CustomException("400", "不允许的状态变更");
        // }

        // 如果是驳回，可能需要记录驳回理由
        // if (VOLUNTEER_STATUS.REJECTED.equals(newStatus) && StringUtils.hasText(rejectionReason)) {
        //   // 存储 rejectionReason 到数据库的相应字段（如果您的表有此字段）
        // }

        int updatedRows = volunteerMapper.updateAccountStatus(volunteerId, newStatus); // 调用新的Mapper方法
        if (updatedRows == 0) {
            throw new CustomException("500", "数据库更新志愿者状态失败");
        }
        // 可能需要记录操作日志，包括 adminId
    }

    /**
     * 修改志愿者密码的业务逻辑
     *
     * @param volunteerId 志愿者的ID
     * @param oldPassword 用户输入的原密码
     * @param newPassword 用户输入的新密码
     * @throws CustomException 如果用户不存在、原密码不匹配、或新密码为空
     */
    public void changePassword(String volunteerId, String oldPassword, String newPassword) throws CustomException {
        // 1. 根据 volunteerId 查询现有志愿者信息
        Volunteer dbVolunteer = volunteerMapper.selectByID(volunteerId);

        if (dbVolunteer == null) {
            throw new CustomException("404", "志愿者账号不存在");
        }

        // 2. 验证旧密码是否匹配
        if (!oldPassword.equals(dbVolunteer.getPassword())) {
            throw new CustomException("401", "原密码不正确");
        }

        // 3. 验证新密码是否为空 (前端通常已做，但后端也应做安全检查)
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new CustomException("400", "新密码不能为空");
        }

        // 4. 可以闯进一些别的方法进行判断和处理 (例如密码强度验证)
        validatePasswordStrength(newPassword); // 调用辅助方法

        // 5. 更新密码
        volunteerMapper.updatePassword(volunteerId, newPassword);
    }

    /**
     * 辅助方法：验证密码强度 (示例，你可以根据需要实现更复杂的逻辑)
     *
     * @param password 要验证的密码
     * @throws CustomException 如果密码不符合强度要求
     */
    private void validatePasswordStrength(String password) throws CustomException {
        if (password.length() < 6) {
            throw new CustomException("400", "新密码长度不能少于6位");
        }
        // 更多规则可以添加
    }


    /**
     * 根据志愿者ID从视图中获取志愿者星级。
     *
     * @param volunteerId 志愿者的ID
     * @return 志愿者的星级 (整数)。如果ID无效或未找到星级，则返回 0星 作为默认值。
     * @throws IllegalArgumentException 如果 volunteerId 为 null 或空字符串。
     */
    public int getVolunteerStarLevel(String volunteerId) {
        if (volunteerId == null || volunteerId.trim().isEmpty()) {
            throw new IllegalArgumentException("志愿者ID不能为空或空白"); // 抛出非法参数异常
        }

        // 调用 Mapper 方法查询视图，确保对传入的 volunteerId 也进行了 trim()
        // 使用 Optional 来处理 findStarLevelByVolunteerID 可能返回 null 的情况
        Integer starLevel = volunteerMapper.findStarLevelByVolunteerID(volunteerId.trim());

        // 如果数据库中没有对应的星级记录 (Mapper 返回 null)，则返回 0 星
        return Optional.ofNullable(starLevel).orElse(0);
    }

    // 插入新的志愿者信息
    public void insert(Volunteer volunteer) {
        volunteerMapper.insert(volunteer);
    }

    // 根据志愿者ID更新志愿者信息
    public void updateById(Volunteer volunteer) {
        volunteerMapper.updateById(volunteer);
    }

    // 查询所有志愿者信息
    public List<Volunteer> selectAll(Volunteer volunteer) {
        return volunteerMapper.selectAll(volunteer);
    }

    // 根据志愿者ID查询志愿者信息
    public Volunteer selectByID(String volunteerId) {
        return volunteerMapper.selectByID(volunteerId);
    }

    // 分页查询志愿者信息
    public PageInfo<Volunteer> selectPage(Volunteer volunteer, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Volunteer> list = volunteerMapper.selectAll(volunteer);
        return PageInfo.of(list);
    }

    // 根据志愿者ID删除志愿者信息
    public void deleteById(String volunteerId) {
        volunteerMapper.deleteById(volunteerId);
    }

    //查找用户名对应用户
    public Volunteer selectByUsername(String username) {
        return volunteerMapper.selectByUsername(username);
    }

    // 批量删除志愿者信息
    public void deleteBatch(List<String> volunteerIds) {
        for (String id : volunteerIds) {
            this.deleteById(id);
        }
    }

    // 志愿者登录
    public Volunteer volunteerLogin(Volunteer volunteer) throws CustomException {
        String username = volunteer.getUsername();
        Volunteer dbvolunteer = volunteerMapper.selectByUsername(username);
        if (dbvolunteer == null) {
            throw new CustomException("账号不存在", "500");
        }
        String password = volunteer.getPassword();
        if (!password.equals(dbvolunteer.getPassword())) {
            throw new CustomException("密码错误", "500");
        }
        return dbvolunteer;
    }

    // 志愿者注册
    public void register(Volunteer volunteer) throws CustomException {
        String username = volunteer.getUsername();
        Volunteer dbVolunteer = volunteerMapper.selectByUsername(username);
        if (dbVolunteer != null) {
            throw new CustomException("账号已存在", "500");
        }
        this.insert(volunteer);
    }
}