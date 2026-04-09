# Bootstrap + 深色主题重构

## 目标
将项目从自定义CSS全面迁移到Bootstrap 5，同时定制深色科技主题，保持现有视觉风格的核心特征。

## 背景
当前项目使用纯自定义CSS（约2200+行），存在以下问题：
- 表单控件（select、多选）使用原生HTML样式，视觉效果差
- 级联选择器交互体验不够现代
- 缺乏响应式设计，移动端体验待优化
- 样式维护成本高

## 方案概述

### 选型理由
**方案C：全站Bootstrap + 深色主题定制**

- 获得Bootstrap的响应式网格、组件规范、可访问性支持
- 通过Sass变量定制，保持现有深蓝科技风格
- 引入 TreeselectJS 解决单下拉区域级联，引入 Tom Select 解决多选下拉问题

### 技术栈
- Bootstrap 5.3 (通过CDN或npm)
- Bootstrap Icons (图标库)
- TreeselectJS (单下拉树形区域选择)
- Tom Select 2.3 (多选组件与普通下拉增强)
- 自定义Sass变量覆盖深色主题

### 配色映射
| 现有变量 | Bootstrap变量 | 值 |
|---------|--------------|-----|
| `--bg-main: #061437` | `$body-bg` | `#061437` |
| `--text-main: #f1f7ff` | `$body-color` | `#f1f7ff` |
| `--brand: #32d6ff` | `$primary` | `#32d6ff` |
| `--bg-panel` | `$card-bg` | `rgba(7, 20, 54, 0.88)` |
| `--border` | `$border-color` | `rgba(69, 145, 255, 0.2)` |

### 关键组件替换
1. **表单控件**：Bootstrap表单样式 + Tom Select美化
2. **按钮**：Bootstrap按钮 + 自定义渐变样式
3. **卡片/面板**：Bootstrap Card + 透明背景定制
4. **侧边栏**：Bootstrap Offcanvas/定制固定侧边栏
5. **表格**：Bootstrap Table + 深色主题
6. **级联选择**：TreeselectJS 实现单下拉树形省市区选择
7. **多选下拉**：Tom Select实现标签式多选

## 范围

### In Scope
- 全站CSS替换为Bootstrap + 定制主题
- 所有表单页面样式更新
- 侧边栏、顶部栏重构
- 级联选择器改用Tom Select
- 多选下拉改用Tom Select
- 响应式布局支持

### Out of Scope
- 功能逻辑变更（仅样式）
- ECharts图表配置调整
- 后端Java代码修改
- 新增业务功能

## 工作量估算
约3-4天（包含测试调整）

## 风险
- 样式迁移可能导致短期视觉不一致
- 需要全页面回归测试
- Tom Select与Bootstrap深色主题需要样式调和
