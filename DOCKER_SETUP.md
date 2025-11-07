# Docker Setup Guide for EAD Application

This guide provides instructions for containerizing and running the EAD Application (Enterprise Application Development - Automobile Management System) using Docker and Docker Compose.

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

- **Docker**: Version 20.10 or higher
- **Docker Compose**: Version 2.0 or higher

### Installation Instructions

#### Windows
Download and install [Docker Desktop for Windows](https://www.docker.com/products/docker-desktop/)

#### macOS
Download and install [Docker Desktop for Mac](https://www.docker.com/products/docker-desktop/)

#### Linux
```bash
# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

Verify installation:
```bash
docker --version
docker-compose --version
```

## 🏗️ Architecture Overview

The application consists of three containerized services:

1. **PostgreSQL Database** (`postgres`) - Port 5432
   - Stores all application data
   - Persistent volume for data retention

2. **Spring Boot Backend** (`backend`) - Port 8080
   - RESTful API server
   - JWT authentication
   - Email and Cloudinary integration

3. **Next.js Frontend** (`frontend`) - Port 3000
   - React-based user interface
   - Server-side rendering

All services are connected through a dedicated Docker network (`ead-network`).

## ⚙️ Configuration

### 1. Environment Setup

Copy the example environment file and configure it:

```bash
# Copy the example file
cp .env.example .env

# Edit the .env file with your actual values
# Windows: notepad .env
# Linux/Mac: nano .env
```

### 2. Required Environment Variables

Edit `.env` file and provide values for:

#### Database Configuration
```env
POSTGRES_DB=ead_automobile
POSTGRES_USER=postgres
POSTGRES_PASSWORD=your_secure_password
```

#### Email Configuration (Gmail)
Generate an [App Password](https://support.google.com/accounts/answer/185833?hl=en) for Gmail:
```env
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password
```

#### Cloudinary Configuration
Sign up at [Cloudinary](https://cloudinary.com/) and get your credentials:
```env
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
```

## 🚀 Getting Started

### Option 1: Quick Start (Development)

Build and start all services with one command:

```bash
docker-compose up --build
```

This will:
- Build Docker images for frontend and backend
- Start PostgreSQL, backend, and frontend containers
- Create necessary networks and volumes
- Show logs from all services

### Option 2: Detached Mode (Production-like)

Run services in the background:

```bash
docker-compose up --build -d
```

Check service status:
```bash
docker-compose ps
```

View logs:
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f postgres
```

## 📊 Service Health Checks

All services include health checks. Check their status:

```bash
# Using docker-compose
docker-compose ps

# Using docker directly
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
```

## 🔗 Accessing the Application

Once all services are healthy:

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **Backend Health**: http://localhost:8080/actuator/health
- **H2 Console**: http://localhost:8080/h2-console

### Default Admin Credentials
- Username: `admin` (or value from `ADMIN_USERNAME`)
- Password: `admin123` (or value from `ADMIN_PASSWORD`)

## 🛠️ Common Commands

### Building Services

```bash
# Build all services
docker-compose build

# Build specific service
docker-compose build backend
docker-compose build frontend

# Build without cache (clean build)
docker-compose build --no-cache
```

### Starting/Stopping Services

```bash
# Start services
docker-compose up

# Start in detached mode
docker-compose up -d

# Stop services (keeps containers)
docker-compose stop

# Stop and remove containers
docker-compose down

# Stop and remove containers, volumes, and networks
docker-compose down -v
```

### Managing Individual Services

```bash
# Restart a service
docker-compose restart backend

# Stop a service
docker-compose stop frontend

# Start a stopped service
docker-compose start frontend

# Rebuild and restart a service
docker-compose up -d --build backend
```

### Viewing Logs

```bash
# View all logs
docker-compose logs

# Follow logs in real-time
docker-compose logs -f

# Last 100 lines
docker-compose logs --tail=100

# Service-specific logs
docker-compose logs -f backend
```

### Executing Commands in Containers

```bash
# Access backend shell
docker-compose exec backend sh

# Access database
docker-compose exec postgres psql -U postgres -d ead_automobile

# Run Maven commands in backend
docker-compose exec backend mvn --version

# Access frontend shell
docker-compose exec frontend sh
```

## 🔍 Troubleshooting

### Service Won't Start

1. **Check logs**:
   ```bash
   docker-compose logs backend
   ```

2. **Verify environment variables**:
   ```bash
   docker-compose config
   ```

3. **Check port conflicts**:
   ```bash
   # Windows
   netstat -ano | findstr :8080
   
   # Linux/Mac
   lsof -i :8080
   ```

### Database Connection Issues

1. **Ensure PostgreSQL is healthy**:
   ```bash
   docker-compose ps postgres
   ```

2. **Check database logs**:
   ```bash
   docker-compose logs postgres
   ```

3. **Verify connection from backend**:
   ```bash
   docker-compose exec backend ping postgres
   ```

### Build Failures

1. **Clean Docker build cache**:
   ```bash
   docker-compose build --no-cache
   ```

2. **Remove old images**:
   ```bash
   docker system prune -a
   ```

3. **Check disk space**:
   ```bash
   docker system df
   ```

### Frontend Not Loading

1. **Check if backend is reachable**:
   ```bash
   curl http://localhost:8080/actuator/health
   ```

2. **Verify API URL configuration** in `.env`:
   ```env
   NEXT_PUBLIC_API_URL=http://localhost:8080
   ```

3. **Rebuild frontend**:
   ```bash
   docker-compose up -d --build frontend
   ```

## 🗄️ Data Persistence

PostgreSQL data is stored in a Docker volume (`ead-postgres-data`) and persists across container restarts.

### Backing Up Database

```bash
# Create backup
docker-compose exec postgres pg_dump -U postgres ead_automobile > backup.sql

# Restore backup
docker-compose exec -T postgres psql -U postgres ead_automobile < backup.sql
```

### Reset Database

```bash
# Stop and remove volumes
docker-compose down -v

# Start fresh
docker-compose up -d
```

## 🔧 Advanced Configuration

### Custom Ports

Edit `.env` file:
```env
FRONTEND_PORT=3001
BACKEND_PORT=8081
POSTGRES_PORT=5433
```

Then restart:
```bash
docker-compose down
docker-compose up -d
```

### Production Deployment

1. **Use production environment variables**:
   ```env
   NODE_ENV=production
   SHOW_SQL=false
   ```

2. **Enable resource limits** in `docker-compose.yml`:
   ```yaml
   backend:
     deploy:
       resources:
         limits:
           cpus: '2'
           memory: 2G
   ```

3. **Use secrets for sensitive data** instead of environment variables.

### Scaling Services

```bash
# Scale backend to 3 instances
docker-compose up -d --scale backend=3
```

Note: Requires load balancer configuration for multiple backend instances.

## 🧹 Cleanup

### Remove Everything

```bash
# Stop and remove containers, networks, volumes
docker-compose down -v

# Remove images
docker-compose down --rmi all -v

# Clean Docker system
docker system prune -a --volumes
```

### Remove Specific Volumes

```bash
docker volume ls
docker volume rm ead-postgres-data
docker volume rm ead-backend-logs
```

## 📚 Additional Resources

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Spring Boot with Docker](https://spring.io/guides/gs/spring-boot-docker/)
- [Next.js Docker Deployment](https://nextjs.org/docs/deployment#docker-image)

## 🆘 Support

If you encounter issues:

1. Check the logs: `docker-compose logs -f`
2. Verify configuration: `docker-compose config`
3. Review health checks: `docker-compose ps`
4. Consult application-specific documentation in `backend/` and `frontend/` directories

## 📝 Notes

- First build may take 10-20 minutes depending on internet speed
- Backend waits for PostgreSQL to be healthy before starting
- Frontend waits for backend to be healthy before starting
- All services have automatic restart policies configured
- Health checks ensure services are truly ready before accepting traffic
