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
);

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
);

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
);

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
);

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
);

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
    key idx_sys_dict_type_deleted (dict_type, deleted)
);

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
    unique key uk_sys_region_code (region_code)
);

create table if not exists sys_major_catalog (
    id bigint primary key auto_increment,
    major_name varchar(100) not null,
    category_dict_item_id bigint not null,
    create_time datetime not null default current_timestamp,
    create_by bigint,
    update_time datetime not null default current_timestamp on update current_timestamp,
    update_by bigint,
    deleted tinyint not null default 0,
    unique key uk_sys_major_catalog_name (major_name)
);

create table if not exists sys_school_tag (
    id bigint primary key auto_increment,
    tag_name varchar(100) not null,
    create_time datetime not null default current_timestamp,
    create_by bigint,
    update_time datetime not null default current_timestamp on update current_timestamp,
    update_by bigint,
    deleted tinyint not null default 0,
    unique key uk_sys_school_tag_name (tag_name)
);

create table if not exists sys_school (
    id bigint primary key auto_increment,
    school_name varchar(150) not null,
    category_dict_item_id bigint not null,
    create_time datetime not null default current_timestamp,
    create_by bigint,
    update_time datetime not null default current_timestamp on update current_timestamp,
    update_by bigint,
    deleted tinyint not null default 0,
    unique key uk_sys_school_name (school_name),
    key idx_sys_school_category_deleted (category_dict_item_id, deleted)
);

create table if not exists sys_school_tag_rel (
    id bigint primary key auto_increment,
    school_id bigint not null,
    tag_id bigint not null,
    create_time datetime not null default current_timestamp,
    create_by bigint,
    update_time datetime not null default current_timestamp on update current_timestamp,
    update_by bigint,
    deleted tinyint not null default 0,
    unique key uk_sys_school_tag_rel (school_id, tag_id)
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
    school_name varchar(100),
    school_province_code varchar(32),
    school_city_code varchar(32),
    school_county_code varchar(32),
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
    create_time datetime not null default current_timestamp,
    create_by bigint,
    update_time datetime not null default current_timestamp on update current_timestamp,
    update_by bigint,
    deleted tinyint not null default 0,
    key idx_youth_type_deleted (youth_type, deleted),
    key idx_youth_name_deleted (name, deleted),
    key idx_youth_phone_deleted (phone, deleted)
);

set @major_category_column_exists = (
    select count(*)
    from information_schema.columns
    where table_schema = database()
      and table_name = 'youth_info'
      and column_name = 'major_category'
);
set @major_category_column_sql = if(
    @major_category_column_exists = 0,
    'alter table youth_info add column major_category varchar(64) after major',
    'select 1'
);
prepare add_major_category_column_stmt from @major_category_column_sql;
execute add_major_category_column_stmt;
deallocate prepare add_major_category_column_stmt;

set @native_province_column_exists = (
    select count(*)
    from information_schema.columns
    where table_schema = database()
      and table_name = 'youth_info'
      and column_name = 'native_province_code'
);
set @native_province_column_sql = if(
    @native_province_column_exists = 0,
    'alter table youth_info add column native_province_code varchar(32) after political_status',
    'select 1'
);
prepare add_native_province_column_stmt from @native_province_column_sql;
execute add_native_province_column_stmt;
deallocate prepare add_native_province_column_stmt;

set @native_city_column_exists = (
    select count(*)
    from information_schema.columns
    where table_schema = database()
      and table_name = 'youth_info'
      and column_name = 'native_city_code'
);
set @native_city_column_sql = if(
    @native_city_column_exists = 0,
    'alter table youth_info add column native_city_code varchar(32) after native_province_code',
    'select 1'
);
prepare add_native_city_column_stmt from @native_city_column_sql;
execute add_native_city_column_stmt;
deallocate prepare add_native_city_column_stmt;

