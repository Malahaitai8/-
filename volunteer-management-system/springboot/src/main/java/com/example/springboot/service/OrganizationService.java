package com.example.springboot.service;

import com.example.springboot.entity.Organization;
import com.example.springboot.exception.CustomException;
import com.example.springboot.mapper.OrganizationMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
// import org.springframework.security.crypto.password.PasswordEncoder; // 实际项目中用于密码加密

import java.util.List;
import java.util.UUID; // 用于生成ID

@Service
public class OrganizationService {

    @Resource
    private OrganizationMapper organizationMapper;

    // @Resource
    // private PasswordEncoder passwordEncoder; // 实际项目中用于密码加密

/**
     * (新方法) 更新组织账户状态
     * @param orgId 组织ID
     * @param newStatus 新状态
     */
    public void updateOrganizationStatus(String orgId, String newStatus) {
        // 1. 参数校验
        if (orgId == null || newStatus == null || newStatus.trim().isEmpty()) {
            // 您应该有一个自定义的异常类，如果没有，可以暂时抛出通用异常
            // throw new CustomException("400", "组织ID和新状态不能为空");
            throw new IllegalArgumentException("组织ID和新状态不能为空");
        }

        // 2. 调用 Mapper 执行数据库更新
        int affectedRows = organizationMapper.updateStatusByOrgId(orgId, newStatus);

        // 3. 检查更新结果
        if (affectedRows == 0) {
            // 如果影响行数为0，说明可能没有找到对应的组织ID
            // throw new CustomException("404", "未找到ID为 " + orgId + " 的组织，状态更新失败");
            throw new RuntimeException("未找到ID为 " + orgId + " 的组织，状态更新失败");
        }
    }

    /**
     * 组织注册
     * @param organization 包含注册信息的组织对象
     * @throws CustomException 如果用户名已存在或必要信息缺失
     */
    public void register(Organization organization) throws CustomException {
        if (organization == null || !StringUtils.hasText(organization.getOrgLoginUserName()) || !StringUtils.hasText(organization.getOrgLoginPassword())) {
            throw new CustomException("400", "登录用户名和密码不能为空");
        }
        if (!StringUtils.hasText(organization.getOrgName())) {
            throw new CustomException("400", "组织名称不能为空");
        }
        // 校验负责人电话和组织规模
        if (!StringUtils.hasText(organization.getContactPersonPhone())) {
            throw new CustomException("400", "负责人联系方式不能为空");
        }
        if (organization.getOrgScale() == null || organization.getOrgScale() <= 0) {
            throw new CustomException("400", "组织规模必须为大于0的整数");
        }


        Organization dbOrganization = organizationMapper.selectByOrgLoginUserName(organization.getOrgLoginUserName().trim());
        if (dbOrganization != null) {
            throw new CustomException("409", "登录用户名已存在");
        }

        // 设置ID
        if (!StringUtils.hasText(organization.getOrgId())) {
             organization.setOrgId("ORG_" + UUID.randomUUID().toString().substring(0, 11).toUpperCase().replace("-",""));
        }
        // 密码加密 (实际项目中必须)
        // organization.setOrgLoginPassword(passwordEncoder.encode(organization.getOrgLoginPassword()));
        organization.setOrgAccountStatus("待审核"); // 注册后默认状态
        organization.setOrgRating(0.0); // 初始评分
        organization.setTotalServiceHours(0);
        organization.setActivityCount(0);
        organization.setTrainingCount(0);

        organizationMapper.insert(organization);
    }

