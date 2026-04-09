# 设计文档：Bootstrap深色主题

## 1. 主题架构

```
┌─────────────────────────────────────────────────────────┐
│                    样式架构                              │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  Bootstrap 5.3 CDN                                      │
│       ↓                                                 │
│  自定义变量覆盖 (_variables.scss)                       │
│       ↓                                                 │
│  深色主题定制样式 (custom-dark.css)                     │
│       ↓                                                 │
│  组件级样式调整 (components.css)                       │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

## 2. 核心变量映射

```scss
// 主色调
$primary: #32d6ff;
$secondary: #8ea6cf;
$success: #22c55e;
$danger: #ff6b81;
$warning: #fbbf24;
$info: #1497ff;

// 背景色
$body-bg: #061437;
$body-color: #f1f7ff;

// 卡片/面板
$card-bg: rgba(7, 20, 54, 0.88);
$card-border-color: rgba(69, 145, 255, 0.2);

// 表单
$input-bg: rgba(8, 24, 63, 0.8);
$input-color: #f1f7ff;
$input-border-color: rgba(69, 145, 255, 0.2);
$input-focus-border-color: rgba(58, 216, 255, 0.5);
$input-placeholder-color: #6280b1;

// 侧边栏
$sidebar-bg: linear-gradient(180deg, rgba(5, 18, 47, 0.98), rgba(3, 12, 32, 0.98));
$sidebar-border: rgba(70, 132, 222, 0.22);

// 表格
$table-bg: transparent;
$table-color: #f1f7ff;
$table-border-color: rgba(69, 145, 255, 0.15);
$table-hover-bg: rgba(50, 214, 255, 0.08);
```

## 3. 组件设计

### 3.1 侧边栏
```html
<aside class="sidebar d-flex flex-column vh-100 position-fixed">
  <div class="brand d-flex align-items-center p-3">
    <div class="brand-mark">HD</div>
    <div class="brand-text">海东市联络服务平台</div>
  </div>
  <nav class="nav flex-column flex-grow-1">
    <a class="nav-link active" href="/dashboard">
      <i class="bi bi-house-door"></i> 首页
    </a>
    <a class="nav-link" href="/youth/college">
      <i class="bi bi-people"></i> 青年信息库
    </a>
    <!-- ... -->
  </nav>
</aside>
```

### 3.2 表单布局
```html
<div class="container-fluid py-4">
  <div class="row">
    <div class="col-12 col-lg-8">
      <div class="card border-primary-subtle">
        <div class="card-header">青年信息</div>
        <div class="card-body">
          <form>
            <div class="row g-3">
              <div class="col-md-6">
                <label class="form-label">姓名</label>
                <input type="text" class="form-control">
              </div>
              <div class="col-md-6">
                <label class="form-label">性别</label>
                <select class="form-select tomselect">
                  <option>请选择</option>
                </select>
              </div>
            </div>
            <!-- 级联选择 -->
            <div class="row g-3 mt-2">
              <div class="col-md-12">
                <label class="form-label">籍贯</label>
                <div class="row g-2">
                  <div class="col-4">
                    <select id="province" class="form-select tomselect-cascade"></select>
                  </div>
                  <div class="col-4">
                    <select id="city" class="form-select tomselect-cascade"></select>
                  </div>
                  <div class="col-4">
                    <select id="county" class="form-select tomselect-cascade"></select>
                  </div>
                </div>
              </div>
            </div>
            <!-- 多选 -->
            <div class="row g-3 mt-2">
              <div class="col-md-12">
                <label class="form-label">岗位要求</label>
                <select id="requirements" class="form-select tomselect-multiple" multiple></select>
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</div>
```

### 3.3 TreeselectJS + Tom Select 配置
```javascript
// 单选下拉（美化原生select）
new TomSelect('.tomselect', {
  plugins: ['dropdown_input'],
  placeholder: '请选择'
});

// 单下拉区域级联
const treeselect = new Treeselect({
  parentHtmlContainer: document.querySelector('.region-treeselect'),
  options: regionTreeOptions,
  isSingleSelect: true,
  searchable: true,
  clearable: true,
  showTags: false,
  placeholder: '请选择地区'
});

