## Context

当前仓库仍处于 Spring Boot 初始化阶段，只有简单示例代码，还没有可直接承载后台平台的认证、菜单、业务模块、统计分析和文件上传等能力。本次变更的目标是先落地一套“可用优先”的后台一体化平台，用于承载青年就业创业信息的采集、管理、发布和分析。

已确认的关键约束如下：
- 技术栈固定为 Spring Boot、MyBatis、MySQL，代码结构采用 controller、service、dao 三层模式。
- 前后端一体，不拆分独立 SPA 前端。
- 需要后台管理员登录、角色管理和菜单级权限，不做按钮级权限。
- 青年信息使用单张 `youth_info` 总表，通过 `youth_type` 区分四类青年。
- 青年信息支持 Excel 模板下载、导入、导出；企业信息仅支持表单录入维护。
- 政策正文需要富文本编辑，不设计附件表，也不支持附件上传。
- 第一阶段以功能可用为主，不追求截图级视觉还原。

## Goals / Non-Goals

**Goals:**
- 建立统一后台基础框架，具备登录认证、角色分配、菜单渲染和页面访问控制能力。
- 建立青年信息、企业信息、招聘信息、政策管理等核心业务模块。
- 提供标准化的青年信息 Excel 模板下载、导入校验和导出能力。
- 建立字典与区域管理，为表单下拉、模板下拉和分析统计提供统一数据源。
- 建立首页驾驶舱与分类分析页面，支撑基础运营统计。

**Non-Goals:**
- 第一阶段不追求完全还原参考截图中的样式细节。
- 第一阶段不做按钮级权限控制和更细粒度的权限码体系。
- 第一阶段不支持企业信息 Excel 导入。
- 第一阶段不拆分前后端、不引入微服务，也不建设独立 API 网关体系。
- 第一阶段不接入云存储、对象存储或分布式文件系统，也不实现附件上传能力。

## Decisions

### 1. 采用服务端渲染的一体化后台架构
平台采用 Spring MVC + Thymeleaf 作为页面渲染基础，配合必要的静态 JavaScript 实现图表、弹窗表单、上传等交互能力。

选择原因：
- 符合当前工程基础和“前后端一体”的明确要求。
- 相比前后端分离，开发和联调成本更低，更适合尽快落地业务后台。
- 与 Spring Security 的 Session 认证天然契合。

备选方案：
- 前后端分离：第一阶段不采用，因为范围更大、交付更慢。
- 纯 REST 后端：不采用，因为目标是直接交付可用后台，而不是仅提供接口层。

### 2. 使用 Spring Security + Session 实现后台管理员认证
认证方式采用表单登录和 HTTP Session，会话主体限定为后台管理员用户。

选择原因：
- 后台系统更适合 Session 模式。
- 与 Thymeleaf、服务端菜单渲染集成简单。
- 避免 JWT、刷新令牌、前端存储等额外复杂度。

备选方案：
- JWT 认证：第一阶段不采用，收益不足以覆盖复杂度。
- 自研认证流程：不采用，框架能力已足够且更安全稳定。

### 3. 权限模型只做到菜单级
第一阶段权限只控制“角色可访问哪些菜单和页面”，不扩展到按钮级操作权限。

选择原因：
- 与当前确认范围一致。
- 能显著降低用户、角色、菜单模块的设计和实现复杂度。
- 页面渲染和路由访问控制更直接。

备选方案：
- 按钮级权限：暂缓，后续如有需要可在当前表结构基础上继续扩展。

### 4. 青年信息采用单表统一建模
四类青年信息统一存入 `youth_info`，通过 `youth_type` 进行分类管理。

选择原因：
- 与用户明确要求一致。
- 有利于统一查询、统一导入导出和统一统计分析。
- 能降低多表联查、多套模板和多套逻辑带来的复杂度。

