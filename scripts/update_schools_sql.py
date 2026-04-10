#!/usr/bin/env python3
"""
转换 data_schools.sql 格式：
从: (school_code, school_name, school_type, province_code, city_code, category, create_by, update_by, deleted)
到: (school_code, school_name, category_dict_item_id, create_by, update_by, deleted)

category 映射:
- 双一流建设 -> 84
- 本科 -> 85
- 专科 -> 86
"""

import re

# category 到 dict_item_id 的映射
CATEGORY_MAP = {
    '双一流建设': '84',
    '本科': '85',
    '专科': '86'
}

def convert_line(line):
    """转换单行数据"""
    # 匹配形如: ('4111010001', '北京大学', '本科', '110000', '110100', '双一流建设', 1, 1, 0)
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

        # 转换 category 为 category_dict_item_id
        category_id = CATEGORY_MAP.get(category, '85')  # 默认本科

        return f"('{school_code}', '{school_name}', {category_id}, {create_by}, {update_by}, {deleted})"

    return re.sub(pattern, replace_tuple, line)

def main():
    input_file = '/Users/huxiaodong/PycharmProjects/haidong-tuanwei/src/main/resources/data_schools.sql'
    output_file = '/Users/huxiaodong/PycharmProjects/haidong-tuanwei/src/main/resources/data_schools.sql'

    with open(input_file, 'r', encoding='utf-8') as f:
        content = f.read()

    # 替换 INSERT 语句的字段名
    content = content.replace(
        '(school_code, school_name, school_type, province_code, city_code, category, create_by, update_by, deleted)',
        '(school_code, school_name, category_dict_item_id, create_by, update_by, deleted)'
    )

    # 按行处理数据
    lines = content.split('\n')
    converted_lines = []

    for line in lines:
        if line.strip().startswith('(') and 'values' not in line:
            # 这是数据行，需要转换
            converted_lines.append(convert_line(line))
        else:
            converted_lines.append(line)

    # 写回文件
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write('\n'.join(converted_lines))

    print("转换完成！")

    # 验证结果
    with open(output_file, 'r', encoding='utf-8') as f:
        content = f.read()
        # 检查是否还有旧格式残留
        if 'school_type' in content and 'province_code' in content:
            print("警告: 文件中仍有旧字段名")
        else:
            print("✓ 字段名更新成功")

        # 统计
        count_84 = content.count(', 84,')
        count_85 = content.count(', 85,')
        count_86 = content.count(', 86,')
        print(f"✓ 双一流建设高校: {count_84} 所")
        print(f"✓ 本科高校: {count_85} 所")
        print(f"✓ 专科高校: {count_86} 所")
        print(f"✓ 总计: {count_84 + count_85 + count_86} 所")

if __name__ == '__main__':
    main()
