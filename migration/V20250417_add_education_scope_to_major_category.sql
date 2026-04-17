-- ============================================
-- 数据库迁移脚本：为专业类别添加所属学历层次字段
-- 执行时间：2025-04-17
-- 执行环境：线上数据库
-- ============================================

-- 1. 新增 education_scopes 字段到 sys_dict_item 表
-- 注意：如果字段已存在，需要先删除或跳过此步骤
ALTER TABLE sys_dict_item
    ADD COLUMN education_scopes VARCHAR(100) NULL COMMENT '所属学历层次（专科专业/本科专业/研究生专业，可多选）';

-- 2. 更新现有的14个专业类别，设置默认值为本科专业（UNDERGRADUATE）
-- 这14个专业类别对应本科14个学科门类
UPDATE sys_dict_item
SET education_scopes = 'UNDERGRADUATE'
WHERE dict_type = 'major_category'
  AND dict_value IN ('01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12', '13', '14');

-- 3. 验证更新结果（可选，执行后检查）
-- SELECT id, dict_label, dict_value, education_scopes
-- FROM sys_dict_item
-- WHERE dict_type = 'major_category'
-- ORDER BY sort_no;

INSERT INTO sys_dict_item (dict_type, dict_label, dict_value, sort_no, enabled, education_scopes) VALUES
('major_category', '农林牧渔', 'jc_01', 15, 1, 'JUNIOR_COLLEGE'),
('major_category', '资源环境', 'jc_02', 16, 1, 'JUNIOR_COLLEGE'),
('major_category', '能源动力与材料', 'jc_03', 17, 1, 'JUNIOR_COLLEGE'),
('major_category', '土木建筑', 'jc_04', 18, 1, 'JUNIOR_COLLEGE'),
('major_category', '水利', 'jc_05', 19, 1, 'JUNIOR_COLLEGE'),
('major_category', '装备制造', 'jc_06', 20, 1, 'JUNIOR_COLLEGE'),
('major_category', '生物与化工', 'jc_07', 21, 1, 'JUNIOR_COLLEGE'),
('major_category', '轻工纺织', 'jc_08', 22, 1, 'JUNIOR_COLLEGE'),
('major_category', '食品药品与粮食', 'jc_09', 23, 1, 'JUNIOR_COLLEGE'),
('major_category', '交通运输', 'jc_10', 24, 1, 'JUNIOR_COLLEGE'),
('major_category', '电子信息', 'jc_11', 25, 1, 'JUNIOR_COLLEGE'),
('major_category', '医药卫生', 'jc_12', 26, 1, 'JUNIOR_COLLEGE'),
('major_category', '财经商贸', 'jc_13', 27, 1, 'JUNIOR_COLLEGE'),
('major_category', '旅游', 'jc_14', 28, 1, 'JUNIOR_COLLEGE'),
('major_category', '文化艺术', 'jc_15', 29, 1, 'JUNIOR_COLLEGE'),
('major_category', '新闻传播', 'jc_16', 30, 1, 'JUNIOR_COLLEGE'),
('major_category', '教育与体育', 'jc_17', 31, 1, 'JUNIOR_COLLEGE'),
('major_category', '公安与司法', 'jc_18', 32, 1, 'JUNIOR_COLLEGE'),
('major_category', '公共管理与服务', 'jc_19', 33, 1, 'JUNIOR_COLLEGE');