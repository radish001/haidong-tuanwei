insert into sys_user (id, username, password, nickname, phone, enabled, deleted)
select 1, 'admin', '{noop}123456', '系统管理员', '13800000000', 1, 0
where not exists (select 1 from sys_user where username = 'admin');

insert into sys_role (id, role_code, role_name, enabled, deleted)
select 1, 'ADMIN', '管理员', 1, 0
where not exists (select 1 from sys_role where role_code = 'ADMIN');

insert into sys_user_role (user_id, role_id, deleted)
select 1, 1, 0
where not exists (select 1 from sys_user_role where user_id = 1 and role_id = 1);

insert into sys_menu (id, parent_id, menu_name, menu_path, icon, sort_no, visible, deleted)
select 1, 0, '首页', '/dashboard', 'home', 1, 1, 0
where not exists (select 1 from sys_menu where id = 1);
insert into sys_menu (id, parent_id, menu_name, menu_path, icon, sort_no, visible, deleted)
select 2, 0, '青年信息库', '/youth/college', 'team', 2, 1, 0
where not exists (select 1 from sys_menu where id = 2);
insert into sys_menu (id, parent_id, menu_name, menu_path, icon, sort_no, visible, deleted)
select 3, 2, '在校大学生', '/youth/college', null, 1, 1, 0
where not exists (select 1 from sys_menu where id = 3);
insert into sys_menu (id, parent_id, menu_name, menu_path, icon, sort_no, visible, deleted)
select 4, 2, '毕业未就业', '/youth/graduate', null, 2, 1, 0
where not exists (select 1 from sys_menu where id = 4);
insert into sys_menu (id, parent_id, menu_name, menu_path, icon, sort_no, visible, deleted)
select 5, 2, '农村社区待业', '/youth/rural', null, 3, 1, 0
where not exists (select 1 from sys_menu where id = 5);
insert into sys_menu (id, parent_id, menu_name, menu_path, icon, sort_no, visible, deleted)
select 6, 2, '创业青年', '/youth/entrepreneur', null, 4, 1, 0
where not exists (select 1 from sys_menu where id = 6);
insert into sys_menu (id, parent_id, menu_name, menu_path, icon, sort_no, visible, deleted)
select 7, 8, '企业信息', '/enterprises', 'building', 2, 1, 0
where not exists (select 1 from sys_menu where id = 7);
insert into sys_menu (id, parent_id, menu_name, menu_path, icon, sort_no, visible, deleted)
select 8, 0, '企业招聘信息', '/jobs', 'briefcase', 3, 1, 0
where not exists (select 1 from sys_menu where id = 8);
insert into sys_menu (id, parent_id, menu_name, menu_path, icon, sort_no, visible, deleted)
select 9, 0, '就业创业政策', '/policies', 'file-text', 5, 1, 0
where not exists (select 1 from sys_menu where id = 9);
insert into sys_menu (id, parent_id, menu_name, menu_path, icon, sort_no, visible, deleted)
select 10, 0, '数据分析', '/analytics/college', 'chart', 6, 1, 0
where not exists (select 1 from sys_menu where id = 10);
insert into sys_menu (id, parent_id, menu_name, menu_path, icon, sort_no, visible, deleted)
select 11, 10, '在校大学生分析', '/analytics/college', null, 1, 1, 0
where not exists (select 1 from sys_menu where id = 11);
insert into sys_menu (id, parent_id, menu_name, menu_path, icon, sort_no, visible, deleted)
select 12, 10, '毕业未就业分析', '/analytics/graduate', null, 2, 1, 0
where not exists (select 1 from sys_menu where id = 12);
insert into sys_menu (id, parent_id, menu_name, menu_path, icon, sort_no, visible, deleted)
select 13, 10, '农村社区待业分析', '/analytics/rural', null, 3, 1, 0
where not exists (select 1 from sys_menu where id = 13);
insert into sys_menu (id, parent_id, menu_name, menu_path, icon, sort_no, visible, deleted)
select 14, 10, '创业青年分析', '/analytics/entrepreneur', null, 4, 1, 0
where not exists (select 1 from sys_menu where id = 14);
insert into sys_menu (id, parent_id, menu_name, menu_path, icon, sort_no, visible, deleted)
select 15, 0, '系统设置', '/system/dictionaries', 'setting', 7, 1, 0
where not exists (select 1 from sys_menu where id = 15);
insert into sys_menu (id, parent_id, menu_name, menu_path, icon, sort_no, visible, deleted)
select 16, 15, '字典管理', '/system/dictionaries', null, 1, 1, 0
where not exists (select 1 from sys_menu where id = 16);
insert into sys_menu (id, parent_id, menu_name, menu_path, icon, sort_no, visible, deleted)
select 17, 15, '区域管理', '/system/regions', null, 2, 1, 0
where not exists (select 1 from sys_menu where id = 17);

insert into sys_role_menu (role_id, menu_id, deleted)
select 1, id, 0 from sys_menu
where deleted = 0
  and not exists (select 1 from sys_role_menu where role_id = 1 and menu_id = sys_menu.id);

insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'gender', '男', '男', 1, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'gender' and dict_value = '男');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'gender', '女', '女', 2, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'gender' and dict_value = '女');

insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'ethnicity', '汉族', '汉族', 1, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'ethnicity' and dict_value = '汉族');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'ethnicity', '土族', '土族', 2, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'ethnicity' and dict_value = '土族');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'ethnicity', '回族', '回族', 3, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'ethnicity' and dict_value = '回族');

insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'political_status', '中共党员', '中共党员', 1, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'political_status' and dict_value = '中共党员');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'political_status', '共青团员', '共青团员', 2, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'political_status' and dict_value = '共青团员');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'political_status', '群众', '群众', 3, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'political_status' and dict_value = '群众');

update sys_dict_item
set dict_value = 'ZK'
where dict_type = 'education_level'
  and dict_label = '专科'
  and dict_value != 'ZK';

update sys_dict_item
set dict_value = 'BK'
where dict_type = 'education_level'
  and dict_label = '本科'
  and dict_value != 'BK';

update sys_dict_item
set dict_value = 'SSYJS'
where dict_type = 'education_level'
  and dict_label = '硕士研究生'
  and dict_value != 'SSYJS';

insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'education_level', '专科', 'ZK', 1, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'education_level' and dict_value = 'ZK');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'education_level', '本科', 'BK', 2, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'education_level' and dict_value = 'BK');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'education_level', '硕士研究生', 'SSYJS', 3, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'education_level' and dict_value = 'SSYJS');

update sys_dict_item
set dict_value = 'WXW'
where dict_type = 'degree'
  and dict_label = '无'
  and dict_value != 'WXW';

update sys_dict_item
set dict_value = 'XS'
where dict_type = 'degree'
  and dict_label = '学士'
  and dict_value != 'XS';

update sys_dict_item
set dict_value = 'SS'
where dict_type = 'degree'
  and dict_label = '硕士'
  and dict_value != 'SS';

insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'degree', '无', 'WXW', 1, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'degree' and dict_value = 'WXW');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'degree', '学士', 'XS', 2, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'degree' and dict_value = 'XS');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'degree', '硕士', 'SS', 3, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'degree' and dict_value = 'SS');

update youth_info
set education_level = 'ZK'
where education_level = '专科';

update youth_info
set education_level = 'BK'
where education_level = '本科';

update youth_info
set education_level = 'SSYJS'
where education_level = '硕士研究生';

update youth_info
set degree_code = 'WXW'
where degree_code = '无';

update youth_info
set degree_code = 'XS'
where degree_code = '学士';

update youth_info
set degree_code = 'SS'
where degree_code = '硕士';

update job_post
set education_requirement = 'ZK'
where education_requirement = '专科';

update job_post
set education_requirement = 'BK'
where education_requirement = '本科';

update job_post
set education_requirement = 'SSYJS'
where education_requirement = '硕士研究生';

insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'experience_requirement', '不限', '不限', 1, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'experience_requirement' and dict_value = '不限');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'experience_requirement', '应届毕业生', '应届毕业生', 2, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'experience_requirement' and dict_value = '应届毕业生');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'experience_requirement', '1-3年', '1-3年', 3, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'experience_requirement' and dict_value = '1-3年');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'experience_requirement', '3-5年', '3-5年', 4, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'experience_requirement' and dict_value = '3-5年');

insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'salary_range', '3000元以下', '3000元以下', 1, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'salary_range' and dict_value = '3000元以下');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'salary_range', '3000-5000元', '3000-5000元', 2, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'salary_range' and dict_value = '3000-5000元');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'salary_range', '5000-8000元', '5000-8000元', 3, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'salary_range' and dict_value = '5000-8000元');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'salary_range', '8000-12000元', '8000-12000元', 4, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'salary_range' and dict_value = '8000-12000元');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'salary_range', '12000元以上', '12000元以上', 5, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'salary_range' and dict_value = '12000元以上');

insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'major_category', '哲学', '哲学', 1, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'major_category' and dict_value = '哲学');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'major_category', '经济学', '经济学', 2, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'major_category' and dict_value = '经济学');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'major_category', '法学', '法学', 3, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'major_category' and dict_value = '法学');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'major_category', '教育学', '教育学', 4, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'major_category' and dict_value = '教育学');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'major_category', '文学', '文学', 5, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'major_category' and dict_value = '文学');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'major_category', '历史学', '历史学', 6, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'major_category' and dict_value = '历史学');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'major_category', '理学', '理学', 7, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'major_category' and dict_value = '理学');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'major_category', '工学', '工学', 8, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'major_category' and dict_value = '工学');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'major_category', '农学', '农学', 9, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'major_category' and dict_value = '农学');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'major_category', '医学', '医学', 10, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'major_category' and dict_value = '医学');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'major_category', '管理学', '管理学', 11, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'major_category' and dict_value = '管理学');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'major_category', '艺术学', '艺术学', 12, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'major_category' and dict_value = '艺术学');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'major_category', '军事学', '军事学', 13, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'major_category' and dict_value = '军事学');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'major_category', '交叉学科', '交叉学科', 14, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'major_category' and dict_value = '交叉学科');

insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'school_category', '双一流', '双一流', 1, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'school_category' and dict_value = '双一流');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'school_category', '重点本科', '重点本科', 2, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'school_category' and dict_value = '重点本科');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'school_category', '普通本科', '普通本科', 3, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'school_category' and dict_value = '普通本科');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'school_category', '专科', '专科', 4, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'school_category' and dict_value = '专科');

insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'enterprise_scale', '1-9人', '1-9人', 1, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'enterprise_scale' and dict_value = '1-9人');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'enterprise_scale', '10-49人', '10-49人', 2, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'enterprise_scale' and dict_value = '10-49人');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'enterprise_scale', '50-99人', '50-99人', 3, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'enterprise_scale' and dict_value = '50-99人');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'enterprise_scale', '100-499人', '100-499人', 4, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'enterprise_scale' and dict_value = '100-499人');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'enterprise_scale', '500人以上', '500人以上', 5, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'enterprise_scale' and dict_value = '500人以上');

insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'enterprise_nature', '国有企业', '国有企业', 1, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'enterprise_nature' and dict_value = '国有企业');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'enterprise_nature', '民营企业', '民营企业', 2, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'enterprise_nature' and dict_value = '民营企业');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'enterprise_nature', '事业单位', '事业单位', 3, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'enterprise_nature' and dict_value = '事业单位');

insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'enterprise_industry', '智能硬件', '智能硬件', 1, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'enterprise_industry' and dict_value = '智能硬件');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'enterprise_industry', '互联网', '互联网', 2, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'enterprise_industry' and dict_value = '互联网');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'enterprise_industry', '文化旅游', '文化旅游', 3, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'enterprise_industry' and dict_value = '文化旅游');

insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'youth_type', '在校大学生', 'COLLEGE', 1, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'youth_type' and dict_value = 'COLLEGE');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'youth_type', '毕业未就业', 'GRADUATED_UNEMPLOYED', 2, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'youth_type' and dict_value = 'GRADUATED_UNEMPLOYED');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'youth_type', '农村社区待业', 'RURAL_COMMUNITY', 3, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'youth_type' and dict_value = 'RURAL_COMMUNITY');
insert into sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, deleted)
select 'youth_type', '创业青年', 'ENTREPRENEUR', 4, 1, 0
where not exists (select 1 from sys_dict_item where dict_type = 'youth_type' and dict_value = 'ENTREPRENEUR');

insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no, deleted)
select 1, 0, '630000', '青海省', 1, 1, 0
where not exists (select 1 from sys_region where region_code = '630000');
insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no, deleted)
select 101, 0, '110000', '北京市', 1, 2, 0
where not exists (select 1 from sys_region where region_code = '110000');
insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no, deleted)
select 102, 0, '120000', '天津市', 1, 3, 0
where not exists (select 1 from sys_region where region_code = '120000');
insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no, deleted)
select 103, 0, '130000', '河北省', 1, 4, 0
where not exists (select 1 from sys_region where region_code = '130000');
insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no, deleted)
select 104, 0, '140000', '山西省', 1, 5, 0
where not exists (select 1 from sys_region where region_code = '140000');
insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no, deleted)
select 105, 0, '150000', '内蒙古自治区', 1, 6, 0
where not exists (select 1 from sys_region where region_code = '150000');
insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no, deleted)
select 106, 0, '210000', '辽宁省', 1, 7, 0
where not exists (select 1 from sys_region where region_code = '210000');
insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no, deleted)
select 107, 0, '220000', '吉林省', 1, 8, 0
where not exists (select 1 from sys_region where region_code = '220000');
insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no, deleted)
select 108, 0, '230000', '黑龙江省', 1, 9, 0
where not exists (select 1 from sys_region where region_code = '230000');
insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no, deleted)
select 109, 0, '310000', '上海市', 1, 10, 0
where not exists (select 1 from sys_region where region_code = '310000');
insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no, deleted)
select 110, 0, '320000', '江苏省', 1, 11, 0
where not exists (select 1 from sys_region where region_code = '320000');
insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no, deleted)
select 111, 0, '330000', '浙江省', 1, 12, 0
where not exists (select 1 from sys_region where region_code = '330000');
insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no, deleted)
select 112, 0, '340000', '安徽省', 1, 13, 0
where not exists (select 1 from sys_region where region_code = '340000');
insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no, deleted)
select 113, 0, '350000', '福建省', 1, 14, 0
where not exists (select 1 from sys_region where region_code = '350000');
insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no, deleted)
select 114, 0, '360000', '江西省', 1, 15, 0
where not exists (select 1 from sys_region where region_code = '360000');
insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no, deleted)
select 115, 0, '370000', '山东省', 1, 16, 0
where not exists (select 1 from sys_region where region_code = '370000');
insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no, deleted)
select 116, 0, '410000', '河南省', 1, 17, 0
where not exists (select 1 from sys_region where region_code = '410000');
insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no, deleted)
select 117, 0, '420000', '湖北省', 1, 18, 0
where not exists (select 1 from sys_region where region_code = '420000');
insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no, deleted)
select 118, 0, '430000', '湖南省', 1, 19, 0
where not exists (select 1 from sys_region where region_code = '430000');
insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no, deleted)
select 119, 0, '440000', '广东省', 1, 20, 0
where not exists (select 1 from sys_region where region_code = '440000');
insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no, deleted)
select 120, 0, '450000', '广西壮族自治区', 1, 21, 0
where not exists (select 1 from sys_region where region_code = '450000');
insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no, deleted)
select 121, 0, '460000', '海南省', 1, 22, 0
where not exists (select 1 from sys_region where region_code = '460000');
insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no, deleted)
select 122, 0, '500000', '重庆市', 1, 23, 0
where not exists (select 1 from sys_region where region_code = '500000');
insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no, deleted)
select 123, 0, '510000', '四川省', 1, 24, 0
where not exists (select 1 from sys_region where region_code = '510000');
insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no, deleted)
select 124, 0, '520000', '贵州省', 1, 25, 0
where not exists (select 1 from sys_region where region_code = '520000');
insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no, deleted)
select 125, 0, '530000', '云南省', 1, 26, 0
where not exists (select 1 from sys_region where region_code = '530000');
insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no, deleted)
select 126, 0, '540000', '西藏自治区', 1, 27, 0
where not exists (select 1 from sys_region where region_code = '540000');
insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no, deleted)
select 127, 0, '610000', '陕西省', 1, 28, 0
where not exists (select 1 from sys_region where region_code = '610000');
insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no, deleted)
select 128, 0, '620000', '甘肃省', 1, 29, 0
where not exists (select 1 from sys_region where region_code = '620000');
insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no, deleted)
select 129, 0, '640000', '宁夏回族自治区', 1, 30, 0
where not exists (select 1 from sys_region where region_code = '640000');
insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no, deleted)
select 130, 0, '650000', '新疆维吾尔自治区', 1, 31, 0
where not exists (select 1 from sys_region where region_code = '650000');
insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no, deleted)
select 131, 0, '710000', '台湾省', 1, 32, 0
where not exists (select 1 from sys_region where region_code = '710000');
insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no, deleted)
select 132, 0, '810000', '香港特别行政区', 1, 33, 0
where not exists (select 1 from sys_region where region_code = '810000');
insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no, deleted)
select 133, 0, '820000', '澳门特别行政区', 1, 34, 0
where not exists (select 1 from sys_region where region_code = '820000');
insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no, deleted)
select 2, 1, '630200', '海东市', 2, 1, 0
where not exists (select 1 from sys_region where region_code = '630200');
insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no, deleted)
select 3, 2, '630202', '乐都区', 3, 1, 0
where not exists (select 1 from sys_region where region_code = '630202');
insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no, deleted)
select 4, 2, '630203', '平安区', 3, 2, 0
where not exists (select 1 from sys_region where region_code = '630203');
insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no, deleted)
select 5, 2, '630222', '民和回族土族自治县', 3, 3, 0
where not exists (select 1 from sys_region where region_code = '630222');
update sys_region
set region_name = '民和回族土族自治县'
where region_code = '630222'
  and deleted = 0;
insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no, deleted)
select 1006, 2, '630223', '互助土族自治县', 3, 4, 0
where not exists (select 1 from sys_region where region_code = '630223');
insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no, deleted)
select 1007, 2, '630224', '化隆回族自治县', 3, 5, 0
where not exists (select 1 from sys_region where region_code = '630224');
insert into sys_region (id, parent_id, region_code, region_name, region_level, sort_no, deleted)
select 1008, 2, '630225', '循化撒拉族自治县', 3, 6, 0
where not exists (select 1 from sys_region where region_code = '630225');

update sys_major_catalog
set major_code = '080901'
where major_name = '计算机科学与技术'
  and (major_code is null or major_code = '');

insert into sys_major_catalog (major_code, major_name, category_dict_item_id, create_by, update_by, deleted)
select '080901', '计算机科学与技术', id, 1, 1, 0
from sys_dict_item
where dict_type = 'major_category'
  and dict_value = '工学'
  and not exists (select 1 from sys_major_catalog where major_code = '080901' or major_name = '计算机科学与技术');

update sys_major_catalog
set major_code = '120801'
where major_name = '电子商务'
  and (major_code is null or major_code = '');

insert into sys_major_catalog (major_code, major_name, category_dict_item_id, create_by, update_by, deleted)
select '120801', '电子商务', id, 1, 1, 0
from sys_dict_item
where dict_type = 'major_category'
  and dict_value = '管理学'
  and not exists (select 1 from sys_major_catalog where major_code = '120801' or major_name = '电子商务');

insert into sys_school_tag (tag_name, create_by, update_by, deleted)
select '双一流', 1, 1, 0
where not exists (select 1 from sys_school_tag where tag_name = '双一流');
insert into sys_school_tag (tag_name, create_by, update_by, deleted)
select '985', 1, 1, 0
where not exists (select 1 from sys_school_tag where tag_name = '985');
insert into sys_school_tag (tag_name, create_by, update_by, deleted)
select '211', 1, 1, 0
where not exists (select 1 from sys_school_tag where tag_name = '211');

update sys_school
set school_code = '10743'
where school_name = '青海大学'
  and (school_code is null or school_code = '');

insert into sys_school (school_code, school_name, category_dict_item_id, create_by, update_by, deleted)
select '10743', '青海大学', d.id, 1, 1, 0
from sys_dict_item d
where d.dict_type = 'school_category'
  and d.dict_value = '双一流'
  and not exists (select 1 from sys_school where school_code = '10743' or school_name = '青海大学');

update sys_school
set school_code = 'QHZY001'
where school_name = '青海职业技术大学'
  and (school_code is null or school_code = '');

