# EAD Automobile Management System - Kubernetes Deployment Guide

## Overview
This guide covers deploying the EAD application to Kubernetes using Kubernetes manifests.

## Prerequisites

### 1. Kubernetes Cluster
- Kubernetes 1.25+
- kubectl installed and configured
- At least 4GB RAM and 2 CPUs available

### 2. Required Tools
```powershell
# Install kubectl (if not already installed)
choco install kubernetes-cli

# Verify installation
kubectl version --client
```

### 3. Container Registry
Your Docker images must be pushed to a registry:
```powershell
# Build images
cd backend
docker build -t dila72/ead-backend:latest .

cd ..\frontend
docker build -t dila72/ead-frontend:latest .

# Push to Docker Hub (or your registry)
docker push dila72/ead-backend:latest
docker push dila72/ead-frontend:latest
```

## Deployment Steps

### Step 1: Update Configuration
Edit the secrets file with your actual credentials:
```powershell
# Navigate to kubernetes directory
cd kubernetes

# Edit 02-secrets.yaml and replace placeholder values
notepad 02-secrets.yaml
```

Required secrets:
- `POSTGRES_PASSWORD`: Your database password (e.g., dila2001)
- `JWT_SECRET`: Random secure string for JWT (generate with: `openssl rand -base64 32`)
- `CLOUDINARY_*`: Your Cloudinary credentials
- `GEMINI_API_KEY`: Your Google Gemini API key
- `SPRING_MAIL_*`: Your email service credentials

### Step 2: Apply Manifests
```powershell
# Apply all manifests in order
kubectl apply -f 00-namespace.yaml
kubectl apply -f 01-configmap.yaml
kubectl apply -f 02-secrets.yaml
kubectl apply -f 03-postgres.yaml
kubectl apply -f 04-backend.yaml
kubectl apply -f 05-frontend.yaml
kubectl apply -f 06-ingress.yaml

# Or apply all at once
kubectl apply -f .
```

### Step 3: Verify Deployment
```powershell
# Check namespace
kubectl get namespace ead-application

# Check all resources
kubectl get all -n ead-application

# Check pods status
kubectl get pods -n ead-application

# Check services
kubectl get svc -n ead-application

# Check ingress
kubectl get ingress -n ead-application
```

## Post-Deployment Configuration

### 1. Setup Ingress Controller (if not installed)
```powershell
# Install NGINX Ingress Controller
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo update
helm install nginx-ingress ingress-nginx/ingress-nginx

# Get LoadBalancer IP
kubectl get svc nginx-ingress-ingress-nginx-controller
```

### 2. Configure DNS
Point your domain to the LoadBalancer IP:
```
A Record: ead-app.yourdomain.com -> <LoadBalancer-IP>
```

### 3. Setup TLS (Optional with cert-manager)
```powershell
# Install cert-manager
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.13.0/cert-manager.yaml

# Create ClusterIssuer for Let's Encrypt
kubectl apply -f - <<EOF
apiVersion: cert-manager.io/v1
kind: ClusterIssuer
metadata:
  name: letsencrypt-prod
spec:
  acme:
    server: https://acme-v02.api.letsencrypt.org/directory
    email: your-email@example.com
    privateKeySecretRef:
      name: letsencrypt-prod
    solvers:
    - http01:
        ingress:
          class: nginx
EOF
```

## Monitoring and Maintenance

### Check Application Health
```powershell
# Backend health
kubectl exec -n ead-application -it <backend-pod> -- curl http://localhost:8080/actuator/health

# Database connection
kubectl exec -n ead-application -it postgres-0 -- psql -U postgres -d ead_db -c "SELECT 1;"

# Frontend access
kubectl port-forward -n ead-application svc/frontend-service 3000:3000
# Visit http://localhost:3000
```

### View Logs
```powershell
# Backend logs
kubectl logs -n ead-application -l app=backend -f

# Frontend logs
kubectl logs -n ead-application -l app=frontend -f

# PostgreSQL logs
kubectl logs -n ead-application postgres-0 -f
```

