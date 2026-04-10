#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
生成全国高校SQL数据文件
数据来源：第三方API（2901所高校）
"""

import json
import re

# 省份代码映射
PROVINCE_CODES = {
    '北京市': '110000',
    '天津市': '120000',
    '河北省': '130000',
    '山西省': '140000',
    '内蒙古自治区': '150000',
    '辽宁省': '210000',
    '吉林省': '220000',
    '黑龙江省': '230000',
    '上海市': '310000',
    '江苏省': '320000',
    '浙江省': '330000',
    '安徽省': '340000',
    '福建省': '350000',
    '江西省': '360000',
    '山东省': '370000',
    '河南省': '410000',
    '湖北省': '420000',
    '湖南省': '430000',
    '广东省': '440000',
    '广西壮族自治区': '450000',
    '海南省': '460000',
    '重庆市': '500000',
    '四川省': '510000',
    '贵州省': '520000',
    '云南省': '530000',
    '西藏自治区': '540000',
    '陕西省': '610000',
    '甘肃省': '620000',
    '青海省': '630000',
    '宁夏回族自治区': '640000',
    '新疆维吾尔自治区': '650000',
}

# 城市代码映射（主要城市）
CITY_CODES = {
    '北京市': '110100',
    '天津市': '120100',
    '石家庄市': '130100',
    '太原市': '140100',
    '呼和浩特市': '150100',
    '沈阳市': '210100',
    '大连市': '210200',
    '长春市': '220100',
    '哈尔滨市': '230100',
    '上海市': '310100',
    '南京市': '320100',
    '苏州市': '320500',
    '无锡市': '320200',
    '杭州市': '330100',
    '宁波市': '330200',
    '合肥市': '340100',
    '福州市': '350100',
    '厦门市': '350200',
    '南昌市': '360100',
    '济南市': '370100',
    '青岛市': '370200',
    '郑州市': '410100',
    '武汉市': '420100',
    '长沙市': '430100',
    '广州市': '440100',
    '深圳市': '440300',
    '南宁市': '450100',
    '海口市': '460100',
    '重庆市': '500100',
    '成都市': '510100',
    '贵阳市': '520100',
    '昆明市': '530100',
    '拉萨市': '540100',
    '西安市': '610100',
    '兰州市': '620100',
    '西宁市': '630100',
    '银川市': '640100',
    '乌鲁木齐市': '650100',
    '海东市': '630200',
    '海西蒙古族藏族自治州': '632800',
}

# 双一流高校名单（147所）- 用于标记
DOUBLE_FIRST_CLASS = {
    '北京大学', '中国人民大学', '清华大学', '北京交通大学', '北京工业大学',
    '北京航空航天大学', '北京理工大学', '北京科技大学', '北京化工大学',
    '北京邮电大学', '中国农业大学', '北京林业大学', '北京协和医学院',
    '北京中医药大学', '北京师范大学', '首都师范大学', '北京外国语大学',
    '中国传媒大学', '中央财经大学', '对外经济贸易大学', '外交学院',
    '中国人民公安大学', '北京体育大学', '中央音乐学院', '中国音乐学院',
    '中央美术学院', '中央戏剧学院', '中央民族大学', '中国政法大学',
    '华北电力大学', '中国矿业大学（北京）', '中国石油大学（北京）',
    '中国地质大学（北京）', '中国科学院大学',
    '南开大学', '天津大学', '天津工业大学', '天津医科大学', '天津中医药大学',
    '河北工业大学',
    '山西大学', '太原理工大学',
    '内蒙古大学',
    '大连理工大学', '东北大学', '大连海事大学',
    '吉林大学', '延边大学', '东北师范大学',
    '哈尔滨工业大学', '哈尔滨工程大学', '东北农业大学', '东北林业大学',
    '复旦大学', '同济大学', '上海交通大学', '华东理工大学', '东华大学',
    '上海海洋大学', '上海中医药大学', '华东师范大学', '上海外国语大学',
    '上海财经大学', '上海体育学院', '上海音乐学院', '上海大学',
    '海军军医大学', '南京大学', '苏州大学', '东南大学', '南京航空航天大学',
    '南京理工大学', '中国矿业大学', '南京邮电大学', '河海大学', '江南大学',
    '南京林业大学', '南京信息工程大学', '南京农业大学', '南京中医药大学',
    '中国药科大学', '南京师范大学',
    '浙江大学', '中国美术学院', '宁波大学',
    '安徽大学', '中国科学技术大学', '合肥工业大学',
    '厦门大学', '福州大学',
    '南昌大学',
    '山东大学', '中国海洋大学', '中国石油大学（华东）',
    '郑州大学', '河南大学',
    '武汉大学', '华中科技大学', '中国地质大学（武汉）', '武汉理工大学',
    '华中农业大学', '华中师范大学', '中南财经政法大学',
    '湘潭大学', '湖南大学', '中南大学', '湖南师范大学',
    '中山大学', '暨南大学', '华南理工大学', '华南农业大学', '广州医科大学',
    '广州中医药大学', '华南师范大学', '南方科技大学', '海南大学',
    '广西大学',
    '重庆大学', '西南大学',
    '四川大学', '西南交通大学', '电子科技大学', '西南石油大学', '成都理工大学',
    '四川农业大学', '成都中医药大学', '西南财经大学',
    '贵州大学',
    '云南大学',
    '西藏大学',
    '西北大学', '西安交通大学', '西北工业大学', '西安电子科技大学',
    '长安大学', '西北农林科技大学', '陕西师范大学', '空军军医大学',
    '兰州大学',
    '青海大学',
    '宁夏大学',
    '新疆大学', '石河子大学',
    '国防科技大学'
}

def get_school_category(name):
    """获取学校类别"""
    if name in DOUBLE_FIRST_CLASS:
        return '双一流建设'
    return '本科'  # 默认为本科，后面再调整

def generate_sql():
    """生成SQL文件"""
    # 读取JSON数据
    with open('/Users/huxiaodong/.cursor/projects/Users-huxiaodong-PycharmProjects-haidong-tuanwei/agent-tools/f542930c-71a3-41a4-a1b3-1be3759bc527.txt', 'r', encoding='utf-8') as f:
        data = json.load(f)
    
    schools = data['data']
    
    # 过滤掉港澳台
    excluded_provinces = {'台湾省', '香港特别行政区', '澳门特别行政区'}
    schools = [s for s in schools if s['province'] not in excluded_provinces]
    
    # 统计信息
    print(f"总学校数（不含港澳台）: {len(schools)}")
    
    # 按省份分组
    by_province = {}
    for school in schools:
        prov = school['province']
        if prov not in by_province:
            by_province[prov] = []
        by_province[prov].append(school)
    
    # 生成SQL
    sql_lines = [
        "-- ============================================",
        "-- 全国高校数据 - 普通高等学校（不含港澳台）",
        "-- 数据来源：第三方API聚合数据",
        "-- 学校数量：{}所".format(len(schools)),
        "-- 生成时间：2025年",
        "-- ============================================",
        "",
        "truncate table sys_school;",
        "",
        "insert into sys_school (school_code, school_name, school_type, province_code, city_code, category, create_by, update_by, deleted) values",
        ""
    ]
    
    all_values = []
    code_counter = 10001
    
    for province in sorted(by_province.keys()):
        schools_in_prov = by_province[province]
        prov_code = PROVINCE_CODES.get(province, '000000')
        
        sql_lines.append("-- {}（{}所）".format(province, len(schools_in_prov)))
        
        for school in schools_in_prov:
            name = school['name']
            city = school['city']
            
            # 获取城市代码
            city_code = CITY_CODES.get(city, prov_code[:4] + '00')
            
            # 获取类别（双一流或其他）
            category = get_school_category(name)
            
            # 生成学校代码
            school_code = str(code_counter)
            code_counter += 1
            
            # 默认学校类型（这里简化处理，实际需要更多逻辑）
            school_type = '本科' if category == '双一流建设' else '本科'
            
            # 特殊处理：某些学校已知是专科
            if '职业' in name and '大学' not in name and '学院' not in name:
                school_type = '专科'
                category = '专科'
            elif '职业技术' in name:
                school_type = '专科'
                category = '专科'
            elif '高等专科' in name:
                school_type = '专科'
                category = '专科'
            
            values = "('{}', '{}', '{}', '{}', '{}', '{}', 1, 1, 0)".format(
                school_code, name, school_type, prov_code, city_code, category
            )
            all_values.append(values)
    
    # 合并所有值，每行10个
    for i in range(0, len(all_values), 10):
        batch = all_values[i:i+10]
        if i + 10 >= len(all_values):
            # 最后一个 batch，以分号结尾
            sql_lines.append(','.join(batch) + ';')
        else:
            sql_lines.append(','.join(batch) + ',')
    
    return '\n'.join(sql_lines)

if __name__ == '__main__':
    sql_content = generate_sql()
    output_file = '/Users/huxiaodong/PycharmProjects/haidong-tuanwei/src/main/resources/data_schools.sql'
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write(sql_content)
    print(f"SQL文件已生成: {output_file}")
    print(f"文件大小: {len(sql_content)} 字符")
