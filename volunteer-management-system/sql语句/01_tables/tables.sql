--USE volunteer_db_test
USE volunteer_web_05;
GO
--select * from tbl_Volunteer;
--select * from tbl_Organization;

-- ˳�� 1: ���� ����Ա�� (tbl_Administrator)
CREATE TABLE tbl_Administrator (
    AdminID CHAR(15) PRIMARY KEY,                                  -- ����ԱΨһ��ʶ
    Name NVARCHAR(20) NOT NULL,                                   -- ��ʵ����
    Gender NCHAR(1) NOT NULL CHECK (Gender IN (N'��', N'Ů')),     -- �Ա�
    IDCardNumber VARCHAR(18) NOT NULL UNIQUE,                      -- ����֤��, ��׼����
    PhoneNumber VARCHAR(11) NOT NULL UNIQUE,                       -- ��׼�ֻ���
    Password NVARCHAR(50) NOT NULL,                                -- ��¼���� (���޸�Ϊ���Ĵ洢)
    ServiceArea NVARCHAR(100) NOT NULL,                            -- ����Ա�����Ͻ�ĵ���
    CurrentPosition NVARCHAR(20) NOT NULL DEFAULT N'��ͨ����Ա'
	CHECK (CurrentPosition IN (N'ϵͳά��Ա', N'��˼ලԱ', N'Ȩ�޹���Ա', N'����ά��Ա', N'Ӧ������Ա', N'�û�����Ա',N'��ͨ����Ա')), -- ��ǰְ��
    PermissionLevel NVARCHAR(8) NOT NULL CHECK (PermissionLevel IN (N'��', N'��', N'��')) -- Ȩ�޵ȼ�
);
GO
select * from tbl_Administrator;
-- ˳�� 2: ���� ��֯������ (tbl_Organization)
CREATE TABLE tbl_Organization (
    OrgID CHAR(15) PRIMARY KEY,                                    -- Ψһ��ʶ��֯�ı��
    OrgName NVARCHAR(20) NOT NULL,                                 -- ��֯����
    OrgLoginUserName NVARCHAR(20) NOT NULL UNIQUE,                 -- ��֯��¼ƾ֤����֯��¼�û�����
    OrgLoginPassword NVARCHAR(50) NOT NULL,                        -- ��½���� (���޸�Ϊ���Ĵ洢)
    ContactPersonPhone NVARCHAR(11) NOT NULL,                      -- ��׼�ֻ���
    ServiceRegion NVARCHAR(50) NOT NULL
	CHECK(ServiceRegion IN('����', '���', '�Ϻ�', '����', '�ӱ�', 'ɽ��', '����', '����', '������','����', '�㽭', '����', '����', '����',
	'ɽ��', '����', '����', '����','�㶫', '����', '����', '�Ĵ�', '����', '����', '����', '����', '����','�ຣ', '̨��', '���', '����')),
	-- ��������
    OrgScale INT NOT NULL,                                         -- ��֯����
    OrgRating DECIMAL(3,1) NOT NULL DEFAULT 0.0 CHECK (OrgRating >= 0.0 AND OrgRating <= 10.0), -- ��֯����
    OrgAccountStatus NVARCHAR(10) NOT NULL DEFAULT N'����֤'
	CHECK (OrgAccountStatus IN (N'����֤', N'����֤', N'����',N'��֤δͨ��')), -- ��֯ע������ĵ�ǰ����״̬
    TotalServiceHours INT NOT NULL DEFAULT 0,                      -- ��ӳ��֯��Ծ���빱�׶ȣ���Ϊ��֯���۱�׼֮һ
    ActivityCount INT NOT NULL DEFAULT 0,
    TrainingCount INT NOT NULL DEFAULT 0
);
GO


