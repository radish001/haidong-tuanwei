# haidong-tuanwei

基于 Spring Boot 的前后端一体项目模板，使用 MySQL 作为数据库，ORM 使用 MyBatis，代码结构按 `controller`、`service`、`dao` 三层组织。

## 技术栈

- Spring Boot 3.5.13
- Thymeleaf
- MyBatis
- MySQL
- Lombok
- Maven

## 目录说明

```text
src/main/java/com/haidong/tuanwei
├── controller   # 控制层
├── service      # 业务接口层
├── service/impl # 业务实现层
├── dao          # 数据访问层
├── dto          # 请求对象
└── entity       # 实体对象
```

## 数据库准备

1. 创建数据库：

```sql
create database haidong_tuanwei default character set utf8mb4;
```

2. 修改 `src/main/resources/application.properties` 中的 MySQL 连接信息，或直接使用环境变量：

```bash
export MYSQL_HOST=localhost
export MYSQL_PORT=3306
export MYSQL_DATABASE=haidong_tuanwei
export MYSQL_USERNAME=root
export MYSQL_PASSWORD=123456
```

3. 当前默认不自动执行 `schema.sql` 或 `data.sql`。数据库表结构和初始化数据需要按实际环境手工导入。

## 启动方式

```bash
./mvnw spring-boot:run
```

启动后访问：

- 登录页: `http://localhost:8080/login`
- 管理后台: `http://localhost:8080/dashboard`

## Docker Compose 运行

1. 准备 Compose 环境变量：

```bash
cp docker/.env.example docker/.env
```

2. 按需修改 `docker/.env` 中的端口、数据库名和账号密码。

3. 在项目根目录执行：

```bash
cd docker
docker compose up -d --build
```

4. 启动后访问：

- 登录页: `http://localhost:${APP_PORT:-8080}/login`
- 管理后台: `http://localhost:${APP_PORT:-8080}/dashboard`
- MySQL 端口: `${MYSQL_EXPOSE_PORT:-3306}`

5. 运行时数据目录约定：

- MySQL 数据: `docker/data/mysql`
- 应用日志: `docker/data/logs`
- 上传文件: `docker/data/uploads`

6. 首次使用 Compose 拉起环境后，仍需手工将数据库表结构和业务初始化数据导入到 `MYSQL_DATABASE` 指定的库中；`docker compose up -d` 只负责启动应用和数据库容器，不会自动建表或导入数据。

常用命令：

```bash
cd docker
docker compose logs -f app
docker compose down
```
