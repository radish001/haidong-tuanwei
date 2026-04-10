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