-- ˳�� 3: ���� ־Ը�߱� (tbl_Volunteer)
CREATE TABLE tbl_Volunteer (
    VolunteerID CHAR(15) PRIMARY KEY,                                -- Ψһ��ʶ־Ը��
    Username NVARCHAR(20) NOT NULL UNIQUE,                           -- �û�ע���¼ʹ��
    Name NVARCHAR(20) NOT NULL,                                      -- ��ʵ����
    PhoneNumber VARCHAR(11)  NOT NULL UNIQUE,                         -- ��׼�ֻ���
    IDCardNumber VARCHAR(18) NOT NULL UNIQUE,                        -- Ψһ��֤֤��
    Password NVARCHAR(50) NOT NULL,                                  -- ��¼���� (���޸�Ϊ���Ĵ洢)
    Country NVARCHAR(10) DEFAULT N'�й�',                             -- ������Ϣ
    Gender NCHAR(1) NOT NULL CHECK (Gender IN (N'��', N'Ů')),       -- ��ʵ�Ա�ɸѡ
    ServiceArea NVARCHAR(50) NOT NULL
	CHECK(ServiceArea IN('����', '���', '�Ϻ�', '����', '�ӱ�', 'ɽ��', '����', '����', '������','����', '�㽭', '����', '����', '����',
	'ɽ��', '����', '����', '����','�㶫', '����', '����', '�Ĵ�', '����', '����', '����', '����', '����','�ຣ', '̨��', '���', '����')),
 -- ��������
    Ethnicity NVARCHAR(10) DEFAULT N'����' CHECK (Ethnicity IN ('����', '�ɹ���', '����', '����', 'ά�����', '����', '����', '׳��',
        '������', '������', '����', '����', '����', '����', '������',
        '������', '��������', '����', '����', '������', '����', '���',
        '��ɽ��', '������', 'ˮ��', '������', '������', '������',
        '�¶�������', '����', '���Ӷ���', '������', 'Ǽ��', '������',
        '������', 'ë����', '������', '������', '������', '������',
        '��������', 'ŭ��', '���α����', '����˹��', '���¿���',
        '�°���', '������', 'ԣ����', '����', '��������', '������',
        '���״���', '������', '�Ű���', '�����')), -- ��������
    PoliticalStatus NVARCHAR(20) DEFAULT N'Ⱥ��'
	CHECK (PoliticalStatus IN ('�й���������Ա','�й�������Ԥ����Ա','�й�����������������Ա',
        '�й����񵳸���ίԱ���Ա','�й�����ͬ����Ա','�й������������Ա',
        '�й������ٽ����Ա','�й�ũ����������Ա','�й��¹�����Ա',
        '����ѧ����Ա','̨����������ͬ����Ա','�޵���������ʿ','Ⱥ��')), -- ��������
    HighestEducation NVARCHAR(20) DEFAULT N'δ˵�����'
	CHECK (HighestEducation IN ('��ʿ�о���', '��ѧ����', '����ѧУ', '����', '����', 'Сѧ',
        '˶ʿ�о���', '��ѧר�ƺ�ר��ѧУ', '�׶�԰ѧ��ǰ', '�������',
        '��ä�����ä', 'δ˵�����')), -- ��������
    EmploymentStatus NVARCHAR(20) DEFAULT N'δ˵�����'
	CHECK (EmploymentStatus IN ('���ҹ���Ա', 'ְԱ', '��ҵ������Ա', '����', 'ѧ��', '���۾���',
        '����ְҵ', '���徭Ӫ��', '��ҵ��Ա', '��(��)����Ա', 'ҽ��',
        '˾��', '��ʦ', '��ʦ', 'ũ��','δ˵�����')), -- ��ҵ���
    ServiceCategory NVARCHAR(20) DEFAULT N'����־Ը��'
	CHECK (ServiceCategory IN ('������������־Ը��', '��ƶ����־Ը��', '����־Ը��',
        '����־Ը��', '����־Ը��', '�Ļ�־Ը��', 'ҽ��־Ը��',
        '����־Ը��', '����־Ը��', '����־Ը��', '����־Ը��',
        '��ʮ��־Ը��', '˰��־Ը��', '�������־Ը��')), -- ־Ը���
    TotalVolunteerHours DECIMAL(10,2) DEFAULT 0.00,                  -- ��־Ըʱ��
    VolunteerRating DECIMAL(10,2) DEFAULT 0.00,                      -- ־Ը���ۺ�����
    AccountStatus NVARCHAR(10) NOT NULL DEFAULT N'δʵ����֤'
	CHECK (AccountStatus IN (N'δʵ����֤', N'��ʵ����֤', N'�Ѷ���',N'��֤δͨ��'))
);
GO
--select * from tbl_VolunteerActivity;

