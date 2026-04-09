# Bootstrap 深色主题迁移指南

## 概述

已完成Bootstrap + Tom Select深色主题的实施，新文件均带有 `-bootstrap` 后缀，原有文件保持不变。

## 新文件清单

### 1. 样式文件
- `src/main/resources/static/css/bootstrap-dark.css` - Bootstrap深色主题定制
- `src/main/resources/static/css/tomselect-dark.css` - Tom Select深色适配

### 2. 组件脚本
- `src/main/resources/static/js/region-cascader-tomselect.js` - 级联选择器（省市区联动）
- `src/main/resources/static/js/multiselect-tomselect.js` - 多选下拉框组件

### 3. 布局模板
- `src/main/resources/templates/fragments/bootstrap-layout.html` - 基础布局（引入所有CDN资源）
- `src/main/resources/templates/fragments/bootstrap-sidebar.html` - 侧边栏
- `src/main/resources/templates/fragments/bootstrap-topbar.html` - 顶部栏

### 4. 页面模板（新）
- `src/main/resources/templates/dashboard/index-bootstrap.html` - 首页
- `src/main/resources/templates/youth/list-bootstrap.html` - 青年列表
- `src/main/resources/templates/youth/form-bootstrap.html` - 青年表单
- `src/main/resources/templates/job/form-bootstrap.html` - 岗位表单

## 切换方式

### 方式一：Controller修改（推荐）

修改Controller返回新的模板名：

```java
// 原代码
return "youth/list";

// 临时测试新样式
return "youth/list-bootstrap";
```

### 方式二：文件替换（正式上线）

1. 备份原文件
2. 删除原文件
3. 将新文件重命名（去掉 `-bootstrap` 后缀）

```bash
# 备份
mv youth/list.html youth/list-backup.html
mv youth/form.html youth/form-backup.html

# 启用新文件
mv youth/list-bootstrap.html youth/list.html
mv youth/form-bootstrap.html youth/form.html
```

## CDN资源（已通过模板自动引入）

```html
<!-- Bootstrap 5.3 CSS -->
https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css

<!-- Bootstrap Icons -->
https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.2/font/bootstrap-icons.css

<!-- Tom Select CSS -->
https://cdn.jsdelivr.net/npm/tom-select@2.3.1/dist/css/tomselect.bootstrap5.css

<!-- Bootstrap JS -->
https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js

<!-- Tom Select JS -->
https://cdn.jsdelivr.net/npm/tom-select@2.3.1/dist/js/tom-select.complete.min.js
```

## 使用示例

### 基础下拉框（单选）
```html
<select class="form-select tomselect-basic">
    <option value="">请选择</option>
    <option value="1">选项1</option>
</select>
```

### 可搜索下拉框（单选）
```html
<select class="form-select tomselect-searchable">
    <option value="">请选择</option>
    <option value="1">选项1</option>
</select>
```

### 级联选择器（省市区）
```html
<div data-region-cascader data-region-api-url="/api/regions" data-region-placeholder="请选择">
    <input type="hidden" name="provinceCode" data-region-province>
    <input type="hidden" name="cityCode" data-region-city>
    <input type="hidden" name="countyCode" data-region-county>
</div>
```

### 多选下拉框
```html
<select name="tags" multiple data-multiselect data-placeholder="请选择标签">
    <option value="1">标签1</option>
    <option value="2">标签2</option>
</select>
```

### 初始化脚本
```javascript
// 基础下拉框
new TomSelect('.tomselect-basic', {
    placeholder: '请选择',
    allowEmptyOption: true,
    maxItems: 1
});

// 可搜索下拉框
new TomSelect('.tomselect-searchable', {
    placeholder: '请选择或搜索',
    plugins: ['dropdown_input']
});

// 多选下拉框（自动通过 multiselect-tomselect.js 初始化）
```

## 配色方案

| 用途 | 颜色值 |
|------|-------|
| 主背景 | `#061437` |
| 主文字 | `#f1f7ff` |
| 品牌色/主色 | `#32d6ff` |
| 次级文字 | `#8ea6cf` |
| 边框 | `rgba(69, 145, 255, 0.2)` |
| 成功色 | `#22c55e` |
| 危险色 | `#ff6b81` |
| 警告色 | `#fbbf24` |

## 与原有样式对比

| 特性 | 原有app.css | Bootstrap新样式 |
|------|------------|----------------|
| 响应式 | 手动实现 | Bootstrap原生网格 |
| 表单控件 | 原生HTML | Bootstrap美化+Tom Select |
| 级联选择 | 自定义面板 | Tom Select三级联动 |
| 多选下拉 | 自定义实现 | Tom Select标签多选 |
| 侧边栏 | 固定256px | Bootstrap Offcanvas+桌面固定 |
| 图标 | 无 | Bootstrap Icons |
| 可访问性 | 一般 | Bootstrap标准WCAG |

## 待迁移页面

以下页面还需要迁移：
- [ ] `auth/login.html` - 登录页
- [ ] `enterprise/list.html` - 企业列表
- [ ] `enterprise/form.html` - 企业表单
- [ ] `policy/list.html` - 政策列表
- [ ] `policy/form.html` - 政策表单
- [ ] `policy/detail.html` - 政策详情
- [ ] `system/dictionaries.html` - 字典管理
- [ ] `system/regions.html` - 地区管理
- [ ] 其他system/下的页面
- [ ] `analytics/index.html` - 数据分析
- [ ] `job/list.html` - 岗位列表
- [ ] `job/detail.html` - 岗位详情
- [ ] `youth/detail.html` - 青年详情

## 注意事项

1. **级联选择器**：新组件使用 `data-region-cascader` 属性，原组件使用 `data-region-step`，两者不兼容
2. **多选组件**：新组件使用 `data-multiselect` 属性，原组件使用 `data-multi-select-root`
3. **ECharts图表**：深色主题适配已在dashboard页面中完成
4. **移动端**：新布局已添加响应式支持，侧边栏在小屏幕自动折叠

## 快速预览

临时预览新样式效果，在浏览器访问（假设Controller已修改）：
- http://localhost:8080/dashboard (index-bootstrap)
- http://localhost:8080/youth/college (list-bootstrap)
- http://localhost:8080/youth/create (form-bootstrap)
- http://localhost:8080/jobs/create (form-bootstrap)
