// src/main/java/com/example/springboot/service/VolunteerOrganizationJoinService.java
package com.example.springboot.service;

import com.example.springboot.entity.VolunteerOrganizationJoin;
import com.example.springboot.exception.CustomException;
import com.example.springboot.mapper.VolunteerOrganizationJoinMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class VolunteerOrganizationJoinService {

    @Resource
    private VolunteerOrganizationJoinMapper volunteerOrganizationJoinMapper;

    /**
     * 获取指定志愿者加入的所有组织及其详细信息。
     *
     * @param volunteerId 志愿者的唯一ID。
     * @return 包含组织参与信息和组织详细信息的Map列表。如果志愿者ID为空或无效，将抛出IllegalArgumentException。
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMyJoinedOrganizations(String volunteerId) {
        if (!StringUtils.hasText(volunteerId)) {
            throw new IllegalArgumentException("志愿者ID不能为空或空白");
        }
        return volunteerOrganizationJoinMapper.findMyJoinedOrganizations(volunteerId);
    }

    @Transactional
    public void addMember(VolunteerOrganizationJoin joinInfo) {
        if (joinInfo == null || !StringUtils.hasText(joinInfo.getVolunteerId()) || !StringUtils.hasText(joinInfo.getOrgId())) {
            throw new CustomException("400", "志愿者ID和组织ID不能为空");
        }

        int affectedRows = volunteerOrganizationJoinMapper.insert(joinInfo);

        if (affectedRows == 0) {
            throw new CustomException("500", "添加成员失败");
        }
    }

    /**
     * 处理志愿者退出组织或更新成员状态的请求。
     * 此方法主要用于将成员状态更新为 '已退出'。
     *
     * @param volunteerId 志愿者的唯一ID。
     * @param orgId 组织的唯一ID。
     * @param newStatus 新的成员状态（目前预期为 '已退出'）。
     * @throws CustomException 如果参数无效，或者未能找到对应的成员记录，或者状态更新失败。
     */
    @Transactional
    public void leaveTeam(String volunteerId, String orgId, String newStatus) throws CustomException {
        // 参数非空校验
        if (!StringUtils.hasText(volunteerId) || !StringUtils.hasText(orgId) || !StringUtils.hasText(newStatus)) {
            throw new CustomException("400", "志愿者ID、组织ID和新状态不能为空");
        }

        // 业务逻辑校验：确保新状态是 '已退出'
        if (!"已退出".equals(newStatus)) {
            throw new CustomException("400", "无效的成员状态更新。目前只支持更新为 '已退出'");
        }

        // 调用Mapper更新数据库
        int affectedRows = volunteerOrganizationJoinMapper.updateMemberStatus(volunteerId, orgId, newStatus);

        // 检查更新结果
        if (affectedRows == 0) {
            // 如果影响行数为0，表示没有找到对应的记录或者记录已经处于 '已退出' 状态
            throw new CustomException("404", "未能找到ID为 '" + volunteerId + "' 和组织ID为 '" + orgId + "' 的成员记录，或该成员已处于 '已退出' 状态");
        }
    }


    /**
     * 【新增】处理志愿者申请加入组织。
     *
     * @param volunteerId 志愿者ID。
     * @param orgId 组织ID。
     * @throws CustomException 如果参数无效，或已存在申请/已加入/已退出状态，则抛出异常。
     */
    @Transactional
    public void applyToJoin(String volunteerId, String orgId) throws CustomException {
        if (!StringUtils.hasText(volunteerId) || !StringUtils.hasText(orgId)) {
            throw new CustomException("400", "志愿者ID和组织ID不能为空");
        }

        // 1. 检查是否存在现有记录
        VolunteerOrganizationJoin existingJoin = volunteerOrganizationJoinMapper.selectByVolunteerIdAndOrgId(volunteerId, orgId);

        if (existingJoin != null) {
            // 如果记录已存在，根据当前状态给出不同提示
            if ("申请中".equals(existingJoin.getMemberStatus())) {
                throw new CustomException("409", "您已申请加入该队伍，请等待审核。");
            } else if ("已加入".equals(existingJoin.getMemberStatus())) {
                throw new CustomException("409", "您已是该队伍的成员，无需重复申请。");
            } else if ("已退出".equals(existingJoin.getMemberStatus())) {
                // 如果是已退出状态，可以考虑更新为“申请中”或阻止再次申请，这里我们选择更新
                int updatedRows = volunteerOrganizationJoinMapper.updateMemberStatus(volunteerId, orgId, "申请中");
                if (updatedRows == 0) {
                     throw new CustomException("500", "更新申请状态失败，请稍后再试。");
                }
                return; // 成功更新为申请中，直接返回
            }
        }

        // 2. 如果不存在记录，则创建新记录
        VolunteerOrganizationJoin newJoin = new VolunteerOrganizationJoin();
        newJoin.setVolunteerId(volunteerId);
        newJoin.setOrgId(orgId);
        newJoin.setJoinTime(new Date()); // 设置当前时间
        newJoin.setMemberStatus("申请中"); // 默认状态为“申请中”

        int insertedRows = volunteerOrganizationJoinMapper.insert(newJoin);
        if (insertedRows == 0) {
            throw new CustomException("500", "提交加入申请失败，请稍后再试。");
        }
    }

    //获取所有申请中的成员信息。
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getPendingJoinRequests(String orgId) {
        if (!StringUtils.hasText(orgId)) {
            throw new IllegalArgumentException("组织ID不能为空或空白");
        }
        return volunteerOrganizationJoinMapper.selectMembers(orgId, "申请中");
    }

    //获取所有已加入的成员信息。
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getActiveJoinRequests(String orgId) {
        if (!StringUtils.hasText(orgId)) {
            throw new IllegalArgumentException("组织ID不能为空或空白");
        }
        return volunteerOrganizationJoinMapper.selectMembers(orgId, "已加入");
    }

    //更新成员状态
    @Transactional
    public void approveJoinRequest(String volunteerId, String orgId) {
        if (!StringUtils.hasText(volunteerId) || !StringUtils.hasText(orgId)) {
            throw new CustomException("400", "志愿者ID和组织ID不能为空");
        }

        int affectedRows = volunteerOrganizationJoinMapper.updateMemberStatus(volunteerId, orgId, "已加入");

        if (affectedRows == 0) {
            throw new CustomException("404", "未能找到ID为 '" + volunteerId + "' 和组织ID为 '" + orgId + "' 的成员记录，或该成员已处于 '已加入' 状态");
        }
    }

    @Transactional
    public void deleteMemberRequest(String volunteerId, String orgId) {
        if (!StringUtils.hasText(volunteerId) || !StringUtils.hasText(orgId)) {
            throw new CustomException("400", "志愿者ID和组织ID不能为空");
        }

        int affectedRows = volunteerOrganizationJoinMapper.updateMemberStatus(volunteerId, orgId, "已退出");

        if (affectedRows == 0) {
            throw new CustomException("404", "未能找到ID为 '" + volunteerId + "' 和组织ID为 '" + orgId + "' 的成员记录，或该成员已处于 '已退出' 状态");
        }
    }
}