-- ˳�� 4: ���� ־Ը��� (tbl_VolunteerActivity)
CREATE TABLE tbl_VolunteerActivity (
    ActivityID CHAR(15) PRIMARY KEY,                                  -- Ψһ��ʶ�
    OrgID CHAR(15) NOT NULL
	FOREIGN KEY REFERENCES tbl_Organization(OrgID),					  -- ����־Ը��֯������
    ActivityName NVARCHAR(20) NOT NULL,                               -- ־Ը�����
    StartTime SMALLDATETIME NOT NULL,                                       -- ־Ը���ʼʱ��
    EndTime SMALLDATETIME NOT NULL,                                         -- ־Ը�����ʱ��
    Location NVARCHAR(30) NOT NULL,                                    -- ��ص�
    RecruitmentCount INT NOT NULL CHECK (RecruitmentCount > 0),        -- ��ļ����
    AcceptedCount INT NOT NULL DEFAULT 0 , -- ¼ȡ����
    ActivityStatus NVARCHAR(10) NOT NULL DEFAULT N'�����' ,
	CHECK (ActivityStatus IN (N'�����', N'���ͨ��', N'��˲�ͨ��', N'������', N'�ѽ���', N'��ͣ��')), -- ־Ը�״̬
    CreationTime DATETIME2(0) NOT NULL DEFAULT GETDATE(),                  -- ����ʱ��
    ReviewerAdminID CHAR(15) FOREIGN KEY REFERENCES tbl_Administrator(AdminID), -- ��˹���ԱID, ��������Ա�� (�ɿ�)
    ContactPersonPhone NVARCHAR(11) NOT NULL,                         -- ��������ϵ��ʽ����׼�ֻ���
    ActivityDurationHours INT CHECK (ActivityDurationHours >= 0),      -- ־Ը���ʱ�� (Сʱ)
    ActivityRating INT CHECK (ActivityRating > 0 AND ActivityRating <= 10), -- ־Ը����� (����1-10��)
    IsRatingAggregated CHAR(3) NOT NULL DEFAULT 'NO' CHECK (IsRatingAggregated IN ('YES', 'NO')), -- ���һ�������ѵ�������Ƿ��Ѿ���������
	CONSTRAINT CHK_AcceptedCount_Activity CHECK (AcceptedCount >= 0 AND AcceptedCount <= RecruitmentCount) -- ���ӱ��� CHECK Լ��
);
GO

-- ˳�� 5: ���� ־Ը��ѵ�� (tbl_VolunteerTraining)
CREATE TABLE tbl_VolunteerTraining (
    TrainingID CHAR(15) PRIMARY KEY,                                  -- Ψһ��ʶ��ѵ
    OrgID CHAR(15) NOT NULL FOREIGN KEY REFERENCES tbl_Organization(OrgID), -- ������֯��
    TrainingName NVARCHAR(20) NOT NULL,                               -- ��ѵ����
    Theme NVARCHAR(15) NOT NULL ,                                     -- ���⣨��֯�ڲ���ѵ�����ѵ���ض���λ��ѵ��
    StartTime SMALLDATETIME NOT NULL,                                  -- ��ѵ��ʼʱ��
    EndTime SMALLDATETIME NOT NULL,                                    -- ��ѵ����ʱ��
    Location NVARCHAR(30) NOT NULL,                                    -- ��ѵ�ص�
    RecruitmentCount INT NOT NULL CHECK (RecruitmentCount > 0),        -- ��ļ����
    TrainingStatus NVARCHAR(10) NOT NULL 
	CHECK (TrainingStatus IN (N'�����', N'���ͨ��', N'��˲�ͨ��', N'������', N'�ѽ���', N'��ͣ��')), -- << �޸ĵ㣺���� N'��˲�ͨ��'
    CreationTime SMALLDATETIME NOT NULL DEFAULT GETDATE(),             -- ����ʱ��
    ReviewerAdminID CHAR(15) FOREIGN KEY REFERENCES tbl_Administrator(AdminID), -- ��˹���ԱID, ��������Ա�� (�ɿ�)
    ContactPersonPhone NVARCHAR(11) NOT NULL,                         -- ��������ϵ��ʽ����׼�ֻ���
    TrainingRating INT CHECK (TrainingRating > 0 AND TrainingRating <= 10), -- ��ѵ���� (����1-10��)
    IsRatingAggregated CHAR(3) NOT NULL DEFAULT 'NO' CHECK (IsRatingAggregated IN ('YES', 'NO')) -- ���һ�������ѵ�������Ƿ��Ѿ���������
);
GO

