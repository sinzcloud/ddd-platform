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