    /**
     * 组织登录逻辑
     * @param organizationCredentials 包含登录用户名和密码的组织对象
     * @return 登录成功的组织对象 (密码已擦除)
     * @throws CustomException 登录失败 (用户不存在或密码错误等)
     */
    public Organization login(Organization organizationCredentials) throws CustomException {
        String loginUsername = organizationCredentials.getOrgLoginUserName();
        String password = organizationCredentials.getOrgLoginPassword();

        if (!StringUtils.hasText(loginUsername) || !StringUtils.hasText(password)) {
            throw new CustomException("400", "登录用户名和密码不能为空");
        }

        Organization dbOrganization = organizationMapper.selectByOrgLoginUserName(loginUsername.trim());

        if (dbOrganization == null) {
            throw new CustomException("404", "组织账号不存在");
        }
        // 账户状态检查 - 根据之前的讨论，这里允许“正常”和“已认证”状态登录
        if (!"正常".equals(dbOrganization.getOrgAccountStatus()) && !"已认证".equals(dbOrganization.getOrgAccountStatus()) && !"待审核".equals(dbOrganization.getOrgAccountStatus())) {
            throw new CustomException("403", "账号状态异常：" + dbOrganization.getOrgAccountStatus());
        }

        // 密码比较 (实际应使用加密比较)
        // if (!passwordEncoder.matches(password, dbOrganization.getOrgLoginPassword())) {
        if (!password.equals(dbOrganization.getOrgLoginPassword())) { // 简单比较
            throw new CustomException("401", "密码不正确");
        }
        dbOrganization.setOrgLoginPassword(null); // 不返回密码
        return dbOrganization;
    }

 /**
     * 修改组织密码的业务逻辑
     * @param orgId 组织ID
     * @param oldPassword 用户输入的原密码
     * @param newPassword 用户输入的新密码
     * @throws CustomException 如果组织不存在、原密码不匹配、或新密码为空/不符合强度
     */
    public void changePassword(String orgId, String oldPassword, String newPassword) throws CustomException {
        if (!StringUtils.hasText(orgId) || !StringUtils.hasText(oldPassword) || !StringUtils.hasText(newPassword)) {
            throw new CustomException("400", "组织ID、原密码和新密码均不能为空");
        }

        Organization dbOrganization = organizationMapper.selectByOrgId(orgId);
        if (dbOrganization == null) {
            throw new CustomException("404", "组织账号不存在");
        }

        // **SECURITY WARNING: Plain text password comparison. Use PasswordEncoder in production.**
        // if (!passwordEncoder.matches(oldPassword, dbOrganization.getOrgLoginPassword())) {
        if (!oldPassword.equals(dbOrganization.getOrgLoginPassword())) { // Simple plain text comparison
            throw new CustomException("401", "原密码不正确");
        }

        // Basic validation for new password strength
        if (newPassword.length() < 6) { // Example: Minimum length of 6
            throw new CustomException("400", "新密码长度不能少于6位");
        }
        if (newPassword.equals(oldPassword)) {
            throw new CustomException("400", "新密码不能与原密码相同");
        }

        // **SECURITY WARNING: Store hashed password in production.**
        // String encodedNewPassword = passwordEncoder.encode(newPassword);
        // int updatedRows = organizationMapper.updatePassword(orgId, encodedNewPassword);

        int updatedRows = organizationMapper.updatePassword(orgId, newPassword); // Storing new password (plain text in this example)

        if (updatedRows == 0) {
            // This case might occur if the orgId was valid initially but got deleted concurrently,
            // or if there's an issue with the update statement.
            System.err.println("Password update failed for organization ID: " + orgId + ". No rows affected.");
            throw new CustomException("500", "密码更新失败，请稍后重试");
        }
    }

    private void validatePasswordStrength(String password) throws CustomException {
        if (password.length() < 6) {
            throw new CustomException("400", "新密码长度不能少于6位");
        }
    }

    public void insert(Organization organization) throws CustomException {
        if (organization.getOrgId() != null && organizationMapper.selectByOrgId(organization.getOrgId()) != null) {
            throw new CustomException("409", "组织ID已存在");
        }
        if (organizationMapper.selectByOrgLoginUserName(organization.getOrgLoginUserName()) != null) {
             throw new CustomException("409", "登录用户名已存在");
        }
        // 实际项目中密码应加密
        // organization.setOrgLoginPassword(passwordEncoder.encode(organization.getOrgLoginPassword()));
        if (!StringUtils.hasText(organization.getOrgId())) { // 如果前端没传ID，则生成
            organization.setOrgId("ORG_" + UUID.randomUUID().toString().substring(0, 11).toUpperCase().replace("-",""));
        }
        // 设置默认值
        organization.setOrgRating(organization.getOrgRating() == null ? 0.0 : organization.getOrgRating());
        organization.setOrgAccountStatus(StringUtils.hasText(organization.getOrgAccountStatus()) ? organization.getOrgAccountStatus() : "待审核");
        organization.setTotalServiceHours(organization.getTotalServiceHours() == null ? 0 : organization.getTotalServiceHours());
        organization.setActivityCount(organization.getActivityCount() == null ? 0 : organization.getActivityCount());
        organization.setTrainingCount(organization.getTrainingCount() == null ? 0 : organization.getTrainingCount());

        organizationMapper.insert(organization);
    }