-- ˳�� 6: ���� ־Ը����֯��������� (tbl_VolunteerOrganizationJoin)
CREATE TABLE tbl_VolunteerOrganizationJoin (
    VolunteerID CHAR(15) NOT NULL FOREIGN KEY REFERENCES tbl_Volunteer(VolunteerID), -- ����־Ը�߱�
    OrgID CHAR(15) NOT NULL FOREIGN KEY REFERENCES tbl_Organization(OrgID),       -- ������֯������
    JoinTime DATETIME NOT NULL DEFAULT GETDATE(),                                 -- ־Ը�߼�����֯��ʱ��
    MemberStatus NVARCHAR(3) NOT NULL CHECK (MemberStatus IN (N'������', N'�Ѽ���', N'���˳�')), -- ��Ա״̬
    PRIMARY KEY (VolunteerID, OrgID)                                              -- ��������
);
GO

-- ˳�� 7: ���� ��λ�� (tbl_Position)
CREATE TABLE tbl_Position (
    PositionID CHAR(15) PRIMARY KEY,                                 -- ��λID, ���� (CHAR���͵�Ψһ����Ӧ�ò���ȷ��)
    PositionName NVARCHAR(50) NOT NULL,                              -- ��λ����
    ActivityID CHAR(15) NOT NULL FOREIGN KEY REFERENCES tbl_VolunteerActivity(ActivityID), -- �ID, ���, ����־Ը���
    PositionServiceHours INT NOT NULL CHECK (PositionServiceHours > 0), -- ��λ����ʱ�� (ָ�ڸû����ְ��λ�ɻ�õķ���ʱ��)
    RequiredVolunteers INT NOT NULL,                                  -- ��������
    RecruitedVolunteers INT NOT NULL DEFAULT 0, -- ����ļ���� (С�ڵ�����������, �Ҵ��ڵ���0)
	CONSTRAINT CHK_RecruitedVolunteers_Position CHECK (RecruitedVolunteers <= RequiredVolunteers AND RecruitedVolunteers >= 0) -- ���ӱ��� CHECK Լ��
);
GO

-- ˳�� 8: ���� �ʱ�α� (tbl_ActivityTimeslot)
CREATE TABLE tbl_ActivityTimeslot (
    TimeslotID CHAR(15) PRIMARY KEY,                                  -- ʱ��ID, ����
    EventID CHAR(15) NOT NULL,                                       -- �¼�ID (����: 'activity_xxxx' �� 'training_xxxx')
    StartTime SMALLDATETIME NOT NULL,                                     -- ʱ�ο�ʼʱ��
    EndTime SMALLDATETIME NOT NULL,                                       -- ʱ�ν���ʱ��
    CONSTRAINT CHK_ActivityTimeslot_EndTimeAfterStart CHECK (EndTime > StartTime) -- ȷ������ʱ���ڿ�ʼʱ��֮��
);
GO

-- ˳�� 9: ���� ־Ը�߻������ (tbl_VolunteerActivityApplication)
CREATE TABLE tbl_VolunteerActivityApplication (
    ApplicationID CHAR(15) PRIMARY KEY,                                -- ���������Ψһ��ʶ (CHAR���͵�Ψһ����Ӧ�ò���ȷ��)
    VolunteerID CHAR(15) NOT NULL FOREIGN KEY REFERENCES tbl_Volunteer(VolunteerID), -- ���������־Ը��
    ActivityID CHAR(15) NOT NULL FOREIGN KEY REFERENCES tbl_VolunteerActivity(ActivityID), -- ־Ը�ID
    IntendedPositionID CHAR(15) NULL FOREIGN KEY REFERENCES tbl_Position(PositionID), -- �������λ��
    ApplicationTime DATETIME NOT NULL DEFAULT GETDATE(),               -- ־Ը���ύ����ľ���ʱ��
    ApplicationStatus NVARCHAR(10) NOT NULL DEFAULT N'�����'
	CHECK (ApplicationStatus IN (N'�����', N'��ͨ��', N'�Ѿܾ�',N'ȡ������')) -- ����ĵ�ǰ״̬
);
GO

