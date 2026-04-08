## Why

当前学校主数据、专业主数据和青年信息仍主要按名称关联，学历也仍以普通字典值维护，导致名称变更需要级联回写青年数据，Excel 导入导出和统计分析依赖字符串精确匹配，且无法统一承接教育部官方编码体系。现在需要把学校、专业、专业分类、学历和学位统一纳入“编码 + 名称”的受控主数据口径，为青年 CRUD、导入导出、统计分析以及后续岗位匹配、多系统数据交换和官方数据对接打下基础。

## What Changes

- 为学校主数据、专业主数据分别新增唯一编码字段，并要求后台维护时同时维护编码和名称。
- 将专业分类从普通字典口径升级为“编码 + 名称”的受控主数据，并统一与专业主数据的关联方式；青年信息不单独保存专业分类，而是通过专业编码反查分类。
- 将学历从普通字典口径升级为“编码 + 名称”的主数据，并新增“学位”主数据，同样按教育部编码维护。
- 将青年信息中的学校、专业、学历、学位存储口径从名称或显示值调整为编码，并在列表、详情、表单、筛选和校验中通过主数据回显名称。
- 调整学校和专业主数据管理逻辑，使名称变更不再依赖批量回写青年信息名称字段。
- 调整青年 Excel 模板、导入解析和导出内容，使学校、专业、学历、学位按编码与主数据匹配，同时保持管理员可识别的展示值。
- 调整在校大学生相关统计分析，使学校、专业类聚合和学历统计基于编码关联主数据，而不是名称匹配。
- **BREAKING**: 青年信息表中学校、专业、学历和学位的落库字段及其关联口径将从名称或显示值驱动切换为官方编码驱动，现有历史数据需要迁移回填。

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `dictionary-and-region-management`: 学校、专业、专业分类、学历和学位的维护要求将扩展为编码加名称的唯一受控管理。
- `youth-information-management`: 青年信息录入、编辑、筛选和展示将改为以学校编码、专业编码、学历编码和学位编码作为保存与关联依据。
- `youth-information-import-export`: 青年 Excel 模板、导入校验和导出逻辑将改为使用学校编码、专业编码、学历编码和学位编码驱动主数据匹配。
- `dashboard-and-analytics`: 在校大学生相关统计分析将改为基于学校编码、专业编码和学历编码关联主数据，避免名称匹配带来的统计偏差。

## Impact

- Affected code:
  - `src/main/resources/schema.sql`
  - `src/main/resources/data.sql`
  - `src/main/java/com/haidong/tuanwei/system/**`
  - `src/main/java/com/haidong/tuanwei/youth/**`
  - `src/main/java/com/haidong/tuanwei/analytics/**`
  - `src/main/resources/mapper/system/*.xml`
  - `src/main/resources/mapper/youth/*.xml`
  - `src/main/resources/mapper/analytics/*.xml`
  - `src/main/resources/templates/system/*.html`
  - `src/main/resources/templates/youth/*.html`
  - `src/main/resources/templates/analytics/*.html`
- Data and migration:
  - `sys_school`、`sys_major_catalog` 需要新增唯一编码并回填现有主数据
  - 专业分类、学历、学位需要切换到或新增为编码化主数据口径
  - `youth_info` 需要从学校名称、专业名称、学历显示值、学位显示值迁移为学校编码、专业编码、学历编码、学位编码
  - 现有 Excel 模板和导入数据口径需要同步更新
- Systems:
  - 学校、专业、专业分类、学历、学位主数据管理
  - 青年信息 CRUD 与 Excel 导入导出
  - 在校大学生数据分析和学校标签专题分析
