package com.example.springboot.service;

import com.example.springboot.entity.Administrator;
import com.example.springboot.exception.CustomException;
import com.example.springboot.mapper.AdministratorMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
//package com.example.springboot.service;

import com.example.springboot.entity.Administrator;
import com.example.springboot.exception.CustomException;
import com.example.springboot.mapper.AdministratorMapper;
import jakarta.annotation.Resource;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Service
public class AdministratorService {
    @Resource
    private AdministratorMapper administratorMapper;

    /**
     * 修改管理员密码
     * @param adminId 管理员ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @throws CustomException 验证失败或更新失败
     */
    public void changePassword(String adminId, String oldPassword, String newPassword) throws CustomException {
        if (!StringUtils.hasText(adminId) || !StringUtils.hasText(oldPassword) || !StringUtils.hasText(newPassword)) {
            throw new CustomException("400", "管理员ID、原密码和新密码均不能为空");
        }

        Administrator admin = administratorMapper.selectByAdminIdWithPassword(adminId.trim());
        if (admin == null) {
            throw new CustomException("404", "管理员账号不存在");
        }

        // 验证旧密码 (实际应使用 passwordEncoder.matches)
        if (!oldPassword.equals(admin.getPassword())) {
            throw new CustomException("401", "原密码不正确");
        }

        if (newPassword.length() < 6) { // 简单密码强度校验
            throw new CustomException("400", "新密码长度不能少于6位");
        }
        if (oldPassword.equals(newPassword)) {
            throw new CustomException("400", "新密码不能与原密码相同");
        }

        // 实际项目中新密码应加密存储
        // String encodedNewPassword = passwordEncoder.encode(newPassword);
        int updatedRows = administratorMapper.updatePassword(adminId.trim(), newPassword /* encodedNewPassword */);
        if (updatedRows == 0) {
            throw new CustomException("500", "密码更新失败，请重试");
        }
    }


      /**
     * 更新管理员信息（不包括密码）。
     * 这个方法对应前端的 updateAdminInfo。
     * @param administrator 包含要更新的管理员信息的对象 (adminId 是必须的)
     * @throws CustomException 如果管理员不存在或 adminId 为空
     */
    // 在 AdministratorService.java 的 updateAdminInfo 方法中
public void updateAdminInfo(Administrator administrator) throws CustomException {
    if (administrator == null || administrator.getAdminId() == null || administrator.getAdminId().trim().isEmpty()) { // 修正ID检查
        throw new CustomException("400", "管理员ID不能为空以进行更新");
    }
    // 清理 AdminID 中的空格
    administrator.setAdminId(administrator.getAdminId().trim());

    Administrator existingAdmin = administratorMapper.selectByAdminId(administrator.getAdminId()); // selectByAdminId 内部也应该 trim
    if (existingAdmin == null) {
        throw new CustomException("404", "要更新的管理员不存在");
    }
    administrator.setPassword(null);
    administratorMapper.updateById(administrator);
}

    /**
     * 根据管理员ID查询管理员信息.
     * 此方法用于前端获取详细信息，会擦除密码。
     *
     * @param adminId 管理员ID
     * @return 管理员对象 (密码已擦除)
     * @throws CustomException 如果ID为空或管理员不存在
     */
    public Administrator selectByAdminId(String adminId) throws CustomException {
        if (!StringUtils.hasText(adminId)) {
            throw new CustomException("400", "管理员ID不能为空");
        }
        // .trim() 可以在Controller层或者这里处理，确保查询准确性
        Administrator administrator = administratorMapper.selectByAdminId(adminId.trim());
        if (administrator == null) {
            throw new CustomException("404", "管理员不存在");
        }
        administrator.setPassword(null); // 重要：不返回密码给前端
        return administrator;
    }


    // 插入新的管理员信息
    public void insert(Administrator administrator) {
        administratorMapper.insert(administrator);
    }

    // 根据管理员ID更新管理员信息
    public void updateById(Administrator administrator) {
        administratorMapper.updateById(administrator);
    }

