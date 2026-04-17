## 1. 数据库变更

- [x] 1.1 在 `sys_dict_item` 表新增 `education_scopes` 字段（varchar 100，可为空）
- [x] 1.2 更新现有14个专业类别数据，设置 `education_scopes = 'UNDERGRADUATE'`
- [x] 1.3 在 `schema.sql` 中同步更新表结构定义

## 2. 后端实体与常量定义

- [x] 2.1 创建 `MajorCategoryEducationScope` 枚举（JUNIOR_COLLEGE、UNDERGRADUATE、GRADUATE）
- [x] 2.2 修改 `DictItem` 实体，新增 `educationScopes` 字段
- [x] 2.3 修改 `DictionaryItemForm` DTO，新增 `educationScopes` 字段及校验规则（至少选一项）

## 3. 后端服务与DAO层修改

- [x] 3.1 修改 `DictionaryDao.xml`，在查询语句中包含 `education_scopes` 字段
- [x] 3.2 修改 `DictionaryServiceImpl`，处理 `major_category` 类型的 `education_scopes` 字段保存和更新
- [x] 3.3 修改 `SystemController.populateDictionaryItemForm()`，专业类别表单增加学历层次选项列表

## 4. 数据分析服务修改

- [x] 4.1 在 `YouthAnalyticsDao` 新增 `countByMajorCategoryForUndergraduate(String youthType)` 方法
- [x] 4.2 在 `YouthAnalyticsDao` 新增 `countByMajorCategoryForJuniorCollege(String youthType)` 方法
- [x] 4.3 在 `YouthAnalyticsDao.xml` 编写本科/专科专业类别分布统计SQL（关联专业类别所属学历层次过滤）
- [x] 4.4 修改 `YouthAnalyticsServiceImpl`，新增本科和专科专业类别分布数据获取方法
- [x] 4.5 修改 `AnalyticsController`，向模型添加 `undergraduateMajorCategoryDistributionJson` 和 `juniorCollegeMajorCategoryDistributionJson`

## 5. 系统设置页面修改

- [x] 5.1 修改 `system/dictionaries.html`，专业类别列表增加"所属学历层次"列展示
- [x] 5.2 修改 `system/dictionary-item-form.html`，当 section 为 `major_category` 时显示"所属学历层次"多选框
- [x] 5.3 修改 `SystemController.populateDictionaryItemForm()`，专业类别表单增加学历层次选项列表

## 6. 数据分析页面修改

- [x] 6.1 修改 `analytics/index.html`，将原"专业类别分布"图表ID从 `majorCategoryChart` 改为 `undergraduateMajorCategoryChart`
- [x] 6.2 修改 `analytics/index.html`，新增"专科专业类别分布"图表容器（ID: `juniorCollegeMajorCategoryChart`）
- [x] 6.3 修改 `analytics/index.html` 中的 JavaScript，新增专科专业类别分布图表渲染逻辑
- [x] 6.4 调整页面布局，确保本科和专科图表上下排列且样式一致

## 7. 数据初始化脚本更新

- [x] 7.1 修改 `data_core.sql`，在专业类别 INSERT 语句中包含 `education_scopes` 字段（值设为 'UNDERGRADUATE'）

## 8. 单元测试编写

- [x] 8.1 编写 `DictionaryServiceImplTest` - 覆盖专业类别CRUD及所属学历层次校验
- [x] 8.2 编写 `MajorCategoryEducationScopeTest` - 覆盖枚举常量及转换方法
- [x] 8.3 编写 `AnalyticsControllerTest` - 覆盖数据分析页面模型属性
- [x] 8.4 更新 `YouthAnalyticsServiceImplTest` - 覆盖本科/专科专业类别分布统计
