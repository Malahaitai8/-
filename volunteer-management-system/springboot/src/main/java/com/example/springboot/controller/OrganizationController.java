package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Organization;
import com.example.springboot.exception.CustomException;
import com.example.springboot.service.OrganizationService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/organization") // 基础路径保持不变
public class OrganizationController {

    @Resource
    private OrganizationService organizationService;
/**
     * (新接口) 更新指定组织的账户状态
     * API 端点: PUT /organization/status
     * 请求体: { "orgId": "...", "orgAccountStatus": "..." }
     */
    @PutMapping("/status")
    public Result updateStatus(@RequestBody Map<String, String> payload) {
        try {
            String orgId = payload.get("orgId");
            String newStatus = payload.get("orgAccountStatus");

            // 调用我们刚刚在 Service 中创建的新方法
            organizationService.updateOrganizationStatus(orgId, newStatus);

            return Result.success("组织状态更新成功");
        } catch (IllegalArgumentException e) {
            return Result.error("400", e.getMessage());
        } catch (RuntimeException e) {
             return Result.error("404", e.getMessage());
        } catch (Exception e) {
            // 记录日志 e.printStackTrace();
            return Result.error("500", "更新状态失败，系统内部错误");
        }
    }
    /**
     * 组织注册
     * API端点: POST /organization/register
     */
    @PostMapping("/register")
    public Result register(@RequestBody Organization organization) {
        try {
            // 前端发送的字段名应为: orgName, orgLoginUserName, orgLoginPassword, contactPersonPhone, serviceRegion, orgScale
            organizationService.register(organization);
            return Result.success("组织注册申请已提交，请等待审核");
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            // Log error e
            System.err.println("Register Error: " + e.getMessage()); // 简单打印错误
            return Result.error("500", "注册失败，系统内部错误");
        }
    }

    /**
     * 组织登录
     * API端点: POST /organization/login
     * 请求体: { "orgLoginUserName": "...", "orgLoginPassword": "..." }
     */
    @PostMapping("/login")
    public Result login(@RequestBody Organization organizationCredentials) {
        try {
            // 前端发送的字段名应为: orgLoginUserName, orgLoginPassword
            Organization loggedInOrganization = organizationService.login(organizationCredentials);
            return Result.success(loggedInOrganization);
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            System.err.println("Login Error: " + e.getMessage());
            return Result.error("500", "登录失败，系统内部错误");
        }
    }

/**
     * 修改组织密码
     * API端点: POST /organization/changePassword
     * 请求体: { "orgId": "...", "oldPassword": "...", "newPassword": "..." }
     */
    @PostMapping("/changePassword")
    public Result changePassword(@RequestBody Map<String, String> payload) {
        try {
            String orgId = payload.get("orgId");
            String oldPassword = payload.get("oldPassword");
            String newPassword = payload.get("newPassword");

            // Basic validation for presence of keys
            if (orgId == null || oldPassword == null || newPassword == null) {
                return Result.error("400", "请求参数不完整 (orgId, oldPassword, newPassword 均不能为空)");
            }

            organizationService.changePassword(orgId, oldPassword, newPassword);
            return Result.success("密码修改成功");
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            System.err.println("Change Password Error: " + e.getMessage());
            e.printStackTrace();
            return Result.error("500", "修改密码失败，系统内部错误");
        }
    }

    /**
     * 添加组织 (通常由管理员操作)
     * API端点: POST /organization/insert (或 /organization，根据RESTful风格可省略insert)
     */
    @PostMapping("/insert") // 或者 @PostMapping
    public Result insert(@RequestBody Organization organization) {
        try {
            organizationService.insert(organization);
            return Result.success("组织添加成功");
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            System.err.println("Insert Error: " + e.getMessage());
            return Result.error("500", "添加组织失败：" + e.getMessage());
        }
    }