备选方案：
- 四张业务表分开管理：不采用，会增加维护和统计成本。
- 主表 + 扩展表：暂缓，只有在字段差异复杂到难以维护时再考虑。

### 5. 青年 Excel 导入采用“模板驱动 + 严格校验”模式
系统提供标准 `.xlsx` 模板，包含一个主录入工作表和多个隐藏字典工作表，用于支持性别、民族、政治面貌、区域、学历等下拉选项。

选择原因：
- 完全符合已确认的模板结构与业务流程。
- 能减少自由录入造成的数据污染。
- 导入失败原因可以做到逐行回执，便于业务人员修正。

实现方向：
- 使用 Apache POI 生成模板，控制隐藏 sheet、命名区域、数据验证和下拉来源。
- 上传后逐行解析并校验字典、区域、日期格式、联系方式和重复数据。
- 返回导入结果摘要，包括成功数、失败数、失败行号和失败原因。

备选方案：
- 通用 Excel 任意导入：不采用，错误率高且不利于长期维护。
- CSV 导入：不采用，无法承载隐藏 sheet 和下拉验证。

### 6. 下拉选项与模板字典统一来源于系统字典和区域数据
性别、民族、政治面貌、学历、行政区域等选项不能只存在 Excel 模板中，必须由系统表统一维护。

选择原因：
- 保证页面录入、模板下载、导入校验、分析统计口径一致。
- 后续维护字典时不需要同时修改代码和模板静态文件。
- 可以逐步把模板能力和后台管理能力打通。

备选方案：
- 全部写死在代码中：不采用，后期维护成本高且不灵活。

### 7. 企业信息与招聘信息采用纯表单维护模式
企业信息通过表单新增、编辑、查询、删除进行维护；招聘信息通过表单维护，并且必须关联到已有企业。

选择原因：
- 与“企业信息不做 Excel 导入”的要求一致。
- 能清晰建立企业与岗位的一对多关系。
- 表单校验更简单，第一阶段更容易保证数据质量。

备选方案：
- 企业 Excel 导入：明确不纳入第一阶段范围。

### 8. 政策正文存 HTML，不设计附件能力
政策正文以富文本 HTML 形式存库，第一阶段不设计附件表，也不提供附件上传、下载能力。

选择原因：
- 满足当前已收敛后的范围，只保留政策正文管理主流程。
- 可以显著降低政策模块的数据模型和页面交互复杂度。
- 后续如果需要附件能力，可以作为独立变更再扩展。

备选方案：
- Markdown 正文：不采用，不满足富文本需求。
- 政策附件上传：第一阶段明确不纳入范围。

### 9. 数据分析基于业务表直接聚合
首页和分析页通过对 `youth_info`、`enterprise_info`、`job_post`、`policy_article` 等业务表进行聚合查询来生成图表数据。

选择原因：
- 第一阶段不需要单独建设数仓或 ETL。
- 可以直接复用业务数据，缩短交付周期。
- 足以支撑年龄、学历、性别、民族、政治面貌、创业需求等基础统计。

备选方案：
- 预计算汇总表或定时离线任务：暂缓，等数据量或性能压力上来后再考虑。

## Risks / Trade-offs

- [单张 `youth_info` 表会比较宽、部分字段可能为空] → 第一阶段优先保证通用字段完整，分类扩展字段通过清晰命名和文档约束控制复杂度。
- [Excel 模板和导入校验实现复杂度较高] → 统一模板格式、统一字典来源，并输出逐行错误明细，减少人工排查成本。
- [只做菜单级权限，后续可能不够细] → 表结构设计保持可扩展性，为后续增加按钮级权限预留空间。
- [数据分析直接查业务表，后期可能有性能压力] → 先保证索引和查询结构清晰，后续如有需要再做汇总表或缓存优化。
- [服务端渲染页面较多时容易出现重复模板代码] → 尽早抽取公共布局、列表模板片段和通用表单结构。

## Migration Plan

