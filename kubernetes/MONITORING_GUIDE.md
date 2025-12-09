# 📊 EAD Application - Monitoring Setup Guide

## Overview

This monitoring setup includes:
- **Prometheus**: Metrics collection and storage
- **Grafana**: Visualization and dashboards
- **PostgreSQL Exporter**: Database metrics

## Architecture

```
┌─────────────────────────────────────────────┐
│         Grafana (Port 3001)                 │
│         Dashboards & Visualization          │
└──────────────┬──────────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────────┐
│         Prometheus (Port 9090)              │
│         Metrics Collection & Storage        │
└──┬────────┬────────┬────────────────────────┘
   │        │        │
   ▼        ▼        ▼
┌──────┐ ┌──────┐ ┌─────────────┐
│Backend│ │Postgres│ │ Kubernetes │
│:8080  │ │:9187   │ │  Cluster   │
└──────┘ └────────┘ └────────────┘
```

## 📦 Components

### 1. Prometheus
- **Image**: `prom/prometheus:v2.47.0`
- **Port**: 9090
- **Storage**: 5Gi PVC
- **Resources**: 512Mi-1Gi RAM, 250m-500m CPU

**Scrape Targets**:
- Kubernetes API server
- Kubernetes nodes
- Kubernetes pods (with annotations)
- Backend Spring Boot Actuator (`/actuator/prometheus`)
- PostgreSQL Exporter

### 2. Grafana
- **Image**: `grafana/grafana:10.1.0`
- **Port**: 3001
- **Default Credentials**: admin/admin
- **Storage**: 2Gi PVC
- **Resources**: 256Mi-512Mi RAM, 100m-200m CPU

**Features**:
- Pre-configured Prometheus datasource
- Pre-loaded Kubernetes dashboard
- Anonymous access enabled for easy testing

### 3. PostgreSQL Exporter
- **Image**: `prometheuscommunity/postgres-exporter:v0.15.0`
- **Port**: 9187
- **Resources**: 128Mi-256Mi RAM, 100m-200m CPU

## 🚀 Deployment

### Deploy Monitoring Stack

```powershell
# Deploy in order
kubectl apply -f kubernetes/07-prometheus.yaml
kubectl apply -f kubernetes/08-grafana.yaml
kubectl apply -f kubernetes/09-postgres-exporter.yaml

# Verify deployment
kubectl get pods -n ead-application -l app=prometheus
kubectl get pods -n ead-application -l app=grafana
kubectl get pods -n ead-application -l app=postgres-exporter
```

### Update Backend Deployment

The backend deployment has been updated with Prometheus annotations:
```yaml
annotations:
  prometheus.io/scrape: "true"
  prometheus.io/port: "8080"
  prometheus.io/path: "/actuator/prometheus"
```

Apply the updated backend:
```powershell
kubectl apply -f kubernetes/04-backend.yaml
```

## 🔍 Accessing Monitoring Tools

### Method 1: Port Forwarding (Recommended for Local)

#### Prometheus UI:
```powershell
kubectl port-forward -n ead-application svc/prometheus-service 9090:9090
```
Access at: http://localhost:9090

#### Grafana:
```powershell
kubectl port-forward -n ead-application svc/grafana-service 3001:3001
```
Access at: http://localhost:3001
- **Username**: admin
- **Password**: admin

### Method 2: Ingress (Production)

Add to your ingress configuration:
```yaml
- host: prometheus.ead-app.example.com
  http:
    paths:
    - path: /
      pathType: Prefix
      backend:
        service:
          name: prometheus-service
          port:
            number: 9090
- host: grafana.ead-app.example.com
  http:
    paths:
    - path: /
      pathType: Prefix
      backend:
        service:
          name: grafana-service
          port:
            number: 3001
```

## 📊 Key Metrics to Monitor

### Backend Application Metrics

**Available at**: http://localhost:8080/actuator/prometheus

Key metrics:
- `http_server_requests_seconds_*` - HTTP request metrics
- `jvm_memory_*` - JVM memory usage
- `jvm_threads_*` - Thread counts
- `process_cpu_usage` - CPU usage
- `hikaricp_connections_*` - Database connection pool
- `logback_events_total` - Log events

### PostgreSQL Metrics

Key metrics:
- `pg_up` - Database availability
- `pg_stat_database_*` - Database statistics
- `pg_stat_activity_count` - Active connections
- `pg_locks_count` - Lock information
- `pg_database_size_bytes` - Database size

### Kubernetes Metrics

Key metrics:
- `container_cpu_usage_seconds_total` - CPU usage
- `container_memory_usage_bytes` - Memory usage
- `kube_pod_status_phase` - Pod status
- `kube_deployment_status_replicas` - Replica counts

## 📈 Grafana Dashboards

### Pre-installed Dashboard
1. **EAD Application - Kubernetes Cluster**
   - CPU usage by pod
   - Memory usage by pod
   - Pre-configured and ready to use

### Import Additional Dashboards

Popular dashboard IDs:
- **6417** - Kubernetes Cluster Monitoring
- **12900** - Spring Boot 2.1 Statistics
- **9628** - PostgreSQL Database
- **7645** - Kubernetes Cluster (Prometheus)

To import:
1. Go to Grafana → Dashboards → Import
2. Enter dashboard ID
3. Select Prometheus datasource
4. Click Import

## 🔎 Useful Prometheus Queries

### Backend Queries

```promql
# HTTP Request Rate
rate(http_server_requests_seconds_count[5m])

# Average Response Time
rate(http_server_requests_seconds_sum[5m]) / rate(http_server_requests_seconds_count[5m])

# Error Rate
sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m])) / sum(rate(http_server_requests_seconds_count[5m]))

# JVM Memory Usage
jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"} * 100

# Database Connection Pool
hikaricp_connections_active
```

