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

3. 首次启动时会自动执行 `schema.sql` 和 `data.sql` 初始化表结构及演示数据。

## 启动方式

```bash
./mvnw spring-boot:run
```

启动后访问：

- 页面地址: `http://localhost:8080/users`
- JSON 接口: `http://localhost:8080/users/api`
