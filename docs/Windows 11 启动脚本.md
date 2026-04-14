## Windows 11 启动脚本

### 1. 启动所有服务 `start-all.bat`

```batch
@echo off
chcp 65001 > nul
echo ========================================
echo   Starting DDD Platform All Services
echo ========================================
echo.

set PROJECT_DIR=%CD%

echo [1/5] Starting MySQL...
net start MySQL80 > nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] MySQL started
) else (
    echo [WARN] MySQL may already be running
)

echo [2/5] Starting Redis...
start "Redis" cmd /c "redis-server"
echo [OK] Redis started

echo [3/5] Starting RabbitMQ...
net start RabbitMQ > nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] RabbitMQ started
) else (
    echo [WARN] RabbitMQ may already be running
)

echo [4/5] Starting Prometheus...
start "Prometheus" cmd /c "cd %PROJECT_DIR% && prometheus --config.file=prometheus.yml"
echo [OK] Prometheus started

echo [5/5] Starting Grafana...
start "Grafana" cmd /c "cd C:\Program Files\GrafanaLabs\grafana\bin && grafana-server.exe"
echo [OK] Grafana started

echo.
echo ========================================
echo   All Services Started!
echo ========================================
echo.
echo MySQL:      localhost:3306
echo Redis:      localhost:6379
echo RabbitMQ:   http://localhost:15672 (guest/guest)
echo Prometheus: http://localhost:9090
echo Grafana:    http://localhost:3000 (admin/admin)
echo.
pause
```

### 2. 停止所有服务 `stop-all.bat`

```batch
@echo off
chcp 65001 > nul
echo ========================================
echo   Stopping DDD Platform All Services
echo ========================================
echo.

echo [1/5] Stopping MySQL...
net stop MySQL80 > nul 2>&1
echo [OK] MySQL stopped

echo [2/5] Stopping Redis...
taskkill /f /im redis-server.exe > nul 2>&1
echo [OK] Redis stopped

echo [3/5] Stopping RabbitMQ...
net stop RabbitMQ > nul 2>&1
echo [OK] RabbitMQ stopped

echo [4/5] Stopping Prometheus...
taskkill /f /im prometheus.exe > nul 2>&1
echo [OK] Prometheus stopped

echo [5/5] Stopping Grafana...
taskkill /f /im grafana-server.exe > nul 2>&1
echo [OK] Grafana stopped

echo.
echo ========================================
echo   All Services Stopped!
echo ========================================
pause
```

### 3. 启动应用 `start-app.bat`

```batch
@echo off
chcp 65001 > nul
echo ========================================
echo   Starting DDD Platform Application
echo ========================================
echo.

set PROJECT_DIR=%CD%

echo [1/3] Compiling project...
cd %PROJECT_DIR%
call mvn clean compile -DskipTests
if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed
    pause
    exit /b 1
)
echo [OK] Compilation success

echo [2/3] Packaging project...
call mvn package -DskipTests
if %errorlevel% neq 0 (
    echo [ERROR] Packaging failed
    pause
    exit /b 1
)
echo [OK] Packaging success

echo [3/3] Starting application...
cd %PROJECT_DIR%\ddd-bootstrap
start "DDD Platform App" cmd /k "mvn spring-boot:run"

echo.
echo ========================================
echo   Application Started!
echo ========================================
echo.
echo Swagger UI: http://localhost:8080/swagger-ui.html
echo API Docs:   http://localhost:8080/v3/api-docs
echo.
pause
```

### 4. 停止应用 `stop-app.bat`

```batch
@echo off
chcp 65001 > nul
echo ========================================
echo   Stopping DDD Platform Application
echo ========================================
echo.

taskkill /f /fi "WINDOWTITLE eq DDD Platform App" > nul 2>&1
taskkill /f /im java.exe > nul 2>&1

echo [OK] Application stopped
echo.
pause
```

### 5. Docker 方式启动 `start-docker.bat`