1. 先补齐基础表结构：用户、角色、菜单、字典、区域等公共数据。
2. 引入安全配置、登录页、会话认证和动态菜单渲染。
3. 落地青年信息、企业信息、招聘信息、政策管理四个核心业务模块。
4. 落地青年 Excel 模板下载、导入、导出和导入校验回执。
5. 在基础数据具备后，再落地首页驾驶舱和数据分析页面。
6. 初始化管理员账号、默认角色、基础菜单、字典项和区域数据。

回滚策略：
- 数据库变更按模块逐步推进，保持可回退性。
- 出现问题时可先关闭新模块入口，恢复到基础工程状态。
- 本次变更不涉及附件文件落盘，不需要额外文件回滚策略。

## 核心表结构草案

以下四张主表均统一包含审计字段和逻辑删除字段：
- `create_time`：创建时间
- `create_by`：创建人，建议存后台用户 `sys_user.id`
- `update_time`：修改时间
- `update_by`：修改人，建议存后台用户 `sys_user.id`
- `deleted`：逻辑删除标记，`0` 表示未删除，`1` 表示已删除

### 1. 青年信息表 `youth_info`

```sql
CREATE TABLE `youth_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `youth_type` VARCHAR(32) NOT NULL COMMENT '青年分类：COLLEGE/GRADUATED_UNEMPLOYED/RURAL_COMMUNITY/ENTREPRENEUR',
  `name` VARCHAR(50) NOT NULL COMMENT '姓名',
  `gender` VARCHAR(10) NOT NULL COMMENT '性别',
  `birth_date` DATE DEFAULT NULL COMMENT '出生年月',
  `ethnicity` VARCHAR(32) DEFAULT NULL COMMENT '民族',
  `political_status` VARCHAR(32) DEFAULT NULL COMMENT '政治面貌',
  `native_place_code` VARCHAR(32) DEFAULT NULL COMMENT '籍贯区域编码',
  `native_place_name` VARCHAR(100) DEFAULT NULL COMMENT '籍贯区域名称',
  `education_level` VARCHAR(32) DEFAULT NULL COMMENT '学历',
  `school_name` VARCHAR(100) DEFAULT NULL COMMENT '学校',
  `school_region_code` VARCHAR(32) DEFAULT NULL COMMENT '学校所在区域编码',
  `school_region_name` VARCHAR(100) DEFAULT NULL COMMENT '学校所在区域名称',
  `major` VARCHAR(100) DEFAULT NULL COMMENT '专业',
  `graduation_date` DATE DEFAULT NULL COMMENT '毕业时间',
  `employment_direction` VARCHAR(100) DEFAULT NULL COMMENT '就业方向',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系方式',
  `current_residence_code` VARCHAR(32) DEFAULT NULL COMMENT '现居地编码',
  `current_residence_name` VARCHAR(100) DEFAULT NULL COMMENT '现居地名称',
  `employment_status` VARCHAR(32) DEFAULT NULL COMMENT '就业状态',
  `current_job` VARCHAR(100) DEFAULT NULL COMMENT '当前从事工作',
  `employment_company` VARCHAR(100) DEFAULT NULL COMMENT '就业单位',
  `entrepreneurship_status` VARCHAR(32) DEFAULT NULL COMMENT '创业状态',
  `entrepreneurship_project` VARCHAR(100) DEFAULT NULL COMMENT '创业项目',
  `entrepreneurship_demand` VARCHAR(255) DEFAULT NULL COMMENT '创业需求',
  `remarks` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '修改人',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_youth_type_deleted` (`youth_type`, `deleted`),
  KEY `idx_name_deleted` (`name`, `deleted`),
  KEY `idx_phone_deleted` (`phone`, `deleted`),
  KEY `idx_native_place_deleted` (`native_place_code`, `deleted`),
  KEY `idx_school_region_deleted` (`school_region_code`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='青年信息表';
```

### 2. 企业信息表 `enterprise_info`

