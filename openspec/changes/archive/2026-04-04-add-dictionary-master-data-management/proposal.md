## Why

当前后台只提供一个简单的字典列表新增页，无法支撑青年信息、学校、专业、企业基础口径的统一维护。随着数据分析页面要收敛到在校大学生专题分析，学校、专业、企业字段以及省/市/区（县）区域口径都必须先完成标准化管理，并且删除时需要有明确的关联校验，避免破坏现有业务数据。

## What Changes

- 将现有字典管理页重构为固定六大类的基础数据管理页，顶部展示六个固定 tab，不支持新增或删除顶层大类。
- 在固定六大类内提供查询、分页、新增、编辑、删除能力，并沿用后台统一的列表布局、局部刷新、抽屉表单和确认模态框交互。
- 在“公共字典”内固定维护民族、政治面貌、学历层次三类数据，并新增省、市、区（县）区域数据的级联维护能力，不再允许随意新增新的公共字典类型或新的顶层分组。
- 新增专业类别与专业名称管理，要求每个专业名称必须关联一个专业类别。
- 新增学校类别、学校标签和学校管理，其中学校必须绑定一个学校类别，并可绑定多个学校标签；学校标签作为学校相关基础数据的一部分进行维护，不单独作为新的顶层大类。
- 新增企业基础字典维护，固定管理企业规模、企业性质、企业行业三类数据，并用于企业信息录入与筛选。
- 为所有字典项和基础数据增加删除前关联校验；如存在学校、青年信息、企业信息等业务关联，则拒绝删除并返回明确提示。

## Capabilities

### New Capabilities
- `education-and-enterprise-master-data`: 管理专业类别、专业名称、学校类别、学校标签、学校以及企业基础字典，并定义它们之间的关联和删除校验规则

### Modified Capabilities
- `dictionary-and-region-management`: 将字典管理从自由录入字典类型的简单页面，调整为固定六大类的基础数据管理工作台，并补充省、市、区（县）区域数据的级联 CRUD 能力
- `youth-information-management`: 青年信息录入和校验将逐步依赖受控的学历、专业、学校等基础数据，并要求学校删除前校验青年信息关联
- `enterprise-management`: 企业信息录入、筛选与展示将依赖企业规模、企业性质、企业行业三类受控字典

## Impact

- Affected code:
  - `src/main/resources/templates/system/*`
  - `src/main/java/com/haidong/tuanwei/system/*`
  - `src/main/java/com/haidong/tuanwei/youth/*`
  - `src/main/java/com/haidong/tuanwei/enterprise/*`
  - `src/main/resources/templates/youth/*`
  - `src/main/resources/templates/enterprise/*`
- Data/storage:
  - 现有 `sys_dict_item` 的使用方式将收敛为固定业务类型
  - 需要新增学校、学校标签、专业名称等基础数据表及关联表
  - 现有 `sys_region` 需要支持后台级联维护与引用校验
- Systems:
  - 字典管理页、青年信息页、企业信息页、后续数据分析页都会使用统一的基础数据来源
