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