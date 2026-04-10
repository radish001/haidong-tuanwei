#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
解析全国高校CSV文件并生成SQL
"""

import csv
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

# 城市代码映射
CITY_CODES = {
    '北京市': '110100',
    '天津市': '120100',
    '石家庄市': '130100',
    '太原市': '140100',
    '呼和浩特市': '150100',
    '包头市': '150200',
    '沈阳市': '210100',
    '大连市': '210200',
    '长春市': '220100',
    '吉林市': '220200',
    '哈尔滨市': '230100',
    '上海市': '310100',
    '南京市': '320100',
    '苏州市': '320500',
    '无锡市': '320200',
    '徐州市': '320300',
    '常州市': '320400',
    '南通市': '320600',
    '扬州市': '321000',
    '杭州市': '330100',
    '宁波市': '330200',
    '温州市': '330300',
    '绍兴市': '330600',
    '金华市': '330700',
    '嘉兴市': '330400',
    '台州市': '331000',
    '湖州市': '330500',
    '舟山市': '330900',
    '衢州市': '330800',
    '丽水市': '331100',
    '合肥市': '340100',
    '芜湖市': '340200',
    '蚌埠市': '340300',
    '福州市': '350100',
    '厦门市': '350200',
    '泉州市': '350500',
    '漳州市': '350600',
    '南昌市': '360100',
    '赣州市': '360700',
    '济南市': '370100',
    '青岛市': '370200',
    '烟台市': '370600',
    '潍坊市': '370700',
    '郑州市': '410100',
    '洛阳市': '410300',
    '武汉市': '420100',
    '宜昌市': '420500',
    '襄阳市': '420600',
    '长沙市': '430100',
    '株洲市': '430200',
    '湘潭市': '430300',
    '衡阳市': '430400',
    '广州市': '440100',
    '深圳市': '440300',
    '珠海市': '440400',
    '汕头市': '440500',
    '佛山市': '440600',
    '东莞市': '441900',
    '中山市': '442000',
    '江门市': '440700',
    '湛江市': '440800',
    '南宁市': '450100',
    '桂林市': '450300',
    '柳州市': '450200',
    '海口市': '460100',
    '三亚市': '460200',
    '重庆市': '500100',
    '成都市': '510100',
    '绵阳市': '510700',
    '德阳市': '510600',
    '泸州市': '510500',
    '贵阳市': '520100',
    '遵义市': '520300',
    '昆明市': '530100',
    '曲靖市': '530300',
    '拉萨市': '540100',
    '西安市': '610100',
    '咸阳市': '610400',
    '宝鸡市': '610300',
    '兰州市': '620100',
    '西宁市': '630100',
    '海东市': '630200',
    '银川市': '640100',
    '乌鲁木齐市': '650100',
    '昌吉市': '652300',
    '石河子市': '659001',
    '喀什市': '653100',
    '伊犁哈萨克自治州': '654000',
    '海西蒙古族藏族自治州': '632800',
}

# 双一流高校名单
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
    '海军军医大学',
    '南京大学', '苏州大学', '东南大学', '南京航空航天大学',
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

def get_city_code(province, city):
    """获取城市代码"""
    if city in CITY_CODES:
        return CITY_CODES[city]
    # 如果找不到，使用省会代码
    if province in PROVINCE_CODES:
        return PROVINCE_CODES[province][:4] + '00'
    return '000000'

def parse_csv():
    """解析CSV文件"""
    schools = []
    current_province = None
    
    with open('/Users/huxiaodong/.cursor/projects/Users-huxiaodong-PycharmProjects-haidong-tuanwei/agent-tools/d1b58b33-d2f2-44a2-9e8d-03268a6a003c.txt', 'r', encoding='utf-8') as f:
        reader = csv.reader(f)
        for row in reader:
            # 检查是否是省份标题行
            if len(row) >= 1 and row[0].endswith('（') and '所）' in str(row):
                # 提取省份名称
                match = re.match(r'(.+?)（\d+所）', row[0])
                if match:
                    current_province = match.group(1)
                continue
            
            # 检查是否是学校数据行
            if len(row) >= 6 and row[0].isdigit():
                school_code = row[2]  # 学校标识码
                school_name = row[1]  # 学校名称
                province = row[3] if len(row) > 3 and row[3] else current_province  # 主管部门/省份
                city = row[4] if len(row) > 4 else province  # 所在地
                school_type = row[5] if len(row) > 5 else '本科'  # 办学层次
                remark = row[6] if len(row) > 6 else ''  # 备注
                
                # 清理数据
                if not school_code or not school_name:
                    continue
                
                # 确定省份代码
                prov_code = PROVINCE_CODES.get(province, '000000')
                city_code = get_city_code(province, city)
                
                # 确定类别
                if school_name in DOUBLE_FIRST_CLASS:
                    category = '双一流建设'
                elif '民办' in remark:
                    category = '本科' if school_type == '本科' else '专科'
                elif school_type == '专科':
                    category = '专科'
                else:
                    category = '本科'
                
                schools.append({
                    'code': school_code,
                    'name': school_name,
                    'type': school_type,
                    'province_code': prov_code,
                    'city_code': city_code,
                    'category': category
                })
    
    return schools

def generate_sql(schools):
    """生成SQL文件"""
    # 按省份分组
    by_province = {}
    for school in schools:
        prov = school['province_code']
        if prov not in by_province:
            by_province[prov] = []
        by_province[prov].append(school)
    
    # 省份名称映射
    prov_names = {v: k for k, v in PROVINCE_CODES.items()}
    
    sql_lines = [
        "-- ============================================",
        "-- 全国高校数据 - 普通高等学校",
        "-- 数据来源：教育部全国普通高等学校名单",
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
    
    for prov_code in sorted(by_province.keys()):
        schools_in_prov = by_province[prov_code]
        prov_name = prov_names.get(prov_code, '未知')
        
        sql_lines.append("-- {}（{}所）".format(prov_name, len(schools_in_prov)))
        
        for school in schools_in_prov:
            values = "('{}', '{}', '{}', '{}', '{}', '{}', 1, 1, 0)".format(
                school['code'],
                school['name'],
                school['type'],
                school['province_code'],
                school['city_code'],
                school['category']
            )
            all_values.append(values)
    
    # 合并所有值，每行10个
    for i in range(0, len(all_values), 10):
        batch = all_values[i:i+10]
        if i + 10 >= len(all_values):
            sql_lines.append(','.join(batch) + ';')
        else:
            sql_lines.append(','.join(batch) + ',')
    
    return '\n'.join(sql_lines)

if __name__ == '__main__':
    schools = parse_csv()
    print(f"解析到 {len(schools)} 所学校")
    
    sql_content = generate_sql(schools)
    output_file = '/Users/huxiaodong/PycharmProjects/haidong-tuanwei/src/main/resources/data_schools.sql'
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write(sql_content)
    print(f"SQL文件已生成: {output_file}")
    print(f"文件大小: {len(sql_content)} 字符")
