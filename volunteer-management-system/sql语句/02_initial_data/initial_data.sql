USE volunteer_web_02; -- 确保在正确的数据库上下文中执行
GO

-- 清理先前可能存在的示例数据 (可选，如果需要重复执行此脚本)
-- 注意：如果表之间有严格的外键并且没有设置级联删除，删除顺序很重要，或者先禁用外键约束。
-- 为简单起见，这里不包含删除语句，假设您在干净的测试库或手动处理。
/*
DELETE FROM tbl_Complaint;
DELETE FROM tbl_VolunteerTrainingParticipation;
DELETE FROM tbl_VolunteerActivityParticipation;
DELETE FROM tbl_VolunteerActivityApplication;
DELETE FROM tbl_ActivityTimeslot;
DELETE FROM tbl_Position;
DELETE FROM tbl_VolunteerOrganizationJoin;
DELETE FROM tbl_VolunteerTraining;
DELETE FROM tbl_VolunteerActivity;
DELETE FROM tbl_Volunteer;
DELETE FROM tbl_Organization;
DELETE FROM tbl_Administrator;
GO
*/


--------------------------------------------------------------------------------
-- 顺序 1: 插入 管理员表示例数据 (tbl_Administrator)
--------------------------------------------------------------------------------
INSERT INTO tbl_Administrator (AdminID, Name, Gender, IDCardNumber, PhoneNumber, Password, ServiceArea, CurrentPosition, PermissionLevel) -- Changed PermissionScope to ServiceArea
VALUES
('adm_001', N'周宏伟', N'男', '11010119820715301X', '13910587261', 'adminPass1', N'全国', N'系统维护员', N'高'),
('adm_002', N'陈静姝', N'女', '310102197903224028', '18621590374', 'chenPass2', N'上海市', N'审核监督员', N'中'),
('adm_003', N'刘建国', N'男', '440103198811052015', '13501587923', 'liuPass3', N'广州市天河区', N'普通管理员', N'中');
GO
INSERT INTO tbl_Administrator (AdminID, Name, Gender, IDCardNumber, PhoneNumber, Password, ServiceArea, CurrentPosition, PermissionLevel) 
VALUES
('adm_004',N'金陶然', N'女','22062320050528004X', '13894037809', '23301153', N'全国', N'系统维护员', N'高')
--------------------------------------------------------------------------------
-- 顺序 2: 插入 组织机构表示例数据 (tbl_Organization)
--------------------------------------------------------------------------------
INSERT INTO tbl_Organization (OrgID, OrgName, OrgLoginUserName, OrgLoginPassword, ContactPersonPhone, ServiceRegion, OrgScale, OrgRating, OrgAccountStatus, TotalServiceHours, ActivityCount, TrainingCount)
VALUES
('org_001', N'晨曦社区服务社', 'chenxishequ', 'orgPassChenxi', '13811223344', N'北京', 120, 8.7, N'已认证', 1560, 2, 1),
('org_002', N'绿芽环保志愿者联盟', 'lvyahuanbao', 'orgPassLvya', '13912345678', N'上海', 75, 9.1, N'已认证', 980, 1, 1),
('org_003', N'启航助学基金会', 'qihangzhuxue', 'orgPassQihang', '17702076591', N'广东', 200, 8.2, N'待认证', 450, 1, 0);
GO
select * from tbl_Organization;
--------------------------------------------------------------------------------
-- 顺序 3: 插入 志愿者表示例数据 (tbl_Volunteer)
--------------------------------------------------------------------------------
INSERT INTO tbl_Volunteer (VolunteerID, Username, Name, PhoneNumber, IDCardNumber, Password, Country, Gender, ServiceArea, Ethnicity, PoliticalStatus, HighestEducation, EmploymentStatus, ServiceCategory, TotalVolunteerHours, VolunteerRating, AccountStatus)
VALUES
('vol_001', N'zhang_wei_01', N'张伟', '13671098821', '42010119950815001X', 'volPassZhang', N'中国', N'男', N'北京', N'汉族', N'群众', N'大学本科', N'职员', N'社区志愿者', 180.5, 9.2, N'已实名认证'),
('vol_002', N'li_na_sh', N'李娜', '18918576602', '310105199902200028', 'volPassLi', N'中国', N'女', N'上海', N'汉族', N'中国共产主义青年团团员', N'硕士研究生', N'学生', N'教育志愿者', 95.0, 8.8, N'已实名认证'),
('vol_003', N'wang_feng_gz', N'王峰', '13326480175', '440106200112010035', 'volPassWang', N'中国', N'男', N'广东', N'壮族', N'群众', N'大学专科和专科学校', N'自由职业', N'扶贫济困志愿者', 250.0, 9.5, N'已实名认证'),
('vol_004', N'chen_yan_bj', N'陈燕', '15010356792', '110108199206100043', 'volPassChen', N'中国', N'女', N'北京', N'回族', N'群众', N'大学本科', N'企业管理人员', N'助残志愿者', 120.0, 8.5, N'未实名认证');
GO
select * from tbl_Volunteer
--------------------------------------------------------------------------------
-- 顺序 4: 插入 志愿活动表示例数据 (tbl_VolunteerActivity)
-- AcceptedCount 将在后续步骤中根据报名和参与情况更新，此处初始为0或预估值
--------------------------------------------------------------------------------
INSERT INTO tbl_VolunteerActivity (ActivityID, OrgID, ActivityName, StartTime, EndTime, Location, RecruitmentCount, AcceptedCount, ActivityStatus, CreationTime, ReviewerAdminID, ContactPersonPhone, ActivityDurationHours, ActivityRating, IsRatingAggregated)
VALUES
('act_001', 'org_001', N'周末爱心书屋辅导', '2025-06-10 09:00', '2025-06-10 12:00', N'北京市朝阳区晨曦书屋', 15, 0, N'进行中', '2025-06-01 10:30', 'adm_002', '13811223344', 3, NULL, 'NO'),
('act_002', 'org_002', N'城市公园垃圾清理', '2025-06-15 14:00', '2025-06-15 17:00', N'上海市人民公园', 30, 0, N'审核通过', '2025-06-05 11:15', 'adm_002', '13912345678', 3, NULL, 'NO'),
('act_003', 'org_003', N'“一对一”远程支教', '2025-07-01 00:00', '2025-09-30 23:59', N'线上远程', 50, 0, N'待审核', '2025-06-10 16:45', NULL, '17702076591', 180, NULL, 'NO'),
('act_004', 'org_001', N'长者智能手机教学', '2025-05-10 10:00', '2025-05-10 12:00', N'北京市海淀区乐龄中心', 20, 0, N'已结束', '2025-05-01 08:20', 'adm_002', '13811223344', 2, 9, 'YES');
GO