set @native_county_column_exists = (
    select count(*)
    from information_schema.columns
    where table_schema = database()
      and table_name = 'youth_info'
      and column_name = 'native_county_code'
);
set @native_county_column_sql = if(
    @native_county_column_exists = 0,
    'alter table youth_info add column native_county_code varchar(32) after native_city_code',
    'select 1'
);
prepare add_native_county_column_stmt from @native_county_column_sql;
execute add_native_county_column_stmt;
deallocate prepare add_native_county_column_stmt;

set @school_province_column_exists = (
    select count(*)
    from information_schema.columns
    where table_schema = database()
      and table_name = 'youth_info'
      and column_name = 'school_province_code'
);
set @school_province_column_sql = if(
    @school_province_column_exists = 0,
    'alter table youth_info add column school_province_code varchar(32) after school_name',
    'select 1'
);
prepare add_school_province_column_stmt from @school_province_column_sql;
execute add_school_province_column_stmt;
deallocate prepare add_school_province_column_stmt;

set @school_city_column_exists = (
    select count(*)
    from information_schema.columns
    where table_schema = database()
      and table_name = 'youth_info'
      and column_name = 'school_city_code'
);
set @school_city_column_sql = if(
    @school_city_column_exists = 0,
    'alter table youth_info add column school_city_code varchar(32) after school_province_code',
    'select 1'
);
prepare add_school_city_column_stmt from @school_city_column_sql;
execute add_school_city_column_stmt;
deallocate prepare add_school_city_column_stmt;

set @school_county_column_exists = (
    select count(*)
    from information_schema.columns
    where table_schema = database()
      and table_name = 'youth_info'
      and column_name = 'school_county_code'
);
set @school_county_column_sql = if(
    @school_county_column_exists = 0,
    'alter table youth_info add column school_county_code varchar(32) after school_city_code',
    'select 1'
);
prepare add_school_county_column_stmt from @school_county_column_sql;
execute add_school_county_column_stmt;
deallocate prepare add_school_county_column_stmt;

set @residence_province_column_exists = (
    select count(*)
    from information_schema.columns
    where table_schema = database()
      and table_name = 'youth_info'
      and column_name = 'residence_province_code'
);
set @residence_province_column_sql = if(
    @residence_province_column_exists = 0,
    'alter table youth_info add column residence_province_code varchar(32) after phone',
    'select 1'
);
prepare add_residence_province_column_stmt from @residence_province_column_sql;
execute add_residence_province_column_stmt;
deallocate prepare add_residence_province_column_stmt;

set @residence_city_column_exists = (
    select count(*)
    from information_schema.columns
    where table_schema = database()
      and table_name = 'youth_info'
      and column_name = 'residence_city_code'
);
set @residence_city_column_sql = if(
    @residence_city_column_exists = 0,
    'alter table youth_info add column residence_city_code varchar(32) after residence_province_code',
    'select 1'
);
prepare add_residence_city_column_stmt from @residence_city_column_sql;
execute add_residence_city_column_stmt;
deallocate prepare add_residence_city_column_stmt;

set @residence_county_column_exists = (
    select count(*)
    from information_schema.columns
    where table_schema = database()
      and table_name = 'youth_info'
      and column_name = 'residence_county_code'
);
set @residence_county_column_sql = if(
    @residence_county_column_exists = 0,
    'alter table youth_info add column residence_county_code varchar(32) after residence_city_code',
    'select 1'
);
prepare add_residence_county_column_stmt from @residence_county_column_sql;
execute add_residence_county_column_stmt;
deallocate prepare add_residence_county_column_stmt;

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
    description text,
    status tinyint not null default 1,
    create_time datetime not null default current_timestamp,
    create_by bigint,
    update_time datetime not null default current_timestamp on update current_timestamp,
    update_by bigint,
    deleted tinyint not null default 0,
    key idx_enterprise_name_deleted (enterprise_name, deleted),
    key idx_enterprise_region_deleted (region_county_code, deleted)
);

