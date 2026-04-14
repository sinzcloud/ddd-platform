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