--------------------------------------------------------------------------------
-- 顺序 5: 插入 志愿培训表示例数据 (tbl_VolunteerTraining)
--------------------------------------------------------------------------------
INSERT INTO tbl_VolunteerTraining (TrainingID, OrgID, TrainingName, Theme, StartTime, EndTime, Location, RecruitmentCount, TrainingStatus, CreationTime, ReviewerAdminID, ContactPersonPhone, TrainingRating, IsRatingAggregated)
VALUES
('trn_001', 'org_001', N'新志愿者入职培训', N'志愿服务规范与礼仪', '2025-06-20 14:00', '2025-06-20 17:00', N'晨曦社区会议室', 40, N'进行中', '2025-06-10 09:00', 'adm_002', '13811223344', NULL, 'NO'), -- Changed '运行中' to '进行中'
('trn_002', 'org_002', N'环保知识讲座', N'垃圾分类与回收利用', '2025-05-15 19:00', '2025-05-15 21:00', N'绿芽联盟活动室', 30, N'已结束', '2025-05-05 10:00', 'adm_002', '13912345678', 9, 'YES'); -- Changed '已结项' to '已结束'
GO

--------------------------------------------------------------------------------
-- 顺序 6: 插入 志愿者组织机构参与表示例数据 (tbl_VolunteerOrganizationJoin)
--------------------------------------------------------------------------------
INSERT INTO tbl_VolunteerOrganizationJoin (VolunteerID, OrgID, JoinTime, MemberStatus)
VALUES
('vol_001', 'org_001', '2024-11-25 10:00', N'已加入'),
('vol_002', 'org_002', '2025-02-25 11:00', N'已加入'),
('vol_003', 'org_003', '2025-05-15 14:30', N'已加入'),
('vol_004', 'org_001', '2025-05-20 16:00', N'申请中');
GO

