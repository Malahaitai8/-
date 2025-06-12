// src/main/java/com/example/springboot/controller/VolunteerOrganizationJoinController.java
package com.example.springboot.controller;

import com.example.springboot.common.Result; // 假设这是您的统一响应封装类
import com.example.springboot.exception.CustomException; // 假设这是您的自定义异常类
import com.example.springboot.service.VolunteerOrganizationJoinService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/volunteerOrganizationJoin") // 定义基础请求路径
public class VolunteerOrganizationJoinController {

    @Resource
    private VolunteerOrganizationJoinService volunteerOrganizationJoinService;

    /**
     * API: 获取指定志愿者加入的所有组织及其详细信息。
     * 端点: GET /volunteerOrganizationJoin/myJoinedOrganizations/{volunteerId}
     *
     * @param volunteerId 志愿者的唯一ID，通过路径变量传入。
     * @return 统一封装的 Result 对象，成功时包含组织参与信息和组织详情的列表；失败时包含错误信息。
     */
    @GetMapping("/myJoinedOrganizations/{volunteerId}")
    public Result getMyJoinedOrganizations(@PathVariable String volunteerId) {
        try {
            List<Map<String, Object>> organizations = volunteerOrganizationJoinService.getMyJoinedOrganizations(volunteerId);
            return Result.success(organizations);
        } catch (IllegalArgumentException e) {
            // 捕获非法参数异常（例如，志愿者ID为空），返回400错误
            return Result.error("400", e.getMessage());
        } catch (Exception e) {
            // 捕获其他未知异常，并记录错误日志
            System.err.println("获取我的队伍信息失败: " + e.getMessage());
            e.printStackTrace(); // 打印堆栈信息以便调试
            return Result.error("500", "获取我的队伍信息失败，请稍后再试");
        }
    }

    /**
     * API: 志愿者退出队伍（更新成员状态为 '已退出'）。
     * 端点: PUT /volunteerOrganizationJoin/leaveTeam
     * 请求体示例: { "volunteerId": "...", "orgId": "...", "memberStatus": "已退出" }
     *
     * @param payload 包含 volunteerId, orgId, 和期望的 memberStatus（应为 "已退出"）的请求体Map。
     * @return 统一封装的 Result 对象，表示操作成功或失败。
     */
    @PutMapping("/leaveTeam")
    public Result leaveTeam(@RequestBody Map<String, String> payload) {
        try {
            String volunteerId = payload.get("volunteerId");
            String orgId = payload.get("orgId");
            String memberStatus = payload.get("memberStatus"); // 期望是 '已退出'

            volunteerOrganizationJoinService.leaveTeam(volunteerId, orgId, memberStatus);
            return Result.success("成功退出队伍");
        } catch (CustomException e) {
            // 捕获业务逻辑异常（例如，找不到记录，状态非法等）
            return Result.error(e.getCode(), e.getMsg());
        } catch (IllegalArgumentException e) {
            // 捕获非法参数异常，返回400错误
            return Result.error("400", e.getMessage());
        } catch (Exception e) {
            // 捕获其他未知异常，并记录错误日志
            System.err.println("退出队伍失败: " + e.getMessage());
            e.printStackTrace(); // 打印堆栈信息以便调试
            return Result.error("500", "退出队伍失败，系统内部错误");
        }
    }


     /**
     * API: 志愿者申请加入组织。
     * 端点: POST /volunteerOrganizationJoin/applyToJoin
     * 请求体示例: { "volunteerId": "V001", "orgId": "ORG001" }
     *
     * @param payload 包含 volunteerId 和 orgId 的请求体Map。
     * @return 统一封装的 Result 对象，表示操作成功或失败。
     */
    @PostMapping("/applyToJoin")
    public Result applyToJoin(@RequestBody Map<String, String> payload) {
        try {
            String volunteerId = payload.get("volunteerId");
            String orgId = payload.get("orgId");

            volunteerOrganizationJoinService.applyToJoin(volunteerId, orgId);
            return Result.success("加入申请已提交");
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (IllegalArgumentException e) {
            return Result.error("400", e.getMessage());
        } catch (Exception e) {
            System.err.println("申请加入队伍失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error("500", "申请加入队伍失败，系统内部错误");
        }
    }
}
