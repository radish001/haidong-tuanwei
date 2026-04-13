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

推荐初始化顺序：

1. `schema.sql`
2. `data_core.sql`
3. `data_dict.sql`
4. `data_regions.sql`
5. `data_majors.sql`
6. `data_schools.sql`

说明：

- `data_core.sql` 会初始化用户、角色、菜单和基础字典
- `data_dict.sql` 依赖 `data_core.sql`，因为 `sys_dict_item` 会在 `data_core.sql` 中被清空
- `data_majors.sql` 依赖 `data_core.sql` 中的专业类别字典
- `data_schools.sql` 依赖前面已经初始化好的学校类别字典

手工执行示例：

```bash
mysql -h127.0.0.1 -P13306 -uroot -proot123456 haidong_tuanwei < sql/schema.sql
mysql -h127.0.0.1 -P13306 -uroot -proot123456 haidong_tuanwei < sql/data_core.sql
mysql -h127.0.0.1 -P13306 -uroot -proot123456 haidong_tuanwei < sql/data_dict.sql
mysql -h127.0.0.1 -P13306 -uroot -proot123456 haidong_tuanwei < sql/data_regions.sql
mysql -h127.0.0.1 -P13306 -uroot -proot123456 haidong_tuanwei < sql/data_majors.sql
mysql -h127.0.0.1 -P13306 -uroot -proot123456 haidong_tuanwei < sql/data_schools.sql
```

常用命令：

```bash
docker compose logs -f app
docker compose down
```
