#!/usr/bin/env python3
"""
Generate test data SQL for youth_info, enterprise_info, and job_post tables.
Reads real dictionary/school/major/region data from MySQL, then produces random but realistic records.

Usage:
    pip install mysql-connector-python
    python scripts/generate_test_data.py
"""

import random
import os
import mysql.connector
from datetime import date, timedelta

DB_CONFIG = {
    "host": os.environ.get("MYSQL_HOST", "localhost"),
    "port": int(os.environ.get("MYSQL_PORT", "3306")),
    "user": os.environ.get("MYSQL_USERNAME", "root"),
    "password": os.environ.get("MYSQL_PASSWORD", "root"),
    "database": os.environ.get("MYSQL_DATABASE", "haidong_tuanwei"),
}

YOUTH_COUNT = 100_000
ENTERPRISE_COUNT = 10_000
JOB_COUNT = 100_000
BATCH_SIZE = 2000

SURNAMES = list("赵钱孙李周吴郑王冯陈褚卫蒋沈韩杨朱秦尤许何吕施张孔曹严华金魏陶姜戚谢邹喻柏水窦章苏潘葛奚范彭郎鲁韦昌马苗凤花方任袁柳唐罗薛雷贺倪汤滕殷罗毕郝邬安常乐于时傅卞齐康伍余元卜顾孟黄穆萧尹姚邵湛汪祁毛禹狄米贝明臧计伏成戴宋茅庞熊纪舒屈项祝董梁杜阮蓝闵席季麻强贾路娄危江童颜郭梅盛林刁钟徐邱骆高夏蔡田樊胡凌霍万柯卢莫房缪干解应宗丁宣贲邓郁单杭洪包诸左石崔吉钮龚程嵇邢滑裴陆荣翁荀羊甄家封芮储靳邴松井段富巫乌焦巴弓牧隗山谷车侯伊宁仇祖武符刘景詹束龙叶幸司韶郜黎蓟溥印宿白怀蒲邰从鄂索咸赖卓蔺屠蒙池乔阳郁胥能苍双闻莘翟谭贡劳逄姬申扶堵冉宰郦雍郤璩桑桂濮牛寿通边扈燕冀僪浦尚农温别庄晏柴翟阎充慕连茹习艾鱼容向古易慎戈廖庾暨居衡步都耿满弘国文东殴沃曾关红游盖益桓公")
MALE_NAMES = list("伟刚勇毅俊峰强军平保东文辉力明永健世广志义兴良海山仁波宁贵福生龙元全国胜学祥才发武新利清飞彬富顺信子杰涛昌成康星光天达安岩中茂进林有坚和彪博诚先敬震振壮会思群豪心邦承乐绍功松善厚庆磊民友裕河哲江超浩亮政谦亨奇固之轮翰朗伯宏言若鸣朋斌梁栋维启克伦翔旭鹏泽晨辰士以建家致树炎德行时泰盛雄琛钧冠策腾楠榕风航弘")
FEMALE_NAMES = list("秀娟英华慧巧美娜静淑惠珠翠雅芝玉萍红娥玲芬芳燕彩春菊兰凤洁梅琳素云莲真环雪荣爱妹霞香月莺媛艳瑞凡佳嘉琼勤珍贞莉桂娣叶璧璐娅琦晶妍茜秋珊莎锦黛青倩婷姣婉娴瑾颖露瑶怡婵雁蓓纨仪荷丹蓉眉君琴蕊薇菁梦岚苑婕馨瑗琰韵融园艺咏卿聪澜纯毓悦昭冰爽琬茗羽希宁欣飘育滢馥筠柔竹霭凝晓欢霄枫芸菲寒伊亚宜可姬舒影荔枝丽")
EMPLOYMENT_DIRS = ["信息技术", "金融", "教育", "医疗", "建筑", "制造", "农业", "服务业", "物流", "法律", "传媒", "电商", "新能源", "人工智能", "文化旅游", "公共管理", "环保", "生物科技", "自主创业", "考研深造"]
JOB_TITLES = ["软件工程师", "前端开发", "后端开发", "数据分析师", "产品经理", "UI设计师", "运营专员", "市场营销", "人事专员", "财务会计", "行政助理", "客服专员", "销售代表", "项目经理", "测试工程师", "运维工程师", "机械工程师", "电气工程师", "土木工程师", "建筑设计师", "护士", "药剂师", "教师", "律师助理", "内容编辑", "新媒体运营", "物流专员", "采购专员", "质量管理", "安全管理员"]
JOB_CATEGORIES = ["技术", "设计", "产品", "运营", "市场", "销售", "人力", "财务", "行政", "管理"]
ENT_PREFIXES = ["海东", "西宁", "青海", "互助", "平安", "民和", "乐都", "化隆", "循化", "德令哈", "格尔木", "玉树", "果洛", "海西", "海南", "海北", "黄南"]
ENT_MIDS = ["恒通", "鑫源", "天成", "华盛", "诚信", "万达", "嘉和", "博远", "正泰", "瑞丰", "宏业", "中联", "新世纪", "金桥", "银河", "星光", "长青", "汇丰", "泰安", "永兴", "利达", "富源", "祥云", "龙腾", "凯旋", "盛世", "润泽", "弘毅", "百川", "众合"]
ENT_SUFFIXES = ["科技有限公司", "贸易有限公司", "实业有限公司", "建筑工程有限公司", "文化传媒有限公司", "农牧业发展有限公司", "旅游开发有限公司", "电子商务有限公司", "信息技术有限公司", "新能源有限公司", "环保科技有限公司", "食品有限公司", "物流有限公司", "教育咨询有限公司", "生物科技有限公司"]