    /**
     * 更新组织信息 (不包括密码和登录用户名)
     * API端点: PUT /organization/updateByOrgId (或 /organization/{orgId})
     */
    @PutMapping("/updateByOrgId") // 或者 @PutMapping("/{orgId}") 然后 @PathVariable
    public Result updateByOrgId(@RequestBody Organization organization) {
        try {
            organizationService.updateByOrgId(organization);
            return Result.success("组织信息更新成功");
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            System.err.println("Update Error: " + e.getMessage());
            return Result.error("500", "更新组织信息失败：" + e.getMessage());
        }
    }

    /**
     * 根据组织ID删除组织信息
     * API端点: DELETE /organization/deleteByOrgId/{orgId}
     */
    @DeleteMapping("/deleteByOrgId/{orgId}")
    public Result deleteByOrgId(@PathVariable String orgId) {
        try {
            organizationService.deleteByOrgId(orgId);
            return Result.success("组织删除成功");
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            System.err.println("Delete Error: " + e.getMessage());
            return Result.error("500", "删除组织失败：" + e.getMessage());
        }
    }

    /**
     * 批量删除组织信息
     * API端点: DELETE /organization/deleteBatch
     * 请求体: ["id1", "id2", ...]
     */
    @DeleteMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<String> orgIds) {
        try {
            organizationService.deleteBatch(orgIds);
            return Result.success("批量删除成功");
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            System.err.println("Delete Batch Error: " + e.getMessage());
            return Result.error("500", "批量删除失败：" + e.getMessage());
        }
    }

    /**
     * 查询所有组织 (可带过滤条件)
     * API端点: GET /organization/selectAll
     * 查询参数: 对应 Organization 实体类的字段 (驼峰式)
     */
    @GetMapping("/selectAll")
    public Result selectAll(Organization organizationFilter) {
        try {
            List<Organization> list = organizationService.selectAll(organizationFilter);
            return Result.success(list);
        } catch (Exception e) {
            System.err.println("Select All Error: " + e.getMessage());
            return Result.error("500", "查询所有组织失败：" + e.getMessage());
        }
    }

    /**
     * 根据组织ID查询组织信息
     * API端点: GET /organization/selectByOrgId/{orgId}
     */
    @GetMapping("/selectByOrgId/{orgId}")
    public Result selectByOrgId(@PathVariable String orgId) {
        try {
            Organization organization = organizationService.selectByOrgId(orgId);
            return Result.success(organization);
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            System.err.println("Select By ID Error: " + e.getMessage());
            return Result.error("500", "查询组织信息失败：" + e.getMessage());
        }
    }

    /**
     * 根据登录用户名查询组织机构信息
     * API端点: GET /organization/selectByOrgLoginUserName?orgLoginUserName=...
     */
    @GetMapping("/selectByOrgLoginUserName")
    public Result selectByOrgLoginUserName(@RequestParam String orgLoginUserName) {
        try {
            Organization organization = organizationService.selectByOrgLoginUserName(orgLoginUserName);
            return Result.success(organization);
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            System.err.println("Select By Username Error: " + e.getMessage());
            return Result.error("500", "查询组织信息失败：" + e.getMessage());
        }
    }

    /**
     * 分页查询组织列表
     * API端点: GET /organization/page?pageNum=1&pageSize=10&orgName=... (或其他过滤字段)
     */
    @GetMapping("/page")
    public Result selectPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            Organization organizationFilter) { // Spring会自动将查询参数映射到filter对象的驼峰式字段
        try {
            PageInfo<Organization> pageInfo = organizationService.selectPage(organizationFilter, pageNum, pageSize);
            return Result.success(pageInfo);
        } catch (Exception e) {
            System.err.println("Select Page Error: " + e.getMessage());
            return Result.error("500", "分页查询组织失败：" + e.getMessage());
        }
    }

    /**
     * 根据名称模糊查询组织列表
     * API 端点: GET /organization/findByOrgName?orgName=...
     */
    @GetMapping("/findByOrgName") // 保持与Service方法名一致性
    public Result findOrganizationsByOrgName(@RequestParam String orgName) {
        try {
            List<Organization> organizations = organizationService.findOrganizationsByOrgName(orgName);
            return Result.success(organizations);
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            System.err.println("Find By Name Error: " + e.getMessage());
            return Result.error("500", "按名称查询组织失败：" + e.getMessage());
        }
    }
}
