## Why

当前专业类别分析图不分学历层次，本科和专科学生的专业类别混在一起展示。业务需要分别查看本科专业类别分布和专科专业类别分布，以支持针对性的就业服务决策。同时，研究生专业类别也需要在维护层面预留支持。

## What Changes

- **数据库**：在 `sys_dict_item` 表新增 `education_scopes` 字段（varchar），用于存储专业类别所属的学历层次（可多选）
- **系统设置**：专业类别管理增加"所属学历层次"多选框，选项固定为：专科专业、本科专业、研究生专业，新增/编辑时必选至少一项
- **数据分析页**：
  - 原"专业类别分布"改为"本科专业类别分布"，只统计所属学历层次包含"本科专业"的类别
  - 新增"专科专业类别分布"图表，统计所属学历层次包含"专科专业"的类别
- **查询逻辑**：数据分析按专业类别绑定的学历层次过滤，同时结合学生的实际学历字段交叉验证

## Capabilities

### New Capabilities
- `major-category-education-scope`: 专业类别支持按学历层次分类，包括维护层面的多选配置和数据分析层面的分层统计

### Modified Capabilities
- `dictionary-and-region-management`: 专业类别维护增加"所属学历层次"字段，该字段只对 `major_category` 类型字典项生效
- `dashboard-and-analytics`: 专业类别分布分析改为分学历层次展示，增加本科和专科两张独立图表

## Impact

- **数据库表**：`sys_dict_item` 结构变更，需要执行 DDL 迁移
- **后端服务**：
  - `DictionaryService` 处理 `major_category` 时需额外处理 `education_scopes` 字段
  - `YouthAnalyticsService` 新增本科/专科专业类别分布统计方法
  - `AnalyticsController` 返回新的图表数据
- **前端页面**：
  - `system/dictionaries.html` 专业类别列表增加"所属学历层次"列
  - `system/dictionary-item-form.html` 专业类别表单增加多选框
  - `analytics/index.html` 拆分专业类别分布为两张图
- **SQL Mapper**：`YouthAnalyticsDao.xml` 统计 SQL 增加学历层次过滤条件
- **历史数据**：现有14个专业类别需要补充 `education_scopes` 值（建议默认设为"本科专业"以保持现有分析口径一致）
