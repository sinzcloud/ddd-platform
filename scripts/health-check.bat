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