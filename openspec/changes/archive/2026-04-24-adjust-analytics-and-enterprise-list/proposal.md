## Why

当前在校大学生分析页中的招聘信息专业分析展示前十五专业并聚合“其他”，与当前业务希望聚焦头部专业、减少图例噪音的诉求不一致。与此同时，企业信息列表页仍展示企业规模且将企业性质排在行业之后，不符合当前页面信息优先级要求，需要同步调整分析页和企业列表页的展示规则。

## What Changes

- 将在校大学生分析页中的“招聘信息专业分析”调整为仅按岗位数量降序展示前 10 个专业，不再展示“其他”聚合项。
- 保持招聘信息专业分析对具体专业扇区的点击跳转能力，但不再保留“其他”聚合项相关的展示和交互语义。
- 调整企业信息列表页展示列，移除“企业规模”列。
- 调整企业信息列表页现有业务字段顺序，将“企业性质”排在“行业”前面。
- 同步更新相关规格与测试断言，使页面展示要求与实现保持一致。

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `dashboard-and-analytics`: 招聘信息专业分析的展示范围从前十五专业加“其他”调整为仅前十专业，且移除“其他”展示语义。
- `enterprise-management`: 企业信息列表页的业务列展示规则调整为不显示企业规模，并将企业性质排在行业前面。

## Impact

- Affected specs: `openspec/specs/dashboard-and-analytics/spec.md`, `openspec/specs/enterprise-management/spec.md`
- Affected templates: `src/main/resources/templates/analytics/index.html`, `src/main/resources/templates/enterprise/list.html`
- Affected tests: analytics and enterprise page integration assertions related to chart configuration and table column order