        /**
     * 更新组织信息。
     * 允许更新的字段: orgName, orgLoginUserName, contactPersonPhone, serviceRegion, orgScale。
     * 其他字段（如评分、状态、统计数据）通常不由组织自行修改。
     * 密码修改应通过专门的 changePassword 接口。
     *
     * @param organizationFromRequest 包含前端提交的待更新信息的 Organization 对象
     * @throws CustomException 如果发生业务逻辑错误（如组织不存在、用户名已存在等）
     */
    public void updateByOrgId(Organization organizationFromRequest) throws CustomException {
        if (organizationFromRequest == null || !StringUtils.hasText(organizationFromRequest.getOrgId())) {
            throw new CustomException("400", "组织ID不能为空以进行更新");
        }

        // 1. 从数据库获取当前组织信息
        Organization existingOrg = organizationMapper.selectByOrgId(organizationFromRequest.getOrgId());
        if (existingOrg == null) {
            throw new CustomException("404", "组织不存在，无法更新");
        }

        // 2. 创建一个 Organization 对象用于实际更新，只填充允许修改的字段
        Organization orgToUpdate = new Organization();
        orgToUpdate.setOrgId(existingOrg.getOrgId()); // ID 是必须的，用于 WHERE 条件

        boolean hasChanges = false;

        // 更新组织名称 (如果前端提供了新的值)
        if (StringUtils.hasText(organizationFromRequest.getOrgName()) &&
            !organizationFromRequest.getOrgName().equals(existingOrg.getOrgName())) {
            orgToUpdate.setOrgName(organizationFromRequest.getOrgName());
            hasChanges = true;
        } else {
            // 如果前端未提供或与现有值相同，则不设置（或设置为现有值，取决于 MyBatis update 逻辑）
            // 为确保 MyBatis 的 <if test> 条件能正确工作，如果字段未提供，可以不 set
        }

        // 更新组织登录用户名 (如果前端提供了新的值)
        if (StringUtils.hasText(organizationFromRequest.getOrgLoginUserName()) &&
            !organizationFromRequest.getOrgLoginUserName().equals(existingOrg.getOrgLoginUserName())) {
            // 检查新的登录用户名是否已被其他组织占用
            String newLoginUserNameTrimmed = organizationFromRequest.getOrgLoginUserName().trim();
            Organization orgWithNewUsername = organizationMapper.selectByOrgLoginUserName(newLoginUserNameTrimmed);
            if (orgWithNewUsername != null && !orgWithNewUsername.getOrgId().equals(existingOrg.getOrgId())) {
                throw new CustomException("409", "新的登录用户名 '" + newLoginUserNameTrimmed + "' 已被其他组织使用");
            }
            orgToUpdate.setOrgLoginUserName(newLoginUserNameTrimmed);
            hasChanges = true;
        }

        // 更新负责人联系方式
        if (StringUtils.hasText(organizationFromRequest.getContactPersonPhone()) &&
            !organizationFromRequest.getContactPersonPhone().equals(existingOrg.getContactPersonPhone())) {
            orgToUpdate.setContactPersonPhone(organizationFromRequest.getContactPersonPhone());
            hasChanges = true;
        }

        // 更新服务区域
        if (StringUtils.hasText(organizationFromRequest.getServiceRegion()) &&
            !organizationFromRequest.getServiceRegion().equals(existingOrg.getServiceRegion())) {
            // 这里可以添加对 serviceRegion 是否在预定义列表中的校验，如果需要
            orgToUpdate.setServiceRegion(organizationFromRequest.getServiceRegion());
            hasChanges = true;
        }

        // 更新组织规模
        if (organizationFromRequest.getOrgScale() != null &&
            !organizationFromRequest.getOrgScale().equals(existingOrg.getOrgScale())) {
            if (organizationFromRequest.getOrgScale() <= 0) {
                throw new CustomException("400", "组织规模必须为正整数");
            }
            orgToUpdate.setOrgScale(organizationFromRequest.getOrgScale());
            hasChanges = true;
        }

        // 密码不应通过此接口更新，明确设置为 null (或不在 orgToUpdate 中设置该字段)
        // orgToUpdate.setOrgLoginPassword(null);
        // MyBatis 的 update 语句中，密码字段的更新条件应该为  <if test="orgLoginPassword != null and orgLoginPassword != ''">
        // 由于我们不希望通过此接口更新密码，所以不设置 orgLoginPassword 到 orgToUpdate 对象中，
        // 或者像您之前的 service 代码中设置 organizationFromRequest.setOrgLoginPassword(null);
        // 这里采用不往 orgToUpdate 中设置密码字段的方式。

        // 其他字段如 orgRating, orgAccountStatus, totalServiceHours, activityCount, trainingCount
        // 通常不由组织自行修改，因此不从 organizationFromRequest 中取值来更新它们。
        // 如果您的业务逻辑允许修改这些，需要在这里添加相应的处理逻辑。

        if (!hasChanges) {
            // 如果没有任何字段发生变化，可以不执行数据库更新操作
            // 或者抛出一个提示信息，但这通常不是必需的，可以直接让MyBatis处理
             System.out.println("OrganizationService: No actual changes detected for organization ID: " + existingOrg.getOrgId());
            // return; // 可以选择直接返回，避免不必要的数据库操作
        }

        // 3. 执行更新
        // orgToUpdate 对象现在只包含了 orgId 和实际需要更新的字段及其新值
        // 其他未在 orgToUpdate 中设置的字段，在 MyBatis 的 <set><if test...></if></set> 结构中将不会被更新
        int updatedRows = organizationMapper.updateByOrgId(orgToUpdate);

        if (updatedRows == 0) {
            // 虽然前面已经检查过 existingOrg 是否存在，但 update 可能因为并发等原因失败
            System.err.println("Update failed for organization ID: " + existingOrg.getOrgId() + ". No rows affected.");
            // 可以选择抛出异常或记录日志
        }
    }

