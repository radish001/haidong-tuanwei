## 1. 公共基础设施

- [x] 1.1 新建 `DataImportResult` 类（含 `successCount`、`failCount`、`List<DataImportError>`），`DataImportError` 含 `rowNumber` 和 `message`
- [x] 1.2 在 `MajorCatalogDao` 新增 `findExistingCodes(List<String> codes)` 方法，返回数据库中已存在的 major_code 集合
- [x] 1.3 在 `SchoolDao` 新增 `findExistingCodes(List<String> codes)` 方法，返回数据库中已存在的 school_code 集合

## 2. 专业批量导入后端

- [x] 2.1 在 `MasterDataService` 接口新增 `generateMajorImportTemplate()` 和 `importMajorsFromExcel(MultipartFile)` 方法签名
- [x] 2.2 在 `MasterDataServiceImpl` 实现 `generateMajorImportTemplate()`：用 POI 生成含「专业编码、专业名称、学科门类」三列的 xlsx 模板，学科门类列带隐藏 Sheet 下拉
- [x] 2.3 在 `MasterDataServiceImpl` 实现 `importMajorsFromExcel()`：解析全部行 → 检查文件内 major_code 重复 → 批量查询 DB 重复 → 验证必填和学科门类字典 label → 有错误则返回失败结果，无错误则 `@Transactional` 批量 insert
- [x] 2.4 在 `SystemController` 新增 `GET /system/majors/template` 端点（调用 `generateMajorImportTemplate()`，返回 xlsx 下载响应）
- [x] 2.5 在 `SystemController` 新增 `POST /system/majors/import` 端点（接收 `MultipartFile file`，调用 `importMajorsFromExcel()`，flash attributes 传递结果，redirect 到 `dictionaries?tab=major`）

## 3. 学校批量导入后端

- [x] 3.1 在 `MasterDataService` 接口新增 `generateSchoolImportTemplate()` 和 `importSchoolsFromExcel(MultipartFile)` 方法签名
- [x] 3.2 在 `MasterDataServiceImpl` 实现 `generateSchoolImportTemplate()`：用 POI 生成含「学校编码、学校名称、学校类别」三列的 xlsx 模板，学校类别列带隐藏 Sheet 下拉
- [x] 3.3 在 `MasterDataServiceImpl` 实现 `importSchoolsFromExcel()`：解析全部行 → 检查文件内 school_code 重复 → 批量查询 DB 重复 → 验证必填和学校类别字典 label → 有错误则返回失败结果，无错误则 `@Transactional` 批量 insert
- [x] 3.4 在 `SystemController` 新增 `GET /system/schools/template` 端点（调用 `generateSchoolImportTemplate()`，返回 xlsx 下载响应）
- [x] 3.5 在 `SystemController` 新增 `POST /system/schools/import` 端点（接收 `MultipartFile file`，调用 `importSchoolsFromExcel()`，flash attributes 传递结果，redirect 到 `dictionaries?tab=school`）

## 4. 前端 UI

- [x] 4.1 在 `dictionaries.html` 的专业标签页工具栏新增「批量导入」按钮，触发 Modal `#majorImportModal`
- [x] 4.2 实现 `#majorImportModal`：包含「下载导入模板」链接（`/system/majors/template`）、文件上传区（`POST /system/majors/import`，`enctype="multipart/form-data"`，`accept=".xlsx,.xls"`）
- [x] 4.3 在 `dictionaries.html` 的学校标签页工具栏新增「批量导入」按钮，触发 Modal `#schoolImportModal`
- [x] 4.4 实现 `#schoolImportModal`：包含「下载导入模板」链接（`/system/schools/template`）、文件上传区（`POST /system/schools/import`，`enctype="multipart/form-data"`，`accept=".xlsx,.xls"`）
- [x] 4.5 在专业标签页和学校标签页分别展示 flash 的 `importMessage`（成功/失败摘要）和 `importResult`（错误明细表格，含行号和错误信息列）

## 5. 数据库迁移

- [x] 5.1 在 `data.sql` 添加迁移语句：`ALTER TABLE sys_school DROP INDEX IF EXISTS uk_sys_school_name`，保证已运行实例去除旧唯一约束
