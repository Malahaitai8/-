USE volunteer_web_02
GO
/*
 * ============================================================================
 * 志愿者管理系统 - 全功能测试初始数据脚本 (V4.0 - 最终丰富版)
 * ============================================================================
 * 描述:
 * 此最终版脚本提供了海量、状态多样、高度真实的初始数据，用于进行终极功能演示。
 * - 大幅增加了志愿者、组织、活动、培训的数量。
 * - 为每个活动和培训都添加了详细的【时段】数据。
 * - 为组织添加了大量的【成员】数据。
 * - 所有数据均严格遵守唯一性和真实性约束。
 * ============================================================================
*/

USE volunteer_db_test;
GO

SET NOCOUNT ON;
BEGIN TRY
    BEGIN TRANSACTION;

    -- 清理旧数据
    PRINT N'--- 开始清理所有相关表中的旧数据 ---';
    DELETE FROM dbo.tbl_Complaint;
    DELETE FROM dbo.tbl_VolunteerActivityParticipation;
    DELETE FROM dbo.tbl_VolunteerTrainingParticipation;
    DELETE FROM dbo.tbl_VolunteerActivityApplication;
    DELETE FROM dbo.tbl_Position;
    DELETE FROM dbo.tbl_ActivityTimeslot;
    DELETE FROM dbo.tbl_VolunteerActivity;
    DELETE FROM dbo.tbl_VolunteerTraining;
    DELETE FROM dbo.tbl_VolunteerOrganizationJoin;
    DELETE FROM dbo.tbl_Organization;
    DELETE FROM dbo.tbl_Volunteer;
    DELETE FROM dbo.tbl_Administrator;
    PRINT N'--- 旧数据清理完毕 ---';

    -- 1. 插入管理员数据
    PRINT N'--- 1. 正在插入管理员数据 ---';
    INSERT INTO dbo.tbl_Administrator (AdminID, Name, Gender, IDCardNumber, PhoneNumber, Password, ServiceArea, CurrentPosition, PermissionLevel) VALUES
    ('adm_001', N'张伟', N'男', '11010119850510351X', '13910856214', 'adminPass1', N'全国', N'系统维护员', N'高'),
    ('adm_002', N'李静', N'女', '310101199008152424', '13818695302', 'adminPass2', N'华东区', N'审核监督员', N'中'),
    ('adm_003', N'刘洋', N'男', '440103198812017838', '13715982401', 'adminPass3', N'华南区', N'普通管理员', N'低');

    -- 2. 插入组织机构数据
    PRINT N'--- 2. 正在插入组织机构数据 ---';
    INSERT INTO dbo.tbl_Organization (OrgID, OrgName, OrgLoginUserName, OrgLoginPassword, ContactPersonPhone, ServiceRegion, OrgScale, OrgAccountStatus, TotalServiceHours, ActivityCount, TrainingCount, OrgRating) VALUES
    ('org_001', N'晨曦社区服务社', 'chenxishequ', 'orgPassChenxi', '13810294817', N'北京', 0, N'已认证', 1250, 15, 5, 8.5),
    ('org_002', N'绿芽环保联盟', 'lvyahuanbao', 'orgPassLvya', '13918519374', N'上海', 0, N'已认证', 880, 8, 3, 7.8),
    ('org_003', N'启航助学基金', 'qihangzhuxue', 'orgPassQihang', '17702076591', N'广东', 0, N'待认证', 0, 0, 0, 1.0),
    ('org_004', N'蓝天救援预备队', 'lskyrescue', 'orgPassLantian', '13612847590', N'四川', 0, N'冻结', 2500, 25, 10, 9.5),
    ('org_005', N'夕阳红老年服务中心', 'xiyanghong', 'orgPassXiyang', '13501258493', N'北京', 0, N'认证未通过', 300, 5, 1, 1.0),
    ('org_006', N'文津图书社', 'wenjinshe', 'orgPassWenjin', '18611593857', N'北京', 0, N'已认证', 450, 6, 2, 8.2);

    -- 3. 插入志愿者数据
    PRINT N'--- 3. 正在插入志愿者数据 ---';
    INSERT INTO dbo.tbl_Volunteer (VolunteerID, Username, Name, PhoneNumber, IDCardNumber, Password, Gender, ServiceArea, AccountStatus, TotalVolunteerHours, VolunteerRating) VALUES
    ('vol_001', N'wang_wei_v', N'王伟', '13671098821', '110102199503154212', 'volPassWang', N'男', N'北京', N'已实名认证', 120.5, 8.8),
    ('vol_002', N'li_na_sh', N'李娜', '13918273645', '31010419981120172X', 'volPassLi', N'女', N'上海', N'已实名认证', 250.0, 9.2),
    ('vol_003', N'chen_hao_gz', N'陈浩', '13570384421', '440106199307073135', 'volPassChen', N'男', N'广东', N'已实名认证', 55.0, 7.5),
    ('vol_004', N'zhang_min_pending', N'张敏', '18622749158', '120101199901305946', 'volPassZhang', N'女', N'天津', N'未实名认证', 0, 1.0),
    ('vol_005', N'liu_yang_frozen', N'刘洋', '15045098762', '230102199605253859', 'volPassLiu', N'男', N'黑龙江', N'已冻结', 30.0, 6.0),
    ('vol_006', N'wu_jing_failed', N'吴静', '13388514936', '510107199709182763', 'volPassWu', N'女', N'四川', N'认证未通过', 0, 1.0),
    ('vol_007', N'zhao_lei_conflict', N'赵磊', '13901287654', '110108199402116178', 'volPassZhao', N'男', N'北京', N'已实名认证', 80.0, 8.0),
    ('vol_008', N'qian_sun_new', N'孙倩', '18516982475', '310115199704223129', 'volPassSun', N'女', N'上海', N'已实名认证', 15.0, 6.5),
    ('vol_009', N'li_qiang_member', N'李强', '13764218903', '310107199210051211', 'volPassLiQ', N'男', N'上海', N'已实名认证', 310.0, 8.9),
    ('vol_010', N'zhou_fang_member', N'周芳', '13621859941', '31010519930614532X', 'volPassZhou', N'女', N'上海', N'已实名认证', 180.0, 8.2),
    ('vol_011', N'wu_lei_new_app', N'吴磊', '13916337582', '310110199508284618', 'volPassWuL', N'男', N'上海', N'已实名认证', 25.5, 7.0),
    ('vol_012', N'zhao_qian_new', N'赵倩', '18601214358', '110105199803082221', 'volPassZhaoQ', N'女', N'北京', N'未实名认证', 0, 1.0);

    -- 4. 插入志愿者与组织关系数据 (丰富成员)
    PRINT N'--- 4. 正在插入丰富的成员关系数据 ---';
    INSERT INTO dbo.tbl_VolunteerOrganizationJoin (VolunteerID, OrgID, JoinTime, MemberStatus) VALUES
    ('vol_001', 'org_001', DATEADD(year, -1, GETDATE()), N'已加入'),
    ('vol_007', 'org_001', DATEADD(month, -8, GETDATE()), N'已加入'),
    ('vol_012', 'org_001', DATEADD(day, -5, GETDATE()), N'申请中'),
    ('vol_002', 'org_002', DATEADD(month, -6, GETDATE()), N'已加入'),
    ('vol_009', 'org_002', DATEADD(month, -5, GETDATE()), N'已加入'),
    ('vol_010', 'org_002', DATEADD(month, -4, GETDATE()), N'已加入'),
    ('vol_011', 'org_002', DATEADD(day, -20, GETDATE()), N'申请中'),
    ('vol_003', 'org_006', DATEADD(day, -1, GETDATE()), N'申请中');

    -- 执行触发器，更新初始OrgScale
    PRINT N'--- 执行触发器以更新初始组织规模 ---';
    UPDATE dbo.tbl_VolunteerOrganizationJoin SET JoinTime = JoinTime WHERE OrgID IN ('org_001', 'org_002');

    -- 5. 插入活动、培训、岗位、时段数据 (丰富内容)
    PRINT N'--- 5. 正在插入丰富的活动、培训、岗位、时段数据 ---';
    -- 活动1: 已结束
    INSERT INTO dbo.tbl_VolunteerActivity (ActivityID, OrgID, ActivityName, StartTime, EndTime, Location, RecruitmentCount, ActivityStatus, ContactPersonPhone, ActivityDurationHours) VALUES ('act_001', 'org_001', N'周末爱心书屋辅导', '2025-05-10 09:00:00', '2025-05-11 17:00:00', N'晨曦社区书屋', 10, N'已结束', '13810294817', 16);
    INSERT INTO dbo.tbl_Position(PositionID, PositionName, ActivityID, PositionServiceHours, RequiredVolunteers) VALUES ('pos_001a', N'故事讲解员(周六)', 'act_001', 8, 5), ('pos_001b', N'故事讲解员(周日)', 'act_001', 8, 5);
    INSERT INTO dbo.tbl_ActivityTimeslot(TimeslotID, EventID, StartTime, EndTime) VALUES ('ts_a01_d1', 'act_001', '2025-05-10 09:00:00', '2025-05-10 17:00:00'), ('ts_a01_d2', 'act_001', '2025-05-11 09:00:00', '2025-05-11 17:00:00');

    -- 活动2: 进行中
    INSERT INTO dbo.tbl_VolunteerActivity (ActivityID, OrgID, ActivityName, StartTime, EndTime, Location, RecruitmentCount, ActivityStatus, ContactPersonPhone, ActivityDurationHours) VALUES ('act_002', 'org_002', N'城市公园垃圾清理', DATEADD(hour, -2, GETDATE()), DATEADD(hour, 2, GETDATE()), N'世纪公园', 20, N'进行中', '13918519374', 4);
    INSERT INTO dbo.tbl_Position(PositionID, PositionName, ActivityID, PositionServiceHours, RequiredVolunteers) VALUES ('pos_002a', N'垃圾分类督导', 'act_002', 4, 10), ('pos_002b', N'环保宣传员', 'act_002', 4, 10);
    INSERT INTO dbo.tbl_ActivityTimeslot(TimeslotID, EventID, StartTime, EndTime) VALUES ('ts_a02_d1', 'act_002', DATEADD(hour, -2, GETDATE()), DATEADD(hour, 2, GETDATE()));

    -- 活动3: 将来活动, 审核通过
    INSERT INTO dbo.tbl_VolunteerActivity (ActivityID, OrgID, ActivityName, StartTime, EndTime, Location, RecruitmentCount, ActivityStatus, ContactPersonPhone, ActivityDurationHours) VALUES ('act_003', 'org_001', N'社区嘉年华协助', DATEADD(day, 10, GETDATE()), DATEADD(day, 10, DATEADD(hour, 8, GETDATE())), N'晨曦社区广场', 30, N'审核通过', '13810294817', 8);
    INSERT INTO dbo.tbl_Position(PositionID, PositionName, ActivityID, PositionServiceHours, RequiredVolunteers) VALUES ('pos_003a', N'游戏摊位协助', 'act_003', 8, 15), ('pos_003b', N'秩序维护', 'act_003', 8, 15);
    INSERT INTO dbo.tbl_ActivityTimeslot(TimeslotID, EventID, StartTime, EndTime) VALUES ('ts_a03_d1', 'act_003', DATEADD(day, 10, GETDATE()), DATEADD(day, 10, DATEADD(hour, 8, GETDATE())));

    -- 活动4: 将来活动, 待审核
    INSERT INTO dbo.tbl_VolunteerActivity (ActivityID, OrgID, ActivityName, StartTime, EndTime, Location, RecruitmentCount, ActivityStatus, ContactPersonPhone) VALUES ('act_004', 'org_002', N'湿地生态调研', DATEADD(day, 20, GETDATE()), DATEADD(day, 21, GETDATE()), N'东滩湿地公园', 15, N'待审核', '13918519374');
    INSERT INTO dbo.tbl_ActivityTimeslot(TimeslotID, EventID, StartTime, EndTime) VALUES ('ts_a04_d1', 'act_004', DATEADD(day, 20, GETDATE()), DATEADD(day, 21, GETDATE()));

    -- 活动5: 用于测试时间冲突
    INSERT INTO dbo.tbl_VolunteerActivity (ActivityID, OrgID, ActivityName, StartTime, EndTime, Location, RecruitmentCount, ActivityStatus, ContactPersonPhone) VALUES ('act_cfl_005', 'org_002', N'时间冲突测试活动', DATEADD(day, 15, GETDATE()), DATEADD(day, 15, DATEADD(hour, 4, GETDATE())), N'科技馆', 10, N'审核通过', '13918519374');
    INSERT INTO dbo.tbl_Position(PositionID, PositionName, ActivityID, PositionServiceHours, RequiredVolunteers) VALUES ('pos_cfl_001', N'冲突活动岗位', 'act_cfl_005', 4, 10);
    INSERT INTO dbo.tbl_ActivityTimeslot(TimeslotID, EventID, StartTime, EndTime) VALUES ('ts_a05_d1', 'act_cfl_005', DATEADD(day, 15, GETDATE()), DATEADD(day, 15, DATEADD(hour, 4, GETDATE())));

    -- 活动6: 刚刚结束，用于测试评价窗口期 (在7天内)
	INSERT INTO dbo.tbl_VolunteerActivity (ActivityID, OrgID, ActivityName, StartTime, EndTime, Location, RecruitmentCount, ActivityStatus, ContactPersonPhone, ActivityDurationHours) 
	VALUES ('act_rate_test', 'org_001', N'评价窗口期测试活动', DATEADD(day, -4, GETDATE()), DATEADD(day, -3, GETDATE()), N'市图书馆', 10, N'已结束', '13810294817', 6);

	INSERT INTO dbo.tbl_Position(PositionID, PositionName, ActivityID, PositionServiceHours, RequiredVolunteers) 
	VALUES ('pos_rate_test', N'图书整理员', 'act_rate_test', 6, 10);

	INSERT INTO dbo.tbl_VolunteerActivityParticipation (VolunteerID, ActivityID, ActualPositionID, IsCheckedIn, VolunteerToOrgRating, OrgToVolunteerRating) 
	VALUES ('vol_001', 'act_rate_test', 'pos_rate_test', '是', NULL, NULL);

	PRINT N'--- 已添加用于评价窗口期测试的补充数据 ---';
	

	-- 培训1: 已结束
    INSERT INTO dbo.tbl_VolunteerTraining(TrainingID, OrgID, TrainingName, Theme, StartTime, EndTime, Location, RecruitmentCount, TrainingStatus, ContactPersonPhone) VALUES ('trn_001', 'org_001', N'基础急救知识培训', N'应急救护', '2025-04-20 09:00:00', '2025-04-20 13:00:00', N'社区活动中心', 30, N'已结束', '13810294817');
    INSERT INTO dbo.tbl_ActivityTimeslot(TimeslotID, EventID, StartTime, EndTime) VALUES ('ts_t01_d1', 'trn_001', '2025-04-20 09:00:00', '2025-04-20 13:00:00');

    -- 培训2: 将来培训, 用于测试时间冲突
    INSERT INTO dbo.tbl_VolunteerTraining(TrainingID, OrgID, TrainingName, Theme, StartTime, EndTime, Location, RecruitmentCount, TrainingStatus, ContactPersonPhone) VALUES ('trn_cfl_002', 'org_001', N'时间冲突测试培训', N'沟通技巧', DATEADD(day, 15, DATEADD(hour, 1, GETDATE())), DATEADD(day, 15, DATEADD(hour, 3, GETDATE())), N'总部会议室', 20, N'审核通过', '13810294817');
    INSERT INTO dbo.tbl_ActivityTimeslot(TimeslotID, EventID, StartTime, EndTime) VALUES ('ts_t02_d1', 'trn_cfl_002', DATEADD(day, 15, DATEADD(hour, 1, GETDATE())), DATEADD(day, 15, DATEADD(hour, 3, GETDATE())));

    -- 6. 插入报名和参与数据
    PRINT N'--- 6. 正在插入丰富的报名和参与数据 ---';
    -- 参与记录
    INSERT INTO dbo.tbl_VolunteerActivityParticipation (VolunteerID, ActivityID, ActualPositionID, IsCheckedIn, VolunteerToOrgRating, OrgToVolunteerRating) VALUES ('vol_001', 'act_001', 'pos_001a', '是', 8, 9), ('vol_007', 'act_001', 'pos_001b', '是', 9, 9);
    INSERT INTO dbo.tbl_VolunteerActivityParticipation (VolunteerID, ActivityID, ActualPositionID, IsCheckedIn) VALUES ('vol_002', 'act_002', 'pos_002a', '否'), ('vol_009', 'act_002', 'pos_002b', '否');
    INSERT INTO dbo.tbl_VolunteerActivityParticipation (VolunteerID, ActivityID, ActualPositionID, IsCheckedIn) VALUES ('vol_007', 'act_cfl_005', 'pos_cfl_001', '否');

    -- 报名记录
    INSERT INTO dbo.tbl_VolunteerActivityApplication(ApplicationID, VolunteerID, ActivityID, IntendedPositionID, ApplicationStatus) VALUES
    ('app_001', 'vol_001', 'act_003', 'pos_003a', N'待审核'),
    ('app_002', 'vol_008', 'act_003', 'pos_003a', N'待审核'),
    ('app_003', 'vol_007', 'act_003', 'pos_003b', N'已通过');

    -- 培训参与记录
    INSERT INTO dbo.tbl_VolunteerTrainingParticipation(VolunteerID, TrainingID, IsCheckedIn, VolunteerToOrgRating, OrgToVolunteerRating) VALUES
    ('vol_001', 'trn_001', '是', 9, 8),
    ('vol_002', 'trn_001', '是', 8, 8),
    ('vol_007', 'trn_001', '是', 10, 9);

    -- 7. 插入投诉数据
    PRINT N'--- 7. 正在插入投诉数据 ---';
    INSERT INTO dbo.tbl_Complaint (ComplaintID, ComplainantID, ComplaintTargetID, ComplaintType, ComplaintContent, ProcessingStatus, VisitResult) VALUES
    ('cmp_001', 'vol_002', 'org_001', N'活动违规', N'活动`act_001`的实际服务时长与宣传不符。', N'未处理', N'满意'),
    ('cmp_002', 'vol_001', 'vol_003', N'行为不当', N'志愿者陈浩在活动中言语不当。', N'已应诉', N'不满意');

    -- 重新计算并更新所有活动、岗位的录取人数等
    PRINT N'--- 触发器数据同步：正在更新所有活动和岗位的统计数据 ---';
    UPDATE dbo.tbl_VolunteerActivity SET ActivityName = ActivityName;
    UPDATE dbo.tbl_VolunteerActivityApplication SET ApplicationStatus = ApplicationStatus;
    UPDATE dbo.tbl_VolunteerActivityParticipation SET IsCheckedIn = IsCheckedIn;
    
    COMMIT TRANSACTION;
    PRINT N'--- 所有初始数据插入完毕 ---';

END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0
        ROLLBACK TRANSACTION;

    DECLARE @ErrorMessage NVARCHAR(4000) = ERROR_MESSAGE();
    DECLARE @ErrorSeverity INT = ERROR_SEVERITY();
    DECLARE @ErrorState INT = ERROR_STATE();
    DECLARE @ErrorLine INT = ERROR_LINE();
    DECLARE @ErrorProcedure NVARCHAR(200) = ISNULL(ERROR_PROCEDURE(), '-');

    PRINT N'--- 数据插入失败，事务已回滚 ---';
    RAISERROR ('错误发生在过程 %s 的第 %d 行: %s', @ErrorSeverity, @ErrorState, @ErrorProcedure, @ErrorLine, @ErrorMessage);
END CATCH;
GO