    public Organization selectByOrgId(String orgId) throws CustomException {
        if(!StringUtils.hasText(orgId)) {
            throw new CustomException("400", "组织ID不能为空");
        }
        Organization organization = organizationMapper.selectByOrgId(orgId);
        if (organization == null) {
            throw new CustomException("404", "组织不存在");
        }
        organization.setOrgLoginPassword(null); // 不返回密码
        return organization;
    }

    public Organization selectByOrgLoginUserName(String orgLoginUserName) throws CustomException {
        if (!StringUtils.hasText(orgLoginUserName)) {
            throw new CustomException("400", "登录用户名不能为空");
        }
        Organization organization = organizationMapper.selectByOrgLoginUserName(orgLoginUserName.trim());
        if (organization != null) {
            organization.setOrgLoginPassword(null); // 不返回密码
        }
        return organization;
    }

    public List<Organization> selectAll(Organization organizationFilter) {
        List<Organization> list = organizationMapper.selectAll(organizationFilter);
        list.forEach(org -> org.setOrgLoginPassword(null));
        return list;
    }

    public PageInfo<Organization> selectPage(Organization organizationFilter, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Organization> list = organizationMapper.selectAll(organizationFilter);
        list.forEach(org -> org.setOrgLoginPassword(null));
        return PageInfo.of(list);
    }

    public void deleteByOrgId(String orgId) throws CustomException {
        if (!StringUtils.hasText(orgId)) {
            throw new CustomException("400", "组织ID不能为空");
        }
        if (organizationMapper.selectByOrgId(orgId) == null) {
            throw new CustomException("404", "要删除的组织不存在");
        }
        organizationMapper.deleteByOrgId(orgId);
    }

    public void deleteBatch(List<String> orgIds) throws CustomException {
        if (orgIds == null || orgIds.isEmpty()) {
            throw new CustomException("400", "要删除的组织ID列表不能为空");
        }
        for (String id : orgIds) {
            this.deleteByOrgId(id); // 调用单个删除，内部有校验
        }
    }

    public List<Organization> findOrganizationsByOrgName(String orgName) throws CustomException {
        if (!StringUtils.hasText(orgName)) {
            throw new CustomException("400", "查询名称不能为空");
        }
        List<Organization> list = organizationMapper.selectByOrgNameFuzzy(orgName);
        list.forEach(org -> org.setOrgLoginPassword(null));
        return list;
    }
}
