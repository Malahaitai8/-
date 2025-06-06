package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Administrator;
import com.example.springboot.exception.CustomException;
import com.example.springboot.service.AdministratorService;
import com.example.springboot.service.VolunteerService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/administrator")
public class AdministratorController {
    @Resource
    private AdministratorService administratorService;

    /**
     * 修改管理员密码
     * API端点: POST /administrator/changePassword
     * 请求体: { "adminId": "...", "oldPassword": "...", "newPassword": "..." }
     */
    @PostMapping("/changePassword")
    public Result changePassword(@RequestBody Map<String, String> payload) {
        try {
            String adminId = payload.get("adminId");
            String oldPassword = payload.get("oldPassword");
            String newPassword = payload.get("newPassword");
            // 简单的非空检查
            if (adminId == null || oldPassword == null || newPassword == null ||
                adminId.trim().isEmpty() || oldPassword.isEmpty() || newPassword.isEmpty()) {
                return Result.error("400", "管理员ID、原密码和新密码均不能为空");
            }
            administratorService.changePassword(adminId, oldPassword, newPassword);
            return Result.success("密码修改成功");
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            System.err.println("Change password error: " + e.getMessage());
            e.printStackTrace();
            return Result.error("500", "修改密码失败，系统内部错误");
        }
    }

    /**
     * 更新管理员个人信息 (不包括密码)
     * API端点: PUT /administrator/updateInfo
     * 前端应发送包含 adminId 及其他要更新字段的 JSON 对象 (密码字段不应包含或将被忽略)
     */
    @PutMapping("/updateInfo")
    public Result updateAdminInfo(@RequestBody Administrator administrator) {
        try {
            // 前端应确保发送了 adminId
            if (administrator.getAdminId() == null || administrator.getAdminId().trim().isEmpty()) {
                return Result.error("400", "管理员ID不能为空");
            }
            // Service 层会处理密码字段（不更新）和其他业务逻辑
            administratorService.updateAdminInfo(administrator);
            return Result.success("个人信息更新成功");
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            // Log error e
            return Result.error("500", "更新信息失败，系统内部错误: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public Result adminLogin(@RequestBody Administrator administrator) {
        try {
            Administrator loggedInAdmin = administratorService.adminLogin(administrator);
            // 登录成功，返回管理员信息 (通常不包括密码)
            // 可以在 Administrator 实体中控制序列化，或创建一个 AdminDTO 返回给前端
            if (loggedInAdmin != null) {
                loggedInAdmin.setPassword(null); // 清除密码信息再返回给前端
            }
            return Result.success(loggedInAdmin);
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            // 其他未预料的异常
            // 最好记录一下这个异常 e.printStackTrace(); 或使用日志框架
            return Result.error("500", "登录时发生未知错误: " + e.getMessage());
        }
    }
    /**
     * 根据管理员ID查询管理员详细信息.
     * API端点: GET /administrator/selectByAdminId/{adminId}
     *
     * @param adminId 管理员ID，从路径中获取
     * @return 包含管理员信息的Result对象
     */
    @GetMapping("/selectByAdminId/{adminId}")
    public Result selectByAdminId(@PathVariable String adminId) {
        try {
            Administrator administrator = administratorService.selectByAdminId(adminId);
            return Result.success(administrator); // administrator 对象的密码已在Service层被设为null
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            // 记录通用异常日志
            System.err.println("Error fetching admin by ID: " + adminId + "; Error: " + e.getMessage());
            return Result.error("500", "查询管理员信息失败，系统内部错误");
        }
    }

    }
