package com.example.springboot.mapper;

import com.example.springboot.entity.VolunteerOrganizationJoin;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

public interface VolunteerOrganizationJoinMapper {
    /**
     * 根据志愿者ID查询其加入的所有组织列表，并关联查询组织的详细信息。
     * 返回的Map结构包含tbl_VolunteerOrganizationJoin和tbl_Organization的合并字段。
     *
     * @param volunteerId 志愿者的唯一ID
     * @return 包含组织参与信息和组织详细信息的Map列表。
     */
    @Select("SELECT " +
            "voj.volunteerId, " +
            "voj.orgId, " +
            "voj.joinTime, " +
            "voj.memberStatus, " +
            "o.orgName, " +
            "o.contactPersonPhone, " +
            "o.serviceRegion, " +
            "o.orgScale, " +
            "o.orgRating, " +
            "o.orgAccountStatus, " +
            "o.totalServiceHours, " +
            "o.activityCount, " +
            "o.trainingCount " +
            "FROM tbl_VolunteerOrganizationJoin voj " +
            "JOIN tbl_Organization o ON voj.orgId = o.orgId " +
            "WHERE voj.volunteerId = #{volunteerId}")
    List<Map<String, Object>> findMyJoinedOrganizations(@Param("volunteerId") String volunteerId);

    /**
     * 志愿者申请加入组织或记录加入信息
     * @param joinInfo 包含 VolunteerID, OrgID, MemberStatus 的加入信息对象
     * @return 影响行数
     */
    @Insert("INSERT INTO tbl_VolunteerOrganizationJoin (VolunteerID, OrgID, MemberStatus, JoinTime) " +
            "VALUES (#{volunteerId,jdbcType=CHAR}, #{orgId,jdbcType=CHAR}, #{memberStatus,jdbcType=NVARCHAR}, GETDATE())")
    int insert(VolunteerOrganizationJoin joinInfo);

    /**
     * 更新成员在组织中的状态 (例如：批准申请，标记退出)
     * @param volunteerId 志愿者ID
     * @param orgId 组织ID
     * @param memberStatus 新的成员状态
     * @return 影响行数
     */
    @Update("UPDATE tbl_VolunteerOrganizationJoin SET MemberStatus = #{memberStatus,jdbcType=NVARCHAR} " +
            "WHERE VolunteerID = #{volunteerId,jdbcType=CHAR} AND OrgID = #{orgId,jdbcType=CHAR}")
    int updateMemberStatus(@Param("volunteerId") String volunteerId,
                           @Param("orgId") String orgId,
                           @Param("memberStatus") String memberStatus);



    /**
     * 志愿者退出组织 (或管理员移除成员) - 实际上是更新状态为“已退出”
     * 为了保持记录，通常不直接删除，而是更新状态。如果确实需要删除，则使用下面的delete方法。
     * @param volunteerId 志愿者ID
     * @param orgId 组织ID
     * @return 影响行数
     */
    default int markAsExited(String volunteerId, String orgId) {
        return updateMemberStatus(volunteerId, orgId, "已退出");
    }

    /**
     * 根据联合主键删除加入记录 (物理删除，谨慎使用)
     * @param volunteerId 志愿者ID
     * @param orgId 组织ID
     * @return 影响行数
     */
    @Delete("DELETE FROM tbl_VolunteerOrganizationJoin " +
            "WHERE VolunteerID = #{volunteerId,jdbcType=CHAR} AND OrgID = #{orgId,jdbcType=CHAR}")
    int deleteByPrimaryKey(@Param("volunteerId") String volunteerId, @Param("orgId") String orgId);

    /**
     * 根据联合主键查询加入记录
     * @param volunteerId 志愿者ID
     * @param orgId 组织ID
     * @return 加入记录对象
     */
    @Select("SELECT VolunteerID as volunteerId, OrgID as orgId, JoinTime as joinTime, MemberStatus as memberStatus " +
            "FROM tbl_VolunteerOrganizationJoin " +
            "WHERE VolunteerID = #{volunteerId,jdbcType=CHAR} AND OrgID = #{orgId,jdbcType=CHAR}")
    VolunteerOrganizationJoin selectByPrimaryKey(@Param("volunteerId") String volunteerId, @Param("orgId") String orgId);

    /**
     * 查询某个志愿者的所有组织加入记录
     * @param volunteerId 志愿者ID
     * @return 加入记录列表
     */
    @Select("SELECT VolunteerID as volunteerId, OrgID as orgId, JoinTime as joinTime, MemberStatus as memberStatus " +
            "FROM tbl_VolunteerOrganizationJoin WHERE VolunteerID = #{volunteerId,jdbcType=CHAR} ORDER BY JoinTime DESC")
    List<VolunteerOrganizationJoin> selectByVolunteerId(@Param("volunteerId") String volunteerId);

