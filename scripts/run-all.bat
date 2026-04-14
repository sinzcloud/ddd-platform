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