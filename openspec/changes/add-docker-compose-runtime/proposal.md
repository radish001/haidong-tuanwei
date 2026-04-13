## Why

当前项目只能依赖本地 JDK、Maven 和手工配置环境变量启动，缺少标准化的容器运行方式，不利于在新机器或服务器上快速拉起统一运行环境。需要补充 `docker/` 目录和 `docker compose` 运行方案，让应用与 MySQL 能通过一次命令完成环境启动，同时明确数据库结构和业务数据仍由后续手工导入。

## What Changes

- 新增 `docker/` 目录，提供应用镜像构建所需的 `Dockerfile`。
- 新增包含 Spring Boot 应用和 MySQL 服务的 `docker-compose.yml`，支持一条 `docker compose up -d` 启动运行环境。
- 为 Compose 方案补充环境变量示例、日志与上传目录持久化约定，以及应用依赖数据库服务的启动顺序。
- 更新项目文档，说明容器启动步骤、访问方式，以及“数据库表结构与初始数据需后续手工导入”的边界。

## Capabilities

### New Capabilities
- `containerized-runtime-deployment`: 定义基于 Docker Compose 启动应用与 MySQL 运行环境、使用环境变量配置连接、并保留数据库手工导入边界的部署能力。

### Modified Capabilities
- None.

## Impact

- 影响代码与资源包括新的 `docker/` 目录、镜像构建脚本、Compose 编排文件和项目部署文档。
- 需要明确容器内应用如何读取现有 `MYSQL_*` 配置，以及日志目录、上传目录和数据库数据目录的卷挂载方式。
- 不改变现有业务代码和数据库初始化逻辑，但会定义新的标准运行入口供本地和服务器部署复用。
