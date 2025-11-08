# NGINX Ingress Controller Configuration Guide

## ✅ Installation Complete!

The NGINX Ingress Controller has been successfully installed on your Docker Desktop Kubernetes cluster.

## Status

```
✅ Pod: ingress-nginx-controller-7cfc9845c9-ksjtb (Running)
✅ Service: LoadBalancer with EXTERNAL-IP: localhost
✅ Ports: 80:30356/TCP, 443:31782/TCP
✅ Ingress Class: nginx
```

## Configure Local DNS

To access your application via the ingress, add the following line to your hosts file:

### Windows (Run as Administrator):
```powershell
# Open hosts file
notepad C:\Windows\System32\drivers\etc\hosts

# Add this line:
127.0.0.1 ead-app.example.com
```

### Or use PowerShell (Run as Administrator):
```powershell
Add-Content -Path C:\Windows\System32\drivers\etc\hosts -Value "`n127.0.0.1 ead-app.example.com"
```

## Access Your Application

After adding the hosts entry, you can access:

### Frontend:
```
http://ead-app.example.com
```

### Backend API:
```
http://ead-app.example.com/api/actuator/health
```

## Verify Ingress Configuration

```powershell
# Check ingress status
kubectl get ingress -n ead-application

# Describe ingress details
kubectl describe ingress ead-ingress -n ead-application

# Check ingress controller logs
kubectl logs -n ingress-nginx -l app.kubernetes.io/component=controller -f
```

## Current Ingress Rules

**Host**: `ead-app.example.com`

| Path | Backend Service | Pods |
|------|----------------|------|
| `/api` | backend-service:8080 | 2 pods |
| `/` | frontend-service:3000 | 2 pods |

## Test Ingress

```powershell
# Test backend health (after adding hosts entry)
curl http://ead-app.example.com/api/actuator/health

# Test frontend
curl http://ead-app.example.com

# Or open in browser
Start-Process "http://ead-app.example.com"
```

## Ingress Controller Details

```
Namespace: ingress-nginx
Deployment: ingress-nginx-controller
Replicas: 1/1
Service Type: LoadBalancer
External IP: localhost
HTTP Port: 80 (NodePort: 30356)
HTTPS Port: 443 (NodePort: 31782)
```

## TLS Configuration (Optional)

Currently, TLS is configured but requires cert-manager and a valid certificate. For local development:

### Option 1: Disable SSL Redirect
Edit the ingress to remove SSL redirect for local testing:
```powershell
kubectl edit ingress ead-ingress -n ead-application
# Remove: nginx.ingress.kubernetes.io/ssl-redirect: "true"
```

### Option 2: Install cert-manager
```powershell
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.13.0/cert-manager.yaml
```

## Troubleshooting

### Issue: Can't access via domain

**Solution 1**: Verify hosts file entry
```powershell
Get-Content C:\Windows\System32\drivers\etc\hosts | Select-String "ead-app"
```

**Solution 2**: Check ingress controller is running
```powershell
kubectl get pods -n ingress-nginx
```

**Solution 3**: Test with direct localhost
```powershell
curl http://localhost/api/actuator/health -H "Host: ead-app.example.com"
```

### Issue: 404 Not Found

**Solution**: Check backend pods are running
```powershell
kubectl get pods -n ead-application
```

### Issue: SSL/TLS errors

**Solution**: Temporarily disable SSL redirect or access via HTTP
```powershell
# Access via HTTP
http://ead-app.example.com

# Or edit ingress
kubectl edit ingress ead-ingress -n ead-application
```

## Ingress Logs

```powershell
# Watch ingress controller logs
kubectl logs -n ingress-nginx deployment/ingress-nginx-controller -f

# Check for errors
kubectl logs -n ingress-nginx deployment/ingress-nginx-controller | Select-String -Pattern "error|Error|ERROR"
```

## Ingress Controller Management

### Restart Ingress Controller
```powershell
kubectl rollout restart deployment ingress-nginx-controller -n ingress-nginx
```

### Scale Ingress Controller
```powershell
kubectl scale deployment ingress-nginx-controller -n ingress-nginx --replicas=2
```

### Uninstall Ingress Controller
```powershell
kubectl delete namespace ingress-nginx
kubectl delete clusterrole ingress-nginx
kubectl delete clusterrolebinding ingress-nginx
kubectl delete validatingwebhookconfiguration ingress-nginx-admission
```

## Next Steps

1. ✅ **DONE**: Install NGINX Ingress Controller
2. **TODO**: Add `127.0.0.1 ead-app.example.com` to hosts file
3. **TODO**: Test application via ingress
4. **TODO**: Configure TLS certificates (optional for local dev)
5. **TODO**: Setup monitoring for ingress metrics

---

**Installation Date**: November 8, 2025
**Ingress Controller Version**: v1.8.1
**Status**: ✅ Running and Ready
