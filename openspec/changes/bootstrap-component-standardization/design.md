## Context

当前仓库已经完成 Bootstrap、Bootstrap Icons、Tom Select 和 TreeselectJS 的引入，并且多数后台页面已迁移到新的深色主题链路中运行。但当前实现仍保留了一层较厚的自定义 UI 壳：

- `app.css` 同时承担主题、布局、抽屉、弹窗、分页、表格和历史兼容组件实现
- `record-drawer.js` 仍通过自定义抽屉容器加载详情页和表单页
- `list-pagination.js` 仍通过自定义 `app-modal-*` 结构实现确认对话框
- 模板普遍混用 Bootstrap 类和自定义语义类，例如 `panel`、`page-hero`、`drawer-*`、`youth-toolbar`
- 已被 Tom Select 和 TreeselectJS 替代的旧多选、旧区域级联兼容样式仍遗留在 `app.css`

这次变更是一个跨模块的前端收敛工作，覆盖共享片段、共享脚本和多个后台页面模板。约束条件是保留现有深色品牌风格，不调整业务流程，不替换当前已经选定的结果插件，并继续兼容基于 Thymeleaf 的服务端渲染和局部 AJAX 刷新。

## Goals / Non-Goals

**Goals:**
- 将后台通用结构组件优先收敛为 Bootstrap 原生组件和 utility class 组合
- 保留 Tom Select 与 TreeselectJS 作为唯一的下拉增强插件，不再维持并行的旧自定义实现
- 将 `app.css` 收敛为主题层，主要负责 Bootstrap 和插件的配色、边框、阴影与状态覆盖
- 删除已被替换的旧样式、旧脚本和冗余模板包装层，减少长期维护成本
- 在不改变业务流程的前提下，保持后台页面的深色视觉一致性和交互连续性

**Non-Goals:**
- 不新增业务功能或调整后端接口
- 不替换 Tom Select、TreeselectJS 或 ECharts
- 不重做信息架构或菜单结构
- 不追求将所有自定义类完全清零；允许保留少量语义类作为模板钩子，但这些类不应继续承担通用组件结构实现

## Decisions

### 1. 共享结构组件改为 Bootstrap 语义优先

共享页面骨架将以 Bootstrap 组件语义为主：

- 导航和顶部区域优先使用 `nav`、`navbar`、`dropdown`、`breadcrumb`
- 内容容器优先使用 `card`、`card-header`、`card-body`、`row`、`col`、`stack`、spacing utilities
- 表格和分页优先使用 `table`、`table-responsive`、`pagination`
- 提示和反馈优先使用 `alert`、`btn-close`

保留少量项目类名仅用于模板标识或主题钩子，但不再通过这些类重新定义一套与 Bootstrap 平行的组件结构。

**Why:** 这样可以把视觉风格和组件语义分层，避免继续由 `app.css` 维护一整套项目私有组件系统。  
**Alternative considered:** 保留现有 `panel` / `page-hero` / `drawer-*` 语义层，仅继续删减样式。该方案会持续保留结构耦合，无法达到“Bootstrap/plugin 优先”的目标。

### 2. 列表页抽屉交互统一到 Bootstrap `offcanvas`

现有右侧抽屉将改为 Bootstrap `offcanvas offcanvas-end` 承载，仍保留当前的 AJAX 内容加载模式，但加载目标改为 `offcanvas-body` 内部容器。

**Why:** Bootstrap 已提供成熟的抽屉行为、焦点管理、键盘交互和遮罩控制，自定义抽屉样式与位移动画可以整体删除。  
**Alternative considered:** 继续保留自定义 `record-drawer`，仅改少量类名。该方案无法删除大量抽屉样式和状态脚本，也无法统一交互语义。

### 3. 删除确认交互统一到 Bootstrap `Modal`

当前 `list-pagination.js` 中动态拼接的 `app-modal-*` 结构将切换为共享的 Bootstrap `Modal`，由统一脚本驱动标题、文案、按钮文本和确认回调。

