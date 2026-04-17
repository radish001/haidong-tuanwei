-- ============================================
-- 集成测试基础数据 (精简版)
-- ============================================

-- 用户表
truncate table sys_user;
insert into sys_user (id, username, password, nickname, phone, enabled) values
(1, 'admin', '{noop}123456', '系统管理员', '13800000000', 1);

-- 角色表
truncate table sys_role;
insert into sys_role (id, role_code, role_name, enabled) values
(1, 'ADMIN', '管理员', 1);

-- 用户角色关联表
truncate table sys_user_role;
insert into sys_user_role (user_id, role_id) values
(1, 1);

-- 字典表 - 企业相关
truncate table sys_dict_item;
insert into sys_dict_item (id, dict_type, dict_label, dict_value, sort_no, enabled) values
-- 性别
(1, 'gender', '男', 'M', 1, 1),
(2, 'gender', '女', 'F', 2, 1),
-- 民族
(10, 'ethnicity', '汉族', 'HAN', 1, 1),
(11, 'ethnicity', '藏族', 'TIBETAN', 2, 1),
(12, 'ethnicity', '回族', 'HUI', 3, 1),
-- 政治面貌
(20, 'political_status', '共青团员', 'CYL', 1, 1),
(21, 'political_status', '中共党员', 'CPC', 2, 1),
-- 学历
(30, 'education_level', '本科', 'BK', 1, 1),
(31, 'education_level', '硕士', 'SS', 2, 1),
(32, 'education_level', '专科', 'ZK', 3, 1),
-- 学位
(40, 'degree', '学士', 'XS', 1, 1),
(41, 'degree', '硕士', 'SS', 2, 1),
(42, 'degree', '博士', 'BS', 3, 1),
-- 企业行业
(50, 'enterprise_industry', '互联网/IT', 'IT', 1, 1),
(51, 'enterprise_industry', '制造业', 'MANUFACTURING', 2, 1),
(52, 'enterprise_industry', '金融', 'FINANCE', 3, 1),
(53, 'enterprise_industry', '教育', 'EDUCATION', 4, 1),
-- 企业性质
(60, 'enterprise_nature', '国有企业', 'SOE', 1, 1),
(61, 'enterprise_nature', '民营企业', 'PRIVATE', 2, 1),
(62, 'enterprise_nature', '外资企业', 'FOREIGN', 3, 1),
(63, 'enterprise_nature', '合资企业', 'JOINT', 4, 1),
-- 企业规模
(70, 'enterprise_scale', '大型企业', 'LARGE', 1, 1),
(71, 'enterprise_scale', '中型企业', 'MEDIUM', 2, 1),
(72, 'enterprise_scale', '小型企业', 'SMALL', 3, 1),
(73, 'enterprise_scale', '微型企业', 'MICRO', 4, 1),
-- 经验要求
(80, 'experience_requirement', '应届生', 'FRESH', 1, 1),
(81, 'experience_requirement', '1-3年', 'EXP_1_3', 2, 1),
(82, 'experience_requirement', '3-5年', 'EXP_3_5', 3, 1),
(83, 'experience_requirement', '5年以上', 'EXP_5_PLUS', 4, 1),
-- 薪资范围
(90, 'salary_range', '3000-5000', 'SALARY_3_5', 1, 1),
(91, 'salary_range', '5000-8000', 'SALARY_5_8', 2, 1),
(92, 'salary_range', '8000-12000', 'SALARY_8_12', 3, 1),
(93, 'salary_range', '12000以上', 'SALARY_12_PLUS', 4, 1),
-- 专业类别
(140, 'major_category', '工学', 'ENGINEERING', 1, 1),
(141, 'major_category', '管理学', 'MANAGEMENT', 2, 1),
-- 学校类别
(100, 'school_category', '双一流', 'DOUBLE_FIRST', 1, 1),
(101, 'school_category', '普通本科', 'REGULAR', 2, 1),
(102, 'school_category', '专科', 'VOCATIONAL', 3, 1),
-- 青年类型
(110, 'youth_type', '在校大学生', 'COLLEGE', 1, 1),
(111, 'youth_type', '毕业未就业', 'GRADUATE', 2, 1),
(112, 'youth_type', '农村待业', 'RURAL', 3, 1),
(113, 'youth_type', '创业青年', 'ENTREPRENEUR', 4, 1),
-- 就业状态
(120, 'employment_status', '未就业', 'UNEMPLOYED', 1, 1),
(121, 'employment_status', '已就业', 'EMPLOYED', 2, 1),
(122, 'employment_status', '创业中', 'STARTUP', 3, 1),
-- 创业需求
(130, 'entrepreneurship_demand', '资金支持', 'FUNDING', 1, 1),
(131, 'entrepreneurship_demand', '场地支持', 'VENUE', 2, 1),
(132, 'entrepreneurship_demand', '导师指导', 'MENTOR', 3, 1);

update sys_dict_item
set education_scopes = 'UNDERGRADUATE'
where dict_type = 'major_category';

-- 区域表
truncate table sys_region;
insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no) values
-- 青海省
(1, 0, '630000', '青海省', 1, 1),
-- 海东市
(2, 1, '630200', '海东市', 2, 1),
-- 乐都区
(3, 2, '630202', '乐都区', 3, 1),
(4, 2, '630203', '平安区', 3, 2),
-- 西宁市
(5, 1, '630100', '西宁市', 2, 2),
(6, 5, '630102', '城东区', 3, 1),
(7, 5, '630103', '城中区', 3, 2);

-- 专业目录表
truncate table sys_major_catalog;
insert into sys_major_catalog (id, major_code, major_name, category_dict_item_id) values
(1, '080901', '计算机科学与技术', 140),
(2, '080902', '软件工程', 140),
(3, '080903', '网络工程', 140),
(4, '120201', '工商管理', 141),
(5, '120202', '市场营销', 141);

-- 学校标签表
truncate table sys_school_tag;
insert into sys_school_tag (id, tag_name) values
(1, '985'),
(2, '211'),
(3, '双一流');

-- 学校表
truncate table sys_school;
insert into sys_school (id, school_code, school_name, category_dict_item_id) values
(1, '10743', '青海大学', 100),
(2, '10746', '青海师范大学', 101);

-- 学校标签关联
truncate table sys_school_tag_rel;
insert into sys_school_tag_rel (school_id, tag_id) values
(1, 1),
(1, 3);

-- 企业表 - 预置示例企业供岗位测试使用
truncate table enterprise_info;
insert into enterprise_info (id, enterprise_name, industry, enterprise_nature, enterprise_scale, 
    region_province_code, region_city_code, region_county_code, address, contact_person, contact_phone, 
    status, deleted) values
(1, '测试科技有限公司', 'IT', 'PRIVATE', 'MEDIUM', 
    '630000', '630200', '630202', '海东市乐都区科技园', '张经理', '13900000001', 
    1, 0),
(2, '青海制造集团', 'MANUFACTURING', 'SOE', 'LARGE', 
    '630000', '630100', '630102', '西宁市城东区工业区', '李主任', '13900000002', 
    1, 0);