```batch
@echo off
chcp 65001 > nul
echo ========================================
echo   Starting DDD Platform with Docker
echo ========================================
echo.

echo [1/3] Building Docker image...
docker build -t ddd-platform:latest .

echo [2/3] Starting containers...
docker-compose up -d

echo [3/3] Checking status...
docker-compose ps

echo.
echo ========================================
echo   Docker Services Started!
echo ========================================
echo.
echo MySQL:      localhost:3306
echo Redis:      localhost:6379
echo RabbitMQ:   http://localhost:15672
echo App:        http://localhost:8080
echo.
pause
```

### 6. Docker 方式停止 `stop-docker.bat`

```batch
@echo off
chcp 65001 > nul
echo ========================================
echo   Stopping DDD Platform Docker Services
echo ========================================
echo.

docker-compose down

echo [OK] All containers stopped
echo.
pause
```

### 7. 查看日志 `logs.bat`

```batch
@echo off
chcp 65001 > nul
echo ========================================
echo   Viewing Application Logs
echo ========================================
echo.

echo [1] Application log
echo [2] Docker logs
echo [3] All logs
echo.
set /p choice="Select option (1-3): "

if "%choice%"=="1" (
    if exist "ddd-bootstrap\app.log" (
        type ddd-bootstrap\app.log
    ) else (
        echo No application log found
    )
)
if "%choice%"=="2" (
    docker-compose logs --tail=100
)
if "%choice%"=="3" (
    echo === Application Log ===
    type ddd-bootstrap\app.log
    echo.
    echo === Docker Log ===
    docker-compose logs --tail=50
)

pause
```

### 8. 一键启动所有 `run-all.bat`

```batch
@echo off
chcp 65001 > nul
echo ========================================
echo   DDD Platform - One Click Start
echo ========================================
echo.

REM 启动所有服务
call start-all.bat

REM 等待服务就绪
echo Waiting for services to be ready...
timeout /t 10 /nobreak > nul

REM 启动应用
call start-app.bat

echo.
echo ========================================
echo   DDD Platform Fully Operational!
echo ========================================
pause
```

### 9. 健康检查 `health-check.bat`

```batch
@echo off
chcp 65001 > nul
echo ========================================
echo   Health Check
echo ========================================
echo.

echo Checking MySQL...
curl -s -o nul -w "%%{http_code}" http://localhost:3306
if %errorlevel% equ 0 (
    echo [OK] MySQL is running
) else (
    echo [ERROR] MySQL is not responding
)

echo Checking Redis...
curl -s -o nul -w "%%{http_code}" http://localhost:6379
if %errorlevel% equ 0 (
    echo [OK] Redis is running
) else (
    echo [ERROR] Redis is not responding
)

echo Checking RabbitMQ...
curl -s -o nul -w "%%{http_code}" http://localhost:15672
if %errorlevel% equ 0 (
    echo [OK] RabbitMQ is running
) else (
    echo [ERROR] RabbitMQ is not responding
)

echo Checking Application...
curl -s -o nul -w "%%{http_code}" http://localhost:8080/actuator/health
if %errorlevel% equ 0 (
    echo [OK] Application is running
) else (
    echo [ERROR] Application is not responding
)

echo.
echo ========================================
echo   Health Check Complete
echo ========================================
pause
```

## 使用说明

| 脚本 | 用途 | 运行方式 |
|------|------|----------|
| `start-all.bat` | 启动所有依赖服务 | 双击运行 |
| `stop-all.bat` | 停止所有依赖服务 | 双击运行 |
| `start-app.bat` | 编译并启动应用 | 双击运行 |
| `stop-app.bat` | 停止应用 | 双击运行 |
| `start-docker.bat` | Docker方式启动 | 双击运行 |
| `stop-docker.bat` | Docker方式停止 | 双击运行 |
| `run-all.bat` | 一键启动全部 | 双击运行 |
| `health-check.bat` | 健康检查 | 双击运行 |

## 注意事项

1. **MySQL服务名**：如果是其他版本，修改 `net start MySQL80` 为实际服务名
2. **Redis路径**：确保 `redis-server` 在系统PATH中
3. **Grafana路径**：根据实际安装路径修改
4. **以管理员运行**：部分命令需要管理员权限

将这些 `.bat` 文件放在项目根目录 `C:\Users\Administrator\Desktop\ddd-platform\` 下即可使用。