insert into sys_school (school_code, school_name, category_dict_item_id, create_by, update_by, deleted)
select 'QHZY001', '青海职业技术大学', d.id, 1, 1, 0
from sys_dict_item d
where d.dict_type = 'school_category'
  and d.dict_value = '专科'
  and not exists (select 1 from sys_school where school_code = 'QHZY001' or school_name = '青海职业技术大学');

update sys_school
set school_code = '10730'
where school_name = '兰州大学'
  and (school_code is null or school_code = '');

insert into sys_school (school_code, school_name, category_dict_item_id, create_by, update_by, deleted)
select '10730', '兰州大学', d.id, 1, 1, 0
from sys_dict_item d
where d.dict_type = 'school_category'
  and d.dict_value = '双一流'
  and not exists (select 1 from sys_school where school_code = '10730' or school_name = '兰州大学');

update sys_school
set school_code = '10698'
where school_name = '西安交通大学'
  and (school_code is null or school_code = '');

insert into sys_school (school_code, school_name, category_dict_item_id, create_by, update_by, deleted)
select '10698', '西安交通大学', d.id, 1, 1, 0
from sys_dict_item d
where d.dict_type = 'school_category'
  and d.dict_value = '双一流'
  and not exists (select 1 from sys_school where school_code = '10698' or school_name = '西安交通大学');

update sys_school
set school_code = '10246'
where school_name = '复旦大学'
  and (school_code is null or school_code = '');

insert into sys_school (school_code, school_name, category_dict_item_id, create_by, update_by, deleted)
select '10246', '复旦大学', d.id, 1, 1, 0
from sys_dict_item d
where d.dict_type = 'school_category'
  and d.dict_value = '双一流'
  and not exists (select 1 from sys_school where school_code = '10246' or school_name = '复旦大学');

insert into sys_school_tag_rel (school_id, tag_id, create_by, update_by, deleted)
select s.id, t.id, 1, 1, 0
from sys_school s, sys_school_tag t
where s.school_name = '青海大学'
  and t.tag_name = '双一流'
  and not exists (
      select 1 from sys_school_tag_rel rel
      where rel.school_id = s.id and rel.tag_id = t.id
  );

insert into sys_school_tag_rel (school_id, tag_id, create_by, update_by, deleted)
select s.id, t.id, 1, 1, 0
from sys_school s, sys_school_tag t
where s.school_name = '青海大学'
  and t.tag_name = '211'
  and not exists (
      select 1 from sys_school_tag_rel rel
      where rel.school_id = s.id and rel.tag_id = t.id
  );

insert into sys_school_tag_rel (school_id, tag_id, create_by, update_by, deleted)
select s.id, t.id, 1, 1, 0
from sys_school s, sys_school_tag t
where s.school_name = '兰州大学'
  and t.tag_name = '双一流'
  and not exists (
      select 1 from sys_school_tag_rel rel
      where rel.school_id = s.id and rel.tag_id = t.id
  );

insert into sys_school_tag_rel (school_id, tag_id, create_by, update_by, deleted)
select s.id, t.id, 1, 1, 0
from sys_school s, sys_school_tag t
where s.school_name = '兰州大学'
  and t.tag_name = '985'
  and not exists (
      select 1 from sys_school_tag_rel rel
      where rel.school_id = s.id and rel.tag_id = t.id
  );

insert into sys_school_tag_rel (school_id, tag_id, create_by, update_by, deleted)
select s.id, t.id, 1, 1, 0
from sys_school s, sys_school_tag t
where s.school_name = '兰州大学'
  and t.tag_name = '211'
  and not exists (
      select 1 from sys_school_tag_rel rel
      where rel.school_id = s.id and rel.tag_id = t.id
  );

insert into sys_school_tag_rel (school_id, tag_id, create_by, update_by, deleted)
select s.id, t.id, 1, 1, 0
from sys_school s, sys_school_tag t
where s.school_name = '西安交通大学'
  and t.tag_name = '双一流'
  and not exists (
      select 1 from sys_school_tag_rel rel
      where rel.school_id = s.id and rel.tag_id = t.id
  );

insert into sys_school_tag_rel (school_id, tag_id, create_by, update_by, deleted)
select s.id, t.id, 1, 1, 0
from sys_school s, sys_school_tag t
where s.school_name = '西安交通大学'
  and t.tag_name = '985'
  and not exists (
      select 1 from sys_school_tag_rel rel
      where rel.school_id = s.id and rel.tag_id = t.id
  );

insert into sys_school_tag_rel (school_id, tag_id, create_by, update_by, deleted)
select s.id, t.id, 1, 1, 0
from sys_school s, sys_school_tag t
where s.school_name = '西安交通大学'
  and t.tag_name = '211'
  and not exists (
      select 1 from sys_school_tag_rel rel
      where rel.school_id = s.id and rel.tag_id = t.id
  );

insert into sys_school_tag_rel (school_id, tag_id, create_by, update_by, deleted)
select s.id, t.id, 1, 1, 0
from sys_school s, sys_school_tag t
where s.school_name = '复旦大学'
  and t.tag_name = '双一流'
  and not exists (
      select 1 from sys_school_tag_rel rel
      where rel.school_id = s.id and rel.tag_id = t.id
  );

