## 项目运行说明

### 1. 环境要求
- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 2. 配置修改
修改 `ddd-bootstrap/src/main/resources/application.yml` 中的数据库和Redis连接信息

### 3. 初始化数据库
执行 `sql/init.sql` 脚本创建数据库和表

### 4. 编译打包
```bash
mvn clean package -DskipTests
```

### 5. 运行项目
```bash
java -jar ddd-bootstrap/target/ddd-bootstrap-1.0.0-SNAPSHOT.jar
```

### 6. 访问地址
- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/v3/api-docs

### 7. 测试接口
```bash
# 登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'

# 注册
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"username":"newuser","password":"123456","email":"new@example.com","nickname":"新用户"}'
```

# DDD Platform

## 快速启动

1. 确保 MySQL、Redis 已启动
2. 双击运行 `start.bat`

## 文档位置

详细文档请查看 `docs/` 目录（如有）

## 脚本位置

其他脚本请查看 `scripts/` 目录（如有）