--------------------------------------------------------------------------------
-- 顺序 7: 插入 岗位表示例数据 (tbl_Position)
-- RecruitedVolunteers 将在后续步骤中根据报名和参与情况更新，此处初始为0
--------------------------------------------------------------------------------
INSERT INTO tbl_Position (PositionID, PositionName, ActivityID, PositionServiceHours, RequiredVolunteers, RecruitedVolunteers)
VALUES
('pos_001', N'课业辅导老师', 'act_001', 3, 8, 0),
('pos_002', N'活动秩序维护', 'act_001', 3, 2, 0),
('pos_003', N'垃圾收集员', 'act_002', 3, 20, 0),
('pos_004', N'宣传引导员', 'act_002', 3, 5, 0),
('pos_005', N'智能设备助教', 'act_004', 2, 10, 0);
GO

--------------------------------------------------------------------------------
-- 顺序 8: 插入 活动时段表示例数据 (tbl_ActivityTimeslot)
--------------------------------------------------------------------------------
INSERT INTO tbl_ActivityTimeslot (TimeslotID, EventID, StartTime, EndTime)
VALUES
('tslot_001', 'act_001', '2025-06-10 09:00', '2025-06-10 10:30'),
('tslot_002', 'act_001', '2025-06-10 10:30', '2025-06-10 12:00'),
('tslot_003', 'trn_001', '2025-06-20 14:00', '2025-06-20 17:00');
GO

--------------------------------------------------------------------------------
-- 顺序 9: 插入 志愿者活动报名表示例数据 (tbl_VolunteerActivityApplication)
-- 这些数据将驱动后续的参与记录和人数统计
--------------------------------------------------------------------------------
INSERT INTO tbl_VolunteerActivityApplication (ApplicationID, VolunteerID, ActivityID, IntendedPositionID, ApplicationTime, ApplicationStatus)
VALUES
-- 活动 act_001 报名
('app_001', 'vol_001', 'act_001', 'pos_001', '2025-06-05 10:00', N'已通过'),
('app_002', 'vol_002', 'act_001', 'pos_001', '2025-06-05 11:00', N'已通过'),
('app_003', 'vol_004', 'act_001', 'pos_002', '2025-06-06 14:00', N'已通过'),
('app_007', 'vol_003', 'act_001', 'pos_001', '2025-06-07 09:00', N'待审核'),

-- 活动 act_002 报名
('app_004', 'vol_001', 'act_002', NULL, '2025-06-10 09:30', N'待审核'),
('app_005', 'vol_003', 'act_002', 'pos_003', '2025-06-11 10:00', N'已通过'),
('app_006', 'vol_002', 'act_002', 'pos_004', '2025-06-11 15:00', N'已拒绝'),

-- 活动 act_004 报名 (已结束活动)
('app_008', 'vol_001', 'act_004', 'pos_005', '2025-05-02 10:00', N'已通过'),
('app_009', 'vol_004', 'act_004', 'pos_005', '2025-05-02 11:00', N'已通过');
GO

--------------------------------------------------------------------------------
-- 顺序 10: 插入 志愿者活动参与表示例数据 (tbl_VolunteerActivityParticipation)
-- 只有报名状态为 '已通过' 的才能参与
--------------------------------------------------------------------------------
INSERT INTO tbl_VolunteerActivityParticipation (VolunteerID, ActivityID, ActualPositionID, IsCheckedIn, VolunteerToOrgRating, OrgToVolunteerRating)
VALUES
-- 参与 act_001 (周末爱心书屋辅导)
('vol_001', 'act_001', 'pos_001', N'是', 9, 9),
('vol_002', 'act_001', 'pos_001', N'是', 8, NULL),
('vol_004', 'act_001', 'pos_002', N'是', NULL, 9),

