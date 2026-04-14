@echo off
chcp 65001 > nul
echo ========================================
echo   Starting DDD Platform Application
echo ========================================
echo.

set PROJECT_DIR=%CD%

echo [1/3] Compiling project...
cd %PROJECT_DIR%
call mvn clean compile -DskipTests
if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed
    pause
    exit /b 1
)
echo [OK] Compilation success

echo [2/3] Packaging project...
call mvn package -DskipTests
if %errorlevel% neq 0 (
    echo [ERROR] Packaging failed
    pause
    exit /b 1
)
echo [OK] Packaging success

echo [3/3] Starting application...
cd %PROJECT_DIR%\ddd-bootstrap
start "DDD Platform App" cmd /k "mvn spring-boot:run"

echo.
echo ========================================
echo   Application Started!
echo ========================================
echo.
echo Swagger UI: http://localhost:8080/swagger-ui.html
echo API Docs:   http://localhost:8080/v3/api-docs
echo.
pause