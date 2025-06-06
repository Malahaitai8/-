USE volunteer_web_02; -- 确保在正确的数据库上下文中执行
GO

PRINT N'--- 开始创建ID自动生成触发器 ---';
GO

--------------------------------------------------------------------------------
-- 触发器: trg_GenerateVolunteerID
-- 监控表：   dbo.tbl_Volunteer (志愿者表)
-- 触发事件： INSTEAD OF INSERT (在尝试插入新志愿者记录之前)
-- 核心功能： 自动为新插入的志愿者记录生成一个格式为 'VOL' + 12位数字 的 VolunteerID。
--------------------------------------------------------------------------------
-- 检查触发器是否存在，如果存在则删除
IF OBJECT_ID('dbo.trg_AutoGenerateVolunteerID', 'TR') IS NOT NULL
    DROP TRIGGER dbo.trg_AutoGenerateVolunteerID;
GO

-- 检查触发器是否存在，如果存在则删除
IF OBJECT_ID('dbo.trg_AutoGenerateVolunteerID', 'TR') IS NOT NULL
    DROP TRIGGER dbo.trg_AutoGenerateVolunteerID;
GO

-- 创建 INSTEAD OF INSERT 触发器以自动生成 VolunteerID
CREATE TRIGGER trg_AutoGenerateVolunteerID
ON dbo.tbl_Volunteer
INSTEAD OF INSERT
AS
BEGIN
    -- 防止返回触发器执行的行数信息
    SET NOCOUNT ON;

    DECLARE @newVolunteerID NVARCHAR(15); -- 根据表定义 CHAR(15)
    DECLARE @maxNumericID INT;
    DECLARE @prefix NVARCHAR(2);
    SET @prefix = 'V_'; -- 志愿者ID前缀
    DECLARE @idPart NVARCHAR(13); -- To store the substring part

    -- 使用事务确保操作的原子性
    BEGIN TRANSACTION;

    -- 获取当前最大的 VolunteerID 的数字部分
    -- 修正：更安全地处理非数字的ID后缀
    SELECT @maxNumericID = MAX(
        CASE
            -- 检查后缀是否只包含数字且不为空
            WHEN PATINDEX('%[^0-9]%', SUBSTRING(VolunteerID, LEN(@prefix) + 1, LEN(VolunteerID) - LEN(@prefix))) = 0
                 AND LEN(SUBSTRING(VolunteerID, LEN(@prefix) + 1, LEN(VolunteerID) - LEN(@prefix))) > 0
            THEN CAST(SUBSTRING(VolunteerID, LEN(@prefix) + 1, LEN(VolunteerID) - LEN(@prefix)) AS INT)
            ELSE 0 -- 将无法转换或格式不正确的ID视为0，以便ISNULL能正确处理从0开始的情况
        END
    )
    FROM dbo.tbl_Volunteer WITH (TABLOCKX, HOLDLOCK) -- 添加锁提示以处理并发
    WHERE VolunteerID LIKE @prefix + '%'; -- 确保只处理符合前缀的ID

    -- 如果表中还没有符合格式的ID，或者所有符合格式的ID的数字部分都无法转换/无效，则从1开始
    SET @maxNumericID = ISNULL(@maxNumericID, 0) + 1;

    -- 格式化新的 VolunteerID，例如：V_000001 (6位数字)
    SET @newVolunteerID = @prefix + RIGHT('000000' + CAST(@maxNumericID AS NVARCHAR(6)), 6);

    -- 检查生成的ID是否已存在（极小概率事件，但作为安全措施）
    WHILE EXISTS (SELECT 1 FROM dbo.tbl_Volunteer WHERE VolunteerID = @newVolunteerID)
    BEGIN
        SET @maxNumericID = @maxNumericID + 1;
        SET @newVolunteerID = @prefix + RIGHT('000000' + CAST(@maxNumericID AS NVARCHAR(6)), 6);
    END

    -- 插入新记录，使用生成的 @newVolunteerID 和来自 inserted 伪表的数据
    INSERT INTO dbo.tbl_Volunteer (
        VolunteerID,
        Username,
        Name,
        PhoneNumber,
        IDCardNumber,
        Password,
        Country,
        Gender,
        ServiceArea,
        Ethnicity,
        PoliticalStatus,
        HighestEducation,
        EmploymentStatus,
        ServiceCategory,
        TotalVolunteerHours,
        VolunteerRating,
        AccountStatus
    )
    SELECT
        @newVolunteerID, -- 使用新生成的ID
        i.Username,
        i.Name,
        i.PhoneNumber,
        i.IDCardNumber,
        i.Password,
        ISNULL(i.Country, N'中国'),             -- 如果应用未提供，使用表定义的默认值
        i.Gender,                             -- Gender 是 NOT NULL，应用必须提供
        i.ServiceArea,                        -- ServiceArea 是 NOT NULL，应用必须提供
        ISNULL(i.Ethnicity, N'汉族'),           -- 如果应用未提供，使用表定义的默认值
        ISNULL(i.PoliticalStatus, N'群众'),    -- 如果应用未提供，使用表定义的默认值
        i.HighestEducation,                   -- 可以为 NULL
        i.EmploymentStatus,                   -- 可以为 NULL
        i.ServiceCategory,                    -- 表定义中此列允许NULL
        ISNULL(i.TotalVolunteerHours, 0.00),  -- 如果应用未提供，使用表定义的默认值
        ISNULL(i.VolunteerRating, 0.00),      -- 如果应用未提供，使用表定义的默认值
        ISNULL(i.AccountStatus, N'未实名认证') -- 如果应用未提供，使用表定义的默认值
    FROM inserted i;

    COMMIT TRANSACTION;

