## Why

青年信息库当前已经引入了“专业类别”和“专业”的筛选字段，但两者的交互与数据存储口径还不一致。专业类别筛选使用字典值，青年信息保存时却写入类别名称，导致级联查询链路不统一，也让专业类别筛选依赖额外的反查兜底逻辑。

## What Changes

- 在青年信息列表页提供“专业类别 -> 专业”的级联筛选，管理员选择专业类别后，专业下拉仅展示该类别下的专业。
- 统一青年信息中 `majorCategory` 的保存口径为专业类别字典值，不再保存类别展示名称。
- 调整青年信息新增、编辑、导入、查询和展示链路，使专业类别统一基于字典值存储、基于字典标签展示。
- 调整专业主数据查询返回结构，为青年页表单和筛选级联提供类别字典值。

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `youth-information-management`: 调整青年信息中专业类别的录入、存储、筛选和展示规则，并要求青年页筛选支持专业类别到专业的级联选择。

## Impact

- 受影响页面：`src/main/resources/templates/youth/list.html`、`src/main/resources/templates/youth/form.html`
- 受影响后端：`YouthController`、`SystemController`、`MasterDataService`、`YouthInfoServiceImpl`
- 受影响数据访问：`MajorCatalogDao`、`YouthInfoDao.xml`
- 受影响接口：青年页专业级联使用的专业查询接口返回字段会增加类别字典值