    /**
     * 查询某个组织的所有成员加入记录
     * @param orgId 组织ID
     * @return 加入记录列表
     */
    @Select("SELECT VolunteerID as volunteerId, OrgID as orgId, JoinTime as joinTime, MemberStatus as memberStatus " +
            "FROM tbl_VolunteerOrganizationJoin WHERE OrgID = #{orgId,jdbcType=CHAR} ORDER BY JoinTime DESC")
    List<VolunteerOrganizationJoin> selectByOrgId(@Param("orgId") String orgId);

    /**
     * 查询某个组织特定状态的成员
     * @param orgId 组织ID
     * @param memberStatus 成员状态
     * @return 加入记录列表
     */
    @Select("SELECT voj.volunteerId, voj.orgId, voj.joinTime, voj.memberStatus, " +
            "v.name, v.phoneNumber AS telephone, v.idCardNumber, v.totalVolunteerHours, v.volunteerRating, " +
            "v.username, v.country, v.gender, v.ethnicity, v.politicalStatus, v.highestEducation, " +
            "v.employmentStatus, v.serviceArea, v.serviceCategory " +
            "FROM tbl_VolunteerOrganizationJoin voj " +
            "JOIN tbl_Volunteer v ON voj.volunteerId = v.volunteerId " +
            "WHERE voj.orgId = #{orgId} AND voj.memberStatus = #{memberStatus}")
    List<Map<String, Object>> selectByOrgIdAndStatus(@Param("orgId") String orgId, @Param("memberStatus") String memberStatus);
    /**
     * 查询所有加入记录 (可用于后台管理，谨慎使用，数据量可能较大)
     * (SQL defined in XML for potential dynamic filtering)
     * @param filterCriteria 过滤条件
     * @return 加入记录列表
     */
    List<VolunteerOrganizationJoin> selectAll(VolunteerOrganizationJoin filterCriteria);

    /**
     * 查找特定的志愿者和组织之间的加入记录。
     * 用于检查是否已存在申请或已加入。
     * @param volunteerId 志愿者ID
     * @param orgId 组织ID
     * @return VolunteerOrganizationJoin 实体，如果存在则返回，否则返回 null。
     */
    @Select("SELECT volunteerId, orgId, joinTime, memberStatus FROM tbl_VolunteerOrganizationJoin " +
            "WHERE volunteerId = #{volunteerId} AND orgId = #{orgId}")
    VolunteerOrganizationJoin selectByVolunteerIdAndOrgId(
            @Param("volunteerId") String volunteerId,
            @Param("orgId") String orgId);

    //获得所有特定MemberStatus的成员信息
    @Select("SELECT voj.volunteerId, voj.orgId, voj.joinTime, voj.memberStatus, " +
            "v.name, v.phoneNumber, v.idCardNumber, v.totalVolunteerHours, v.volunteerRating, " +
            "v.username, v.country, v.gender, v.ethnicity, v.politicalStatus, v.highestEducation, " +
            "v.employmentStatus, v.serviceArea, v.serviceCategory " +
            "FROM tbl_VolunteerOrganizationJoin voj " +
            "JOIN tbl_Volunteer v ON voj.volunteerId = v.volunteerId " +
            "WHERE voj.orgId = #{orgId} AND voj.memberStatus = #{memberStatus}")
    List<Map<String, Object>> selectMembers(@Param("orgId") String orgId, @Param("memberStatus") String memberStatus);


    @Select("SELECT voj.volunteerId, voj.orgId, voj.joinTime, voj.memberStatus, " +
            "v.name, v.phoneNumber, v.idCardNumber, v.totalVolunteerHours, v.volunteerRating, " +
            "v.username, v.country, v.gender, v.ethnicity, v.politicalStatus, v.highestEducation, " +
            "v.employmentStatus, v.serviceArea, v.serviceCategory, v.accountStatus " +
            "FROM tbl_VolunteerOrganizationJoin voj " +
            "JOIN tbl_Volunteer v ON voj.volunteerId = v.volunteerId " +
            "WHERE voj.orgId = #{orgId} AND voj.memberStatus = #{memberStatus} AND v.accountStatus = #{accountStatus}")
    List<Map<String, Object>> selectMembersByStatusAndVerification(
            @Param("orgId") String orgId,
            @Param("memberStatus") String memberStatus,
            @Param("accountStatus") String accountStatus);

}