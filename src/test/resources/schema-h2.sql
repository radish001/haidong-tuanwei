create table if not exists sys_user (
    id bigint primary key auto_increment,
    username varchar(50) not null,
    password varchar(255) not null,
    nickname varchar(50) not null,
    phone varchar(20),
    enabled tinyint not null default 1,
    create_time timestamp not null default current_timestamp,
    create_by bigint,
    update_time timestamp not null default current_timestamp,
    update_by bigint,
    deleted tinyint not null default 0,
    unique (username)
);

create table if not exists sys_role (
    id bigint primary key auto_increment,
    role_code varchar(50) not null,
    role_name varchar(50) not null,
    enabled tinyint not null default 1,
    create_time timestamp not null default current_timestamp,
    create_by bigint,
    update_time timestamp not null default current_timestamp,
    update_by bigint,
    deleted tinyint not null default 0,
    unique (role_code)
);

create table if not exists sys_menu (
    id bigint primary key auto_increment,
    parent_id bigint not null default 0,
    menu_name varchar(50) not null,
    menu_path varchar(100),
    icon varchar(50),
    sort_no int not null default 0,
    visible tinyint not null default 1,
    create_time timestamp not null default current_timestamp,
    create_by bigint,
    update_time timestamp not null default current_timestamp,
    update_by bigint,
    deleted tinyint not null default 0
);

create table if not exists sys_user_role (
    id bigint primary key auto_increment,
    user_id bigint not null,
    role_id bigint not null,
    create_time timestamp not null default current_timestamp,
    create_by bigint,
    update_time timestamp not null default current_timestamp,
    update_by bigint,
    deleted tinyint not null default 0,
    unique (user_id, role_id)
);

create table if not exists sys_role_menu (
    id bigint primary key auto_increment,
    role_id bigint not null,
    menu_id bigint not null,
    create_time timestamp not null default current_timestamp,
    create_by bigint,
    update_time timestamp not null default current_timestamp,
    update_by bigint,
    deleted tinyint not null default 0,
    unique (role_id, menu_id)
);

create table if not exists sys_dict_item (
    id bigint primary key auto_increment,
    dict_type varchar(50) not null,
    dict_label varchar(100) not null,
    dict_value varchar(100) not null,
    sort_no int not null default 0,
    enabled tinyint not null default 1,
    create_time timestamp not null default current_timestamp,
    create_by bigint,
    update_time timestamp not null default current_timestamp,
    update_by bigint,
    deleted tinyint not null default 0,
    unique (dict_type, dict_value)
);

create table if not exists sys_region (
    id bigint primary key auto_increment,
    parent_id bigint not null default 0,
    region_code varchar(32) not null,
    region_name varchar(100) not null,
    region_level tinyint not null,
    sort_no int not null default 0,
    create_time timestamp not null default current_timestamp,
    create_by bigint,
    update_time timestamp not null default current_timestamp,
    update_by bigint,
    deleted tinyint not null default 0,
    unique (region_code)
);

create table if not exists sys_major_catalog (
    id bigint primary key auto_increment,
    major_code varchar(64),
    major_name varchar(100) not null,
    category_dict_item_id bigint not null,
    create_time timestamp not null default current_timestamp,
    create_by bigint,
    update_time timestamp not null default current_timestamp,
    update_by bigint,
    deleted tinyint not null default 0,
    unique (major_code),
    unique (major_name)
);

create table if not exists sys_school_tag (
    id bigint primary key auto_increment,
    tag_name varchar(100) not null,
    create_time timestamp not null default current_timestamp,
    create_by bigint,
    update_time timestamp not null default current_timestamp,
    update_by bigint,
    deleted tinyint not null default 0,
    unique (tag_name)
);

create table if not exists sys_school (
    id bigint primary key auto_increment,
    school_code varchar(64),
    school_name varchar(150) not null,
    category_dict_item_id bigint not null,
    create_time timestamp not null default current_timestamp,
    create_by bigint,
    update_time timestamp not null default current_timestamp,
    update_by bigint,
    deleted tinyint not null default 0,
    unique (school_code),
    unique (school_name)
);

create table if not exists sys_school_tag_rel (
    id bigint primary key auto_increment,
    school_id bigint not null,
    tag_id bigint not null,
    create_time timestamp not null default current_timestamp,
    create_by bigint,
    update_time timestamp not null default current_timestamp,
    update_by bigint,
    deleted tinyint not null default 0,
    unique (school_id, tag_id)
);

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
    create_time timestamp not null default current_timestamp,
    create_by bigint,
    update_time timestamp not null default current_timestamp,
    update_by bigint,
    deleted tinyint not null default 0
);

create table if not exists enterprise_info (
    id bigint primary key auto_increment,
    enterprise_name varchar(150) not null,
    industry varchar(64),
    enterprise_nature varchar(64),
    enterprise_scale varchar(32),
    region_province_code varchar(32),
    region_city_code varchar(32),
    region_county_code varchar(32),
    address varchar(255),
    contact_person varchar(50),
    contact_phone varchar(20),
    description clob,
    status tinyint not null default 1,
    create_time timestamp not null default current_timestamp,
    create_by bigint,
    update_time timestamp not null default current_timestamp,
    update_by bigint,
    deleted tinyint not null default 0
);

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
    job_description clob,
    publish_time timestamp,
    status tinyint not null default 1,
    create_time timestamp not null default current_timestamp,
    create_by bigint,
    update_time timestamp not null default current_timestamp,
    update_by bigint,
    deleted tinyint not null default 0
);

create table if not exists job_post_major_rel (
    id bigint primary key auto_increment,
    job_post_id bigint not null,
    major_code varchar(64) not null,
    create_time timestamp not null default current_timestamp,
    create_by bigint,
    unique (job_post_id, major_code)
);

create table if not exists job_post_education_rel (
    id bigint primary key auto_increment,
    job_post_id bigint not null,
    education_code varchar(32) not null,
    create_time timestamp not null default current_timestamp,
    create_by bigint,
    unique (job_post_id, education_code)
);

create table if not exists job_post_school_category_rel (
    id bigint primary key auto_increment,
    job_post_id bigint not null,
    category_dict_item_id bigint not null,
    create_time timestamp not null default current_timestamp,
    create_by bigint,
    unique (job_post_id, category_dict_item_id)
);

create table if not exists job_post_school_tag_rel (
    id bigint primary key auto_increment,
    job_post_id bigint not null,
    tag_id bigint not null,
    create_time timestamp not null default current_timestamp,
    create_by bigint,
    unique (job_post_id, tag_id)
);

create table if not exists policy_article (
    id bigint primary key auto_increment,
    title varchar(200) not null,
    issuing_organization varchar(100),
    policy_source varchar(100),
    summary varchar(500),
    content_html clob not null,
    publish_time timestamp,
    status tinyint not null default 0,
    create_time timestamp not null default current_timestamp,
    create_by bigint,
    update_time timestamp not null default current_timestamp,
    update_by bigint,
    deleted tinyint not null default 0
);