### Scale Applications
```powershell
# Manual scaling
kubectl scale deployment backend -n ead-application --replicas=3
kubectl scale deployment frontend -n ead-application --replicas=3
```

### Update Application
```powershell
# Method 1: Update image tag
kubectl set image deployment/backend backend=dila72/ead-backend:v2.0 -n ead-application
kubectl set image deployment/frontend frontend=dila72/ead-frontend:v2.0 -n ead-application

# Method 2: Edit deployment directly
kubectl edit deployment backend -n ead-application
```

## Backup and Restore

### Backup Database
```powershell
# Create backup
kubectl exec -n ead-application postgres-0 -- pg_dump -U postgres ead_db > backup.sql

# Or with timestamp
$timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
kubectl exec -n ead-application postgres-0 -- pg_dump -U postgres ead_db > "backup_$timestamp.sql"
```

### Restore Database
```powershell
# Restore from backup
Get-Content backup.sql | kubectl exec -i -n ead-application postgres-0 -- psql -U postgres ead_db
```

## Troubleshooting

### Pods Not Starting
```powershell
# Describe pod for events
kubectl describe pod <pod-name> -n ead-application

# Check pod logs
kubectl logs <pod-name> -n ead-application

# Check if images can be pulled
kubectl get events -n ead-application --sort-by='.lastTimestamp'
```

### Database Connection Issues
```powershell
# Test database connectivity
kubectl run -it --rm debug --image=postgres:17-alpine --restart=Never -n ead-application -- psql -h postgres-service -U postgres -d ead_db

# Check secrets
kubectl get secret ead-secrets -n ead-application -o yaml
```

### Ingress Not Working
```powershell
# Check ingress status
kubectl describe ingress ead-ingress -n ead-application

# Check ingress controller logs
kubectl logs -n ingress-nginx -l app.kubernetes.io/component=controller

# Test backend service directly
kubectl port-forward -n ead-application svc/backend-service 8080:8080
# Visit http://localhost:8080/actuator/health
```

### HPA Not Scaling
```powershell
# Check HPA status
kubectl get hpa -n ead-application

# Describe HPA
kubectl describe hpa backend-hpa -n ead-application

# Ensure metrics-server is installed
kubectl top nodes
kubectl top pods -n ead-application
```

## Cleanup

### Delete All Resources
```powershell
# Delete all resources
cd kubernetes
kubectl delete -f .

# Or delete namespace (removes everything)
kubectl delete namespace ead-application
```

## Production Considerations

### 1. Resource Limits
- Adjust resource requests/limits based on actual usage
- Monitor with Prometheus/Grafana

### 2. Storage Class
- Use appropriate storage class for your cloud provider:
  - AWS: `gp3` or `gp2`
  - GCP: `standard` or `pd-ssd`
  - Azure: `default` or `managed-premium`

### 3. Security
- Use private container registry
- Enable Pod Security Standards
- Configure Network Policies
- Rotate secrets regularly

### 4. High Availability
- Run multiple replicas across different nodes
- Use Pod Disruption Budgets
- Configure pod anti-affinity

### 5. Monitoring
- Install Prometheus + Grafana
- Setup alerts for pod health
- Monitor resource usage

## Support and Documentation

- Backend API Docs: `/swagger-ui.html` (when deployed)
- Health Check: `/actuator/health`
- RAG Chatbot Guide: See `backend/RAG_CHATBOT_GUIDE.md`
- Testing Guide: See `backend/TESTING_README.md`

## Architecture

```
[Ingress Controller]
       |
       ├── /api → [Backend Service] → [Backend Pods (2-5)]
       |                                      |
       |                                      ↓
       |                               [PostgreSQL StatefulSet]
       |
       └── /    → [Frontend Service] → [Frontend Pods (2-5)]
```

## Next Steps

1. ✅ Deploy to Kubernetes cluster
2. ✅ Configure ingress and DNS
3. ✅ Setup TLS certificates
4. ✅ Configure monitoring
5. ✅ Setup backup strategy
6. ✅ Implement CI/CD pipeline
