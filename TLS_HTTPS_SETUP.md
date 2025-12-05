# TLS/HTTPS Setup Documentation

## Overview
This document describes the TLS/HTTPS configuration for the EAD Application running on Kubernetes.

## Certificate Details

### Self-Signed Certificate
- **Domain**: ead-app.example.com
- **Valid From**: December 5, 2025
- **Valid Until**: December 5, 2026 (365 days)
- **Algorithm**: RSA 2048-bit
- **Subject**: C=US, ST=State, L=City, O=EAD Application, CN=ead-app.example.com
- **Subject Alternative Names (SAN)**: DNS:ead-app.example.com

### Certificate Files Location
```
kubernetes/certs/
├── tls.crt          # Public certificate
├── tls.key          # Private key
└── openssl.cnf      # OpenSSL configuration used for generation
```

## Kubernetes Configuration

### TLS Secret
```bash
kubectl -n ead-application get secret ead-tls-secret
```

The secret contains:
- `tls.crt`: Base64-encoded certificate
- `tls.key`: Base64-encoded private key

### Ingress Configuration
File: `kubernetes/06-ingress.yaml`

TLS section:
```yaml
spec:
  ingressClassName: nginx
  tls:
  - hosts:
    - ead-app.example.com
    secretName: ead-tls-secret
```

**Note**: The annotation `nginx.ingress.kubernetes.io/ssl-redirect: "false"` is set to allow both HTTP and HTTPS access.

## Access URLs

- **HTTP**: http://ead-app.example.com (port 80)
- **HTTPS**: https://ead-app.example.com (port 443)

Both protocols are supported and work correctly.

## Certificate Generation Process

### Prerequisites
- Git Bash (includes OpenSSL)
- Kubernetes cluster running
- kubectl configured

### Regenerate Certificate (if needed)

1. Navigate to the certs directory:
```bash
cd kubernetes/certs
```

2. Generate new certificate using OpenSSL:
```bash
"C:\Program Files\Git\bin\bash.exe" -c "cd '/c/Users/nilanga dilhara/EAD/kubernetes/certs' && openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout tls.key -out tls.crt -config openssl.cnf -extensions v3_req"
```

3. Recreate the Kubernetes secret:
```bash
kubectl -n ead-application delete secret ead-tls-secret
kubectl -n ead-application create secret tls ead-tls-secret --cert="kubernetes\certs\tls.crt" --key="kubernetes\certs\tls.key"
```

4. Restart the ingress controller (optional):
```bash
kubectl -n ingress-nginx rollout restart deployment/ingress-nginx-controller
```

## Browser Warnings

Since this is a self-signed certificate, browsers will show security warnings:
- Chrome: "Your connection is not private" (NET::ERR_CERT_AUTHORITY_INVALID)
- Firefox: "Warning: Potential Security Risk Ahead"
- Edge: Similar to Chrome

**To proceed**: Click "Advanced" → "Proceed to ead-app.example.com (unsafe)"

### Installing Certificate (Optional)

To remove browser warnings, install the certificate in Windows:

1. Open `kubernetes\certs\tls.crt`
2. Click "Install Certificate"
3. Store Location: "Current User"
4. Place certificate in: "Trusted Root Certification Authorities"
5. Restart browser

## Testing HTTPS

### Using curl (Git Bash)
```bash
# Test frontend (ignoring self-signed cert)
curl -k https://ead-app.example.com

# Test API endpoint
curl -k https://ead-app.example.com/api/auth/login

# View certificate details
echo | openssl s_client -connect ead-app.example.com:443 -servername ead-app.example.com 2>/dev/null | openssl x509 -noout -text
```

### Using PowerShell (bypass certificate validation)
```powershell
[System.Net.ServicePointManager]::ServerCertificateValidationCallback = {$true}
Invoke-WebRequest -Uri "https://ead-app.example.com"
```

### Using Browser
1. Navigate to https://ead-app.example.com
2. Accept the security warning
3. Application should load normally

## Troubleshooting

### Certificate Not Loading
```bash
# Check secret exists
kubectl -n ead-application get secret ead-tls-secret

# Check ingress configuration
kubectl -n ead-application describe ingress ead-ingress

# Check ingress controller logs
kubectl -n ingress-nginx logs deployment/ingress-nginx-controller --tail=50 | grep -i "tls\|certificate\|ead-tls"
```

### HTTPS Not Working
```bash
# Verify NGINX ingress controller is running
kubectl -n ingress-nginx get pods

# Check service ports (should show 80:30805/TCP,443:32056/TCP)
kubectl -n ingress-nginx get svc ingress-nginx-controller

# Test with curl
curl -k -I https://ead-app.example.com
```

### Certificate Warnings in Logs
- "certificate relies on legacy Common Name field, use SANs instead" → Fixed by adding Subject Alternative Names
- "local SSL certificate was not found" → Secret needs to be recreated
- "Unexpected error validating SSL certificate" → Check certificate format and content

## Production Considerations

For production deployment, replace the self-signed certificate with:

1. **Let's Encrypt** (Free, automated):
   - Install cert-manager: `kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.13.0/cert-manager.yaml`
   - Configure ClusterIssuer for Let's Encrypt
   - Add annotation to ingress: `cert-manager.io/cluster-issuer: "letsencrypt-prod"`

2. **Commercial Certificate**:
   - Purchase from Certificate Authority (DigiCert, Sectigo, etc.)
   - Create secret: `kubectl create secret tls ead-tls-secret --cert=path/to/cert.crt --key=path/to/private.key -n ead-application`

3. **Enable SSL Redirect** (force HTTPS):
   ```yaml
   annotations:
     nginx.ingress.kubernetes.io/ssl-redirect: "true"
   ```

## Security Notes

- Private key (`tls.key`) should never be committed to version control
- Certificate files in `kubernetes/certs/` are `.gitignore`d
- Consider rotating certificates before expiration (currently 1 year)
- For production, use certificates with longer validity or automated renewal

## References

- [Kubernetes TLS Secrets](https://kubernetes.io/docs/concepts/configuration/secret/#tls-secrets)
- [NGINX Ingress Controller TLS/HTTPS](https://kubernetes.github.io/ingress-nginx/user-guide/tls/)
- [OpenSSL Certificate Generation](https://www.openssl.org/docs/man1.1.1/man1/req.html)
- [cert-manager Documentation](https://cert-manager.io/docs/)
