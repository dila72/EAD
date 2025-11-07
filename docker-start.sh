#!/bin/bash

# ===========================================
# EAD Application - Docker Quick Start Script
# ===========================================

echo "🚀 EAD Application Docker Setup"
echo "================================"
echo ""

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "❌ Error: Docker is not installed"
    echo "Please install Docker from https://www.docker.com/products/docker-desktop/"
    exit 1
fi

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Error: Docker Compose is not installed"
    exit 1
fi

echo "✅ Docker and Docker Compose are installed"
echo ""

# Check if .env file exists
if [ ! -f .env ]; then
    echo "⚠️  .env file not found"
    echo "Creating .env from .env.example..."
    cp .env.example .env
    echo "✅ .env file created"
    echo ""
    echo "⚠️  IMPORTANT: Please edit .env file and add your actual configuration values"
    echo "   Required: Database password, Email credentials, Cloudinary credentials"
    echo ""
    read -p "Press Enter after you have configured .env file..."
fi

echo ""
echo "🏗️  Building and starting services..."
echo "This may take 10-20 minutes on first run"
echo ""

# Build and start services
docker-compose up --build -d

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Services started successfully!"
    echo ""
    echo "📊 Service Status:"
    docker-compose ps
    echo ""
    echo "🌐 Access the application:"
    echo "   Frontend:  http://localhost:3000"
    echo "   Backend:   http://localhost:8080"
    echo "   Health:    http://localhost:8080/actuator/health"
    echo ""
    echo "📝 Useful commands:"
    echo "   View logs:        docker-compose logs -f"
    echo "   Stop services:    docker-compose stop"
    echo "   Restart services: docker-compose restart"
    echo "   Remove all:       docker-compose down -v"
    echo ""
    echo "⏳ Services are starting up... This may take 1-2 minutes"
    echo "   You can check the logs with: docker-compose logs -f"
else
    echo ""
    echo "❌ Error: Failed to start services"
    echo "Check the error messages above and verify your .env configuration"
    exit 1
fi
