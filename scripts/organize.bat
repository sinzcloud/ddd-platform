@echo off
chcp 65001 > nul
echo ========================================
echo   Organizing Project Structure
echo ========================================
echo.

REM 创建目录
mkdir docs 2>nul
mkdir scripts 2>nul
mkdir config 2>nul

REM 移动文档文件
move *.md docs\ 2>nul
echo [OK] Moved documentation to docs/

REM 移动脚本文件
move *.bat scripts\ 2>nul
move *.sh scripts\ 2>nul
echo [OK] Moved scripts to scripts/

REM 移动配置文件
move *.yml config\ 2>nul
move *.yaml config\ 2>nul
move Dockerfile config\ 2>nul
echo [OK] Moved configs to config/

echo.
echo ========================================
echo   Organization Complete!
echo ========================================
echo.
echo New structure:
echo   - docs/      (documentation)
echo   - scripts/   (batch/shell scripts)
echo   - config/    (configuration files)
echo.
pause