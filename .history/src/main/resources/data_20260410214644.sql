-- ============================================
-- 核心系统数据
-- ============================================

-- 用户表
truncate table sys_user;
insert into sys_user (id, username, password, nickname, phone, enabled, deleted) values (1, 'admin', '{noop}123456', '系统管理员', '13800000000', 1, 0);

-- 角色表
truncate table sys_role;
insert into sys_role (id, role_code, role_name, enabled, deleted) values (1, 'ADMIN', '管理员', 1, 0);

-- 用户角色关联表
truncate table sys_user_role;
insert into sys_user_role (user_id, role_id, deleted) values (1, 1, 0);

-- 菜单表
truncate table sys_menu;
insert into sys_menu (id, parent_id, menu_name, menu_path, icon, sort_no, visible, deleted) values
(1, 0, '首页', '/dashboard', 'home', 1, 1, 0),
(2, 0, '青年信息库', '/youth/college', 'team', 2, 1, 0),
(3, 2, '在校大学生', '/youth/college', null, 1, 1, 0),
(4, 2, '毕业未就业', '/youth/graduate', null, 2, 1, 0),
(5, 2, '农村社区待业', '/youth/rural', null, 3, 1, 0),
(6, 2, '创业青年', '/youth/entrepreneur', null, 4, 1, 0),
(7, 8, '企业信息', '/enterprises', 'building', 2, 1, 0),
(8, 0, '企业招聘信息', '/jobs', 'briefcase', 3, 1, 0),
(9, 0, '就业创业政策', '/policies', 'file-text', 5, 1, 0),
(10, 0, '数据分析', '/analytics/college', 'chart', 6, 1, 0),
(11, 10, '在校大学生分析', '/analytics/college', null, 1, 1, 0),
(12, 10, '毕业未就业分析', '/analytics/graduate', null, 2, 1, 0),
(13, 10, '农村社区待业分析', '/analytics/rural', null, 3, 1, 0),
(14, 10, '创业青年分析', '/analytics/entrepreneur', null, 4, 1, 0),
(15, 0, '系统设置', '/system/dictionaries', 'setting', 7, 1, 0),
(16, 15, '字典管理', '/system/dictionaries', null, 1, 1, 0),
(17, 15, '区域管理', '/system/regions', null, 2, 1, 0);

-- 角色菜单关联表
truncate table sys_role_menu;
insert into sys_role_menu (role_id, menu_id, deleted)
select 1, id, 0 from sys_menu where deleted = 0;

-- 字典表
truncate table sys_dict_item;

-- 性别
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted) values
('gender', '男', '01', 1, 1, 0),
('gender', '女', '02', 2, 1, 0);

-- 民族 (GB/T 3304-1991)
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted) values
('ethnicity', '汉族', '01', 1, 1, 0),
('ethnicity', '蒙古族', '02', 2, 1, 0),
('ethnicity', '回族', '03', 3, 1, 0),
('ethnicity', '藏族', '04', 4, 1, 0),
('ethnicity', '维吾尔族', '05', 5, 1, 0),
('ethnicity', '苗族', '06', 6, 1, 0),
('ethnicity', '彝族', '07', 7, 1, 0),
('ethnicity', '壮族', '08', 8, 1, 0),
('ethnicity', '布依族', '09', 9, 1, 0),
('ethnicity', '朝鲜族', '10', 10, 1, 0),
('ethnicity', '满族', '11', 11, 1, 0),
('ethnicity', '侗族', '12', 12, 1, 0),
('ethnicity', '瑶族', '13', 13, 1, 0),
('ethnicity', '白族', '14', 14, 1, 0),
('ethnicity', '土家族', '15', 15, 1, 0),
('ethnicity', '哈尼族', '16', 16, 1, 0),
('ethnicity', '哈萨克族', '17', 17, 1, 0),
('ethnicity', '傣族', '18', 18, 1, 0),
('ethnicity', '黎族', '19', 19, 1, 0),
('ethnicity', '傈僳族', '20', 20, 1, 0),
('ethnicity', '佤族', '21', 21, 1, 0),
('ethnicity', '畲族', '22', 22, 1, 0),
('ethnicity', '高山族', '23', 23, 1, 0),
('ethnicity', '拉祜族', '24', 24, 1, 0),
('ethnicity', '水族', '25', 25, 1, 0),
('ethnicity', '东乡族', '26', 26, 1, 0),
('ethnicity', '纳西族', '27', 27, 1, 0),
('ethnicity', '景颇族', '28', 28, 1, 0),
('ethnicity', '柯尔克孜族', '29', 29, 1, 0),
('ethnicity', '土族', '30', 30, 1, 0),
('ethnicity', '达斡尔族', '31', 31, 1, 0),
('ethnicity', '仫佬族', '32', 32, 1, 0),
('ethnicity', '羌族', '33', 33, 1, 0),
('ethnicity', '布朗族', '34', 34, 1, 0),
('ethnicity', '撒拉族', '35', 35, 1, 0),
('ethnicity', '毛南族', '36', 36, 1, 0),
('ethnicity', '仡佬族', '37', 37, 1, 0),
('ethnicity', '锡伯族', '38', 38, 1, 0),
('ethnicity', '阿昌族', '39', 39, 1, 0),
('ethnicity', '普米族', '40', 40, 1, 0),
('ethnicity', '塔吉克族', '41', 41, 1, 0),
('ethnicity', '怒族', '42', 42, 1, 0),
('ethnicity', '乌孜别克族', '43', 43, 1, 0),
('ethnicity', '俄罗斯族', '44', 44, 1, 0),
('ethnicity', '鄂温克族', '45', 45, 1, 0),
('ethnicity', '德昂族', '46', 46, 1, 0),
('ethnicity', '保安族', '47', 47, 1, 0),
('ethnicity', '裕固族', '48', 48, 1, 0),
('ethnicity', '京族', '49', 49, 1, 0),
('ethnicity', '塔塔尔族', '50', 50, 1, 0),
('ethnicity', '独龙族', '51', 51, 1, 0),
('ethnicity', '鄂伦春族', '52', 52, 1, 0),
('ethnicity', '赫哲族', '53', 53, 1, 0),
('ethnicity', '门巴族', '54', 54, 1, 0),
('ethnicity', '珞巴族', '55', 55, 1, 0),
('ethnicity', '基诺族', '56', 56, 1, 0);