insert into sys_school_tag_rel (school_id, tag_id, create_by, update_by, deleted)
select s.id, t.id, 1, 1, 0
from sys_school s, sys_school_tag t
where s.school_name = '复旦大学'
  and t.tag_name = '985'
  and not exists (
      select 1 from sys_school_tag_rel rel
      where rel.school_id = s.id and rel.tag_id = t.id
  );

insert into sys_school_tag_rel (school_id, tag_id, create_by, update_by, deleted)
select s.id, t.id, 1, 1, 0
from sys_school s, sys_school_tag t
where s.school_name = '复旦大学'
  and t.tag_name = '211'
  and not exists (
      select 1 from sys_school_tag_rel rel
      where rel.school_id = s.id and rel.tag_id = t.id
  );

insert into youth_info (
    youth_type, name, gender, birth_date, ethnicity, political_status, native_province_code, native_city_code, native_county_code,
    education_level, degree_code, school_code, school_name, school_province_code, school_city_code, school_county_code,
    major_code, major, major_category, graduation_date,
    employment_direction, phone, residence_province_code, residence_city_code, residence_county_code, employment_status, current_job, employment_company,
    entrepreneurship_status, entrepreneurship_project, entrepreneurship_demand, remarks, create_by, update_by, deleted
)
select 'COLLEGE', '李明轩', '男', '2003-09-01', '汉族', '共青团员', '630000', '630200', '630203',
       'BK', 'XS', '10743', '青海大学', '630000', '630200', null, '080901', '计算机科学与技术', '工学', '2027-06-30',
       '互联网开发', '13900000001', '630000', '630200', '630203', '在校', null, null,
       '否', null, null, '示例在校大学生', 1, 1, 0
where not exists (select 1 from youth_info where name = '李明轩' and phone = '13900000001');

insert into youth_info (
    youth_type, name, gender, birth_date, ethnicity, political_status, native_province_code, native_city_code, native_county_code,
    education_level, degree_code, school_code, school_name, school_province_code, school_city_code, school_county_code,
    major_code, major, major_category, graduation_date,
    employment_direction, phone, residence_province_code, residence_city_code, residence_county_code, employment_status, current_job, employment_company,
    entrepreneurship_status, entrepreneurship_project, entrepreneurship_demand, remarks, create_by, update_by, deleted
)
select 'COLLEGE', '张启航', '男', '2002-03-14', '汉族', '中共党员', '630000', '630200', '630202',
       'BK', 'XS', '10730', '兰州大学', '620000', null, null, '080901', '计算机科学与技术', '工学', '2026-06-30',
       '软件工程', '13900000003', '620000', null, null, '在校', null, null,
       '否', null, null, '示例全国分布数据-甘肃', 1, 1, 0
where not exists (select 1 from youth_info where name = '张启航' and phone = '13900000003');

insert into youth_info (
    youth_type, name, gender, birth_date, ethnicity, political_status, native_province_code, native_city_code, native_county_code,
    education_level, degree_code, school_code, school_name, school_province_code, school_city_code, school_county_code,
    major_code, major, major_category, graduation_date,
    employment_direction, phone, residence_province_code, residence_city_code, residence_county_code, employment_status, current_job, employment_company,
    entrepreneurship_status, entrepreneurship_project, entrepreneurship_demand, remarks, create_by, update_by, deleted
)
select 'COLLEGE', '马成龙', '男', '2001-11-06', '回族', '共青团员', '630000', '630200', '630222',
       'BK', 'XS', '10698', '西安交通大学', '610000', null, null, '120801', '电子商务', '管理学', '2025-06-30',
       '运营管理', '13900000004', '610000', null, null, '在校', null, null,
       '否', null, null, '示例全国分布数据-陕西', 1, 1, 0
where not exists (select 1 from youth_info where name = '马成龙' and phone = '13900000004');

insert into youth_info (
    youth_type, name, gender, birth_date, ethnicity, political_status, native_province_code, native_city_code, native_county_code,
    education_level, degree_code, school_code, school_name, school_province_code, school_city_code, school_county_code,
    major_code, major, major_category, graduation_date,
    employment_direction, phone, residence_province_code, residence_city_code, residence_county_code, employment_status, current_job, employment_company,
    entrepreneurship_status, entrepreneurship_project, entrepreneurship_demand, remarks, create_by, update_by, deleted
)
select 'COLLEGE', '韩文静', '女', '2002-08-19', '土族', '群众', '630000', '630200', '630223',
       'BK', 'XS', '10246', '复旦大学', '310000', null, null, '120801', '电子商务', '管理学', '2026-06-30',
       '市场分析', '13900000005', '310000', null, null, '在校', null, null,
       '否', null, null, '示例全国分布数据-上海', 1, 1, 0
where not exists (select 1 from youth_info where name = '韩文静' and phone = '13900000005');

insert into youth_info (
    youth_type, name, gender, birth_date, ethnicity, political_status, native_province_code, native_city_code, native_county_code,
    education_level, degree_code, school_code, school_name, school_province_code, school_city_code, school_county_code,
    major_code, major, major_category, graduation_date,
    employment_direction, phone, residence_province_code, residence_city_code, residence_county_code, employment_status, current_job, employment_company,
    entrepreneurship_status, entrepreneurship_project, entrepreneurship_demand, remarks, create_by, update_by, deleted
)
select 'COLLEGE', '王海宁', '男', '2003-01-22', '撒拉族', '共青团员', '630000', '630200', '630224',
       'BK', 'XS', '10743', '青海大学', '630000', '630200', null, '080901', '计算机科学与技术', '工学', '2027-06-30',
       '人工智能', '13900000006', '630000', '630200', '630224', '在校', null, null,
       '否', null, null, '示例海东分布数据-化隆', 1, 1, 0
