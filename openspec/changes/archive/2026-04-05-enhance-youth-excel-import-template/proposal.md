## Why

当前青年信息 Excel 导入模板虽然已经提供基础表头和部分下拉选项，但模板中的选择项口径与页面受控字段仍不够一致，区域字段也无法按页面同样的规则支持省、省市、省市区县三级完整路径选择。现在需要把模板、导入解析和校验统一到现有字典、学校主数据、专业主数据和区域主数据口径，减少人工填写错误并提升批量导入的可用性。

## What Changes

- 保持青年信息导入模板为 13 列单列表头，不拆分区域列。
- 将模板中来自字典管理或主数据管理的字段统一改为下拉选择，包括性别、民族、政治面貌、学历、学校、专业、籍贯、学校所在区域，其中区域字段复用区域主数据。
- 将区域类下拉选项改为完整层级路径值，并支持三种合法选择口径：省、省 / 市、省 / 市 / 区县。
- 调整导入解析逻辑，使区域字段按完整路径匹配并写回 `province_code`、`city_code`、`county_code`，而不是仅按单个区域名称匹配。
- 保持导出列结构与模板一致，并确保导出数据与导入模板字段口径对齐。
- 强化导入错误提示，明确反馈字典值、学校、专业、区域路径和日期格式等问题。

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `youth-information-import-export`: 青年 Excel 模板、下拉选项来源、区域主数据口径、区域路径规则和导入解析逻辑将改为与现有受控字段和三级区域规则一致。

## Impact

- Affected code:
  - `src/main/java/com/haidong/tuanwei/youth/controller/YouthController.java`
  - `src/main/java/com/haidong/tuanwei/youth/service/impl/YouthInfoServiceImpl.java`
  - `src/main/java/com/haidong/tuanwei/common/util/ExcelUtils.java`
  - `src/main/java/com/haidong/tuanwei/youth/dto/YouthImportResult.java`
- Data/import behavior:
  - 模板下拉项将直接来自字典、学校主数据、专业主数据和区域主数据
  - 区域导入将使用完整路径值并支持部分层级选择
- Systems:
  - 青年信息导入模板、导入校验、导出文件与基础数据管理将共享同一套受控字段与区域口径
