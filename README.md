# DDD Platform

基于 Spring Boot 3 + Java 17 的 DDD 多模块示例项目，包含用户管理、认证授权，以及常见企业级能力（CQRS、领域事件、幂等、限流、分布式锁、操作日志、监控指标）。

## 1. 项目特性

- DDD 分层架构：`common / domain / application / infrastructure / interfaces / bootstrap`
- 用户能力：注册、登录、查询、激活/禁用
- 认证授权：JWT + Spring Security + 角色控制
- 可靠性能力：接口幂等、分布式锁、限流、操作日志
- 可观测性：Spring Boot Actuator + Prometheus 指标
- 数据访问：MyBatis-Plus + MySQL + Redis

## 2. 模块结构

```text
ddd-platform
├── ddd-common          # 公共能力（常量、异常、统一返回）
├── ddd-domain          # 领域层（实体、值对象、聚合、领域事件、仓储接口）
├── ddd-application     # 应用层（应用服务、命令/查询、处理器）
├── ddd-infrastructure  # 基础设施层（仓储实现、安全、Redis、AOP能力）
├── ddd-interfaces      # 接口层（Controller、DTO、参数校验、Swagger、总线实现）
├── ddd-bootstrap       # 启动层（Spring Boot 启动与配置）
├── config              # Docker 与监控相关配置
├── docs                # 设计与使用文档
├── scripts             # Windows 脚本
└── sql                 # 数据库初始化脚本
```

## 3. 技术栈

- Java 17
- Spring Boot 3.1.5
- Spring Security
- MyBatis-Plus 3.5.5
- MySQL 8.x
- Redis 6+/7+
- RabbitMQ 3.x
- Micrometer + Prometheus + Grafana
- Maven 多模块构建

## 4. 快速开始（本地）

### 4.1 前置环境

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- RabbitMQ 3.x（建议，部分异步能力依赖）

### 4.2 初始化数据库

执行脚本：

```bash
mysql -u root -p < sql/init.sql
```

### 4.3 配置连接信息

根据本机环境修改：

- `ddd-bootstrap/src/main/resources/application.yml`

重点关注：

- `spring.datasource.*`
- `spring.data.redis.*`
- `spring.rabbitmq.*`
- `jwt.secret`

### 4.4 构建并启动

```bash
mvn clean package -DskipTests
java -jar ddd-bootstrap/target/ddd-bootstrap-1.0.0-SNAPSHOT.jar
```

或开发模式启动：

```bash
cd ddd-bootstrap
mvn spring-boot:run
```

## 5. 一键启动（Windows）

项目根目录提供 `start.bat`，会自动检查构建、启动依赖并运行应用：

```bat
start.bat
```

其他脚本见 `scripts/` 目录。

## 6. Docker 启动

使用 `config/docker-compose.yml`：

```bash
docker compose -f config/docker-compose.yml up -d --build
docker compose -f config/docker-compose.yml ps
```

停止：

```bash
docker compose -f config/docker-compose.yml down
```

## 7. 默认访问地址

- 应用：http://localhost:8080
- Swagger UI：http://localhost:8080/swagger-ui.html
- OpenAPI：http://localhost:8080/v3/api-docs
- 健康检查：http://localhost:8080/actuator/health
- Prometheus 指标：http://localhost:8080/actuator/prometheus
- RabbitMQ 管理台：http://localhost:15672（`guest/guest`）

## 8. 默认测试账号

`sql/init.sql` 默认初始化：

- 用户名：`admin`
- 密码：`123456`

## 9. 常用 API 示例

登录：

```bash
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"admin\",\"password\":\"123456\"}"
```

注册：

```bash
curl -X POST "http://localhost:8080/api/users/register" \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"newuser\",\"password\":\"123456\",\"email\":\"new@example.com\"}"
```

## 10. 测试

运行全部测试：

```bash
mvn test
```

按模块运行示例：

```bash
mvn test -pl ddd-domain
mvn test -pl ddd-interfaces -Dtest=UserControllerIntegrationTest
mvn test -pl ddd-bootstrap -Dtest=PerformanceTest
```

## 11. 相关文档

- 模块依赖关系：`docs/模块依赖关系.md`
- 功能与演进路线：`docs/功能清单与演进路线图.md`
- 完整验证指南：`docs/完整验证指南.md`
- 限流器说明：`docs/限流器.md`
- 幂等性说明：`docs/幂等性.md`
- 分布式锁说明：`docs/分布式锁.md`
- 操作日志说明：`docs/操作日志.md`

## 12. License

当前仓库未声明 License。如需开源发布，请补充 `LICENSE` 文件。