set @enterprise_region_province_column_exists = (
    select count(*)
    from information_schema.columns
    where table_schema = database()
      and table_name = 'enterprise_info'
      and column_name = 'region_province_code'
);
set @enterprise_region_province_column_sql = if(
    @enterprise_region_province_column_exists = 0,
    'alter table enterprise_info add column region_province_code varchar(32) after enterprise_scale',
    'select 1'
);
prepare add_enterprise_region_province_column_stmt from @enterprise_region_province_column_sql;
execute add_enterprise_region_province_column_stmt;
deallocate prepare add_enterprise_region_province_column_stmt;

set @enterprise_region_city_column_exists = (
    select count(*)
    from information_schema.columns
    where table_schema = database()
      and table_name = 'enterprise_info'
      and column_name = 'region_city_code'
);
set @enterprise_region_city_column_sql = if(
    @enterprise_region_city_column_exists = 0,
    'alter table enterprise_info add column region_city_code varchar(32) after region_province_code',
    'select 1'
);
prepare add_enterprise_region_city_column_stmt from @enterprise_region_city_column_sql;
execute add_enterprise_region_city_column_stmt;
deallocate prepare add_enterprise_region_city_column_stmt;

set @enterprise_region_county_column_exists = (
    select count(*)
    from information_schema.columns
    where table_schema = database()
      and table_name = 'enterprise_info'
      and column_name = 'region_county_code'
);
set @enterprise_region_county_column_sql = if(
    @enterprise_region_county_column_exists = 0,
    'alter table enterprise_info add column region_county_code varchar(32) after region_city_code',
    'select 1'
);
prepare add_enterprise_region_county_column_stmt from @enterprise_region_county_column_sql;
execute add_enterprise_region_county_column_stmt;
deallocate prepare add_enterprise_region_county_column_stmt;

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
    key idx_job_enterprise_deleted (enterprise_id, deleted),
    key idx_job_name_deleted (job_name, deleted)
);

set @job_salary_range_column_exists = (
    select count(*)
    from information_schema.columns
    where table_schema = database()
      and table_name = 'job_post'
      and column_name = 'salary_range'
);
set @job_salary_range_column_sql = if(
    @job_salary_range_column_exists = 0,
    'alter table job_post add column salary_range varchar(64) after experience_requirement',
    'select 1'
);
prepare add_job_salary_range_column_stmt from @job_salary_range_column_sql;
execute add_job_salary_range_column_stmt;
deallocate prepare add_job_salary_range_column_stmt;

set @job_work_province_column_exists = (
    select count(*)
    from information_schema.columns
    where table_schema = database()
      and table_name = 'job_post'
      and column_name = 'work_province_code'
);
set @job_work_province_column_sql = if(
    @job_work_province_column_exists = 0,
    'alter table job_post add column work_province_code varchar(32) after recruit_count',
    'select 1'
);
prepare add_job_work_province_column_stmt from @job_work_province_column_sql;
execute add_job_work_province_column_stmt;
deallocate prepare add_job_work_province_column_stmt;

set @job_work_city_column_exists = (
    select count(*)
    from information_schema.columns
    where table_schema = database()
      and table_name = 'job_post'
      and column_name = 'work_city_code'
);
set @job_work_city_column_sql = if(
    @job_work_city_column_exists = 0,
    'alter table job_post add column work_city_code varchar(32) after work_province_code',
    'select 1'
);
prepare add_job_work_city_column_stmt from @job_work_city_column_sql;
execute add_job_work_city_column_stmt;
deallocate prepare add_job_work_city_column_stmt;

set @job_work_county_column_exists = (
    select count(*)
    from information_schema.columns
    where table_schema = database()
      and table_name = 'job_post'
      and column_name = 'work_county_code'
);
set @job_work_county_column_sql = if(
    @job_work_county_column_exists = 0,
    'alter table job_post add column work_county_code varchar(32) after work_city_code',
    'select 1'
);
prepare add_job_work_county_column_stmt from @job_work_county_column_sql;
execute add_job_work_county_column_stmt;
deallocate prepare add_job_work_county_column_stmt;

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
    key idx_policy_title_deleted (title, deleted),
    key idx_policy_status_deleted (status, deleted)
);
