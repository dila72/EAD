@echo off
REM ===========================================
REM EAD Application - Docker Quick Start Script (Windows)
REM ===========================================

echo.
echo ========================================
echo EAD Application Docker Setup
echo ========================================
echo.

REM Check if Docker is installed
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Docker is not installed
    echo Please install Docker Desktop from https://www.docker.com/products/docker-desktop/
    pause
    exit /b 1
)

REM Check if Docker Compose is installed
docker-compose --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Docker Compose is not installed
    pause
    exit /b 1
)

echo [OK] Docker and Docker Compose are installed
echo.

REM Check if .env file exists
if not exist .env (
    echo [WARNING] .env file not found
    echo Creating .env from .env.example...
    copy .env.example .env
    echo [OK] .env file created
    echo.
    echo [IMPORTANT] Please edit .env file and add your actual configuration values
    echo    Required: Database password, Email credentials, Cloudinary credentials
    echo.
    pause
)

echo.
echo Building and starting services...
echo This may take 10-20 minutes on first run
echo.

REM Build and start services
docker-compose up --build -d

if %errorlevel% equ 0 (
    echo.
    echo [SUCCESS] Services started successfully!
    echo.
    echo Service Status:
    docker-compose ps
    echo.
    echo Access the application:
    echo    Frontend:  http://localhost:3000
    echo    Backend:   http://localhost:8080
    echo    Health:    http://localhost:8080/actuator/health
    echo.
    echo Useful commands:
    echo    View logs:        docker-compose logs -f
    echo    Stop services:    docker-compose stop
    echo    Restart services: docker-compose restart
    echo    Remove all:       docker-compose down -v
    echo.
    echo Services are starting up... This may take 1-2 minutes
    echo You can check the logs with: docker-compose logs -f
    echo.
) else (
    echo.
    echo [ERROR] Failed to start services
    echo Check the error messages above and verify your .env configuration
    pause
    exit /b 1
)

pause
