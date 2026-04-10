## Context

字典管理中的专业（`sys_major_catalog`）和学校（`sys_school`）数据量大，目前只有逐条增删改的维护方式。系统已有在校学生（youth）批量 Excel 导入的成熟模式，本次变更复用该模式，为专业和学校分别实现导入功能。

已有 youth 导入的关键模式：
- `MultipartFile` 上传 → 服务层解析 → `ImportResult`（successCount/failCount/errors）→ flash attributes → 列表页展示
- Apache POI（`XSSFWorkbook`）生成模板，带下拉 sheet
- 每行失败继续，最终部分成功

本次差异：专业/学校采用**全量校验-全成功才写入**策略，任何错误导致整批失败。

## Goals / Non-Goals

**Goals:**
- 为专业和学校分别提供 Excel 模板下载和批量导入接口
- 全量校验（文件内重复 + 与 DB 已有数据重复）：有任何错误则全批失败，返回所有错误明细
- 校验通过后在单个事务内批量 insert
- 在 `dictionaries.html` 的专业和学校标签页添加「批量导入」Modal UI

**Non-Goals:**
- 不支持 upsert / 更新已有数据
- 不支持导入学校标签关联关系
- 不提供导入后的「撤销」能力

## Decisions

### 决策 1：全量校验后再写入（而非逐行写入）

**选择**：先解析全部行并收集所有错误，有任何错误则不写库，全部通过才在一个 `@Transactional` 方法内批量 insert。

**理由**：专业和学校是基础字典数据，部分写入会造成数据不一致，且用户通常是从官方数据集整批导入，期望「要么全成功，要么告诉我哪里有问题」。

**备选**：逐行写入（youth 模式）——对字典数据不合适，用户不知道哪些已写入。

### 决策 2：Excel 列使用字典 Label 文字

**选择**：`学科门类` 和 `学校类别` 列填写字典项的显示文字（如「哲学」「综合类」），服务层做 label→id 查找。

**理由**：与 youth 导入对 gender/ethnicity 的处理一致，对用户友好；字典标签稳定，不需要让用户记忆代码值。

### 决策 3：重复判断仅基于 code

**选择**：专业以 `major_code` 唯一，学校以 `school_code` 唯一；同一文件内以及与数据库中已有记录比较时均以 code 为判重依据。

**理由**：`school_name` 已去除 DB 唯一约束（允许同名不同 code 的学校存在），`major_name` 虽然在 DB 有唯一约束，但业务上 code 是权威标识符。

### 决策 4：复用通用 `DataImportResult`

**选择**：新建 `DataImportResult`（含 `successCount`、`failCount`、`List<DataImportError>`）供专业和学校导入复用，而不是分别新建各自的结果类。

**理由**：两者结构完全相同，复用减少重复代码；youth 的 `YouthImportResult` 因包含 youth 特有字段而不直接复用。

### 决策 5：学校不导入标签

**选择**：Excel 模板只有三列（学校编码、学校名称、学校类别），不包含标签字段。

**理由**：标签是多值字段，Excel 单元格处理复杂且易出错；标签数量有限，导入后手动绑定成本可接受。

## Risks / Trade-offs

- **大文件性能**：全量读取到内存后再校验，对超大文件（数千行）可能较慢。→ 当前字典数据规模（专业约 700 条，学校约 600 条）在可接受范围内，暂不做流式处理。
- **重名学校可导入**：去除 `school_name` 唯一约束后，同名学校可能通过导入进入系统。→ 应用层只报 code 重复，不报 name 重复；name 重复视为合法（允许存在）。
- **无 upsert**：已有数据无法通过导入更新，需要在界面手动编辑。→ 后续可按需扩展。

## Migration Plan

1. `schema.sql` 已移除 `sys_school.uk_sys_school_name`（已完成）
2. 对已运行的数据库实例，需执行 `ALTER TABLE sys_school DROP INDEX uk_sys_school_name`（在 `data.sql` 或单独迁移脚本中处理）
3. 新功能为纯增量，不影响现有单条增删改流程
