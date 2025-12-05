# Generate self-signed certificate for Kubernetes TLS
$certPath = "c:\Users\nilanga dilhara\EAD\kubernetes\certs"

# Create certificate using current user store
$cert = New-SelfSignedCertificate `
    -DnsName "ead-app.example.com" `
    -CertStoreLocation "cert:\CurrentUser\My" `
    -NotAfter (Get-Date).AddYears(1) `
    -KeyExportPolicy Exportable `
    -KeySpec Signature `
    -KeyLength 2048 `
    -KeyAlgorithm RSA `
    -HashAlgorithm SHA256

Write-Host "Certificate created with thumbprint: $($cert.Thumbprint)"

# Export certificate to PFX
$pwd = ConvertTo-SecureString -String "temp123" -Force -AsPlainText
$pfxPath = "$certPath\cert.pfx"
Export-PfxCertificate -Cert $cert -FilePath $pfxPath -Password $pwd | Out-Null

# Convert PFX to PEM format for Kubernetes
$pfxCert = New-Object System.Security.Cryptography.X509Certificates.X509Certificate2($pfxPath, "temp123", [System.Security.Cryptography.X509Certificates.X509KeyStorageFlags]::Exportable)

# Export certificate (public key)
$certPem = "-----BEGIN CERTIFICATE-----`n"
$certPem += [System.Convert]::ToBase64String($pfxCert.RawData, [System.Base64FormattingOptions]::InsertLineBreaks)
$certPem += "`n-----END CERTIFICATE-----"
$certPem | Out-File -FilePath "$certPath\tls.crt" -Encoding ASCII

# Export private key
$rsa = [System.Security.Cryptography.X509Certificates.RSACertificateExtensions]::GetRSAPrivateKey($pfxCert)
$keyBytes = $rsa.ExportRSAPrivateKey()
$keyPem = "-----BEGIN RSA PRIVATE KEY-----`n"
$keyPem += [System.Convert]::ToBase64String($keyBytes, [System.Base64FormattingOptions]::InsertLineBreaks)
$keyPem += "`n-----END RSA PRIVATE KEY-----"
$keyPem | Out-File -FilePath "$certPath\tls.key" -Encoding ASCII

Write-Host "Certificate files created:"
Write-Host "  - $certPath\tls.crt (certificate)"
Write-Host "  - $certPath\tls.key (private key)"

# Clean up certificate from store
Remove-Item -Path "cert:\CurrentUser\My\$($cert.Thumbprint)" -Force

# Clean up PFX file
Remove-Item -Path $pfxPath -Force

Write-Host "`nCertificate generation complete!"
