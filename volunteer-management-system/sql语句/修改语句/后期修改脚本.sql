-- 为志愿活动表添加临时存储字段
-- 用于在审核前保存岗位和时段信息的JSON格式数据

USE volunteer_web_05;  -- 请根据你的实际数据库名称修改
GO

-- 添加两个新字段用于临时存储岗位和时段信息
ALTER TABLE tbl_VolunteerActivity 
ADD PendingPositionsJson NVARCHAR(MAX) NULL,  -- 待审核的岗位信息（JSON格式）
    PendingTimeslotsJson NVARCHAR(MAX) NULL;  -- 待审核的时段信息（JSON格式）
GO

-- 验证字段是否添加成功
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, CHARACTER_MAXIMUM_LENGTH
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'tbl_VolunteerActivity' 
  AND COLUMN_NAME IN ('PendingPositionsJson', 'PendingTimeslotsJson');
GO

PRINT N'已成功为 tbl_VolunteerActivity 表添加 PendingPositionsJson 和 PendingTimeslotsJson 字段';
GO 