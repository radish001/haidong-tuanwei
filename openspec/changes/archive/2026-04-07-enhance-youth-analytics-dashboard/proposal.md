## Why

当前在校大学生分析看板只展示年龄、学历、性别、民族、政治面貌和创业需求等基础维度，无法覆盖用户已经明确的院校层次、专业类别和海东籍顶尖高校标签分析需求。现在需要把分析页升级为更贴近大屏驾驶舱的结构，并让图表口径与现有学校主数据、学校标签和青年信息字段保持一致。

## What Changes

- 调整在校大学生分析看板的图表结构，按已确认的图表类型重新组织布局。
- 新增院校层次分布分析，按学校类别字典统计在校大学生人数和占比，并以环状图展示。
- 新增专业类别分布分析，按 14 大类专业类别统计人数和占比，并以竖向柱状图展示。
- 保留并重构民族分类占比、学历层次分布、性别分布，使图表类型分别匹配饼图、横向柱状图和左右对比条形图。
- 新增海东籍顶尖高校专题分析，按学校标签动态生成环状图，图名直接使用标签名。
- 调整分析页说明文案和数据输出结构，使基础画像与海东籍标签专题分析分区展示。

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `dashboard-and-analytics`: 在校大学生分析看板的统计维度、图表类型、院校层次口径和海东籍顶尖高校标签分析要求将被扩展和重定义。

## Impact

- Affected code:
  - `src/main/java/com/haidong/tuanwei/analytics/controller/AnalyticsController.java`
  - `src/main/java/com/haidong/tuanwei/analytics/service/impl/YouthAnalyticsServiceImpl.java`
  - `src/main/java/com/haidong/tuanwei/analytics/dto/YouthAnalyticsView.java`
  - `src/main/resources/mapper/analytics/YouthAnalyticsDao.xml`
  - `src/main/resources/templates/analytics/index.html`
- Data and dependencies:
  - 继续复用 `youth_info` 中的性别、民族、学历、专业类别、籍贯与院校字段
  - 继续复用学校主数据中的学校类别与学校标签，不新增新的院校分层口径
- Systems:
  - 在校大学生分析页将从通用基础分布页升级为包含基础画像与海东籍顶尖高校专题分析的看板页