-- 参与 act_004 (长者智能手机教学 - 已结束活动)
('vol_001', 'act_004', 'pos_005', N'是', 8, 8),
('vol_004', 'act_004', 'pos_005', N'是', 7, 7);
GO

--------------------------------------------------------------------------------
-- 更新 tbl_Position 的 RecruitedVolunteers 和 tbl_VolunteerActivity 的 AcceptedCount
-- 这部分最好通过触发器或应用逻辑处理，但为了示例数据完整性，这里手动更新
--------------------------------------------------------------------------------
-- 更新活动 act_001 的岗位已招募人数和活动录取人数
UPDATE tbl_Position SET RecruitedVolunteers = 2 WHERE PositionID = 'pos_001' AND ActivityID = 'act_001';
UPDATE tbl_Position SET RecruitedVolunteers = 1 WHERE PositionID = 'pos_002' AND ActivityID = 'act_001';
UPDATE tbl_VolunteerActivity SET AcceptedCount = 3 WHERE ActivityID = 'act_001';

-- 更新活动 act_002 的岗位已招募人数和活动录取人数
UPDATE tbl_Position SET RecruitedVolunteers = 1 WHERE PositionID = 'pos_003' AND ActivityID = 'act_002';
UPDATE tbl_VolunteerActivity SET AcceptedCount = 1 WHERE ActivityID = 'act_002';

-- 更新活动 act_004 的岗位已招募人数和活动录取人数
UPDATE tbl_Position SET RecruitedVolunteers = 2 WHERE PositionID = 'pos_005' AND ActivityID = 'act_004';
UPDATE tbl_VolunteerActivity SET AcceptedCount = 2 WHERE ActivityID = 'act_004';
GO


--------------------------------------------------------------------------------
-- 顺序 11: 插入 志愿者培训参与表示例数据 (tbl_VolunteerTrainingParticipation)
--------------------------------------------------------------------------------
INSERT INTO tbl_VolunteerTrainingParticipation (VolunteerID, TrainingID, IsCheckedIn, OrgToVolunteerRating, VolunteerToOrgRating) -- Changed ParticipationStatus to IsCheckedIn
VALUES
('vol_001', 'trn_001', N'是', NULL, NULL), -- Assumed '正在参加' means checked in
('vol_002', 'trn_002', N'是', 8, 9),    -- Assumed '已结束' means attended and checked in
('vol_004', 'trn_001', N'否', NULL, NULL); -- '未开始' implies not checked in
GO

--------------------------------------------------------------------------------
-- 顺序 12: 插入 投诉表示例数据 (tbl_Complaint)
--------------------------------------------------------------------------------
INSERT INTO tbl_Complaint (ComplaintID, ComplaintTime, ComplainantID, ComplaintTargetID, ComplaintType, ComplaintContent, EvidenceLink, ProcessingStatus, ProcessingResult, LatestProcessingTime, HandlerAdminID, VisitTime, VisitResult, ArbitrationRound, ReviewAdminID)
VALUES
('cmpl_001', '2025-05-22 10:00', 'vol_001', 'org_003', N'信息虚假', N'启航助学基金会发布的“一对一”远程支教活动act_003，招募人数与实际需求不符。', NULL, N'未处理', NULL, NULL, NULL, NULL, N'满意', NULL, NULL),
('cmpl_002', '2025-05-18 11:30', 'vol_004', 'vol_001', N'行为不当', N'在活动act_004（长者智能手机教学）中，志愿者张伟（vol_001）对待老人缺乏耐心。', 'http://example.com/evidence_act004.jpg', N'处理中', N'已与张伟沟通，其表示会改进服务态度。', '2025-05-23 14:00', 'adm_003', NULL, N'满意', NULL, NULL);
GO

PRINT '使用明文密码、固定日期时间(SMALLDATETIME)且联系方式为11位手机号的全新示例数据插入完毕! (已调整报名和参与逻辑)';
GO

--------------------------------------------------------------------------------
-- 数据库表设计正确性测试语句
--------------------------------------------------------------------------------

PRINT N'开始进行数据库表设计正确性测试...';
GO

