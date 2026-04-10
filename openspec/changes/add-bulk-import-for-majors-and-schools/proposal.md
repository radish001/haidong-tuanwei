## Why

字典数据中的专业（`sys_major_catalog`）和学校（`sys_school`）数量庞大，目前只能逐条新增，无法高效维护。需要提供批量导入功能，让管理员通过 Excel 模板一次性导入或更新大量专业/学校数据。

## What Changes

- 在字典管理页（`dictionaries.html`）的专业和学校标签页分别新增「批量导入」按钮和导入 Modal
- 新增专业导入模板下载接口（`GET /system/majors/template`）
- 新增专业批量导入接口（`POST /system/majors/import`）
- 新增学校导入模板下载接口（`GET /system/schools/template`）
- 新增学校批量导入接口（`POST /system/schools/import`）
- 导入采用**全量校验后再写入**策略：发现任何错误（含重复）则整批失败并返回所有错误明细，不做部分写入
- `sys_school` 表移除 `school_name` 唯一约束，仅保留 `school_code` 唯一约束

## Capabilities

### New Capabilities

- `major-school-bulk-import`: 专业和学校的批量 Excel 导入功能，包括模板生成、文件上传、全量校验（重复检测、字典 label 查找）、事务性批量写入及错误明细展示

### Modified Capabilities

- `education-and-enterprise-master-data`: 专业和学校管理新增批量导入入口，`sys_school` 表结构调整（去除 `school_name` 唯一约束）

## Impact

- **Controller**: `SystemController` 新增 4 个端点（2 个模板下载 + 2 个导入）
- **Service**: `MasterDataService` / `MasterDataServiceImpl` 新增专业和学校导入方法
- **DAO**: `MajorCatalogDao`、`SchoolDao` 新增按 code 批量查重查询方法
- **Entity**: 新增 `DataImportResult`（通用导入结果，含 successCount、failCount、errors 列表）
- **Template**: `dictionaries.html` 新增两处 Modal UI
- **Schema**: `sys_school` 表移除 `uk_sys_school_name` 唯一索引（已在 `schema.sql` 完成）
