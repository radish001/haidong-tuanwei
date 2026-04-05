## Why

当前后台首页只展示总量卡片、列表摘要和占位型分布区域，缺少能够直观看出在校大学生空间分布的数据视图。现在青年信息已经具备学校所在地和籍贯的三级区域编码字段，适合在首页补充全国与海东本地的地图分布看板，帮助管理员快速理解学生就学流向和本地生源结构。

## What Changes

- 在后台首页新增中国地图，看板展示在校大学生按学校所在省的分布情况。
- 在后台首页新增海东市地图，看板展示在校大学生按籍贯区县的分布情况。
- 为首页补充基于 `youth_info` 和 `sys_region` 的地图聚合查询，仅统计 `youth_type = 'college'` 的数据。
- 在首页模板中接入地图脚本与地图容器，替换当前占位式分布卡片。
- 为首页地图准备所需的地图边界静态资源，并补齐首页展示所依赖的区域主数据种子，至少覆盖中国省级区域和海东市 2 区 4 县。
- 在地图标题和说明文案中明确统计口径，避免把“学校所在地分布”和“籍贯分布”混淆。

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `dashboard-and-analytics`: 首页驾驶舱需要新增两张基于业务数据实时聚合的学生地图，并明确各自的统计口径与区域覆盖要求

## Impact

- Affected code:
  - `src/main/java/com/haidong/tuanwei/dashboard/*`
  - `src/main/resources/mapper/dashboard/*`
  - `src/main/resources/templates/dashboard/*`
  - `src/main/resources/static/*`
- Data/storage:
  - 需要新增或补齐首页地图依赖的静态 GeoJSON 资源
  - 需要补齐 `sys_region` 中中国省级区域与海东市 2 区 4 县的种子数据
- Systems:
  - 后台首页驾驶舱
  - 区域主数据与青年信息聚合统计
