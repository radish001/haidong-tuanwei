## Why

当前青年信息 Excel 导入在存在失败行时，只会在页面返回行号和失败原因，管理员还需要手工回到原文件中定位并修正数据，处理批量失败场景效率较低。随着基础数据口径和模板约束逐步收紧，系统需要提供一个可直接返修和再次导入的失败数据 Excel，减少重复整理成本。

## What Changes

- 在青年信息 Excel 导入失败时，新增失败数据 Excel 下载能力。
- 失败数据文件仅包含导入失败的原始数据行，并保持与标准导入模板一致的字段顺序、下拉来源和隐藏数据工作表。
- 失败数据文件在末尾新增 `失败原因` 列，用于记录该行未通过导入校验的原因。
- 青年信息导入校验调整为允许再次上传带有末尾 `失败原因` 列的失败数据文件，并在导入时忽略该列内容。
- 青年信息导入结果页保留现有失败明细展示，并在存在失败数据时提供失败 Excel 下载入口。

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `youth-information-import-export`: 青年信息导入在失败时增加失败数据 Excel 下载与返修再导入能力。

## Impact

- Affected specs: `openspec/specs/youth-information-import-export/spec.md`
- Affected backend: `YouthController`, `YouthInfoService`, `YouthInfoServiceImpl`, youth import DTOs, Excel utility flow
- Affected frontend: `templates/youth/list.html` import result area and download entry
- Affected tests: youth service and controller integration tests covering failed import export and re-import behavior