### Database Queries

```promql
# Database Size
pg_database_size_bytes{datname="ead_automobile"}

# Active Connections
pg_stat_database_numbackends{datname="ead_automobile"}

# Transaction Rate
rate(pg_stat_database_xact_commit{datname="ead_automobile"}[5m])

# Database Availability
pg_up
```

### Kubernetes Queries

```promql
# Pod CPU Usage
sum(rate(container_cpu_usage_seconds_total{namespace="ead-application"}[5m])) by (pod) * 100

# Pod Memory Usage
sum(container_memory_usage_bytes{namespace="ead-application"}) by (pod)

# Pod Restart Count
kube_pod_container_status_restarts_total{namespace="ead-application"}
```

## 🔔 Setting Up Alerts (Optional)

### Create Alert Rules in Prometheus

Edit `07-prometheus.yaml` and add:
```yaml
rule_files:
  - '/etc/prometheus/alerts.yml'
```

Create alert rules ConfigMap:
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: prometheus-alerts
  namespace: ead-application
data:
  alerts.yml: |
    groups:
    - name: ead-alerts
      interval: 30s
      rules:
      - alert: HighMemoryUsage
        expr: (container_memory_usage_bytes{namespace="ead-application"} / container_spec_memory_limit_bytes{namespace="ead-application"}) * 100 > 80
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High memory usage detected"
          description: "Pod {{ $labels.pod }} is using {{ $value }}% memory"
      
      - alert: PodDown
        expr: kube_pod_status_phase{namespace="ead-application",phase!="Running"} == 1
        for: 5m
        labels:
          severity: critical
        annotations:
          summary: "Pod is down"
          description: "Pod {{ $labels.pod }} is not running"
      
      - alert: DatabaseDown
        expr: pg_up == 0
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "PostgreSQL is down"
          description: "Database connection failed"
```

## 🧪 Verify Monitoring

### 1. Check Prometheus Targets

```powershell
kubectl port-forward -n ead-application svc/prometheus-service 9090:9090
```
Visit: http://localhost:9090/targets

All targets should show "UP" status.

### 2. Check Metrics Collection

In Prometheus UI, try queries:
```promql
up
http_server_requests_seconds_count
pg_up
```

### 3. View Grafana Dashboard

```powershell
kubectl port-forward -n ead-application svc/grafana-service 3001:3001
```
Visit: http://localhost:3001
- Login: admin/admin
- Go to Dashboards → EAD Application - Kubernetes Cluster

## 🛠️ Troubleshooting

### Prometheus Not Scraping Backend

Check:
1. Backend has Prometheus annotations
2. Backend actuator endpoint is accessible:
   ```powershell
   kubectl exec -it <backend-pod> -n ead-application -- curl localhost:8080/actuator/prometheus
   ```
3. Prometheus logs:
   ```powershell
   kubectl logs -n ead-application -l app=prometheus
   ```

### Grafana Can't Connect to Prometheus

Check:
1. Prometheus service is running:
   ```powershell
   kubectl get svc prometheus-service -n ead-application
   ```
2. Grafana datasource configuration in ConfigMap
3. Grafana logs:
   ```powershell
   kubectl logs -n ead-application -l app=grafana
   ```

### PostgreSQL Exporter Connection Issues

Check:
1. Database credentials in secrets
2. Connection string format
3. Exporter logs:
   ```powershell
   kubectl logs -n ead-application -l app=postgres-exporter
   ```

## 📊 Resource Usage

Total additional resources:
- **CPU**: ~450m requests, ~900m limits
- **Memory**: ~896Mi requests, ~1.77Gi limits
- **Storage**: 7Gi (5Gi Prometheus + 2Gi Grafana)

## 🔒 Security Considerations

### Production Recommendations:

1. **Disable Anonymous Access** in Grafana:
   ```yaml
   - name: GF_AUTH_ANONYMOUS_ENABLED
     value: "false"
   ```

2. **Use Strong Passwords**:
   - Store in Secrets
   - Rotate regularly

3. **Enable TLS**:
   - Configure HTTPS for Grafana
   - Use cert-manager for certificates

4. **Network Policies**:
   - Restrict access to monitoring services
   - Allow only necessary connections

5. **RBAC**:
   - Limit Prometheus service account permissions
   - Use least privilege principle

## 📝 Maintenance

### Backup Grafana Dashboards

```powershell
kubectl exec -n ead-application <grafana-pod> -- tar czf /tmp/dashboards.tar.gz /var/lib/grafana/dashboards
kubectl cp ead-application/<grafana-pod>:/tmp/dashboards.tar.gz ./dashboards-backup.tar.gz
```

### Clean Up Old Metrics

Prometheus automatically manages retention (default 15 days). To change:
```yaml
args:
  - '--storage.tsdb.retention.time=30d'
  - '--storage.tsdb.retention.size=10GB'
```

### Update Images

```powershell
# Update Prometheus
kubectl set image deployment/prometheus prometheus=prom/prometheus:v2.48.0 -n ead-application

# Update Grafana
kubectl set image deployment/grafana grafana=grafana/grafana:10.2.0 -n ead-application
```

## 🔗 Useful Links

- [Prometheus Documentation](https://prometheus.io/docs/)
- [Grafana Documentation](https://grafana.com/docs/)
- [Spring Boot Actuator Metrics](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.metrics)
- [PostgreSQL Exporter](https://github.com/prometheus-community/postgres_exporter)
- [Grafana Dashboard Repository](https://grafana.com/grafana/dashboards/)

---

**Setup Complete!** 🎉

Your monitoring stack is now configured and ready to provide insights into your EAD application's performance and health.
