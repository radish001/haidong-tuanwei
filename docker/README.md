# Docker 独立部署说明

这个目录可以单独打包后发给其他环境使用。

目录内需要包含以下文件：

- `Dockerfile`
- `docker-compose.yml`
- `.env.example`
- `build-and-deploy.sh`
- `haidong-tuanwei-0.0.1-SNAPSHOT.jar`

部署步骤：

```bash
chmod +x build-and-deploy.sh
./build-and-deploy.sh
```

脚本会在 `.env` 不存在时自动根据 `.env.example` 生成。

默认示例使用 MySQL `root` 账号启动应用，如需修改请同时调整 `.env` 中的 `MYSQL_USERNAME`、`MYSQL_PASSWORD` 和 `MYSQL_ROOT_PASSWORD`。

启动后访问：

- 登录页：`http://localhost:${APP_PORT:-8080}/login`
- 管理后台：`http://localhost:${APP_PORT:-8080}/dashboard`
- MySQL：`127.0.0.1:${MYSQL_EXPOSE_PORT:-13306}`

初始化说明：

- Compose 只负责启动应用和 MySQL 容器
- 数据库表结构和业务初始化数据需要在容器启动后手工导入

常用命令：

```bash
docker compose logs -f app
docker compose down
```
