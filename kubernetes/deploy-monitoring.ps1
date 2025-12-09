# EAD Monitoring Stack Deployment Script
# Run this script after starting Docker Desktop and Kubernetes

Write-Host "🚀 Deploying EAD Monitoring Stack..." -ForegroundColor Cyan
Write-Host ""

# Check if kubectl is available
Write-Host "Checking Kubernetes cluster..." -ForegroundColor Yellow
kubectl cluster-info 2>&1 | Out-Null
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Kubernetes cluster is not running!" -ForegroundColor Red
    Write-Host "   Please start Docker Desktop and enable Kubernetes" -ForegroundColor Red
    exit 1
}
Write-Host "✅ Kubernetes cluster is running" -ForegroundColor Green
Write-Host ""

# Check if namespace exists
Write-Host "Checking namespace..." -ForegroundColor Yellow
kubectl get namespace ead-application 2>&1 | Out-Null
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Namespace 'ead-application' not found!" -ForegroundColor Red
    Write-Host "   Please deploy the main application first" -ForegroundColor Red
    exit 1
}
Write-Host "✅ Namespace exists" -ForegroundColor Green
Write-Host ""

# Deploy Prometheus
Write-Host "📊 Deploying Prometheus..." -ForegroundColor Cyan
kubectl apply -f "$PSScriptRoot\07-prometheus.yaml"
if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Prometheus deployed successfully" -ForegroundColor Green
} else {
    Write-Host "❌ Failed to deploy Prometheus" -ForegroundColor Red
    exit 1
}
Write-Host ""

# Deploy Grafana
Write-Host "📈 Deploying Grafana..." -ForegroundColor Cyan
kubectl apply -f "$PSScriptRoot\08-grafana.yaml"
if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Grafana deployed successfully" -ForegroundColor Green
} else {
    Write-Host "❌ Failed to deploy Grafana" -ForegroundColor Red
    exit 1
}
Write-Host ""

# Deploy PostgreSQL Exporter
Write-Host "🗄️  Deploying PostgreSQL Exporter..." -ForegroundColor Cyan
kubectl apply -f "$PSScriptRoot\09-postgres-exporter.yaml"
if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ PostgreSQL Exporter deployed successfully" -ForegroundColor Green
} else {
    Write-Host "❌ Failed to deploy PostgreSQL Exporter" -ForegroundColor Red
    exit 1
}
Write-Host ""

# Update Backend with Prometheus annotations
Write-Host "🔄 Updating Backend deployment..." -ForegroundColor Cyan
kubectl apply -f "$PSScriptRoot\04-backend.yaml"
if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Backend updated successfully" -ForegroundColor Green
} else {
    Write-Host "⚠️  Warning: Failed to update backend" -ForegroundColor Yellow
}
Write-Host ""

# Wait for pods to be ready
Write-Host "⏳ Waiting for monitoring pods to be ready..." -ForegroundColor Yellow
Write-Host "   This may take a few minutes..." -ForegroundColor Gray
Start-Sleep -Seconds 10

# Check Prometheus
Write-Host ""
Write-Host "Checking Prometheus status..." -ForegroundColor Yellow
kubectl wait --for=condition=ready pod -l app=prometheus -n ead-application --timeout=120s
if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Prometheus is ready" -ForegroundColor Green
} else {
    Write-Host "⚠️  Prometheus is still starting..." -ForegroundColor Yellow
}

# Check Grafana
Write-Host ""
Write-Host "Checking Grafana status..." -ForegroundColor Yellow
kubectl wait --for=condition=ready pod -l app=grafana -n ead-application --timeout=120s
if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Grafana is ready" -ForegroundColor Green
} else {
    Write-Host "⚠️  Grafana is still starting..." -ForegroundColor Yellow
}

# Check PostgreSQL Exporter
Write-Host ""
Write-Host "Checking PostgreSQL Exporter status..." -ForegroundColor Yellow
kubectl wait --for=condition=ready pod -l app=postgres-exporter -n ead-application --timeout=120s
if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ PostgreSQL Exporter is ready" -ForegroundColor Green
} else {
    Write-Host "⚠️  PostgreSQL Exporter is still starting..." -ForegroundColor Yellow
}

Write-Host ""
Write-Host "═══════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "🎉 Monitoring Stack Deployment Complete!" -ForegroundColor Green
Write-Host "═══════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""

# Display access instructions
Write-Host "📊 Access Monitoring Tools:" -ForegroundColor Cyan
Write-Host ""
Write-Host "1️⃣  Prometheus UI:" -ForegroundColor Yellow
Write-Host "   Run: kubectl port-forward -n ead-application svc/prometheus-service 9090:9090" -ForegroundColor White
Write-Host "   URL: http://localhost:9090" -ForegroundColor Gray
Write-Host ""
Write-Host "2️⃣  Grafana Dashboard:" -ForegroundColor Yellow
Write-Host "   Run: kubectl port-forward -n ead-application svc/grafana-service 3001:3001" -ForegroundColor White
Write-Host "   URL: http://localhost:3001" -ForegroundColor Gray
Write-Host "   Login: admin / admin" -ForegroundColor Gray
Write-Host ""
Write-Host "3️⃣  View All Pods:" -ForegroundColor Yellow
Write-Host "   kubectl get pods -n ead-application" -ForegroundColor White
Write-Host ""
Write-Host "4️⃣  View Logs:" -ForegroundColor Yellow
Write-Host "   kubectl logs -n ead-application -l app=prometheus -f" -ForegroundColor White
Write-Host "   kubectl logs -n ead-application -l app=grafana -f" -ForegroundColor White
Write-Host ""
Write-Host "📖 For detailed usage, see: MONITORING_GUIDE.md" -ForegroundColor Cyan
Write-Host ""
