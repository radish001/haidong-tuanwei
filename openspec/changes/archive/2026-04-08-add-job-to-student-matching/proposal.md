## Why

当前招聘信息只能维护企业、学历、经验、薪资和工作地区等岗位信息，无法直接表达“适合什么样的学生”，管理员也无法从岗位快速反查符合条件的在校大学生。现在需要把专业、学历、学校类别、学校标签纳入招聘岗位的受控筛选条件，并提供基于岗位条件寻找合适学生的能力，支撑更高效的人岗匹配。

## What Changes

- 为招聘信息新增四类可选的多选条件：专业要求、学历要求、学校类别、学校标签。
- 调整招聘信息录入、编辑、列表展示和查询逻辑，使这四类条件都支持“可不填、可多选、按主数据受控维护”。
- 在招聘信息列表的操作区域新增“匹配学生”入口，允许管理员基于某条岗位信息查找符合条件的在校大学生。
- 新增岗位匹配学生能力：仅对岗位中已填写的条件生效，不填写的条件自动忽略；不同筛选维度之间按 AND 组合，同一维度内按 OR 命中。
- 规定专业要求匹配具体专业，学历要求按枚举命中，不做最低门槛推导。
- 补充岗位引用主数据后的关联约束，避免被招聘信息使用的专业、学校类别或学校标签被直接删除。

## Capabilities

### New Capabilities
- `job-student-matching`: 基于招聘岗位已填写条件筛选在校大学生，并在岗位列表中提供匹配入口与结果展示。

### Modified Capabilities
- `recruitment-management`: 招聘岗位需新增多选的专业要求、学历要求、学校类别和学校标签，并支持在列表页触发匹配学生操作。
- `youth-information-management`: 在校大学生查询能力需要支持基于岗位匹配条件的联动筛选，并按岗位条件返回可读的学生结果。
- `dictionary-and-region-management`: 专业和学历相关主数据需要承接招聘岗位多选引用关系，并在删除时校验是否仍被岗位使用。
- `education-and-enterprise-master-data`: 学校类别和学校标签主数据需要承接招聘岗位多选引用关系，并在删除时校验是否仍被岗位使用。

## Impact

- Affected code:
  - `src/main/resources/schema.sql`
  - `src/main/resources/data.sql`
  - `src/main/java/com/haidong/tuanwei/job/**`
  - `src/main/java/com/haidong/tuanwei/youth/**`
  - `src/main/java/com/haidong/tuanwei/system/**`
  - `src/main/resources/mapper/job/*.xml`
  - `src/main/resources/mapper/youth/*.xml`
  - `src/main/resources/mapper/system/*.xml`
  - `src/main/resources/templates/job/*.html`
  - `src/main/resources/templates/youth/*.html`
- Data and storage:
  - 招聘岗位需要新增多选条件的存储结构，优先采用关系表承接专业、学历、学校类别、学校标签
  - 招聘岗位与主数据之间会新增引用关系，需要补充删除校验与初始化脚本
- Systems:
  - 招聘岗位维护与查询
  - 青年信息筛选与结果展示
  - 学校、专业、学校类别、学校标签主数据管理
