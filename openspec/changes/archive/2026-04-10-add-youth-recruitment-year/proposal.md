## Why

当前青年信息库缺少“招考年份”字段，管理员无法在录入、查看或批量导入青年信息时维护这一业务属性，导致相关台账信息只能分散在备注或线下表格中。现在补齐该字段，可以统一青年信息数据口径，并为后续查询、统计或台账扩展保留基础。

## What Changes

- 在统一青年信息模型中新增“招考年份”字段，保存 4 位年份值。
- 更新青年信息新增、编辑、详情等前后端链路，支持维护和展示招考年份。
- 更新青年信息数据访问层和数据库结构，使该字段可被持久化并兼容已有环境。
- 更新青年信息 Excel 导入模板、导入解析和导出列结构，使批量处理与页面维护保持一致。
- 同步调整测试数据与相关自动化测试，覆盖新增字段的保存与导入导出行为。

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `youth-information-management`: 青年信息维护能力需要支持录入、保存和展示招考年份字段。
- `youth-information-import-export`: 青年信息 Excel 模板、导入和导出需要支持招考年份列。

## Impact

- Affected code: `youth` 模块实体、表单 DTO、控制器映射、服务实现、MyBatis XML、青年信息表单/详情/列表模板。
- Affected data: `youth_info` 表结构、初始化数据、H2 测试结构。
- Affected integrations: 青年信息 Excel 模板下载、Excel 导入校验与 Excel 导出列顺序。
- Testing: 需要更新青年信息导入导出与字段映射相关测试。
