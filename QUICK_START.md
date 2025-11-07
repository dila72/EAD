# Quick Start Guide - Docker Setup

## ✅ What's Already Done

1. ✅ Docker configuration files created
2. ✅ Environment variables configured (.env file)
3. ✅ Dockerfile for backend created
4. ✅ Dockerfile for frontend created
5. ✅ docker-compose.yml created

## 🚀 Steps to Get Started

### Step 1: Start Docker Desktop

**You need to do this first!**

1. **Open Docker Desktop** application from your Start menu or desktop
2. **Wait for Docker to fully start**
   - Look for the Docker whale icon in your system tray (bottom-right)
   - When it's running, the icon will be steady (not animated)
   - Usually takes 30-60 seconds to start

### Step 2: Build and Start All Services

Once Docker Desktop is running, open PowerShell in your project directory and run:

```powershell
cd "C:\Users\nilanga dilhara\EAD"
docker-compose up --build -d
```

**What this does:**
- Builds Docker images for backend and frontend
- Pulls PostgreSQL image
- Creates network and volumes
- Starts all 3 services (PostgreSQL, Backend, Frontend)
- Runs in background (-d flag)

**First build will take 10-20 minutes** (downloads dependencies, builds everything)

### Step 3: Monitor the Build Progress

While building, you can watch the logs:

```powershell
# Watch all logs
docker-compose logs -f

# Watch specific service
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f postgres
```

Press `Ctrl+C` to stop watching logs (containers keep running)

### Step 4: Check Service Status

```powershell
docker-compose ps
```

You should see all 3 services with status "Up (healthy)"

### Step 5: Access the Application

Once all services are healthy (usually 1-2 minutes after build completes):

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **Backend Health**: http://localhost:8080/actuator/health
- **H2 Console**: http://localhost:8080/h2-console

### Step 6: Login

Use these credentials:
- **Username**: admin
- **Password**: admin123

## 📊 Useful Commands

### View Running Containers
```powershell
docker-compose ps
```

### View Logs
```powershell
# All services
docker-compose logs -f

# Last 100 lines
docker-compose logs --tail=100

# Specific service
docker-compose logs -f backend
```

### Stop Services
```powershell
# Stop (keeps containers)
docker-compose stop

# Stop and remove containers
docker-compose down

# Stop, remove containers AND volumes (fresh start)
docker-compose down -v
```

### Restart Services
```powershell
# Restart all
docker-compose restart

# Restart specific service
docker-compose restart backend
```

### Rebuild After Code Changes
```powershell
# Rebuild and restart specific service
docker-compose up -d --build backend

# Rebuild everything
docker-compose up -d --build
```

### Access Container Shell
```powershell
# Backend shell
docker-compose exec backend sh

# Frontend shell
docker-compose exec frontend sh

# Database
docker-compose exec postgres psql -U postgres -d ead_automobile
```

## 🔍 Troubleshooting

### Problem: "Docker daemon is not running"
**Solution**: Start Docker Desktop application and wait for it to fully load

### Problem: Build fails with "port already in use"
**Solution**: 
```powershell
# Check what's using the port
netstat -ano | findstr :8080
netstat -ano | findstr :3000
netstat -ano | findstr :5432

# Stop your local backend/frontend if running
# Or change ports in .env file
```

### Problem: Backend can't connect to database
**Solution**:
```powershell
# Check if postgres is healthy
docker-compose ps postgres

# View postgres logs
docker-compose logs postgres

# Restart backend
docker-compose restart backend
```

### Problem: Frontend shows "Cannot connect to backend"
**Solution**:
```powershell
# Check backend health
curl http://localhost:8080/actuator/health

# Check backend logs
docker-compose logs backend

# Verify environment variable
docker-compose exec frontend env | findstr API_URL
```

### Problem: Want to start fresh
**Solution**:
```powershell
# Remove everything (including database data)
docker-compose down -v

# Rebuild from scratch
docker-compose up --build -d
```

## 📝 Environment Configuration

Your `.env` file is already configured with:

- **Database**: PostgreSQL with password from application.properties
- **Email**: Your Gmail SMTP credentials
- **Ports**: 3000 (frontend), 8080 (backend), 5432 (database)
- **Admin**: admin / admin123

To change any configuration, edit `.env` file and restart:

```powershell
notepad .env
docker-compose down
docker-compose up -d
```

## 🎯 Next Steps After Starting

1. **Access frontend**: http://localhost:3000
2. **Login with admin credentials**
3. **Test the API**: http://localhost:8080/actuator/health
4. **Check the documentation**:
   - `DOCKER_SETUP.md` - Comprehensive guide
   - `DOCKER_IMPLEMENTATION.md` - Technical details

## 💡 Quick Reference

### Start Everything
```powershell
docker-compose up -d
```

### Stop Everything
```powershell
docker-compose down
```

### View Logs
```powershell
docker-compose logs -f
```

### Check Status
```powershell
docker-compose ps
```

### Fresh Start
```powershell
docker-compose down -v
docker-compose up --build -d
```

---

**Ready to start?** Make sure Docker Desktop is running, then execute:

```powershell
cd "C:\Users\nilanga dilhara\EAD"
docker-compose up --build -d
```

Then watch the progress:
```powershell
docker-compose logs -f
```

Wait for "Started EadBackendApplication" message, then access http://localhost:3000
