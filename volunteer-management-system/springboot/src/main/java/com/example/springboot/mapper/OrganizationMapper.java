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
}
