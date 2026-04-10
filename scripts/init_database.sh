#!/bin/bash
# ============================================
# 数据库初始化脚本
# 使用 schema.sql 创建表结构，然后导入所有数据
# ============================================

DB_HOST="${DB_HOST:-127.0.0.1}"
DB_PORT="${DB_PORT:-3306}"
DB_USER="${DB_USER:-root}"
DB_PASS="${DB_PASS:-root}"
DB_NAME="${DB_NAME:-haidong_tuanwei}"

RESOURCES_DIR="/Users/huxiaodong/PycharmProjects/haidong-tuanwei/src/main/resources"

echo "=========================================="
echo "数据库初始化工具"
echo "=========================================="
echo "主机: $DB_HOST:$DB_PORT"
echo "数据库: $DB_NAME"
echo "用户: $DB_USER"
echo ""

# 测试连接
echo "测试数据库连接..."
mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASS -e "SELECT 1" > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "❌ 连接失败，请检查用户名、密码和数据库服务"
    exit 1
fi
echo "✓ 连接成功"
echo ""

# 创建数据库（如果不存在）
echo "创建数据库（如果不存在）..."
mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASS -e "CREATE DATABASE IF NOT EXISTS $DB_NAME CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
echo "✓ 数据库准备完成"
echo ""

# 第1步：执行 schema.sql 创建表结构
echo "=========================================="
echo "第1步：创建表结构 (schema.sql)"
echo "=========================================="
mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASS $DB_NAME < "$RESOURCES_DIR/schema.sql"
if [ $? -eq 0 ]; then
    echo "✓ 表结构创建成功"
else
    echo "❌ 表结构创建失败"
    exit 1
fi
echo ""

# 第2步：导入核心数据（有依赖顺序）
echo "=========================================="
echo "第2步：导入核心数据"
echo "=========================================="

# 2.1 核心系统数据（用户、角色、菜单、基础字典）
echo "→ 导入核心系统数据 (data_core.sql)..."
mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASS $DB_NAME < "$RESOURCES_DIR/data_core.sql"
if [ $? -eq 0 ]; then
    echo "✓ data_core.sql 导入成功"
else
    echo "❌ data_core.sql 导入失败"
    exit 1
fi

# 2.2 学历和学位字典
echo "→ 导入学历学位数据 (data_dict.sql)..."
mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASS $DB_NAME < "$RESOURCES_DIR/data_dict.sql"
if [ $? -eq 0 ]; then
    echo "✓ data_dict.sql 导入成功"
else
    echo "❌ data_dict.sql 导入失败"
    exit 1
fi

# 2.3 行政区划数据
echo "→ 导入行政区划数据 (data_regions.sql)..."
mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASS $DB_NAME < "$RESOURCES_DIR/data_regions.sql"
if [ $? -eq 0 ]; then
    echo "✓ data_regions.sql 导入成功"
else
    echo "❌ data_regions.sql 导入失败"
    exit 1
fi

# 2.4 学校数据（依赖字典表）
echo "→ 导入学校数据 (data_schools.sql)..."
mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASS $DB_NAME < "$RESOURCES_DIR/data_schools.sql"
if [ $? -eq 0 ]; then
    echo "✓ data_schools.sql 导入成功"
else
    echo "❌ data_schools.sql 导入失败"
    exit 1
fi

echo ""
echo "=========================================="
echo "初始化完成！"
echo "=========================================="

# 显示统计信息
echo ""
echo "数据导入统计："
mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASS $DB_NAME -e "
SELECT 'sys_user' as table_name, count(*) as count FROM sys_user UNION ALL
SELECT 'sys_role', count(*) FROM sys_role UNION ALL
SELECT 'sys_menu', count(*) FROM sys_menu UNION ALL
SELECT 'sys_dict_item', count(*) FROM sys_dict_item UNION ALL
SELECT 'sys_region', count(*) FROM sys_region UNION ALL
SELECT 'sys_school', count(*) FROM sys_school;
" 2>/dev/null

echo ""
echo "可以使用以下命令登录数据库查看："
echo "mysql -u $DB_USER -p$DB_PASS -h $DB_HOST -P $DB_PORT $DB_NAME"
