-- ============================================
-- 清空所有数据并重新初始化
-- 执行顺序：先清空，再插入
-- ============================================

-- 关闭外键检查（MySQL）
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- 清空所有业务表数据
-- ============================================

-- 青年信息相关表
TRUNCATE TABLE youth_info;
TRUNCATE TABLE youth_contact;
TRUNCATE TABLE youth_education;
TRUNCATE TABLE youth_employment;
TRUNCATE TABLE youth_job_intention;

-- 企业招聘相关表
TRUNCATE TABLE enterprise;
TRUNCATE TABLE job_recruitment;
TRUNCATE TABLE job_application;
TRUNCATE TABLE job_match_result;

-- 政策相关表
TRUNCATE TABLE policy;

-- 系统相关表（保留菜单和角色，清空其他）
TRUNCATE TABLE sys_user_role;
TRUNCATE TABLE sys_role_menu;
TRUNCATE TABLE sys_operation_log;

-- 学校标签相关（已不使用）
-- TRUNCATE TABLE sys_school_tag_rel;
-- TRUNCATE TABLE sys_school_tag;

-- ============================================
-- 清空基础数据表（会被SQL文件重新填充）
-- ============================================

TRUNCATE TABLE sys_user;
TRUNCATE TABLE sys_role;
TRUNCATE TABLE sys_menu;
TRUNCATE TABLE sys_region;
TRUNCATE TABLE sys_school;
TRUNCATE TABLE sys_dict_item;
TRUNCATE TABLE sys_major_catalog;

-- 开启外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- 执行顺序说明：
-- 1. 先执行 data_core.sql - 核心数据（用户、角色、菜单、字典、学校类别、学科门类）
-- 2. 再执行 data_dict.sql - 字典数据（学历、学位）
-- 3. 再执行 data_regions.sql - 行政区划数据
-- 4. 再执行 data_schools.sql - 2919所高校数据
-- 5. 最后执行 data_majors.sql - 698个本科专业数据
-- ============================================
