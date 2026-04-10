## Why

当前后台页面已经完成 Bootstrap 化，但视觉层级仍然偏重，存在标题重复、卡片嵌套过多、留白不均、筛选区与操作区分散、图表页与列表页节奏不一致等问题。现在需要在不改变业务流程的前提下，继续把页面整理成更紧凑、规整、响应式稳定的政务驾驶舱风格，使信息密度、阅读顺序和操作效率更符合正式后台场景。

## What Changes

- 收敛后台页面的视觉层级，减少列表页、分析页、首页中重复出现的页面标题卡、说明区和嵌套卡片，让共享顶栏承担主标题表达，页面内部只保留必要的工作区标题。
- 将列表页统一整理为“页签/切换区 + 紧凑工具栏 + 表格 + 分页”的结构，压缩筛选区和按钮区的纵向空间，提升表格信息密度和对齐一致性。
- 将分析页和首页整理为规则网格的看板布局，弱化装饰性大卡片和过重阴影，让图表、地图、统计数字成为主要视觉焦点。
- 将右侧抽屉整理为更像工作台侧板的 Bootstrap `offcanvas` 体验，统一头部、正文和底部操作区的间距、对齐和响应式行为。
- 调整 `app.css` 中仍影响整体观感的主题 token，例如圆角、阴影、边框、间距、标题字号和表格密度，使整套 Bootstrap 页面更接近简洁明了的政务大屏风格。

## Capabilities

### New Capabilities
None.

### Modified Capabilities
- `admin-ui-visual-style`: 后台视觉规范将进一步约束页面层级、标题密度、工具栏布局、图表网格和抽屉工作区样式，要求整体呈现更紧凑、规整、适配大屏与常规桌面的政务驾驶舱风格。

## Impact

- Affected specs: `openspec/specs/admin-ui-visual-style/spec.md`
- Affected frontend templates: `src/main/resources/templates/fragments/*.html`, `src/main/resources/templates/dashboard/*.html`, `src/main/resources/templates/analytics/*.html`, `src/main/resources/templates/youth/*.html`, `src/main/resources/templates/job/*.html`, `src/main/resources/templates/enterprise/*.html`, `src/main/resources/templates/policy/*.html`, `src/main/resources/templates/system/*.html`, `src/main/resources/templates/auth/*.html`
- Affected frontend assets: `src/main/resources/static/css/app.css` and any shared frontend initialization that depends on toolbar, offcanvas, or responsive layout structure
- Dependencies and UI libraries: Bootstrap 5, Bootstrap Icons, Tom Select, TreeselectJS, ECharts
