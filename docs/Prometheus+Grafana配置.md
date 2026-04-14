# DDD Platform 监控与告警完整配置指南

## 目录
1. [Grafana 数据源配置](#一grafana-数据源配置)
2. [查看监控数据](#二查看监控数据)
3. [告警规则配置](#三告警规则配置)
4. [常见问题解决](#四常见问题解决)

---

## 一、Grafana 数据源配置

### 1.1 检查 Prometheus 状态

```bash
# 检查容器运行状态
docker ps | grep prometheus

# 访问 Prometheus UI 确认服务正常
http://localhost:9090
```

### 1.2 添加 Prometheus 数据源

**操作步骤：**

| 步骤 | 操作 |
|------|------|
| 1 | 访问 Grafana：`http://localhost:3000`（admin/admin） |
| 2 | 点击左侧齿轮图标 ⚙️ → **Data Sources** |
| 3 | 点击 **Add data source** → 选择 **Prometheus** |
| 4 | 配置 URL（根据部署方式选择） |
| 5 | 点击 **Save & Test** |

**URL 配置参考：**

| 部署方式 | URL 地址 |
|----------|----------|
| Docker 独立运行 | `http://host.docker.internal:9090` |
| 本地安装 | `http://localhost:9090` |
| Docker Compose | `http://prometheus:9090` |

**验证成功：** 看到绿色提示 `Data source is working`

### 1.3 修复 Dashboard 变量问题

**问题现象：** `DS_PROMETHEUS` 变量不存在或名称不匹配

**解决方案：**

| 方法 | 操作步骤 |
|------|----------|
| **创建变量** | Dashboard settings → Variables → New variable → Name: `DS_PROMETHEUS`, Type: Datasource |
| **重命名变量** | 将现有变量重命名为 `DS_PROMETHEUS` |
| **重新导入** | 删除 Dashboard → + → Import → 输入 `12900` → 选择数据源 |

---

## 二、查看监控数据

### 2.1 Prometheus 查询（http://localhost:9090）

**业务指标查询：**

| 指标 | PromQL 查询 |
|------|-------------|
| 用户注册总数 | `app_user_register_count_total` |
| 用户登录总数 | `app_user_login_count_total` |
| 登录失败总数 | `app_user_login_failure_total` |

**接口性能查询：**

| 指标 | PromQL 查询 |
|------|-------------|
| 接口 QPS | `rate(http_server_requests_seconds_count[1m])` |
| 平均响应时间 | `rate(http_server_requests_seconds_sum[1m]) / rate(http_server_requests_seconds_count[1m])` |
| 接口错误率 | `rate(http_server_requests_seconds_count{status=~"5.."}[1m]) / rate(http_server_requests_seconds_count[1m])` |
| P99 响应时间 | `histogram_quantile(0.99, rate(http_server_requests_seconds_bucket[5m]))` |

**JVM 指标查询：**

| 指标 | PromQL 查询 |
|------|-------------|
| 堆内存使用 | `jvm_memory_used_bytes{area="heap"}` |
| 非堆内存使用 | `jvm_memory_used_bytes{area="nonheap"}` |
| GC 次数 | `rate(jvm_gc_pause_seconds_count[5m])` |
| 活跃线程数 | `jvm_threads_live_threads` |

**时间范围查询：**

| 需求 | PromQL 查询 |
|------|-------------|
| 最近1小时注册数 | `increase(app_user_register_count_total[1h])` |
| 最近24小时登录数 | `increase(app_user_login_count_total[24h])` |
| 内存使用率 | `jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"}` |

### 2.2 Grafana 仪表盘（http://localhost:3000）

**导入官方模板：**

| 模板 ID | 名称 | 用途 |
|---------|------|------|
| 12900 | Spring Boot 2.1 Statistics | 综合监控 |
| 10280 | JVM (Micrometer) | JVM 监控 |
| 12856 | Spring Boot System Monitor | 系统监控 |

**手动创建业务面板：**

| 面板名称 | PromQL 查询 | 图表类型 |
|----------|-------------|----------|
| 用户注册总数 | `app_user_register_count_total` | Stat |
| 登录趋势 | `rate(app_user_login_count_total[5m])` | Time series |
| 登录失败趋势 | `rate(app_user_login_failure_total[5m])` | Time series |
| 接口 QPS | `rate(http_server_requests_seconds_count[1m])` | Time series |
| 平均响应时间 | `rate(http_server_requests_seconds_sum[1m]) / rate(http_server_requests_seconds_count[1m])` | Time series |
| 错误率 | `rate(http_server_requests_seconds_count{status=~"5.."}[1m]) / rate(http_server_requests_seconds_count[1m])` | Gauge |

### 2.3 命令行快速验证

```bash
# 查看应用暴露的指标
curl http://localhost:8080/actuator/prometheus | head -30

# 查看自定义业务指标
curl http://localhost:8080/actuator/prometheus | grep "app_user"

# 查看接口指标
curl http://localhost:8080/actuator/prometheus | grep "http_server_requests"

# 查看 JVM 指标
curl http://localhost:8080/actuator/prometheus | grep "jvm"

# 查看健康状态
curl http://localhost:8080/actuator/health
```

### 2.4 触发测试数据

```bash
# 注册用户（触发注册指标）
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"username":"monitor_test","password":"123456","email":"monitor@test.com"}'

# 登录成功（触发登录成功指标）
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'

# 登录失败（触发登录失败指标）
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"wrong"}'

# 查询验证
curl http://localhost:8080/actuator/prometheus | grep "app_user"
```

---

## 三、告警规则配置

### 3.1 创建告警规则文件

**文件位置：** `prometheus.yml` 同级目录下创建 `alerts.yml`

```yaml
groups:
  - name: ddd_platform_alerts
    interval: 30s
    rules:
      # 服务宕机告警
      - alert: ServiceDown
        expr: up{job="ddd-platform"} == 0
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "服务已停止"
          description: "DDD Platform 服务已停止运行超过1分钟"

      # 响应时间过长告警
      - alert: HighResponseTime
        expr: rate(http_server_requests_seconds_sum[1m]) / rate(http_server_requests_seconds_count[1m]) > 1
        for: 2m
        labels:
          severity: warning
        annotations:
          summary: "接口响应时间过长"
          description: "接口平均响应时间超过1秒"

      # 登录失败过多告警
      - alert: ManyLoginFailures
        expr: rate(app_user_login_failure_total[5m]) > 5
        for: 1m
        labels:
          severity: warning
        annotations:
          summary: "登录失败次数过多"
          description: "最近5分钟内登录失败次数超过5次"

      # 错误率过高告警
      - alert: HighErrorRate
        expr: rate(http_server_requests_seconds_count{status=~"5.."}[1m]) / rate(http_server_requests_seconds_count[1m]) > 0.05
        for: 2m
        labels:
          severity: critical
        annotations:
          summary: "接口错误率过高"
          description: "错误率超过5%"

      # JVM 内存过高告警
      - alert: HighJVMMemory
        expr: jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"} > 0.85
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "JVM内存使用过高"
          description: "堆内存使用率超过85%"

      # CPU 使用过高告警
      - alert: HighCPUUsage
        expr: system_cpu_usage > 0.8
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "CPU使用率过高"
          description: "CPU使用率超过80%"
```

### 3.2 配置 prometheus.yml

```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

# 加载告警规则
rule_files:
  - "alerts.yml"

# 告警配置
alerting:
  alertmanagers:
    - static_configs:
        - targets: []
        # - localhost:9093  # 启用 Alertmanager 时取消注释

scrape_configs:
  - job_name: 'ddd-platform'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['host.docker.internal:8080']
        labels:
          application: 'ddd-platform'
          env: 'dev'
```

### 3.3 Docker Compose 配置

```yaml
version: '3.8'

services:
  prometheus:
    image: prom/prometheus:latest
    container_name: prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
      - ./alerts.yml:/etc/prometheus/alerts.yml
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--web.enable-lifecycle'

  grafana:
    image: grafana/grafana:latest
    container_name: grafana
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
    volumes:
      - grafana-data:/var/lib/grafana

volumes:
  grafana-data:
```

### 3.4 验证告警配置

```bash
# 检查配置文件是否正确
docker exec prometheus promtool check config /etc/prometheus/prometheus.yml

# 查看已加载的告警规则
curl http://localhost:9090/api/v1/rules

# 查看当前告警状态
curl http://localhost:9090/api/v1/alerts

# 热加载配置（无需重启）
curl -X POST http://localhost:9090/-/reload
```

---

## 四、常见问题解决

### 4.1 Grafana 无法连接 Prometheus

| 问题 | 解决方案 |
|------|----------|
| 容器网络不通 | 确保 Prometheus 和 Grafana 在同一网络 |
| URL 配置错误 | 使用 `http://host.docker.internal:9090` |
| 端口未暴露 | 检查 docker-compose.yml 端口映射 |
| 服务未启动 | 执行 `docker ps` 检查容器状态 |

### 4.2 Dashboard 显示无数据

| 问题 | 解决方案 |
|------|----------|
| 数据源未配置 | 重新添加 Prometheus 数据源 |
| 变量缺失 | 创建 `DS_PROMETHEUS` 变量 |
| 应用未上报指标 | 检查 `/actuator/prometheus` 是否有数据 |
| 时间范围不对 | 调整 Dashboard 时间范围为最近15分钟 |

### 4.3 告警不生效

| 问题 | 解决方案 |
|------|----------|
| 规则未加载 | 执行 `curl http://localhost:9090/api/v1/rules` 检查 |
| 表达式不正确 | 在 Prometheus UI 中测试表达式 |
| 持续时间未满足 | 等待 `for` 指定时间后再查看 |
| 指标名称错误 | 确认指标名称与实际一致 |

### 4.4 目录结构参考

```
ddd-platform/
├── prometheus.yml           # Prometheus 主配置
├── alerts.yml               # 告警规则文件
├── docker-compose.yml       # 应用服务编排
├── docker-compose-monitoring.yml  # 监控服务编排
└── ddd-bootstrap/           # 应用模块
```

### 4.5 快速排查命令

```bash
# 1. 检查所有服务状态
docker ps

# 2. 检查应用指标端点
curl http://localhost:8080/actuator/prometheus | head -5

# 3. 检查 Prometheus 目标状态
curl http://localhost:9090/api/v1/targets | jq '.data.activeTargets[].health'

# 4. 检查 Prometheus 是否抓到数据
curl http://localhost:9090/api/v1/query?query=up

# 5. 检查 Grafana 数据源
curl -u admin:admin http://localhost:3000/api/datasources

# 6. 重启所有服务
docker-compose -f docker-compose-monitoring.yml down
docker-compose -f docker-compose-monitoring.yml up -d
```

---

**配置完成后，您可以通过 Grafana 直观查看所有监控指标，并在指标异常时收到告警通知。**