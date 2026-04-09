# 实施任务清单

## Phase 1: 基础准备 ✅ 已完成

### 1.1 创建样式文件 ✅
- [x] 创建 `bootstrap-dark.css`（Bootstrap变量覆盖）
- [x] 创建 `tomselect-dark.css`（Tom Select深色主题）
- [x] 创建 `bootstrap-layout.html`（基础布局）

### 1.2 组件脚本 ✅
- [x] 创建 `region-cascader-tomselect.js`（级联选择器）
- [x] 创建 `multiselect-tomselect.js`（多选组件）

### 1.3 布局片段 ✅
- [x] 创建 `bootstrap-sidebar.html`（侧边栏）
- [x] 创建 `bootstrap-topbar.html`（顶部栏）

**实际耗时**：3小时 ✅

---

## Phase 2: 核心页面迁移 ✅ 已完成

### 2.1 Dashboard首页 ✅
- [x] `dashboard/index-bootstrap.html`（地图+统计卡片+列表）
- [x] ECharts图表深色适配
- [x] 响应式网格布局

### 2.2 青年信息模块 ✅
- [x] `youth/list-bootstrap.html`（筛选+表格+分页）
- [x] `youth/form-bootstrap.html`（表单+级联选择器）
- [x] 级联选择器集成（籍贯、学校所在地、现居住地）

### 2.3 岗位管理模块 ✅
- [x] `job/form-bootstrap.html`（表单+多选+级联）
- [x] 多选组件集成（专业要求、学历要求、学校类别、学校标签）
- [x] 工作地区级联选择器

**实际耗时**：4小时 ✅

---

## Phase 2: 公共组件

### 2.1 侧边栏重构
- [x] 使用Bootstrap Nav组件重写sidebar.html
- [x] 保持深色渐变背景
- [x] 添加Icons图标
- [x] 移动端响应式（折叠菜单）

### 2.2 顶部栏重构
- [x] 使用Bootstrap Navbar组件重写topbar.html
- [x] 面包屑导航样式
- [x] 用户菜单下拉

### 2.3 表单基础样式
- [x] 定制form-control样式（透明背景+青色边框）
- [x] 定制form-select样式
- [x] 定制button样式（渐变+发光效果）
- [x] 定制card样式（毛玻璃效果）

**预计耗时**：6小时

---

## Phase 3: TreeselectJS / Tom Select 组件集成

### 3.1 级联选择器
- [x] 编写基于 `TreeselectJS` 的单下拉区域级联
- [x] 省市区路径回填隐藏字段逻辑
- [x] 数据加载和缓存
- [x] 深色主题样式适配

### 3.2 多选下拉框
- [x] 编写 `multiselect-tomselect.js`
- [x] 标签显示和移除功能
- [x] 搜索过滤
- [x] 深色主题样式适配

### 3.3 单选美化
- [x] 普通select美化初始化
- [x] 搜索功能可选

**预计耗时**：6小时

---

## Phase 4: 页面迁移

### 4.1 Dashboard首页
- [ ] 替换整体布局
- [ ] 统计卡片样式
- [ ] ECharts图表容器适配

### 4.2 青年信息模块
- [x] list.html 列表页
  - [x] 表格样式
  - [x] 分页组件
  - [x] 筛选表单
- [x] form.html 表单页
  - [x] 表单布局（Bootstrap grid）
  - [x] 级联选择器替换（籍贯、居住地）
  - [x] 所有select美化

### 4.3 岗位管理模块
- [x] list.html 列表页
- [x] form.html 表单页
  - [x] 多选下拉框（岗位要求）
  - [x] 级联选择器（工作地区）

### 4.4 企业信息模块
- [x] list.html
- [x] form.html

### 4.5 系统管理模块
- [x] regions.html
- [x] dictionaries.html
- [x] 各form页面

### 4.6 其他页面
- [x] login.html（登录页深色主题）
- [x] detail.html（详情页）
- [x] analytics/index.html（数据分析）

**预计耗时**：12小时

---

## Phase 5: 测试与优化

### 5.1 功能测试
- [ ] 所有表单提交正常
- [ ] 级联选择数据正确
- [ ] 多选框数据绑定正常
- [ ] 搜索过滤功能正常

### 5.2 样式测试
- [ ] 深色主题各页面一致
- [ ] 按钮hover效果正常
- [ ] 表单焦点状态明显
- [ ] 卡片阴影和边框正常

### 5.3 响应式测试
- [ ] 桌面端（1920px）
- [ ] 笔记本（1366px）
- [ ] 平板（768px）
- [ ] 移动端（375px）侧边栏折叠

### 5.4 浏览器兼容性
- [ ] Chrome/Edge
- [ ] Firefox
- [ ] Safari

**预计耗时**：6小时

---

## 总计
- 预计总工作量：约34小时（4-5天）
- 建议分阶段交付，每Phase完成后可验证
