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