## Why

当前系统主要业务功能已经基本齐备，但页面信息架构和页面文案仍带有较强的开发演示痕迹，和政府正式展示系统的定位不一致。现在需要在不改后端能力和数据库结构的前提下，对页面入口、标题文案和说明文字做一次整体收口，让系统更聚焦“在校大学生展示”和“基础数据管理”两个实际使用场景。

## What Changes

- 收口后台页面可见入口：青年信息库页面仅展示在校大学生，数据分析页面仅展示在校大学生分析，系统设置继续保留为一级菜单，但页面内统一呈现基础数据工作台。
- 清理全站不适合正式场景的开发提示型文案、设计说明型文案和示例占位文字，包括顶部标题区、首页、分析页、表单页、系统页和登录页。
- 统一侧边导航和页内 tab 的呈现口径，避免同一模块同时出现侧边子菜单和页内 tab 的重复导航。
- 保持现有后端路由、数据模型、服务接口和数据库结构不变，仅调整页面模板、样式和前端展示文案。

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `admin-ui-visual-style`: 调整后台统一页面壳子的文案和导航呈现，要求全站页面使用正式业务化文案，不展示开发状态说明、设计说明和演示占位词。
- `dashboard-and-analytics`: 调整首页和数据分析页面的可见入口与展示文案，数据分析界面仅暴露在校大学生分析视图，不在页面层展示其他青年分类入口。
- `youth-information-management`: 调整青年信息库页面的可见分类和标题文案，界面仅展示在校大学生视图，不在页面层展示其他青年分类入口。
- `dictionary-and-region-management`: 调整系统设置的页面呈现，系统设置保留一级菜单，但界面统一收口到基础数据工作台，不再把区域管理作为独立侧边子入口强调。
- `admin-auth`: 调整登录页展示文案，移除默认账号等演示性提示信息，保持后台登录页为正式系统入口。

## Impact

- Affected code:
  - `src/main/resources/templates/fragments/*.html`
  - `src/main/resources/templates/dashboard/*.html`
  - `src/main/resources/templates/analytics/*.html`
  - `src/main/resources/templates/youth/*.html`
  - `src/main/resources/templates/system/*.html`
  - `src/main/resources/templates/auth/*.html`
  - `src/main/resources/templates/enterprise/*.html`
  - `src/main/resources/templates/job/*.html`
  - `src/main/resources/templates/policy/*.html`
  - `src/main/resources/static/css/app.css`
- APIs:
  - 不新增、不修改后端接口，仅复用现有页面路由和数据模型
- Dependencies:
  - 继续使用现有 Thymeleaf、CSS 和页面内 JavaScript
- Systems:
  - 影响后台管理端页面可见结构和展示文案，不改变数据库和后端业务能力
