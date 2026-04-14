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