    // 查询所有管理员信息
    public List<Administrator> selectAll(Administrator administrator) {
        return administratorMapper.selectAll(administrator);
    }

    // 根据管理员ID查询管理员信息
    public Administrator selectByID(String adminId) {
        return administratorMapper.selectByID(adminId);
    }

    // 分页查询管理员信息
    public PageInfo<Administrator> selectPage(Administrator administrator, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Administrator> list = administratorMapper.selectAll(administrator);
        return PageInfo.of(list);
    }

    // 根据管理员ID删除管理员信息
    public void deleteById(String adminId) {
        administratorMapper.deleteById(adminId);
    }

    // 批量删除管理员信息
    public void deleteBatch(List<String> adminIds) {
        for (String id : adminIds) {
            this.deleteById(id);
        }
    }

    // 管理员登录
    /**
     * 管理员登录 - 修改为接收 Administrator 实体
     * @param administratorRequest 包含登录凭据的 Administrator 对象
     * @return 登录成功的 Administrator 对象
     * @throws CustomException 登录失败时抛出
     */
    public Administrator adminLogin(Administrator administratorRequest) throws CustomException {
        if (administratorRequest == null) {
            throw new CustomException("登录请求不能为空", "400");
        }

        String identifier = administratorRequest.getAdminId(); // 优先使用 AdminId 作为标识符
        if (identifier == null || identifier.trim().isEmpty()) {
            identifier = administratorRequest.getName(); // 如果 AdminId 为空，尝试使用 Name
        }

        if (identifier == null || identifier.trim().isEmpty()) {
            throw new CustomException("管理员标识 (ID或姓名) 不能为空", "LGN000");
        }
        if (administratorRequest.getPassword() == null || administratorRequest.getPassword().isEmpty()) {
            throw new CustomException("密码不能为空", "LGN000");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("adminIdentifier", identifier);
        params.put("password", administratorRequest.getPassword());

        try {
            Administrator admin = administratorMapper.callAdminLogin(params);

            if (admin == null) {
                // 此情况理论上应由存储过程的 RAISERROR 覆盖，导致 SQLException
                // 但作为防御性编程，如果存储过程某天没有RAISERROR而是静默返回NULL (不符合当前SP设计)
                throw new CustomException("登录验证失败，管理员信息未返回。", "500");
            }
            return admin;
        } catch (PersistenceException | DataAccessException e) {
            Throwable cause = e.getCause();
            if (cause instanceof SQLException) {
                SQLException sqlEx = (SQLException) cause;
                String message = sqlEx.getMessage();
                if (message != null && message.contains("不存在")) {
                    throw new CustomException("管理员账号不存在", "LGN001");
                } else if (message != null && message.contains("密码错误")) {
                    throw new CustomException("密码错误", "LGN002");
                } else {
                    throw new CustomException("登录时数据库处理失败: " + message, "DB500");
                }
            }
            // 移除了对 CustomException 的捕获，因为这里主要是处理数据库/持久化层面的异常
            // 如果 CustomException 从其他地方抛出，让上层（如Controller或全局异常处理器）处理
            throw new CustomException("登录服务异常，请稍后再试。", "SRV500");
        }
    }
//    public Administrator adminLogin(Administrator administrator) throws CustomException {
//        String name = administrator.getName();
//        Administrator dbAdmin = administratorMapper.selectByName(name);
//        if (dbAdmin == null) {
//            throw new CustomException("账号不存在", "500");
//        }
//        String password = administrator.getPassword();
//        if (!password.equals(dbAdmin.getPassword())) {
//            throw new CustomException("密码错误", "500");
//        }
//        return dbAdmin;
//    }

    // 管理员注册
    public void register(Administrator administrator) throws CustomException {
        String name = administrator.getName();
        Administrator dbAdmin = administratorMapper.selectByName(name);
        if (dbAdmin != null) {
            throw new CustomException("账号已存在", "500");
        }
        this.insert(administrator);
    }
}