-- ˳�� 10: ���� ־Ը�߻����� (tbl_VolunteerActivityParticipation)
CREATE TABLE tbl_VolunteerActivityParticipation (
    VolunteerID CHAR(15) NOT NULL FOREIGN KEY REFERENCES tbl_Volunteer(VolunteerID),       -- ����־Ը�߱�
    ActivityID CHAR(15) NOT NULL FOREIGN KEY REFERENCES tbl_VolunteerActivity(ActivityID), -- ����־Ը���
    ActualPositionID CHAR(15) NOT NULL FOREIGN KEY REFERENCES tbl_Position(PositionID), -- �������λ��
    IsCheckedIn NCHAR(1) NOT NULL DEFAULT N'��' CHECK (IsCheckedIn IN (N'��', N'��')),     -- ־Ը���Ƿ����ǩ��
    VolunteerToOrgRating INT CHECK (VolunteerToOrgRating >= 1 AND VolunteerToOrgRating <= 10), -- ־Ը�߸���֯����
    OrgToVolunteerRating INT CHECK (OrgToVolunteerRating >= 1 AND OrgToVolunteerRating <= 10), -- ��֯��־Ը������
    PRIMARY KEY (VolunteerID, ActualPositionID) -- �������ID+ʵ�ʸ�λID
);
GO

-- ˳�� 11: ���� ־Ը����ѵ����� (tbl_VolunteerTrainingParticipation)
CREATE TABLE tbl_VolunteerTrainingParticipation (
    VolunteerID CHAR(15) NOT NULL FOREIGN KEY REFERENCES tbl_Volunteer(VolunteerID),       -- ����־Ը�߱���Ψһ��־
    TrainingID CHAR(15) NOT NULL FOREIGN KEY REFERENCES tbl_VolunteerTraining(TrainingID),   -- ����־Ը��ѵ����Ψһ��־
    IsCheckedIn NCHAR(1) NOT NULL DEFAULT N'��' CHECK (IsCheckedIn IN (N'��', N'��')),     -- ־Ը���Ƿ����ǩ��
    OrgToVolunteerRating INT CHECK (OrgToVolunteerRating >= 1 AND OrgToVolunteerRating <= 10), -- ��֯��־Ը������
    VolunteerToOrgRating INT CHECK (VolunteerToOrgRating >= 1 AND VolunteerToOrgRating <= 10), -- ־Ը�߸���֯����
    PRIMARY KEY (VolunteerID, TrainingID)                                                  -- ��������
);
GO

-- ˳�� 12: ���� Ͷ�߱� (tbl_Complaint)
CREATE TABLE tbl_Complaint (
    ComplaintID CHAR(15) PRIMARY KEY,                                 -- Ͷ��ID, ���� (CHAR���͵�Ψһ����Ӧ�ò���ȷ��)
    ComplaintTime DATETIME NOT NULL DEFAULT GETDATE(),                -- Ͷ��ʱ��
    ComplainantID CHAR(15) NOT NULL,                                  -- �����˶���ID (ID��ǰ׺��Ӧ�ò��洦������������)
    ComplaintTargetID CHAR(15) NOT NULL,                              -- Ͷ�߶���ID (ID��ǰ׺��Ӧ�ò��洦������������)
    ComplaintType NVARCHAR(10) NOT NULL DEFAULT N'����'
        CHECK (ComplaintType IN (
            N'��������', N'��Ϊ����', N'��Ϣ���', N'�Υ��', N'����'
        )),
    ComplaintContent NVARCHAR(MAX) NOT NULL,                          -- Ͷ������
    EvidenceLink NVARCHAR(255),                                       -- ֤������
    ProcessingStatus NVARCHAR(10) NOT NULL DEFAULT N'δ����'
        CHECK (ProcessingStatus IN (
            N'δ����', N'תӦ��', N'������', N'��Ӧ��', N'�ٲ���', N'���ٲ�', N'���˻�'
        )),
    ProcessingResult NVARCHAR(MAX),                                   -- �������
    LatestProcessingTime DATETIME,                                    -- ���´���ʱ��
    HandlerAdminID CHAR(15) FOREIGN KEY REFERENCES tbl_Administrator(AdminID), -- ������ID (�ɿ�)
    VisitTime DATETIME,                                               -- �ط�ʱ��
    VisitResult NVARCHAR(20) NOT NULL DEFAULT N'����'
        CHECK (VisitResult IN (N'����', N'������')),
    ArbitrationRound INT CHECK (ArbitrationRound BETWEEN 1 AND 2),    -- �ٲ��ִ�
    ReviewAdminID CHAR(15) FOREIGN KEY REFERENCES tbl_Administrator(AdminID) -- �������ԱID (�ɿ�)
);
GO