## Why

当前后台页面虽然已经接入 Bootstrap、Tom Select 和 TreeselectJS，但仍保留了大量自定义结构样式、兼容脚本和历史组件实现，导致 `app.css` 既承担主题配色，又承担布局、抽屉、弹窗和表单组件实现，偏离了“Bootstrap + 插件优先”的目标。现在需要继续收敛这层遗留实现，降低维护成本，并为后续页面迭代提供稳定、统一的组件基础。

## What Changes

- 将后台剩余的自定义结构组件收敛为 Bootstrap 原生组件和 utility class 组合，优先使用 `card`、`navbar`、`nav`、`table`、`modal`、`offcanvas`、`pagination` 等现成能力。
- 将列表页右侧抽屉、确认弹窗、页面头部/面板壳子等仍依赖自定义 DOM 结构和脚本的部分切换为 Bootstrap 组件实现。
- 删除已经被 Bootstrap、Tom Select、TreeselectJS 替代的旧样式、兼容样式和历史脚本，包括不再被模板使用的旧多选、旧区域级联和历史参考实现。
- 将 `app.css` 收敛为主题层样式文件，仅保留 Bootstrap 与插件的配色、边框、阴影和必要视觉 token 覆盖，不再承载通用布局和组件结构实现。
- 对现有后台模板进行一致性清理，移除仅为兼容旧样式体系而保留的自定义类名和冗余包装层。

## Capabilities

### New Capabilities
None.

### Modified Capabilities
- `admin-ui-visual-style`: 后台视觉规范将进一步收敛为 Bootstrap 和已选插件优先，要求通用结构组件使用 Bootstrap 语义与行为实现，`app.css` 仅承担主题和插件配色覆盖。

## Impact

- Affected specs: `openspec/specs/admin-ui-visual-style/spec.md`
- Affected frontend templates: `src/main/resources/templates/fragments/*.html`, `src/main/resources/templates/dashboard/*.html`, `src/main/resources/templates/youth/*.html`, `src/main/resources/templates/job/*.html`, `src/main/resources/templates/enterprise/*.html`, `src/main/resources/templates/policy/*.html`, `src/main/resources/templates/system/*.html`, `src/main/resources/templates/auth/*.html`
- Affected frontend assets: `src/main/resources/static/css/app.css`, `src/main/resources/static/js/record-drawer.js`, `src/main/resources/static/js/list-pagination.js`, `src/main/resources/static/js/bootstrap-enhancements.js`, and related Bootstrap/plugin initialization scripts
- Dependencies and UI libraries: Bootstrap 5, Bootstrap Icons, Tom Select, TreeselectJS