def load_lookup_data(cursor):
    """Load all dictionary and master data from DB."""
    data = {}

    cursor.execute("SELECT dict_value FROM sys_dict_item WHERE dict_type='gender' AND deleted=0 ORDER BY sort_no")
    data["genders"] = [r[0] for r in cursor.fetchall()]

    cursor.execute("SELECT dict_value FROM sys_dict_item WHERE dict_type='ethnicity' AND deleted=0 ORDER BY sort_no")
    data["ethnicities"] = [r[0] for r in cursor.fetchall()]

    cursor.execute("SELECT dict_value FROM sys_dict_item WHERE dict_type='political_status' AND deleted=0 ORDER BY sort_no")
    data["political_statuses"] = [r[0] for r in cursor.fetchall()]

    cursor.execute("SELECT dict_value FROM sys_dict_item WHERE dict_type='education_level' AND deleted=0 ORDER BY sort_no")
    data["education_levels"] = [r[0] for r in cursor.fetchall()]

    cursor.execute("SELECT dict_value FROM sys_dict_item WHERE dict_type='degree' AND deleted=0 ORDER BY sort_no")
    data["degrees"] = [r[0] for r in cursor.fetchall()]

    cursor.execute("SELECT dict_value FROM sys_dict_item WHERE dict_type='major_category' AND deleted=0 ORDER BY sort_no")
    data["major_categories"] = [r[0] for r in cursor.fetchall()]

    cursor.execute("SELECT dict_value FROM sys_dict_item WHERE dict_type='enterprise_scale' AND deleted=0 ORDER BY sort_no")
    data["enterprise_scales"] = [r[0] for r in cursor.fetchall()]

    cursor.execute("SELECT dict_value FROM sys_dict_item WHERE dict_type='enterprise_nature' AND deleted=0 ORDER BY sort_no")
    data["enterprise_natures"] = [r[0] for r in cursor.fetchall()]

    cursor.execute("SELECT dict_value FROM sys_dict_item WHERE dict_type='enterprise_industry' AND deleted=0 ORDER BY sort_no")
    data["enterprise_industries"] = [r[0] for r in cursor.fetchall()]

    cursor.execute("SELECT dict_value FROM sys_dict_item WHERE dict_type='experience_requirement' AND deleted=0 ORDER BY sort_no")
    data["experience_requirements"] = [r[0] for r in cursor.fetchall()]

    cursor.execute("SELECT dict_value FROM sys_dict_item WHERE dict_type='salary_range' AND deleted=0 ORDER BY sort_no")
    data["salary_ranges"] = [r[0] for r in cursor.fetchall()]

    cursor.execute("SELECT school_code, school_name FROM sys_school WHERE deleted=0 ORDER BY school_code")
    data["schools"] = cursor.fetchall()

    cursor.execute("""
        SELECT mc.major_code, mc.major_name, di.dict_value as category_value
        FROM sys_major_catalog mc
        JOIN sys_dict_item di ON di.id = mc.category_dict_item_id AND di.deleted=0
        WHERE mc.deleted=0
    """)
    data["majors"] = cursor.fetchall()

    # Build region hierarchy: province -> [cities] -> [counties]
    cursor.execute("SELECT id, region_code, region_name, region_level, parent_id FROM sys_region WHERE deleted=0 ORDER BY region_level, sort_no, id")
    regions = cursor.fetchall()
    provinces = []
    city_map = {}   # province_id -> [(city_id, city_code)]
    county_map = {} # city_id -> [(county_code,)]
    for rid, rcode, rname, rlevel, pid in regions:
        if rlevel == 1:
            provinces.append((rid, rcode))
        elif rlevel == 2:
            city_map.setdefault(pid, []).append((rid, rcode))
        elif rlevel == 3:
            county_map.setdefault(pid, []).append((rcode,))

    data["provinces"] = provinces
    data["city_map"] = city_map
    data["county_map"] = county_map

    # Haidong province id (青海省) and its cities
    cursor.execute("SELECT id FROM sys_region WHERE region_code='630000' AND deleted=0")
    row = cursor.fetchone()
    data["qinghai_id"] = row[0] if row else None

    cursor.execute("SELECT id FROM sys_region WHERE region_code='632600' AND deleted=0")
    row = cursor.fetchone()
    data["haidong_id"] = row[0] if row else None

    return data


