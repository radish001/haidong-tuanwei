## Why

当前青年信息 Excel 模板、导入和导出共用同一套字段结构，导致导入模板承载了过多完整业务字段，不适合基层或初始台账采集。现在需要将导入模板收敛为一组更精简的基础字段，同时保留导出文件的完整业务字段，兼顾采集效率和业务查看需求。

## What Changes

- 将青年信息 Excel 导入模板字段调整为精简版表头，仅保留基础采集字段。
- 更新青年信息 Excel 导入解析和表头校验，使其按新的精简模板顺序读取数据。
- 保持青年信息 Excel 导出字段不变，继续输出当前完整业务列。
- 调整相关测试和规格说明，明确“导入模板字段”与“导出字段”允许不同。

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `youth-information-import-export`: 青年信息导入模板和导出文件不再要求使用完全一致的字段集合，导入模板改为基础采集字段，导出继续保留完整业务字段。

## Impact

- Affected code: `YouthInfoServiceImpl` 中模板表头、导入解析、表头校验和导出逻辑；相关单元测试。
- Affected behavior: 青年信息导入模板下载、Excel 导入字段顺序与字段必填范围。
- Unchanged behavior: 青年信息数据表结构、表单维护链路、Excel 导出字段集合。