```sql
CREATE TABLE `enterprise_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `enterprise_name` VARCHAR(150) NOT NULL COMMENT '企业名称',
  `industry` VARCHAR(64) DEFAULT NULL COMMENT '行业',
  `enterprise_nature` VARCHAR(64) DEFAULT NULL COMMENT '企业性质',
  `enterprise_scale` VARCHAR(32) DEFAULT NULL COMMENT '企业规模',
  `region_code` VARCHAR(32) DEFAULT NULL COMMENT '企业所在区域编码',
  `region_name` VARCHAR(100) DEFAULT NULL COMMENT '企业所在区域名称',
  `address` VARCHAR(255) DEFAULT NULL COMMENT '企业地址',
  `contact_person` VARCHAR(50) DEFAULT NULL COMMENT '联系人',
  `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `description` TEXT COMMENT '企业介绍',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用 0停用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '修改人',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_enterprise_name_deleted` (`enterprise_name`, `deleted`),
  KEY `idx_industry_deleted` (`industry`, `deleted`),
  KEY `idx_region_deleted` (`region_code`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企业信息表';
```

### 3. 招聘岗位表 `job_post`

```sql
CREATE TABLE `job_post` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `enterprise_id` BIGINT NOT NULL COMMENT '企业ID',
  `job_name` VARCHAR(100) NOT NULL COMMENT '岗位名称',
  `job_category` VARCHAR(64) DEFAULT NULL COMMENT '岗位类别',
  `education_requirement` VARCHAR(32) DEFAULT NULL COMMENT '学历要求',
  `experience_requirement` VARCHAR(64) DEFAULT NULL COMMENT '经验要求',
  `salary_min` DECIMAL(10,2) DEFAULT NULL COMMENT '最低薪资',
  `salary_max` DECIMAL(10,2) DEFAULT NULL COMMENT '最高薪资',
  `recruit_count` INT DEFAULT NULL COMMENT '招聘人数',
  `work_region_code` VARCHAR(32) DEFAULT NULL COMMENT '工作地区编码',
  `work_region_name` VARCHAR(100) DEFAULT NULL COMMENT '工作地区名称',
  `contact_person` VARCHAR(50) DEFAULT NULL COMMENT '联系人',
  `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `job_description` TEXT COMMENT '岗位描述',
  `publish_time` DATETIME DEFAULT NULL COMMENT '发布时间',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用 0停用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '修改人',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_enterprise_deleted` (`enterprise_id`, `deleted`),
  KEY `idx_job_name_deleted` (`job_name`, `deleted`),
  KEY `idx_publish_time_deleted` (`publish_time`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='招聘岗位表';
```

### 4. 政策表 `policy_article`

```sql
CREATE TABLE `policy_article` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` VARCHAR(200) NOT NULL COMMENT '政策标题',
  `issuing_organization` VARCHAR(100) DEFAULT NULL COMMENT '发文单位',
  `policy_source` VARCHAR(100) DEFAULT NULL COMMENT '政策来源',
  `summary` VARCHAR(500) DEFAULT NULL COMMENT '摘要',
  `content_html` LONGTEXT NOT NULL COMMENT '富文本正文HTML',
  `publish_time` DATETIME DEFAULT NULL COMMENT '发布时间',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0草稿 1已发布',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '修改人',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_title_deleted` (`title`, `deleted`),
  KEY `idx_status_deleted` (`status`, `deleted`),
  KEY `idx_publish_time_deleted` (`publish_time`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='政策信息表';
```

## Open Questions

- 四类青年是否最终共用一套导入模板，还是在共用基础上再扩展分类模板。
- 除已确认的 13 个 Excel 字段外，手工录入时哪些字段必须强制必填。
- 字典与区域管理第一阶段是全部开放后台维护，还是先以初始化数据为主、后台只提供查询。
- 富文本编辑器最终选用哪一个更适合当前一体化工程和部署环境。