def random_region(data):
    """Pick a random province/city/county chain."""
    if not data["provinces"]:
        return ("", "", "")
    prov_id, prov_code = random.choice(data["provinces"])
    cities = data["city_map"].get(prov_id, [])
    if not cities:
        return (prov_code, "", "")
    city_id, city_code = random.choice(cities)
    counties = data["county_map"].get(city_id, [])
    if not counties:
        return (prov_code, city_code, "")
    county_code = random.choice(counties)[0]
    return (prov_code, city_code, county_code)


def haidong_region(data):
    """Pick a random region biased toward Haidong (海东市)."""
    # 70% chance native is Haidong area
    if random.random() < 0.7 and data.get("qinghai_id") and data.get("haidong_id"):
        prov_code = "630000"
        city_code = "632600"
        counties = data["county_map"].get(data["haidong_id"], [])
        county_code = random.choice(counties)[0] if counties else ""
        return (prov_code, city_code, county_code)
    return random_region(data)


def random_name(gender_code):
    surname = random.choice(SURNAMES)
    if gender_code == "01":
        given = random.choice(MALE_NAMES)
        if random.random() < 0.5:
            given += random.choice(MALE_NAMES)
    else:
        given = random.choice(FEMALE_NAMES)
        if random.random() < 0.5:
            given += random.choice(FEMALE_NAMES)
    return surname + given


def random_phone():
    prefixes = ["130", "131", "132", "133", "134", "135", "136", "137", "138", "139",
                "150", "151", "152", "153", "155", "156", "157", "158", "159",
                "170", "176", "177", "178", "180", "181", "182", "183", "185", "186", "187", "188", "189"]
    return random.choice(prefixes) + "".join([str(random.randint(0, 9)) for _ in range(8)])


def escape(s):
    if s is None:
        return "NULL"
    return "'" + s.replace("\\", "\\\\").replace("'", "\\'") + "'"