-- 1. 测试单表数据完整性 (抽样)
PRINT N'--- 1. 单表数据完整性测试 (抽样) ---';
SELECT TOP 5 * FROM dbo.tbl_Administrator ORDER BY AdminID DESC;
SELECT TOP 5 * FROM dbo.tbl_Organization ORDER BY OrgID DESC;
SELECT TOP 5 * FROM dbo.tbl_Volunteer ORDER BY VolunteerID DESC;
SELECT TOP 5 * FROM dbo.tbl_VolunteerActivity ORDER BY ActivityID DESC;
SELECT TOP 5 * FROM dbo.tbl_Position ORDER BY PositionID DESC;
SELECT TOP 5 * FROM dbo.tbl_Complaint ORDER BY ComplaintID DESC;
SELECT TOP 5 * FROM dbo.tbl_ActivityTimeslot ORDER BY TimeslotID DESC;
GO
PRINT N'单表数据抽样检查完毕，请目视检查数据是否符合预期。';
GO

-- 2. 测试表间关联性与业务逻辑
PRINT N'--- 2. 表间关联性与业务逻辑测试 ---';

PRINT N'查询组织(org_001)发布的志愿活动 (应有2条):';
SELECT
    o.OrgName AS '组织名称',
    va.ActivityName AS '活动名称',
    va.StartTime AS '开始时间',
    va.Location AS '地点',
    va.ActivityStatus AS '活动状态',
    va.RecruitmentCount AS '计划招募',
    va.AcceptedCount AS '实际录取'
FROM
    dbo.tbl_Organization o
JOIN
    dbo.tbl_VolunteerActivity va ON o.OrgID = va.OrgID
WHERE
    o.OrgID = 'org_001';
GO

PRINT N'查询志愿者(vol_001)加入的组织:';
SELECT
    v.Name AS '志愿者姓名',
    o.OrgName AS '组织名称',
    voj.JoinTime AS '加入时间',
    voj.MemberStatus AS '成员状态'
FROM
    dbo.tbl_Volunteer v
JOIN
    dbo.tbl_VolunteerOrganizationJoin voj ON v.VolunteerID = voj.VolunteerID
JOIN
    dbo.tbl_Organization o ON voj.OrgID = o.OrgID
WHERE
    v.VolunteerID = 'vol_001';
GO

PRINT N'查询活动(act_001)的所有岗位及招募情况:';
SELECT
    va.ActivityName AS '活动名称',
    p.PositionName AS '岗位名称',
    p.RequiredVolunteers AS '需求人数',
    p.RecruitedVolunteers AS '已招募人数'
FROM
    dbo.tbl_VolunteerActivity va
JOIN
    dbo.tbl_Position p ON va.ActivityID = p.ActivityID
WHERE
    va.ActivityID = 'act_001';
GO

PRINT N'查询志愿者(vol_001)的活动报名情况 (应有2条报名):';
SELECT
    v.Name AS '志愿者姓名',
    va.ActivityName AS '报名活动',
    p.PositionName AS '意向岗位',
    vaa.ApplicationTime AS '报名时间',
    vaa.ApplicationStatus AS '报名状态'
FROM
    dbo.tbl_Volunteer v
JOIN
    dbo.tbl_VolunteerActivityApplication vaa ON v.VolunteerID = vaa.VolunteerID
JOIN
    dbo.tbl_VolunteerActivity va ON vaa.ActivityID = va.ActivityID
LEFT JOIN
    dbo.tbl_Position p ON vaa.IntendedPositionID = p.PositionID
WHERE
    v.VolunteerID = 'vol_001';
GO

PRINT N'查询活动(act_001)的已通过报名志愿者:';
SELECT
    va.ActivityName,
    v.Name AS VolunteerName,
    p.PositionName AS IntendedPosition
FROM dbo.tbl_VolunteerActivityApplication vaa
JOIN dbo.tbl_Volunteer v ON vaa.VolunteerID = v.VolunteerID
JOIN dbo.tbl_VolunteerActivity va ON vaa.ActivityID = va.ActivityID
LEFT JOIN dbo.tbl_Position p ON vaa.IntendedPositionID = p.PositionID
WHERE vaa.ActivityID = 'act_001' AND vaa.ApplicationStatus = N'已通过';
GO