-- 政治面貌
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted) values
('political_status', '群众', '01', 1, 1, 0),
('political_status', '共青团员', '02', 2, 1, 0),
('political_status', '中共党员', '03', 3, 1, 0);

-- 经验要求
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted) values
('experience_requirement', '不限', '01', 1, 1, 0),
('experience_requirement', '应届毕业生', '02', 2, 1, 0),
('experience_requirement', '1-3年', '03', 3, 1, 0),
('experience_requirement', '3-5年', '04', 4, 1, 0);

-- 薪资范围
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted) values
('salary_range', '3000元以下', '01', 1, 1, 0),
('salary_range', '3000-5000元', '02', 2, 1, 0),
('salary_range', '5000-10000元', '03', 3, 1, 0),
('salary_range', '10000元以上', '04', 4, 1, 0);


-- 专业类别 (14个学科门类)
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted) values
('major_category', '哲学', '01', 1, 1, 0),
('major_category', '经济学', '02', 2, 1, 0),
('major_category', '法学', '03', 3, 1, 0),
('major_category', '教育学', '04', 4, 1, 0),
('major_category', '文学', '05', 5, 1, 0),
('major_category', '历史学', '06', 6, 1, 0),
('major_category', '理学', '07', 7, 1, 0),
('major_category', '工学', '08', 8, 1, 0),
('major_category', '农学', '09', 9, 1, 0),
('major_category', '医学', '10', 10, 1, 0),
('major_category', '管理学', '11', 11, 1, 0),
('major_category', '艺术学', '12', 12, 1, 0),
('major_category', '军事学', '13', 13, 1, 0),
('major_category', '交叉学科', '14', 14, 1, 0);

-- 学校类别（双一流建设、本科、专科）
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted) values
('school_category', '双一流建设', '01', 1, 1, 0),
('school_category', '本科', '02', 2, 1, 0),
('school_category', '专科', '03', 3, 1, 0);

-- 企业规模
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted) values
('enterprise_scale', '1-9人', '01', 1, 1, 0),
('enterprise_scale', '10-49人', '02', 2, 1, 0),
('enterprise_scale', '50-99人', '03', 3, 1, 0),
('enterprise_scale', '100-499人', '04', 4, 1, 0),
('enterprise_scale', '500人以上', '05', 5, 1, 0);

-- 企业性质
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted) values
('enterprise_nature', '国有企业', '01', 1, 1, 0),
('enterprise_nature', '民营企业', '02', 2, 1, 0),
('enterprise_nature', '事业单位', '03', 3, 1, 0);

-- 企业行业
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted) values
('enterprise_industry', '智能硬件', '01', 1, 1, 0),
('enterprise_industry', '互联网', '02', 2, 1, 0),
('enterprise_industry', '文化旅游', '03', 3, 1, 0);

-- 青年类型
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted) values
('youth_type', '在校大学生', '01', 1, 1, 0),
('youth_type', '毕业未就业', '02', 2, 1, 0),
('youth_type', '农村社区待业', '03', 3, 1, 0),
('youth_type', '创业青年', '04', 4, 1, 0);
-- ============================================
-- 字典数据 - 学历和学位
-- ============================================

-- 学历代码
-- 注：sys_dict_item表已在data_core.sql中truncate，此处直接插入数据
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted) values
('education_level', '专科', '04', 1, 1, 0),
('education_level', '本科', '05', 2, 1, 0),
('education_level', '硕士研究生', '06', 3, 1, 0),
('education_level', '博士研究生', '07', 4, 1, 0);

-- 学位代码
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted) values
('degree', '无学位', '000', 1, 1, 0),
('degree', '学士学位', '100', 2, 1, 0),
('degree', '硕士学位', '200', 3, 1, 0),
('degree', '博士学位', '300', 4, 1, 0);