def generate_youth(data, f):
    """Generate YOUTH_COUNT youth_info records."""
    edu_degree_map = {
        "04": ["000"],          # 专科 -> 无学位
        "05": ["000", "100"],   # 本科 -> 无学位 or 学士
        "06": ["200"],          # 硕士 -> 硕士学位
        "07": ["300"],          # 博士 -> 博士学位
    }
    # Weight ethnicities: Han dominant (70%), Tibetan/Hui/Tu/Salar elevated for Haidong
    eth_weights = []
    for ev in data["ethnicities"]:
        if ev == "01":     # 汉族
            eth_weights.append(50)
        elif ev == "04":   # 藏族
            eth_weights.append(15)
        elif ev == "03":   # 回族
            eth_weights.append(10)
        elif ev == "30":   # 土族
            eth_weights.append(8)
        elif ev == "35":   # 撒拉族
            eth_weights.append(6)
        elif ev == "02":   # 蒙古族
            eth_weights.append(3)
        else:
            eth_weights.append(1)

    edu_weights = [30, 45, 20, 5]  # 专科30%, 本科45%, 硕士20%, 博士5%
    pol_weights = [20, 65, 15]     # 群众20%, 团员65%, 党员15%
    gender_weights = [52, 48]      # 男52%, 女48%

    print(f"Generating {YOUTH_COUNT} youth records...")
    f.write("-- Youth info test data\n")
    f.write("-- Generated: DO NOT EDIT\n\n")

    batch = []
    for i in range(1, YOUTH_COUNT + 1):
        gender = random.choices(data["genders"], weights=gender_weights)[0]
        name = random_name(gender)
        birth = date(2000, 1, 1) + timedelta(days=random.randint(0, 2555))  # 2000~2006
        ethnicity = random.choices(data["ethnicities"], weights=eth_weights)[0]
        political = random.choices(data["political_statuses"], weights=pol_weights)[0]

        native_prov, native_city, native_county = haidong_region(data)

        edu = random.choices(data["education_levels"], weights=edu_weights)[0]
        degree = random.choice(edu_degree_map.get(edu, ["000"]))

        school_code, school_name = random.choice(data["schools"])
        school_prov, school_city, school_county = random_region(data)

        major_code, major_name, major_cat = random.choice(data["majors"])

        recruitment_year = random.choice([2021, 2022, 2023, 2024, 2025, 2026])
        grad_date = date(recruitment_year + random.randint(3, 4), 6, 30)
        emp_dir = random.choice(EMPLOYMENT_DIRS)
        phone = random_phone()

        res_prov, res_city, res_county = random_region(data)

        batch.append(
            f"('college',{escape(name)},{escape(gender)},'{birth}',{escape(ethnicity)},"
            f"{escape(political)},{escape(native_prov)},{escape(native_city)},{escape(native_county)},"
            f"{escape(edu)},{escape(degree)},{escape(school_code)},{escape(school_name)},"
            f"{escape(school_prov)},{escape(school_city)},{escape(school_county)},"
            f"{escape(major_code)},{escape(major_name)},{escape(major_cat)},"
            f"{recruitment_year},'{grad_date}',{escape(emp_dir)},{escape(phone)},"
            f"{escape(res_prov)},{escape(res_city)},{escape(res_county)},0)"
        )

        if len(batch) >= BATCH_SIZE:
            write_youth_batch(f, batch)
            batch = []
            if i % 20000 == 0:
                print(f"  youth: {i}/{YOUTH_COUNT}")

    if batch:
        write_youth_batch(f, batch)
    print(f"  youth: {YOUTH_COUNT}/{YOUTH_COUNT} done")


def write_youth_batch(f, batch):
    f.write("INSERT INTO youth_info (youth_type,name,gender,birth_date,ethnicity,"
            "political_status,native_province_code,native_city_code,native_county_code,"
            "education_level,degree_code,school_code,school_name,"
            "school_province_code,school_city_code,school_county_code,"
            "major_code,major,major_category,"
            "recruitment_year,graduation_date,employment_direction,phone,"
            "residence_province_code,residence_city_code,residence_county_code,deleted) VALUES\n")
    f.write(",\n".join(batch))
    f.write(";\n\n")


def generate_enterprises(data, f):
    """Generate ENTERPRISE_COUNT enterprise_info records."""
    print(f"Generating {ENTERPRISE_COUNT} enterprise records...")
    f.write("-- Enterprise info test data\n")
    f.write("-- Generated: DO NOT EDIT\n\n")

    batch = []
    for i in range(1, ENTERPRISE_COUNT + 1):
        name = random.choice(ENT_PREFIXES) + random.choice(ENT_MIDS) + random.choice(ENT_SUFFIXES)
        industry = random.choice(data["enterprise_industries"])
        nature = random.choice(data["enterprise_natures"])
        scale = random.choice(data["enterprise_scales"])
        reg_prov, reg_city, reg_county = haidong_region(data)
        contact = random.choice(SURNAMES) + random.choice(MALE_NAMES if random.random() < 0.6 else FEMALE_NAMES)
        phone = random_phone()

        batch.append(
            f"({escape(name)},{escape(industry)},{escape(nature)},{escape(scale)},"
            f"{escape(reg_prov)},{escape(reg_city)},{escape(reg_county)},"
            f"{escape(contact)},{escape(phone)},1,0)"
        )

        if len(batch) >= BATCH_SIZE:
            write_enterprise_batch(f, batch)
            batch = []
            if i % 5000 == 0:
                print(f"  enterprise: {i}/{ENTERPRISE_COUNT}")

    if batch:
        write_enterprise_batch(f, batch)
    print(f"  enterprise: {ENTERPRISE_COUNT}/{ENTERPRISE_COUNT} done")