treeselect.srcElement.addEventListener('input', (event) => {
  const selectedCode = Array.isArray(event.detail) ? event.detail[0] : event.detail;
  syncProvinceCityCountyHiddenInputs(selectedCode);
});

// 多选
new TomSelect('#requirements', {
  plugins: ['remove_button', 'checkbox_options'],
  maxItems: null,
  placeholder: '请选择岗位要求'
});
```

## 4. TreeselectJS / Tom Select 深色主题适配

```css
/* TreeselectJS 深色主题覆盖 */
body {
  --treeselectjs-border-color: rgba(69, 145, 255, 0.2);
  --treeselectjs-bg: rgba(8, 24, 63, 0.8);
  --treeselectjs-border-focus: rgba(58, 216, 255, 0.5);
  --treeselectjs-item-focus-bg: rgba(50, 214, 255, 0.15);
  --treeselectjs-item-selected-bg: rgba(50, 214, 255, 0.2);
}

/* Tom Select 深色主题覆盖 */
.ts-wrapper .ts-control {
  background: rgba(8, 24, 63, 0.8) !important;
  border-color: rgba(69, 145, 255, 0.2) !important;
  color: #f1f7ff !important;
  border-radius: 0.375rem;
}

.ts-wrapper.focus .ts-control {
  border-color: rgba(58, 216, 255, 0.5) !important;
  box-shadow: 0 0 0 0.25rem rgba(50, 214, 255, 0.15) !important;
}

.ts-dropdown {
  background: rgba(7, 20, 54, 0.95) !important;
  border: 1px solid rgba(58, 216, 255, 0.36) !important;
  color: #f1f7ff !important;
  backdrop-filter: blur(10px);
}

.ts-dropdown .option {
  color: #f1f7ff;
}

.ts-dropdown .active {
  background: rgba(50, 214, 255, 0.15) !important;
  color: #32d6ff !important;
}

.ts-dropdown .selected {
  background: rgba(50, 214, 255, 0.2) !important;
}

/* 多选标签 */
.ts-wrapper.multi .ts-control > div {
  background: rgba(50, 214, 255, 0.2) !important;
  border: 1px solid rgba(50, 214, 255, 0.4) !important;
  color: #32d6ff !important;
  border-radius: 0.25rem;
}

.ts-wrapper.multi .ts-control > div .remove {
  color: #ff6b81 !important;
  border-left: 1px solid rgba(50, 214, 255, 0.3) !important;
}

/* 占位符 */
.ts-wrapper .ts-control input::placeholder {
  color: #6280b1 !important;
}
```

## 5. 页面布局结构

```
┌──────────────────────────────────────────┐
│ body (bg-dark #061437)                   │
│  ┌────────┬─────────────────────────┐  │
│  │        │                         │  │
│  │ Sidebar│   Main Content           │  │
│  │ (256px)│   ┌─────────────────┐   │  │
│  │ fixed  │   │ Topbar          │   │  │
│  │        │   ├─────────────────┤   │  │
│  │        │   │ Page Hero       │   │  │
│  │        │   ├─────────────────┤   │  │
│  │        │   │ Card/Panel      │   │  │
│  │        │   │ ┌─────────────┐ │   │  │
│  │        │   │ │ Form        │ │   │  │
│  │        │   │ │             │ │   │  │
│  │        │   │ └─────────────┘ │   │  │
│  │        │   └─────────────────┘   │  │
│  │        │                         │  │
│  └────────┴─────────────────────────┘  │
└──────────────────────────────────────────┘
```

## 6. 迁移策略

1. **准备阶段**
   - 创建bootstrap-dark.css（变量覆盖）
   - 创建tomselect-dark.css（组件适配）
   - 准备CDN资源

2. **按页面迁移**
   - dashboard/index.html（首页）
   - youth/list.html（列表页）
   - youth/form.html（表单页）
   - job/form.html（岗位表单）
   - ...其他页面

3. **测试验证**
   - 所有表单功能正常
   - 级联选择数据正确
   - 多选标签正常显示
   - 响应式布局测试
