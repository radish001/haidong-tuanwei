#!/usr/bin/env python3
"""
更新 data.sql 中的 sys_school 部分，与 schema.sql 一致
"""

import re

CATEGORY_MAP = {
    '双一流建设': '84',
    '本科': '85',
    '专科': '86'
}

def convert_school_data(content):
    """转换 data.sql 中的 sys_school 部分"""

    # 替换 INSERT 字段名
    content = content.replace(
        'insert into sys_school (school_code, school_name, school_type, province_code, city_code, category, create_by, update_by, deleted) values',
        'insert into sys_school (school_code, school_name, category_dict_item_id, create_by, update_by, deleted) values'
    )

    # 匹配学校数据行并转换
    # 格式: ('4111010001', '北京大学', '本科', '110000', '110100', '双一流建设', 1, 1, 0)
    pattern = r"\('([^']+)',\s*'([^']+)',\s*'([^']+)',\s*'([^']+)',\s*'([^']+)',\s*'([^']+)',\s*(\d+),\s*(\d+),\s*(\d+)\)"

    def replace_tuple(match):
        school_code = match.group(1)
        school_name = match.group(2)
        # school_type = match.group(3)  # 丢弃
        # province_code = match.group(4)  # 丢弃
        # city_code = match.group(5)  # 丢弃
        category = match.group(6)
        create_by = match.group(7)
        update_by = match.group(8)
        deleted = match.group(9)

        category_id = CATEGORY_MAP.get(category, '85')
        return f"('{school_code}', '{school_name}', {category_id}, {create_by}, {update_by}, {deleted})"

    return re.sub(pattern, replace_tuple, content)

def main():
    input_file = '/Users/huxiaodong/PycharmProjects/haidong-tuanwei/src/main/resources/data.sql'

    with open(input_file, 'r', encoding='utf-8') as f:
        content = f.read()

    # 转换内容
    converted_content = convert_school_data(content)

    # 写回文件
    with open(input_file, 'w', encoding='utf-8') as f:
        f.write(converted_content)

    print("data.sql 转换完成！")

    # 验证
    with open(input_file, 'r', encoding='utf-8') as f:
        content = f.read()

    if 'school_type' in content:
        print("❌ 警告: 仍有旧字段名 'school_type'")
    else:
        print("✓ 字段名已更新")

    count_84 = content.count(', 84,')
    count_85 = content.count(', 85,')
    count_86 = content.count(', 86,')
    print(f"✓ 双一流建设: {count_84} 所")
    print(f"✓ 本科: {count_85} 所")
    print(f"✓ 专科: {count_86} 所")

if __name__ == '__main__':
    main()
