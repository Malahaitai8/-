package com.example.springboot.mapper;

import com.example.springboot.entity.Organization;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

public interface OrganizationMapper {

    /**
     * 根据组织ID更新其账户状态
     * (这个方法会由 mybatis 自动映射到 OrganizationMapper.xml 中 id="updateStatusByOrgId" 的SQL)
     * @param orgId 组织ID
     * @param newStatus 新的账户状态
     * @return 影响行数
     */
    int updateStatusByOrgId(@Param("orgId") String orgId, @Param("newStatus") String newStatus);



    /**
     * 根据登录用户名查询组织机构信息
     * @param orgLoginUserName 组织登录用户名
     * @return 组织机构对象，如果未找到则返回null
     */
    @Select("SELECT " +
            "OrgID as orgId, " +
            "OrgName as orgName, " +
            "OrgLoginUserName as orgLoginUserName, " +
            "OrgLoginPassword as orgLoginPassword, " +
            "ContactPersonPhone as contactPersonPhone, " +
            "ServiceRegion as serviceRegion, " +
            "OrgScale as orgScale, " +
            "OrgRating as orgRating, " +
            "OrgAccountStatus as orgAccountStatus, " +
            "TotalServiceHours as totalServiceHours, " +
            "ActivityCount as activityCount, " +
            "TrainingCount as trainingCount " +
            "FROM tbl_Organization WHERE OrgLoginUserName = #{orgLoginUserName}")
    Organization selectByOrgLoginUserName(@Param("orgLoginUserName") String orgLoginUserName);

    /**
     * 根据组织ID查询组织机构信息
     * @param orgId 组织ID
     * @return 组织机构对象
     */
    @Select("SELECT " +
            "OrgID as orgId, OrgName as orgName, OrgLoginUserName as orgLoginUserName, " +
            "OrgLoginPassword as orgLoginPassword, ContactPersonPhone as contactPersonPhone, ServiceRegion as serviceRegion, " +
            "OrgScale as orgScale, OrgRating as orgRating, OrgAccountStatus as orgAccountStatus, " +
            "TotalServiceHours as totalServiceHours, ActivityCount as activityCount, TrainingCount as trainingCount " +
            "FROM tbl_Organization WHERE OrgID = #{orgId}")
    Organization selectByOrgId(@Param("orgId") String orgId);

    /**
     * 插入组织信息 (具体实现在XML中)
     * @param organization 组织对象
     * @return 影响行数
     */
    int insert(Organization organization);

    /**
     * 更新组织信息 (具体实现在XML中)
     * @param organization 组织对象
     * @return 影响行数
     */
    int updateByOrgId(Organization organization);

    /**
     * 查询所有组织 (具体实现在XML中，支持动态条件)
     * @param organizationFilter 过滤条件
     * @return 组织列表
     */
    List<Organization> selectAll(Organization organizationFilter);

    /**
     * 根据组织名称模糊查询 (具体实现在XML中)
     * @param orgName 组织名称片段
     * @return 组织列表
     */
    List<Organization> selectByOrgNameFuzzy(@Param("orgName") String orgName);

    /**
     * 更新组织密码 (具体实现在XML中)
     * @param orgId 组织ID
     * @param newPassword 新密码 (应为加密后的密码)
     * @return 影响行数
     */
    int updatePassword(@Param("orgId") String orgId, @Param("newPassword") String newPassword);

    /**
     * 根据组织ID删除组织信息
     * @param orgId 组织ID
     * @return 影响行数
     */
    @Delete("DELETE FROM tbl_Organization WHERE OrgID = #{orgId}")
    int deleteByOrgId(@Param("orgId") String orgId);

           /**
     * 【更新】查询某个志愿者可以加入的组织列表（即该志愿者尚未申请、尚未加入的组织）。
     * 排除掉志愿者是“申请中”或“已加入”状态的组织，但会包含“已退出”的组织。
     * 可以根据组织名称进行模糊查询。
     *
     * @param volunteerId 志愿者的ID，用于排除已关联的组织。
     * @param orgName 组织名称的模糊查询关键词，如果为null或空，则不进行名称过滤。
     * @return 符合条件的Organization列表。
     */
    @Select("<script>" +
            "SELECT o.orgId, o.orgName, o.contactPersonPhone, o.serviceRegion, o.orgScale, " +
            "o.orgRating, o.orgAccountStatus, o.totalServiceHours, o.activityCount, o.trainingCount " +
            "FROM tbl_Organization o " +
            "WHERE o.orgAccountStatus = '已认证' " + // 只显示已认证的组织
            "AND NOT EXISTS ( " +
            "   SELECT 1 " +
            "   FROM tbl_VolunteerOrganizationJoin voj " +
            "   WHERE voj.orgId = o.orgId " +
            "     AND voj.volunteerId = #{volunteerId} " +
            "     AND voj.memberStatus IN (N'申请中', N'已加入') " +
            ") " +
            "<if test='orgName != null and orgName != \"\"'>" +
            "AND o.orgName LIKE CONCAT('%', #{orgName}, '%') " +
            "</if>" +
            "</script>")
    List<Organization> findAvailableOrganizationsForVolunteer(@Param("volunteerId") String volunteerId,
                                                              @Param("orgName") String orgName);
    /**
     * 统计某个志愿者可以加入的组织数量，用于分页。
     *
     * @param volunteerId 志愿者的ID，用于排除已关联的组织。
     * @param orgName 组织名称的模糊查询关键词。
     * @return 符合条件的组织总数。
     */
    @Select("<script>" +
            "SELECT COUNT(o.orgId) " +
            "FROM tbl_Organization o " +
            "LEFT JOIN tbl_VolunteerOrganizationJoin voj ON o.orgId = voj.orgId AND voj.volunteerId = #{volunteerId} " +
            "WHERE voj.volunteerId IS NULL " + // 排除已存在关联的组织
            "AND o.orgAccountStatus = '已认证'" + // 只统计已认证的组织
            "<if test='orgName != null and orgName != \"\"'>" +
            "AND o.orgName LIKE CONCAT('%', #{orgName}, '%') " +
            "</if>" +
            "</script>")
    int countAvailableOrganizationsForVolunteer(@Param("volunteerId") String volunteerId,
                                                @Param("orgName") String orgName);


}