END;
GO
--------------------------------------------------------------------------------
-- 触发器: trg_GenerateOrganizationID
-- 监控表：   dbo.tbl_Organization (组织机构表)
-- 触发事件： INSTEAD OF INSERT (在尝试插入新组织记录之前)
-- 核心功能： 自动为新插入的组织记录生成一个格式为 'ORG' + 12位数字 的 OrgID。
--------------------------------------------------------------------------------
-- 检查触发器是否存在，如果存在则删除
IF OBJECT_ID('dbo.trg_AutoGenerateOrgID', 'TR') IS NOT NULL
    DROP TRIGGER dbo.trg_AutoGenerateOrgID;
GO

-- 创建 INSTEAD OF INSERT 触发器以自动生成 OrgID
CREATE TRIGGER trg_AutoGenerateOrgID
ON dbo.tbl_Organization
INSTEAD OF INSERT
AS
BEGIN
    -- 防止返回触发器执行的行数信息
    SET NOCOUNT ON;

    DECLARE @newOrgID NVARCHAR(15);
    DECLARE @maxNumericID INT;
    DECLARE @prefix NVARCHAR(4);
    SET @prefix = 'ORG_';

    -- 使用事务确保操作的原子性
    BEGIN TRANSACTION;

    -- 获取当前最大的 OrgID 的数字部分 (需要表锁以防止并发问题)
    -- 注意：对于高并发系统，这种方式获取最大ID可能成为瓶颈，可以考虑使用SEQUENCE对象。
    -- 但为了模仿您提供的志愿者ID生成方式，这里采用MAX + SUBSTRING。
    SELECT @maxNumericID = MAX(CAST(SUBSTRING(OrgID, LEN(@prefix) + 1, LEN(OrgID) - LEN(@prefix)) AS INT))
    FROM dbo.tbl_Organization WITH (TABLOCKX, HOLDLOCK) -- 添加锁提示以处理并发
    WHERE OrgID LIKE @prefix + '%'; --确保只处理符合前缀的ID

    -- 如果表中还没有符合格式的ID，则从1开始
    SET @maxNumericID = ISNULL(@maxNumericID, 0) + 1;

    -- 格式化新的 OrgID，例如：ORG_00000001 (8位数字)
    SET @newOrgID = @prefix + RIGHT('00000000' + CAST(@maxNumericID AS NVARCHAR(8)), 8);

    -- 检查生成的ID是否已存在（极小概率事件，但作为安全措施）
    WHILE EXISTS (SELECT 1 FROM dbo.tbl_Organization WHERE OrgID = @newOrgID)
    BEGIN
        SET @maxNumericID = @maxNumericID + 1;
        SET @newOrgID = @prefix + RIGHT('00000000' + CAST(@maxNumericID AS NVARCHAR(8)), 8);
    END

    -- 插入新记录，使用生成的 @newOrgID 和来自 inserted 伪表的数据
    -- 确保所有在 tbl_Organization 中定义的 NOT NULL 列都有值
    -- 或者数据库有这些列的默认值
    INSERT INTO dbo.tbl_Organization (
        OrgID,
        OrgName,
        OrgLoginUserName,
        OrgLoginPassword,
        ContactPersonPhone,
        ServiceRegion,
        OrgScale,
        OrgRating,            -- 从 inserted 表获取，如果应用层设置了
        OrgAccountStatus,     -- 从 inserted 表获取，如果应用层设置了
        TotalServiceHours,    -- 从 inserted 表获取，如果应用层设置了
        ActivityCount,        -- 从 inserted 表获取，如果应用层设置了
        TrainingCount         -- 从 inserted 表获取，如果应用层设置了
    )
    SELECT
        @newOrgID,            -- 使用新生成的ID
        i.OrgName,
        i.OrgLoginUserName,
        i.OrgLoginPassword,
        i.ContactPersonPhone,
        i.ServiceRegion,
        i.OrgScale,
        ISNULL(i.OrgRating, 0.0),           -- 如果应用没传，则使用默认值 (或表定义默认值)
        ISNULL(i.OrgAccountStatus, N'待认证'),-- 如果应用没传，则使用默认值 (或表定义默认值)
        ISNULL(i.TotalServiceHours, 0),   -- 如果应用没传，则使用默认值 (或表定义默认值)
        ISNULL(i.ActivityCount, 0),       -- 如果应用没传，则使用默认值 (或表定义默认值)
        ISNULL(i.TrainingCount, 0)        -- 如果应用没传，则使用默认值 (或表定义默认值)
    FROM inserted i;

    COMMIT TRANSACTION;

