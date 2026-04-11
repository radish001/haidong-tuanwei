create table if not exists sys_user (
    id bigint primary key auto_increment,
    username varchar(50) not null,
    password varchar(255) not null,
    nickname varchar(50) not null,
    phone varchar(20),
    enabled tinyint not null default 1,
    create_time datetime not null default current_timestamp,
    create_by bigint,
    update_time datetime not null default current_timestamp on update current_timestamp,
    update_by bigint,
    deleted tinyint not null default 0,
    unique key uk_sys_user_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

create table if not exists sys_role (
    id bigint primary key auto_increment,
    role_code varchar(50) not null,
    role_name varchar(50) not null,
    enabled tinyint not null default 1,
    create_time datetime not null default current_timestamp,
    create_by bigint,
    update_time datetime not null default current_timestamp on update current_timestamp,
    update_by bigint,
    deleted tinyint not null default 0,
    unique key uk_sys_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

create table if not exists sys_menu (
    id bigint primary key auto_increment,
    parent_id bigint not null default 0,
    menu_name varchar(50) not null,
    menu_path varchar(100),
    icon varchar(50),
    sort_no int not null default 0,
    visible tinyint not null default 1,
    create_time datetime not null default current_timestamp,
    create_by bigint,
    update_time datetime not null default current_timestamp on update current_timestamp,
    update_by bigint,
    deleted tinyint not null default 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

create table if not exists sys_user_role (
    id bigint primary key auto_increment,
    user_id bigint not null,
    role_id bigint not null,
    create_time datetime not null default current_timestamp,
    create_by bigint,
    update_time datetime not null default current_timestamp on update current_timestamp,
    update_by bigint,
    deleted tinyint not null default 0,
    unique key uk_sys_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

create table if not exists sys_role_menu (
    id bigint primary key auto_increment,
    role_id bigint not null,
    menu_id bigint not null,
    create_time datetime not null default current_timestamp,
    create_by bigint,
    update_time datetime not null default current_timestamp on update current_timestamp,
    update_by bigint,
    deleted tinyint not null default 0,
    unique key uk_sys_role_menu (role_id, menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

create table if not exists sys_dict_item (
    id bigint primary key auto_increment,
    dict_type varchar(50) not null,
    dict_label varchar(100) not null,
    dict_value varchar(100) not null,
    sort_no int not null default 0,
    enabled tinyint not null default 1,
    create_time datetime not null default current_timestamp,
    create_by bigint,
    update_time datetime not null default current_timestamp on update current_timestamp,
    update_by bigint,
    deleted tinyint not null default 0,
    unique key uk_sys_dict_type_value (dict_type, dict_value),
    key idx_sys_dict_type_enabled_deleted_sort (dict_type, enabled, deleted, sort_no, id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

create table if not exists sys_region (
    id bigint primary key auto_increment,
    parent_id bigint not null default 0,
    region_code varchar(32) not null,
    region_name varchar(100) not null,
    region_level tinyint not null,
    sort_no int not null default 0,
    create_time datetime not null default current_timestamp,
    create_by bigint,
    update_time datetime not null default current_timestamp on update current_timestamp,
    update_by bigint,
    deleted tinyint not null default 0,
    unique key uk_sys_region_code (region_code),
    key idx_sys_region_deleted_level_sort (deleted, region_level, sort_no, id),
    key idx_sys_region_parent_deleted_sort (parent_id, deleted, sort_no, id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

create table if not exists sys_major_catalog (
    id bigint primary key auto_increment,
    major_code varchar(64) not null,
    major_name varchar(100) not null,
    category_dict_item_id bigint not null,
    create_time datetime not null default current_timestamp,
    create_by bigint,
    update_time datetime not null default current_timestamp on update current_timestamp,
    update_by bigint,
    deleted tinyint not null default 0,
    unique key uk_sys_major_catalog_code (major_code),
    key idx_sys_major_category_deleted_code (category_dict_item_id, deleted, major_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

create table if not exists sys_school_tag (
    id bigint primary key auto_increment,
    tag_name varchar(100) not null,
    create_time datetime not null default current_timestamp,
    create_by bigint,
    update_time datetime not null default current_timestamp on update current_timestamp,
    update_by bigint,
    deleted tinyint not null default 0,
    unique key uk_sys_school_tag_name (tag_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

create table if not exists sys_school (
    id bigint primary key auto_increment,
    school_code varchar(64) not null,
    school_name varchar(150) not null,
    category_dict_item_id bigint not null,
    create_time datetime not null default current_timestamp,
    create_by bigint,
    update_time datetime not null default current_timestamp on update current_timestamp,
    update_by bigint,
    deleted tinyint not null default 0,
    unique key uk_sys_school_code (school_code),
    key idx_sys_school_category_deleted (category_dict_item_id, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

create table if not exists sys_school_tag_rel (
    id bigint primary key auto_increment,
    school_id bigint not null,
    tag_id bigint not null,
    create_time datetime not null default current_timestamp,
    create_by bigint,
    update_time datetime not null default current_timestamp on update current_timestamp,
    update_by bigint,
    deleted tinyint not null default 0,
    unique key uk_sys_school_tag_rel (school_id, tag_id),
    key idx_sys_school_tag_rel_tag_deleted_school (tag_id, deleted, school_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

create table if not exists sys_analytics_school_tag (
    id bigint primary key auto_increment,
    tag_id bigint not null,
    create_time datetime not null default current_timestamp,
    create_by bigint,
    deleted tinyint not null default 0,
    unique key uk_sys_analytics_school_tag (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

create table if not exists youth_info (
    id bigint primary key auto_increment,
    youth_type varchar(32) not null,
    name varchar(50) not null,
    gender varchar(10) not null,
    birth_date date,
    ethnicity varchar(32),
    political_status varchar(32),
    native_province_code varchar(32),
    native_city_code varchar(32),
    native_county_code varchar(32),
    education_level varchar(32),
    degree_code varchar(32),
    school_code varchar(64),
    school_name varchar(100),
    school_province_code varchar(32),
    school_city_code varchar(32),
    school_county_code varchar(32),
    major_code varchar(64),
    major varchar(100),
    major_category varchar(64),
    recruitment_year int,
    graduation_date date,
    employment_direction varchar(100),
    phone varchar(20),
    residence_province_code varchar(32),
    residence_city_code varchar(32),
    residence_county_code varchar(32),
    employment_status varchar(32),
    current_job varchar(100),
    employment_company varchar(100),
    entrepreneurship_status varchar(32),
    entrepreneurship_project varchar(100),
    entrepreneurship_demand varchar(255),
    remarks varchar(500),
    create_time datetime not null default current_timestamp,
    create_by bigint,
    update_time datetime not null default current_timestamp on update current_timestamp,
    update_by bigint,
    deleted tinyint not null default 0,
    key idx_youth_deleted_type_id (deleted, youth_type, id),
    key idx_youth_deleted_type_gender (deleted, youth_type, gender),
    key idx_youth_deleted_type_ethnicity (deleted, youth_type, ethnicity),
    key idx_youth_deleted_type_political_status (deleted, youth_type, political_status),
    key idx_youth_deleted_type_education_level (deleted, youth_type, education_level),
    key idx_youth_deleted_type_degree_code (deleted, youth_type, degree_code),
    key idx_youth_deleted_type_school_code (deleted, youth_type, school_code),
    key idx_youth_deleted_type_major_code (deleted, youth_type, major_code),
    key idx_youth_deleted_type_birth_date (deleted, youth_type, birth_date),
    key idx_youth_deleted_type_native_province (deleted, youth_type, native_province_code),
    key idx_youth_deleted_type_native_city (deleted, youth_type, native_city_code),
    key idx_youth_deleted_type_native_county (deleted, youth_type, native_county_code),
    key idx_youth_deleted_type_school_province (deleted, youth_type, school_province_code),
    key idx_youth_deleted_type_school_city (deleted, youth_type, school_city_code),
    key idx_youth_deleted_type_school_county (deleted, youth_type, school_county_code),
    key idx_youth_deleted_type_residence_province (deleted, youth_type, residence_province_code),
    key idx_youth_deleted_type_residence_city (deleted, youth_type, residence_city_code),
    key idx_youth_deleted_type_residence_county (deleted, youth_type, residence_county_code),
    key idx_youth_deleted_type_name_phone (deleted, youth_type, name, phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

create table if not exists enterprise_info (
    id bigint primary key auto_increment,
    enterprise_name varchar(150) not null,
    industry varchar(64),
    enterprise_nature varchar(64),
    enterprise_scale varchar(32),
    region_province_code varchar(32),
    region_city_code varchar(32),
    region_county_code varchar(32),
    unified_social_credit_code varchar(64),
    business_license_path varchar(255),
    address varchar(255),
    contact_person varchar(50),
    contact_phone varchar(20),
    description text,
    status tinyint not null default 1,
    create_time datetime not null default current_timestamp,
    create_by bigint,
    update_time datetime not null default current_timestamp on update current_timestamp,
    update_by bigint,
    deleted tinyint not null default 0,
    key idx_enterprise_deleted_status_name (deleted, status, enterprise_name),
    key idx_enterprise_deleted_industry (deleted, industry),
    key idx_enterprise_deleted_nature (deleted, enterprise_nature),
    key idx_enterprise_deleted_scale (deleted, enterprise_scale),
    key idx_enterprise_deleted_region_province (deleted, region_province_code),
    key idx_enterprise_deleted_region_city (deleted, region_city_code),
    key idx_enterprise_deleted_region_county (deleted, region_county_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

create table if not exists job_post (
    id bigint primary key auto_increment,
    enterprise_id bigint not null,
    job_name varchar(100) not null,
    job_category varchar(64),
    education_requirement varchar(32),
    experience_requirement varchar(64),
    salary_range varchar(64),
    recruit_count int,
    work_province_code varchar(32),
    work_city_code varchar(32),
    work_county_code varchar(32),
    contact_person varchar(50),
    contact_phone varchar(20),
    job_description text,
    publish_time datetime,
    status tinyint not null default 1,
    create_time datetime not null default current_timestamp,
    create_by bigint,
    update_time datetime not null default current_timestamp on update current_timestamp,
    update_by bigint,
    deleted tinyint not null default 0,
    key idx_job_deleted_publish_time_id (deleted, publish_time, id),
    key idx_job_deleted_enterprise_id (deleted, enterprise_id, id),
    key idx_job_deleted_experience (deleted, experience_requirement),
    key idx_job_deleted_salary (deleted, salary_range),
    key idx_job_deleted_work_province (deleted, work_province_code),
    key idx_job_deleted_work_city (deleted, work_city_code),
    key idx_job_deleted_work_county (deleted, work_county_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

create table if not exists job_post_major_rel (
    id bigint primary key auto_increment,
    job_post_id bigint not null,
    major_code varchar(64) not null,
    create_time datetime not null default current_timestamp,
    create_by bigint,
    unique key uk_job_post_major_rel (job_post_id, major_code),
    key idx_job_post_major_rel_job (job_post_id),
    key idx_job_post_major_rel_major (major_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

create table if not exists job_post_education_rel (
    id bigint primary key auto_increment,
    job_post_id bigint not null,
    education_code varchar(32) not null,
    create_time datetime not null default current_timestamp,
    create_by bigint,
    unique key uk_job_post_education_rel (job_post_id, education_code),
    key idx_job_post_education_rel_job (job_post_id),
    key idx_job_post_education_rel_education (education_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

create table if not exists job_post_school_category_rel (
    id bigint primary key auto_increment,
    job_post_id bigint not null,
    category_dict_item_id bigint not null,
    create_time datetime not null default current_timestamp,
    create_by bigint,
    unique key uk_job_post_school_category_rel (job_post_id, category_dict_item_id),
    key idx_job_post_school_category_rel_job (job_post_id),
    key idx_job_post_school_category_rel_category (category_dict_item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

create table if not exists job_post_school_tag_rel (
    id bigint primary key auto_increment,
    job_post_id bigint not null,
    tag_id bigint not null,
    create_time datetime not null default current_timestamp,
    create_by bigint,
    unique key uk_job_post_school_tag_rel (job_post_id, tag_id),
    key idx_job_post_school_tag_rel_job (job_post_id),
    key idx_job_post_school_tag_rel_tag (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

create table if not exists policy_article (
    id bigint primary key auto_increment,
    title varchar(200) not null,
    issuing_organization varchar(100),
    policy_source varchar(100),
    summary varchar(500),
    content_html longtext not null,
    publish_time datetime,
    status tinyint not null default 0,
    create_time datetime not null default current_timestamp,
    create_by bigint,
    update_time datetime not null default current_timestamp on update current_timestamp,
    update_by bigint,
    deleted tinyint not null default 0,
    key idx_policy_deleted_publish_time_id (deleted, publish_time, id),
    key idx_policy_deleted_status_id (deleted, status, id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
