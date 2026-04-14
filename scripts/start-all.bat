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