where not exists (select 1 from youth_info where name = '王海宁' and phone = '13900000006');

insert into youth_info (
    youth_type, name, gender, birth_date, ethnicity, political_status, native_province_code, native_city_code, native_county_code,
    education_level, degree_code, school_code, school_name, school_province_code, school_city_code, school_county_code,
    major_code, major, major_category, graduation_date,
    employment_direction, phone, residence_province_code, residence_city_code, residence_county_code, employment_status, current_job, employment_company,
    entrepreneurship_status, entrepreneurship_project, entrepreneurship_demand, remarks, create_by, update_by, deleted
)
select 'COLLEGE', '马晓璇', '女', '2002-05-09', '回族', '群众', '630000', '630200', '630225',
       'BK', 'XS', '10730', '兰州大学', '620000', null, null, '120801', '电子商务', '管理学', '2026-06-30',
       '数字媒体', '13900000007', '620000', null, null, '在校', null, null,
       '否', null, null, '示例海东分布数据-循化', 1, 1, 0
where not exists (select 1 from youth_info where name = '马晓璇' and phone = '13900000007');

insert into youth_info (
    youth_type, name, gender, birth_date, ethnicity, political_status, native_province_code, native_city_code, native_county_code,
    education_level, degree_code, school_code, school_name, school_province_code, school_city_code, school_county_code,
    major_code, major, major_category, graduation_date,
    employment_direction, phone, residence_province_code, residence_city_code, residence_county_code, employment_status, current_job, employment_company,
    entrepreneurship_status, entrepreneurship_project, entrepreneurship_demand, remarks, create_by, update_by, deleted
)
select 'COLLEGE', '赵星宇', '男', '2003-02-18', '汉族', '共青团员', '630000', '630200', '630202',
       'BK', 'XS', '10743', '青海大学', '630000', '630200', null, '080901', '计算机科学与技术', '工学', '2027-06-30',
       '后端开发', '13900000008', '630000', '630200', '630202', '在校', null, null,
       '否', null, null, '扩展示例在校大学生-乐都', 1, 1, 0
where not exists (select 1 from youth_info where name = '赵星宇' and phone = '13900000008');

insert into youth_info (
    youth_type, name, gender, birth_date, ethnicity, political_status, native_province_code, native_city_code, native_county_code,
    education_level, degree_code, school_code, school_name, school_province_code, school_city_code, school_county_code,
    major_code, major, major_category, graduation_date,
    employment_direction, phone, residence_province_code, residence_city_code, residence_county_code, employment_status, current_job, employment_company,
    entrepreneurship_status, entrepreneurship_project, entrepreneurship_demand, remarks, create_by, update_by, deleted
)
select 'COLLEGE', '周雨彤', '女', '2002-07-11', '土族', '中共党员', '630000', '630200', '630203',
       'BK', 'XS', '10730', '兰州大学', '620000', null, null, '080901', '计算机科学与技术', '工学', '2026-06-30',
       '数据分析', '13900000009', '620000', null, null, '在校', null, null,
       '否', null, null, '扩展示例在校大学生-平安', 1, 1, 0
where not exists (select 1 from youth_info where name = '周雨彤' and phone = '13900000009');

insert into youth_info (
    youth_type, name, gender, birth_date, ethnicity, political_status, native_province_code, native_city_code, native_county_code,
    education_level, degree_code, school_code, school_name, school_province_code, school_city_code, school_county_code,
    major_code, major, major_category, graduation_date,
    employment_direction, phone, residence_province_code, residence_city_code, residence_county_code, employment_status, current_job, employment_company,
    entrepreneurship_status, entrepreneurship_project, entrepreneurship_demand, remarks, create_by, update_by, deleted
)
select 'COLLEGE', '陈思源', '男', '2003-10-03', '回族', '群众', '630000', '630200', '630222',
       'BK', 'XS', '10246', '复旦大学', '310000', null, null, '120801', '电子商务', '管理学', '2027-06-30',
       '产品运营', '13900000010', '310000', null, null, '在校', null, null,
       '否', null, null, '扩展示例在校大学生-民和', 1, 1, 0
where not exists (select 1 from youth_info where name = '陈思源' and phone = '13900000010');

insert into youth_info (
    youth_type, name, gender, birth_date, ethnicity, political_status, native_province_code, native_city_code, native_county_code,
    education_level, degree_code, school_code, school_name, school_province_code, school_city_code, school_county_code,
    major_code, major, major_category, graduation_date,
    employment_direction, phone, residence_province_code, residence_city_code, residence_county_code, employment_status, current_job, employment_company,
    entrepreneurship_status, entrepreneurship_project, entrepreneurship_demand, remarks, create_by, update_by, deleted
)
select 'COLLEGE', '李若涵', '女', '2004-04-26', '汉族', '共青团员', '630000', '630200', '630224',
       'ZK', 'WXW', 'QHZY001', '青海职业技术大学', '630000', '630200', null, '120801', '电子商务', '管理学', '2026-06-30',
       '新媒体运营', '13900000011', '630000', '630200', '630224', '在校', null, null,
       '否', null, null, '扩展示例在校大学生-化隆', 1, 1, 0