PRINT N'查询志愿者(vol_001)的活动参与情况 (应有2条参与):';
SELECT
    v.Name AS '志愿者姓名',
    va.ActivityName AS '参与活动',
    p.PositionName AS '实际岗位',
    vap.IsCheckedIn AS '是否签到'
FROM
    dbo.tbl_Volunteer v
JOIN
    dbo.tbl_VolunteerActivityParticipation vap ON v.VolunteerID = vap.VolunteerID
JOIN
    dbo.tbl_VolunteerActivity va ON vap.ActivityID = va.ActivityID
JOIN
    dbo.tbl_Position p ON vap.ActualPositionID = p.PositionID
WHERE
    v.VolunteerID = 'vol_001';
GO

PRINT N'查询培训(trn_001)的参与志愿者:';
SELECT
    vt.TrainingName AS '培训名称',
    v.Name AS '志愿者姓名',
    vtp.IsCheckedIn AS '参与状态' -- Changed from vtp.ParticipationStatus
FROM
    dbo.tbl_VolunteerTraining vt
JOIN
    dbo.tbl_VolunteerTrainingParticipation vtp ON vt.TrainingID = vtp.TrainingID
JOIN
    dbo.tbl_Volunteer v ON vtp.VolunteerID = v.VolunteerID
WHERE
    vt.TrainingID = 'trn_001';
GO

PRINT N'查询活动(act_001)的时段 (应用层面通过EventID前缀判断类型):';
SELECT
    EventID,
    StartTime,
    EndTime
FROM
    dbo.tbl_ActivityTimeslot
WHERE EventID = 'act_001';
GO

PRINT N'查询培训(trn_001)的时段 (应用层面通过EventID前缀判断类型):';
SELECT
    EventID,
    StartTime,
    EndTime
FROM
    dbo.tbl_ActivityTimeslot
WHERE EventID = 'trn_001';
GO


PRINT N'查询由管理员(adm_003)处理的投诉:';
SELECT
    c.ComplaintID,
    c.ComplaintContent,
    a.Name AS HandlerName
FROM
    dbo.tbl_Complaint c
JOIN
    dbo.tbl_Administrator a ON c.HandlerAdminID = a.AdminID
WHERE c.HandlerAdminID = 'adm_003';
GO

PRINT N'查询特定投诉(cmpl_001)的信息:';
SELECT * FROM dbo.tbl_Complaint WHERE ComplaintID = 'cmpl_001';
GO

-- 3. 测试约束 (示例 - 您可以根据需要添加更多反面测试用例)
PRINT N'--- 3. 约束测试 (部分示例，应返回错误或阻止操作) ---';

PRINT N'尝试插入一个重复的志愿者用户名 (应失败):';

-- 尝试插入一个用户名已为 'zhang_wei_01' 的志愿者
-- 注意: 执行此操作会导致错误，这是预期的测试结果。为避免中断整个脚本，可以注释掉。
/*
INSERT INTO tbl_Volunteer (VolunteerID, Username, Name, PhoneNumber, IDCardNumber, Password, Gender, ServiceArea, AccountStatus)
VALUES ('vol_999', N'zhang_wei_01', N'测试重名', '13000000000', '990101199001010011', 'testpass', N'男', N'北京', N'未实名认证');
GO
*/

PRINT N'尝试将活动的录取人数设置为大于招募人数 (应失败):';
-- 注意: 执行此操作会导致错误，这是预期的测试结果。为避免中断整个脚本，可以注释掉。
/*
UPDATE tbl_VolunteerActivity
SET AcceptedCount = RecruitmentCount + 1
WHERE ActivityID = 'act_001';
GO
*/

PRINT N'尝试将志愿者服务类别设置为无效值 (应失败):';
-- 注意: 执行此操作会导致错误，这是预期的测试结果。为避免中断整个脚本，可以注释掉。
/*
UPDATE tbl_Volunteer
SET ServiceCategory = N'无效类别'
WHERE VolunteerID = 'vol_001';
GO
*/

PRINT N'测试完毕。请检查查询结果和预期的错误信息（如果执行了约束测试）。';
GO