**Why:** 这能复用 Bootstrap 已有的弹窗行为并删除自定义确认弹窗 DOM/CSS，实现方式也更符合现有页面中已经使用 Bootstrap 弹窗的部分页面。  
**Alternative considered:** 继续维护自定义确认弹窗，或回退到 `window.confirm()`。前者不利于收敛，后者会破坏视觉一致性。

### 4. `app.css` 重构为“主题层 + 插件覆盖层”

`app.css` 最终仅保留以下类别：

- 全局主题 token 和 Bootstrap 变量映射
- Bootstrap 组件的深色主题覆盖
- Tom Select 的深色主题覆盖
- TreeselectJS 的深色主题覆盖
- 少量无法通过 Bootstrap utility 直接表达、但确属品牌主题的视觉修饰

以下类别将被删除或迁移到 Bootstrap 语义本身：

- 抽屉、弹窗、分页、多选、旧级联等通用组件的结构样式
- 历史兼容层，例如 `multi-dropdown-*`、`region-panel-*`
- 纯布局类样式，如果可由 `row` / `col` / spacing / flex utilities 表达

**Why:** 只有明确收紧 `app.css` 的职责，才能防止仓库再次滑回“Bootstrap 外面再包一层自定义组件系统”的状态。  
**Alternative considered:** 渐进式保留现有样式，只删除死代码。该方案能减重，但不能建立稳定边界。

### 5. 共享片段和共享脚本优先治理

实施顺序优先处理：

1. 共享片段：`sidebar`、`topbar`
2. 共享交互脚本：`record-drawer.js`、`list-pagination.js`
3. 页面模板：列表、表单、详情、系统页、分析页
4. 最后清理 `app.css` 和已废弃脚本/类名

**Why:** 共享层先收敛，页面模板才能同步删掉重复壳子和兼容类，避免返工。  
**Alternative considered:** 逐页清理。该方案会反复碰到共享样式边界不清的问题。

## Risks / Trade-offs

- [抽屉从自定义实现切到 `offcanvas` 后，桌面端宽度和页面占位方式可能变化] → 先统一共享抽屉容器，再逐页检查是否需要通过 Bootstrap 变量或少量主题覆盖保持桌面端体验
- [删除旧兼容样式可能误伤仍在使用的模板钩子] → 先完成模板替换，再基于搜索结果和页面回归删除遗留样式
- [将 `app.css` 收窄后，某些页面可能暴露对自定义结构样式的隐式依赖] → 按共享片段、共享脚本、页面模板、最终清理的顺序分层推进
- [Bootstrap 组件语义增强后，现有 AJAX 局部刷新逻辑可能需要重绑事件] → 保留现有初始化入口，但将目标容器切换到 Bootstrap 组件容器内部
- [深色主题在 Bootstrap 原生组件上可能出现视觉差异] → 允许保留少量主题覆盖，但这些覆盖只处理视觉，不重新发明结构组件

## Migration Plan

1. 先将共享抽屉和确认弹窗切换到 Bootstrap `offcanvas` / `modal`
2. 同步更新列表页和表单页模板，去掉与自定义抽屉、弹窗绑定的旧包装层
3. 收敛共享片段中的顶部栏、侧边栏和页头容器，优先使用 Bootstrap 语义和 utilities
4. 清理 `app.css` 中与旧抽屉、旧弹窗、旧多选、旧级联和纯结构布局相关的样式
5. 删除不再被模板和脚本引用的历史脚本、类名和兼容逻辑
6. 对后台核心页面进行回归验证，确认功能、插件和深色主题表现一致

如需回滚，可整体回退本次前端模板、共享脚本和 `app.css` 变更；本次变更不涉及数据库或后端接口迁移。

## Open Questions

- 桌面端抽屉是否需要保留当前“右侧大面板”的视觉宽度，还是完全采用 Bootstrap 默认 `offcanvas` 宽度后再按主题变量覆盖
- 某些自定义类是否仍需保留为稳定的 Thymeleaf 模板钩子；如果保留，需要明确这些类只承担标识职责，不承担结构样式
