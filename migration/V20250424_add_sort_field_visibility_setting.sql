-- ============================================
-- 数据库迁移脚本：新增排序字段显示配置表
-- 执行时间：2025-04-24
-- 执行环境：线上数据库
-- ============================================

CREATE TABLE IF NOT EXISTS sys_display_settings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sort_field_visible TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否显示业务页面中的排序字段，1=显示，0=隐藏',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO sys_display_settings (sort_field_visible)
SELECT 1
WHERE NOT EXISTS (
    SELECT 1 FROM sys_display_settings
);
