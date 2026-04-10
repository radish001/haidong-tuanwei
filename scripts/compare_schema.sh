#!/bin/bash
# 对比 schema.sql 和实际数据库结构

DB_USER="${1:-root}"
DB_PASS="${2:-root}"
DB_NAME="${3:-haidong_tuanwei}"

echo "=========================================="
echo "数据库 Schema 对比工具"
echo "=========================================="
echo "数据库: $DB_NAME"
echo "用户: $DB_USER"
echo ""

# 获取实际数据库结构
echo "正在获取数据库表结构..."
mysql -u $DB_USER -p$DB_PASS -e "
SELECT 
    table_name as '表名',
    count(*) as '字段数'
FROM information_schema.columns
WHERE table_schema = '$DB_NAME'
GROUP BY table_name
ORDER BY table_name;
" 2>/dev/null

if [ $? -ne 0 ]; then
    echo "❌ 连接失败，请检查用户名和密码"
    echo "用法: ./compare_schema.sh [用户名] [密码] [数据库名]"
    exit 1
fi

echo ""
echo "=========================================="
echo "详细字段对比"
echo "=========================================="

# 获取每个表的详细字段
mysql -u $DB_USER -p$DB_PASS -e "
SELECT 
    table_name as '表名',
    column_name as '字段名',
    data_type as '类型',
    character_maximum_length as '长度',
    is_nullable as '可空',
    column_default as '默认值',
    extra as '额外信息'
FROM information_schema.columns
WHERE table_schema = '$DB_NAME'
ORDER BY table_name, ordinal_position;
" 2>/dev/null

echo ""
echo "=========================================="
echo "索引对比"
echo "=========================================="

mysql -u $DB_USER -p$DB_PASS -e "
SELECT 
    table_name as '表名',
    index_name as '索引名',
    column_name as '字段',
    non_unique as '非唯一'
FROM information_schema.statistics
WHERE table_schema = '$DB_NAME'
  and index_name != 'PRIMARY'
ORDER BY table_name, index_name, seq_in_index;
" 2>/dev/null

echo ""
echo "=========================================="
echo "schema.sql 中定义的表"
echo "=========================================="
grep -E "^create table if not exists" /Users/huxiaodong/PycharmProjects/haidong-tuanwei/src/main/resources/schema.sql | sed 's/create table if not exists /  /'

echo ""
echo "=========================================="
echo "对比完成"
echo "=========================================="