def write_enterprise_batch(f, batch):
    f.write("INSERT INTO enterprise_info (enterprise_name,industry,enterprise_nature,enterprise_scale,"
            "region_province_code,region_city_code,region_county_code,"
            "contact_person,contact_phone,status,deleted) VALUES\n")
    f.write(",\n".join(batch))
    f.write(";\n\n")


def generate_jobs(data, f, enterprise_count):
    """Generate JOB_COUNT job_post records."""
    print(f"Generating {JOB_COUNT} job records...")
    f.write("-- Job post test data\n")
    f.write("-- Generated: DO NOT EDIT\n\n")

    batch = []
    for i in range(1, JOB_COUNT + 1):
        ent_id = random.randint(1, enterprise_count)
        job_name = random.choice(JOB_TITLES)
        job_cat = random.choice(JOB_CATEGORIES)
        edu_req = random.choice(data["education_levels"])
        exp_req = random.choice(data["experience_requirements"])
        salary = random.choice(data["salary_ranges"])
        recruit_count = random.choice([1, 2, 3, 5, 8, 10, 15, 20])
        work_prov, work_city, work_county = haidong_region(data)
        contact = random.choice(SURNAMES) + random.choice(MALE_NAMES if random.random() < 0.6 else FEMALE_NAMES)
        phone = random_phone()
        pub_date = date(2024, 1, 1) + timedelta(days=random.randint(0, 800))

        batch.append(
            f"({ent_id},{escape(job_name)},{escape(job_cat)},{escape(edu_req)},"
            f"{escape(exp_req)},{escape(salary)},{recruit_count},"
            f"{escape(work_prov)},{escape(work_city)},{escape(work_county)},"
            f"{escape(contact)},{escape(phone)},'{pub_date}',1,0)"
        )

        if len(batch) >= BATCH_SIZE:
            write_job_batch(f, batch)
            batch = []
            if i % 20000 == 0:
                print(f"  job: {i}/{JOB_COUNT}")

    if batch:
        write_job_batch(f, batch)
    print(f"  job: {JOB_COUNT}/{JOB_COUNT} done")


def write_job_batch(f, batch):
    f.write("INSERT INTO job_post (enterprise_id,job_name,job_category,education_requirement,"
            "experience_requirement,salary_range,recruit_count,"
            "work_province_code,work_city_code,work_county_code,"
            "contact_person,contact_phone,publish_time,status,deleted) VALUES\n")
    f.write(",\n".join(batch))
    f.write(";\n\n")


def main():
    print("Connecting to database...")
    conn = mysql.connector.connect(**DB_CONFIG)
    cursor = conn.cursor()

    print("Loading lookup data...")
    data = load_lookup_data(cursor)
    print(f"  Loaded: {len(data['schools'])} schools, {len(data['majors'])} majors, "
          f"{len(data['provinces'])} provinces, {len(data['ethnicities'])} ethnicities")

    out_path = os.path.join(os.path.dirname(__file__), "..", "src", "main", "resources", "data_test.sql")
    out_path = os.path.normpath(out_path)

    with open(out_path, "w", encoding="utf-8") as f:
        f.write("-- ============================================\n")
        f.write("-- Test data: 10w youth + 1w enterprise + 10w job\n")
        f.write("-- Auto-generated by scripts/generate_test_data.py\n")
        f.write("-- ============================================\n\n")
        f.write("SET @old_autocommit = @@autocommit;\n")
        f.write("SET autocommit = 0;\n")
        f.write("SET unique_checks = 0;\n")
        f.write("SET foreign_key_checks = 0;\n\n")

        generate_youth(data, f)
        generate_enterprises(data, f)
        generate_jobs(data, f, ENTERPRISE_COUNT)

        f.write("\nCOMMIT;\n")
        f.write("SET autocommit = @old_autocommit;\n")
        f.write("SET unique_checks = 1;\n")
        f.write("SET foreign_key_checks = 1;\n")

    cursor.close()
    conn.close()
    print(f"\nDone! Output: {out_path}")
    print(f"To import: mysql -u root -p haidong_tuanwei < {out_path}")


if __name__ == "__main__":
    main()
