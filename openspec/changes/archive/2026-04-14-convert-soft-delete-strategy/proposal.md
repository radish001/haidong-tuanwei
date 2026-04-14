## Why

当前工程同时存在软删除、物理删除和“有 `deleted` 字段但缺少统一删除语义”的混合状态，导致主数据与关系表在重复创建、重复关联和唯一键约束下频繁出现历史脏数据冲突。现在需要统一删除策略，只保留青年信息、企业信息、招聘信息和政策信息的软删除能力，其余表改为物理删除，以降低维护复杂度并消除非业务数据的删除冲突。

## What Changes

- 统一系统删除策略，仅保留 `youth_info`、`enterprise_info`、`job_post`、`policy_article` 为软删除。
- 将主数据、配置数据、权限配置和关系表统一调整为物理删除，不再依赖 `deleted = 0` 过滤。
- 调整相关查询、删除接口、级联删除和引用校验逻辑，确保删除后不会残留占用唯一键的历史记录。
- 明确招聘岗位继续保留软删除，但其关联关系表维持物理删除策略，并补足删除时的关系清理语义。
- 更新测试与测试数据，覆盖软删除保留范围和物理删除范围下的关键场景。

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `youth-information-management`: 明确青年信息删除继续保留软删除语义。
- `enterprise-management`: 明确企业信息删除继续保留软删除语义。
- `recruitment-management`: 明确招聘岗位删除继续保留软删除语义，且关联条件关系保持物理删除。
- `policy-management`: 明确政策信息删除继续保留软删除语义。
- `dictionary-and-region-management`: 将字典项与区域数据删除改为物理删除，并去除软删除过滤依赖。
- `education-and-enterprise-master-data`: 将学校、学校标签、学校类别相关主数据删除改为物理删除。
- `admin-auth`: 将用户、角色、菜单及其关系数据的删除语义统一为物理删除。

## Impact

- 受影响代码主要位于 `src/main/resources/schema.sql`、`src/main/resources/mapper/**`、`src/main/java/**` 和 `src/test/**`。
- 会影响系统管理、认证授权、青年信息、企业信息、招聘信息、政策信息、分析查询等依赖 `deleted` 过滤的查询链路。
- 需要同步清理或重构与唯一键、关系表删除、引用校验和历史测试断言相关的逻辑。