END;
GO

-- 测试触发器 (可选)
/*
-- 尝试插入一条记录 (不指定 OrgID，或指定一个将被忽略的 OrgID)
INSERT INTO tbl_Organization (
    OrgName, OrgLoginUserName, OrgLoginPassword, ContactPersonPhone, ServiceRegion, OrgScale, OrgAccountStatus
) VALUES (
    N'测试组织触发器', 'test_trigger_org', 'password123', '13800138001', N'北京', 100, N'待审核'
);

-- 查看插入结果
SELECT * FROM tbl_Organization WHERE OrgLoginUserName = 'test_trigger_org';

-- 再次插入，查看ID是否自增
INSERT INTO tbl_Organization (
    OrgName, OrgLoginUserName, OrgLoginPassword, ContactPersonPhone, ServiceRegion, OrgScale, OrgAccountStatus
) VALUES (
    N'测试组织触发器2', 'test_trigger_org2', 'password123', '13800138002', N'上海', 50, N'正常'
);
SELECT * FROM tbl_Organization WHERE OrgLoginUserName = 'test_trigger_org2';
*/

--------------------------------------------------------------------------------
-- 触发器: trg_GenerateAdministratorID
-- 监控表：   dbo.tbl_Administrator (管理员表)
-- 触发事件： INSTEAD OF INSERT (在尝试插入新管理员记录之前)
-- 核心功能： 自动为新插入的管理员记录生成一个格式为 'ADM' + 12位数字 的 AdminID。
--------------------------------------------------------------------------------
IF OBJECT_ID('dbo.trg_GenerateAdministratorID', 'TR') IS NOT NULL
    DROP TRIGGER dbo.trg_GenerateAdministratorID;
GO

CREATE TRIGGER dbo.trg_GenerateAdministratorID
ON dbo.tbl_Administrator
INSTEAD OF INSERT
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO dbo.tbl_Administrator (
        AdminID, Name, Gender, IDCardNumber, PhoneNumber, Password,
        ServiceArea, CurrentPosition, PermissionLevel
    )
    SELECT
        'ADM' + RIGHT('000000000000' + CAST(NEXT VALUE FOR dbo.AdministratorID_Seq AS VARCHAR(12)), 12) AS GeneratedAdminID,
        i.Name, i.Gender, i.IDCardNumber, i.PhoneNumber, i.Password,
        i.ServiceArea, 
        ISNULL(i.CurrentPosition, N'普通管理员'), -- 假设默认职位
        i.PermissionLevel
    FROM
        inserted i;
END;
GO
PRINT N'触发器 [trg_GenerateAdministratorID] 已创建。';
GO

PRINT N'--- ID自动生成触发器创建完毕 ---';
GO