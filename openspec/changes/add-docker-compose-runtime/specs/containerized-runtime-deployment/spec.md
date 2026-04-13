## ADDED Requirements

### Requirement: Docker Compose 运行环境
系统 MUST 提供基于 Docker Compose 的标准运行环境，使管理员或运维人员可以通过单条 `docker compose up -d` 命令同时启动 Spring Boot 应用和 MySQL 容器；该运行环境 MUST 明确区分“容器环境拉起成功”与“数据库内容后续手工导入”两个步骤，不得默认承诺自动完成业务表结构和初始化数据导入。

#### Scenario: 使用 Compose 启动应用与数据库服务
- **WHEN** 运维人员在项目提供的部署目录中执行 `docker compose up -d`
- **THEN** 系统 MUST 启动应用容器和 MySQL 容器
- **AND** 应用容器 MUST 使用 Compose 中定义的数据库连接信息指向该 MySQL 服务

#### Scenario: 明确数据库内容仍需手工导入
- **WHEN** 运维人员按照项目提供的容器部署说明启动环境
- **THEN** 文档 MUST 明确说明数据库表结构和业务初始化数据需要后续手工导入
- **AND** 部署方案 MUST 不将“自动建表并导入初始数据”作为默认行为

### Requirement: 容器运行配置与持久化
系统 MUST 为容器运行方式提供最小可用的配置与持久化约定，确保数据库数据目录、应用日志目录和上传文件目录在容器重建后可保留，并允许通过环境变量调整数据库连接和运行参数。

#### Scenario: 通过环境变量配置数据库连接
- **WHEN** 运维人员使用 Compose 部署应用
- **THEN** 应用容器 MUST 通过环境变量配置数据库主机、端口、数据库名、用户名和密码
- **AND** 这些环境变量命名 MUST 与项目现有数据库连接配置保持兼容或提供清晰映射

#### Scenario: 持久化数据库与应用文件
- **WHEN** 运维人员使用 Compose 部署并重建容器
- **THEN** MySQL 数据目录 MUST 使用持久化卷或等效挂载保存
- **AND** 应用日志目录与上传文件目录 MUST 使用持久化卷或宿主机挂载保存

#### Scenario: 数据库服务未就绪时应用等待依赖
- **WHEN** Compose 同时启动应用容器和 MySQL 容器
- **THEN** 部署方案 MUST 提供数据库健康检查或等效依赖等待机制
- **AND** 应用容器 MUST 避免在数据库服务尚未就绪时直接进入失败状态

### Requirement: 容器化部署文档
系统 MUST 提供与容器运行方式一致的部署文档，说明镜像构建、Compose 启动、访问方式、常用环境变量以及数据库手工导入边界，避免文档与实际运行行为不一致。

#### Scenario: 查看容器部署说明
- **WHEN** 运维人员阅读项目中的容器化部署说明
- **THEN** 文档 MUST 说明如何构建和启动 Compose 运行环境
- **AND** 文档 MUST 说明应用访问地址、MySQL 连接端口和关键环境变量用途

#### Scenario: 文档反映手工导库边界
- **WHEN** 运维人员参考容器部署说明进行首次部署
- **THEN** 文档 MUST 明确指出数据库 schema 和业务数据需要手工导入
- **AND** 文档 MUST 不再暗示仅通过启动应用即可自动完成数据库初始化
