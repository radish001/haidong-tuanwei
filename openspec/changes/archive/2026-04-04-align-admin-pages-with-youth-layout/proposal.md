## Why

当前后台各模块的列表页、操作区、删除确认和详情/编辑交互方式不一致，管理员在青年信息、企业招聘、政策等模块之间切换时需要重新适应不同布局和操作路径。现在已经以青年信息库为目标样式完成了一轮页面优化，需要将同样的布局和交互规范推广到其他管理页面，形成统一的后台使用体验。

## What Changes

- 将企业信息、招聘信息、就业创业政策等列表页统一为与青年信息库一致的页面结构：顶部查询区，下方右侧操作区，列表区与分页区保持一致的视觉层级。
- 统一各列表页的分页、每页条数、页码选择、局部刷新查询体验，避免不同模块使用不同的交互模式。
- 将详情、编辑、新增等管理操作统一为右侧抽屉式面板，而不是跳转到独立页面。
- 将所有删除操作统一为平台风格的确认模态框，不再使用浏览器原生确认框。
- 调整后台导航文案与分组，使企业与招聘相关页面在侧边栏中保持统一命名和入口表现。

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `youth-information-management`: 调整青年信息管理页面的布局基线和抽屉交互，作为其他后台列表页对齐的统一标准。
- `enterprise-management`: 将企业列表、操作区和编辑流程调整为与青年信息库一致的布局和右侧抽屉交互。
- `recruitment-management`: 将招聘列表、操作区和编辑流程调整为与青年信息库一致的布局和右侧抽屉交互。
- `policy-management`: 将政策列表、删除确认、详情与编辑流程调整为与青年信息库一致的布局和右侧抽屉交互。

## Impact

- 影响后台模板：`src/main/resources/templates/youth/`、`enterprise/`、`job/`、`policy/`、`fragments/sidebar.html`
- 影响前端样式与交互脚本：`src/main/resources/static/css/app.css`、相关列表/抽屉脚本
- 影响控制器返回模式与局部渲染逻辑：`YouthController`、`EnterpriseController`、`JobController`、`PolicyController`
- 不涉及新的外部依赖，不改变现有数据模型和核心 CRUD 接口语义
