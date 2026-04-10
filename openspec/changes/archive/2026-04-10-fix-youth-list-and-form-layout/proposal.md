## Why

当前后台表单页面还存在几处明显的展示层问题：青年列表中继续显示“就业方向”而不是管理员更关心的“民族”，而青年、企业、招聘的新增/编辑表单中区域选择项又明显长于其他字段，导致列表信息组织和多模块表单排布都不够统一。这些都属于当前后台页面的直接可见问题，适合尽快以小范围模板修复完成。

## What Changes

- 调整青年信息列表字段顺序，将“民族”显示在“出生年月”后，并移除当前列表中的“就业方向”列。
- 调整青年、企业、招聘新增/编辑表单中区域选择字段的栅格宽度，使其与其他常规表单控件保持一致。

## Capabilities

### New Capabilities

### Modified Capabilities

- `youth-information-management`: 更新青年信息列表和维护表单的展示要求，使列表字段和表单区域控件宽度符合当前页面使用预期。
- `enterprise-management`: 更新企业维护表单的区域控件宽度要求，使其与其他字段对齐。
- `recruitment-management`: 更新招聘岗位表单的工作地区控件宽度要求，使其与其他字段对齐。

## Impact

- 主要影响 `src/main/resources/templates/youth/list.html`、`src/main/resources/templates/youth/form.html`、`src/main/resources/templates/enterprise/form.html` 和 `src/main/resources/templates/job/form.html`。
- 不涉及数据库结构、查询逻辑或后端接口。
