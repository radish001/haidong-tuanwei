## 1. Backend Matching Query

- [x] 1.1 Extend the岗位查询请求与 DAO 条件，支持按专业、学历、学校类别、学校标签四个维度过滤招聘岗位。
- [x] 1.2 Add a学生匹配招聘结果入口到 `YouthController`，读取学生记录和学校主数据并构造对称的岗位匹配条件。
- [x] 1.3 Return岗位匹配结果所需的分页信息、匹配条件摘要和岗位列表数据，保持与现有岗位匹配学生流程一致。

## 2. Drawer Interaction and Templates

- [x] 2.1 Update在校学生详情抽屉模板，新增“匹配招聘信息”按钮并传递来源详情抽屉上下文。
- [x] 2.2 Create学生匹配招聘结果抽屉模板，展示匹配条件摘要、分页控件和岗位结果列表。
- [x] 2.3 Adjust抽屉前端脚本，使匹配结果抽屉覆盖学生详情抽屉，关闭后恢复原学生详情抽屉而不是返回列表页。

## 3. Verification

- [x] 3.1 Add or update tests covering学生匹配招聘的四维匹配规则与未配置维度忽略逻辑。
- [x] 3.2 Add or update tests covering学生详情打开匹配结果抽屉及关闭后恢复详情抽屉的交互链路。
- [x] 3.3 Run targeted regression checks for岗位匹配学生、学生详情抽屉和招聘岗位查询分页，确认双向匹配都正常。
