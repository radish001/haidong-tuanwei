## Why

当前青年信息、企业信息和招聘岗位中仍有一批关键字段依赖自由文本或单段区域口径，导致录入标准不一致、筛选统计不稳定，也无法直接支撑后续面向在校大学生的专题分析。现在基础字典和主数据工作台已经具备统一维护能力，需要继续把业务表单中的受控字段全部切换到标准来源，并将区域统一为省/市/县三级代码存储。

## What Changes

- 将青年信息中的性别、民族、政治面貌、学历层次、学校、专业名称改为受控选择项，并将籍贯、现居住地、学校所在地改为省/市/县级联选择。
- 将企业信息中的企业规模、企业性质、企业行业改为受控选择项，并将所在地区改为省/市/县级联选择。
- 将招聘岗位中的所属企业、学历要求、经验要求、薪资待遇改为受控选择项，并将工作地区改为省/市/县级联选择。
- 在基础数据管理中新增 `经验要求` 和 `薪资待遇` 两类固定字典，用于招聘岗位录入、筛选和后续统计。
- 将青年、企业、招聘中的区域字段统一改为三段式代码存储，仅保存 `province_code`、`city_code`、`county_code`，区域名称统一通过 `sys_region` 反查，不再在业务表中保存冗余名称。
- 支持区域级联选择的部分填写场景，即允许只选省、或省加市、或省加市加县；系统需保证下级选择必须隶属于已选上级。
- 将招聘岗位的薪资口径从 `salary_min` / `salary_max` 数值区间调整为字典型 `salary_range` 字段。**BREAKING**
- 明确学校主数据继续只维护学校本身，不维护学校所在地；学校所在地仅在青年信息记录中单独维护。

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `dictionary-and-region-management`: 扩展固定字典范围，新增经验要求和薪资待遇字典，并定义业务区域字段统一使用三级代码与级联选择规则
- `education-and-enterprise-master-data`: 扩展固定企业字典范围，将经验要求和薪资待遇纳入统一维护，并继续保持学校主数据不维护区域
- `youth-information-management`: 青年信息录入、编辑、筛选与校验改为依赖受控字段和三级区域代码，学校所在地仅在青年记录中维护
- `enterprise-management`: 企业信息录入、编辑、筛选与校验改为依赖受控企业字典和三级区域代码
- `recruitment-management`: 招聘岗位录入、编辑、筛选与展示改为依赖受控企业/学历/经验/薪资字典，并将工作地区改为三级区域代码，薪资从数值区间切换为字典值

## Impact

- Affected code:
  - `src/main/java/com/haidong/tuanwei/youth/*`
  - `src/main/java/com/haidong/tuanwei/enterprise/*`
  - `src/main/java/com/haidong/tuanwei/recruitment/*`
  - `src/main/java/com/haidong/tuanwei/system/*`
  - `src/main/resources/templates/youth/*`
  - `src/main/resources/templates/enterprise/*`
  - `src/main/resources/templates/recruitment/*`
  - `src/main/resources/templates/system/*`
- Data/storage:
  - 需要为青年、企业、招聘业务表补充省/市/县三级区域代码字段
  - 招聘岗位需要以 `salary_range` 替代 `salary_min`、`salary_max`
  - `sys_dict_item` 需要新增经验要求和薪资待遇两类固定业务字典
- Systems:
  - 基础数据管理、青年信息、企业信息、招聘管理和后续数据分析将共享同一套受控字段与区域口径