where not exists (select 1 from youth_info where name = '李若涵' and phone = '13900000011');

insert into youth_info (
    youth_type, name, gender, birth_date, ethnicity, political_status, native_province_code, native_city_code, native_county_code,
    education_level, degree_code, school_code, school_name, school_province_code, school_city_code, school_county_code,
    major_code, major, major_category, graduation_date,
    employment_direction, phone, residence_province_code, residence_city_code, residence_county_code, employment_status, current_job, employment_company,
    entrepreneurship_status, entrepreneurship_project, entrepreneurship_demand, remarks, create_by, update_by, deleted
)
select 'ENTREPRENEUR', '王海霞', '女', '1998-05-12', '土族', '群众', '630000', '630200', '630202',
       'ZK', 'WXW', 'QHZY001', '青海职业技术大学', '630000', '630200', null, '120801', '电子商务', '管理学', '2020-06-30',
       '创业', '13900000002', '630000', '630200', '630202', '创业中', '个体经营', '海东市青年创业服务中心',
       '是', '助农电商', '资金支持, 创业培训', '示例创业青年', 1, 1, 0
where not exists (select 1 from youth_info where name = '王海霞' and phone = '13900000002');

insert into enterprise_info (
    id, enterprise_name, industry, enterprise_nature, enterprise_scale, region_province_code, region_city_code, region_county_code,
    address, contact_person, contact_phone, description, status, create_by, update_by, deleted
)
select 1, '无锡华阳科技有限公司', '智能硬件', '民营企业', '500人以上', '630000', '630200', '630202',
       '海东市乐都区工业园区', '陈先生', '13810000001', '示例企业信息，用于招聘岗位关联。', 1, 1, 1, 0
where not exists (select 1 from enterprise_info where id = 1);

insert into job_post (
    enterprise_id, job_name, job_category, education_requirement, experience_requirement, salary_range,
    recruit_count, work_province_code, work_city_code, work_county_code, contact_person, contact_phone, job_description,
    publish_time, status, create_by, update_by, deleted
)
select 1, 'Java开发工程师', '研发', 'BK', '1-3年', '8000-12000元',
       3, '630000', '630200', '630202', '陈先生', '13810000001', '负责后台管理系统开发与维护。',
       current_timestamp, 1, 1, 1, 0
where not exists (select 1 from job_post where job_name = 'Java开发工程师' and enterprise_id = 1);

insert into job_post (
    enterprise_id, job_name, job_category, education_requirement, experience_requirement, salary_range,
    recruit_count, work_province_code, work_city_code, work_county_code, contact_person, contact_phone, job_description,
    publish_time, status, create_by, update_by, deleted
)
select 1, '海东青年储备岗', '储备', null, '不限', '5000-8000元',
       12, '630000', '630200', '630202', '陈先生', '13810000001', '面向多专业、多学历在校大学生的综合储备岗位，用于演示岗位匹配学生列表。',
       current_timestamp, 1, 1, 1, 0
where not exists (select 1 from job_post where job_name = '海东青年储备岗' and enterprise_id = 1);

insert into job_post_education_rel (job_post_id, education_code, create_by)
select j.id, 'BK', 1
from job_post j
where j.job_name = 'Java开发工程师'
  and j.enterprise_id = 1
  and not exists (
      select 1 from job_post_education_rel rel
      where rel.job_post_id = j.id
        and rel.education_code = 'BK'
  );

insert into job_post_major_rel (job_post_id, major_code, create_by)
select j.id, '080901', 1
from job_post j
where j.job_name = 'Java开发工程师'
  and j.enterprise_id = 1
  and not exists (
      select 1 from job_post_major_rel rel
      where rel.job_post_id = j.id
        and rel.major_code = '080901'
  );

insert into job_post_school_category_rel (job_post_id, category_dict_item_id, create_by)
select j.id, d.id, 1
from job_post j
join sys_dict_item d on d.dict_type = 'school_category' and d.dict_value = '双一流' and d.deleted = 0
where j.job_name = 'Java开发工程师'
  and j.enterprise_id = 1
  and not exists (
      select 1 from job_post_school_category_rel rel
      where rel.job_post_id = j.id
        and rel.category_dict_item_id = d.id
  );

insert into job_post_school_tag_rel (job_post_id, tag_id, create_by)
select j.id, t.id, 1
from job_post j
join sys_school_tag t on t.tag_name = '双一流' and t.deleted = 0
where j.job_name = 'Java开发工程师'
  and j.enterprise_id = 1
  and not exists (
      select 1 from job_post_school_tag_rel rel
      where rel.job_post_id = j.id
        and rel.tag_id = t.id
  );

insert into policy_article (
    title, issuing_organization, policy_source, summary, content_html, publish_time, status, create_by, update_by, deleted
)
select '海东市急需紧缺人才引进管理办法（试行）', '海东市组织部', '海东市人民政府',
       '示例政策摘要，用于后台首页和政策管理展示。',
       '<p>这是一个示例政策正文，可在富文本编辑器中继续维护。</p>',
       current_timestamp, 1, 1, 1, 0
where not exists (select 1 from policy_article where title = '海东市急需紧缺人才引进管理办法（试行）');
