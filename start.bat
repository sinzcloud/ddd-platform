@echo off
chcp 65001 > nul
title DDD Platform Launcher

echo ========================================
echo   DDD Platform - Quick Start
echo ========================================
echo.

set PROJECT_DIR=%CD%

REM 检查是否已编译
echo [1/4] Checking project...
if not exist "%PROJECT_DIR%\ddd-bootstrap\target\*.jar" (
    echo Building project for first time...
    call mvn clean package -DskipTests
    if %errorlevel% neq 0 (
        echo [ERROR] Build failed
        pause
        exit /b 1
    )
)
echo [OK] Project ready

REM 检查MySQL
echo [2/4] Checking MySQL...
net start MySQL80 > nul 2>&1
echo [OK] MySQL ready

REM 检查Redis
echo [3/4] Checking Redis...
tasklist /fi "imagename eq redis-server.exe" | find "redis-server" > nul
if %errorlevel% neq 0 (
    start "Redis" cmd /c "redis-server"
    timeout /t 2 /nobreak > nul
)
echo [OK] Redis ready

REM 启动应用
echo [4/4] Starting application...
cd %PROJECT_DIR%\ddd-bootstrap
start "DDD Platform" cmd /k "mvn spring-boot:run"

echo.
echo ========================================
echo   Application Starting!
echo ========================================
echo.
echo Swagger: http://localhost:8080/swagger-ui.html
echo.
echo Close this window to stop the application
echo ========================================

pause