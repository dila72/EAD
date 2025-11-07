@echo off
REM Testing Execution Script for EAD Automobile Backend
REM This script helps run tests and generate reports

echo ========================================
echo EAD Automobile Backend - Testing Suite
echo ========================================
echo.

echo Step 1: Creating test database...
echo.
psql -U postgres -c "CREATE DATABASE ead_automobile_test;" 2>nul
if %errorlevel% equ 0 (
    echo [SUCCESS] Test database created successfully
) else (
    echo [INFO] Test database may already exist
)
echo.

echo Step 2: Running all tests with coverage...
echo.
call mvn clean test jacoco:report
if %errorlevel% neq 0 (
    echo [ERROR] Tests failed! Check the output above.
    pause
    exit /b 1
)
echo.

echo Step 3: Generating test report...
echo.
call mvn surefire-report:report
echo.

echo ========================================
echo Testing Complete!
echo ========================================
echo.
echo Coverage Report: target\site\jacoco\index.html
echo Test Results: target\site\surefire-report.html
echo.
echo Opening reports in browser...
start target\site\jacoco\index.html
timeout /t 2 >nul
start target\site\surefire-report.html
echo.
echo Press any key to exit...
pause >nul
