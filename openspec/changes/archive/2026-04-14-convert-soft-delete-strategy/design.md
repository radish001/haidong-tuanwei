## Context

当前工程的删除语义处于混合状态：`youth_info`、`enterprise_info`、`job_post`、`policy_article` 等业务表使用软删除；部分关系表已经是物理删除；部分主数据和关系表虽然带有 `deleted` 字段，但又受唯一键约束和历史数据残留影响，导致删除后重新创建或重新关联时出现数据库冲突。现有实现还在查询层大量依赖 `deleted = 0`，因此删除策略不是局部 SQL 替换，而是跨 schema、mapper、service、测试的系统性改造。

本变更明确只保留四张核心业务表的软删除能力：

- `youth_info`
- `enterprise_info`
- `job_post`
- `policy_article`

其余主数据、配置表、权限配置表和关系表统一改为物理删除。`docker/sql` 不在本次改造范围内，仅调整应用主线 `src/main/resources/schema.sql`、`src/main/resources/mapper/**`、`src/main/java/**` 和 `src/test/**`。

## Goals / Non-Goals

**Goals:**

- 统一删除策略，避免“软删除 + 唯一键 + 重新创建/关联”导致的重复键冲突。
- 保留核心业务表的可恢复性，同时简化主数据、配置数据和关系表的维护语义。
- 去除非业务表对 `deleted = 0` 的长期依赖，减少查询和删除逻辑分叉。
- 为后续实现阶段提供清晰的分层规则和迁移顺序。

**Non-Goals:**

- 不处理 `docker/sql`、独立 Docker 初始化脚本和部署包内 SQL 的同步改造。
- 不引入额外的归档表、审计表或恢复工作流。
- 不改变现有页面信息架构、表单布局或与删除无关的业务规则。
- 不在本设计中讨论将来是否为软删除业务表增加恢复接口。

## Decisions

### Decision: 仅保留四张业务内容表的软删除

保留 `youth_info`、`enterprise_info`、`job_post`、`policy_article` 的 `deleted` 字段、软删除接口和 `deleted = 0` 查询过滤。

原因：

- 这四张表承载核心业务内容，保留软删除有误删缓冲和业务留痕价值。
- 它们当前不存在与主数据相同级别的唯一键重建冲突，保留软删除的技术成本相对可接受。

备选方案：

- 全量改为物理删除：实现更简单，但会丢失所有业务内容表的恢复空间。
- 全量保留软删除：继续保留当前复杂度和重复键冲突风险，不满足本次目标。

### Decision: 主数据、配置表、权限表和关系表统一改为物理删除

以下类型统一采用物理删除：

- 主数据 / 配置表：如 `sys_dict_item`、`sys_region`、`sys_major_catalog`、`sys_school`、`sys_school_tag`、`sys_analytics_school_tag`
- 权限配置表：如 `sys_user`、`sys_role`、`sys_menu`
- 关系表：如 `sys_user_role`、`sys_role_menu`、`sys_school_tag_rel`，以及已是物理删除的 `job_post_*_rel`

原因：

- 这些表更强调当前配置状态，而不是历史留痕。
- 唯一键约束在这些表上非常常见，物理删除能直接消除软删历史记录占位问题。
- 关系表采用物理删除符合“删旧关系、插新关系”的天然用法。

备选方案：

- 保留软删除并新增“恢复已删除记录”逻辑：可以实现，但要在多张表、多个 DAO/Service 中维护额外分支，复杂度更高。
- 删除唯一索引：会放大脏数据问题，不接受。

### Decision: 业务软删除表引用的关系表仍按物理删除处理

`job_post` 保持软删除，但 `job_post_major_rel`、`job_post_education_rel`、`job_post_school_category_rel`、`job_post_school_tag_rel` 仍采用物理删除。

原因：

- 这些关系表当前已经是物理删除，继续保持一致可减少改造面。
- 删除岗位时同步清理关系表，避免残留孤儿关系。

取舍：

- 被软删除的岗位记录保留主体信息，但不保证继续保留全部已删除关系行用于后续恢复。
- 这是本方案刻意接受的折中，用于换取删除策略整体简化。

### Decision: schema 与 mapper 同步去除非保留表的软删除语义

对改为物理删除的表，不仅删除接口改为 `DELETE`，还要同步：

- 从 `schema.sql` 去掉 `deleted` 字段及相关索引
- 从所有查询和 join 中移除 `deleted = 0`
- 删除或改名 `softDelete` / `softDeleteBatch` 相关 DAO 与 service 方法

原因：

- 如果只改删除 SQL 而不清理 schema 与查询语义，会长期保留无效字段和错误心智。
- 统一后更容易让测试和后续开发遵循单一规则。

## Risks / Trade-offs

- [关系表与业务表策略不一致] `job_post` 保留软删除，但其关系表物理删除后会失去完整恢复能力 → 在设计中显式接受，并在删除实现中确保关系清理完整。
- [权限表物理删除风险] 用户、角色、菜单一旦删除将彻底消失 → 在实现前确认当前系统是否存在真实的删除入口和恢复诉求。
- [查询改动面大] 非业务表相关 mapper 广泛依赖 `deleted = 0` → 通过按表分类梳理影响面，并以全量测试回归作为收口手段。
- [测试基线变化] 现有测试部分刻画的是软删除现状 → 需要把“现状刻画型”测试逐步转为“新策略回归测试”。
- [历史数据兼容] 线上已有 `deleted = 1` 的非业务表历史记录需要清理或迁移 → 实施时增加一次性数据清理步骤。
