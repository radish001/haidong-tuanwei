## Why

当前后台已经具备主要业务功能，但页面整体视觉仍停留在通用管理后台样式，和目标设计稿在布局层次、驾驶舱氛围、列表编排和表单呈现上存在明显差距。现在需要先把页面整体风格拉齐，在不改后台接口的前提下提升系统观感和使用一致性，为后续细化页面体验打基础。

## What Changes

- 统一后台全局视觉语言，包括侧边导航、顶部标题区、卡片面板、按钮、输入框、表格和提示信息的深色驾驶舱风格。
- 重构首页、青年信息、数据分析、企业信息、招聘信息、政策管理等核心页面的布局与样式，使其更贴近目标设计稿。
- 将新增、编辑、详情等表单页调整为更接近右侧抽屉/侧滑面板的视觉结构，但保持现有页面路由和提交接口不变。
- 重写分析页图表布局和 ECharts 配置，在现有数据接口不变的前提下改为更接近目标稿的混合图表表现。
- 补充统一的前端样式约束，确保各模块页面在视觉层级、间距、配色和交互反馈上保持一致。

## Capabilities

### New Capabilities
- `admin-ui-visual-style`: 定义后台统一的驾驶舱风格页面壳子、视觉组件和跨页面样式约束

### Modified Capabilities
- `dashboard-and-analytics`: 调整首页驾驶舱和分类分析页的页面结构、图表布局和视觉表现要求
- `youth-information-management`: 调整青年信息列表、分类标签和表单页面的呈现方式，使其更贴近目标设计稿
- `enterprise-management`: 调整企业信息列表和表单页面的布局与视觉风格
- `recruitment-management`: 调整招聘信息列表和表单页面的布局与视觉风格
- `policy-management`: 调整政策列表、详情和编辑页面的布局与视觉风格

## Impact

- Affected code:
  - `src/main/resources/templates/fragments/*.html`
  - `src/main/resources/templates/dashboard/*.html`
  - `src/main/resources/templates/analytics/*.html`
  - `src/main/resources/templates/youth/*.html`
  - `src/main/resources/templates/enterprise/*.html`
  - `src/main/resources/templates/job/*.html`
  - `src/main/resources/templates/policy/*.html`
  - `src/main/resources/static/css/app.css`
- APIs:
  - 不新增、不修改后台接口，继续复用现有页面路由和模型数据
- Dependencies:
  - 继续使用现有 Thymeleaf 和 ECharts，不引入必须依赖的新后端组件
- Systems:
  - 影响后台管理端整体 UI 呈现，但不改变业务存储和服务接口
