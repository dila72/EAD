# Cloudinary Setup Guide

## Overview
This application uses Cloudinary for storing and managing vehicle images.

## Setup Instructions

### 1. Create a Cloudinary Account
1. Go to [Cloudinary](https://cloudinary.com/)
2. Sign up for a free account
3. Once logged in, go to your [Dashboard](https://cloudinary.com/console)

### 2. Get Your Credentials
From your Cloudinary Dashboard, copy:
- **Cloud Name**
- **API Key**
- **API Secret**

### 3. Configure Environment Variables

#### Option 1: Using Environment Variables (Recommended for Production)
Set the following environment variables:

**Windows (PowerShell):**
```powershell
$env:CLOUDINARY_CLOUD_NAME="your-cloud-name"
$env:CLOUDINARY_API_KEY="your-api-key"
$env:CLOUDINARY_API_SECRET="your-api-secret"
```

**Windows (Command Prompt):**
```cmd
set CLOUDINARY_CLOUD_NAME=your-cloud-name
set CLOUDINARY_API_KEY=your-api-key
set CLOUDINARY_API_SECRET=your-api-secret
```

**Linux/Mac:**
```bash
export CLOUDINARY_CLOUD_NAME="your-cloud-name"
export CLOUDINARY_API_KEY="your-api-key"
export CLOUDINARY_API_SECRET="your-api-secret"
```

#### Option 2: Update application.properties (For Development)
Replace the placeholder values in `src/main/resources/application.properties`:
```properties
cloudinary.cloud-name=your-actual-cloud-name
cloudinary.api-key=your-actual-api-key
cloudinary.api-secret=your-actual-api-secret
```

⚠️ **Warning:** Never commit your actual credentials to version control!

### 4. Test the Configuration
Run your application and try uploading a vehicle image using the API endpoints.

## API Endpoints

### Create Vehicle with Image
```http
POST /api/vehicles
Content-Type: multipart/form-data

Parameters:
- vehicle: JSON string of VehicleDTO
- image: Image file (optional)
```

### Update Vehicle with Image
```http
PUT /api/vehicles/{id}
Content-Type: multipart/form-data

Parameters:
- vehicle: JSON string of VehicleDTO
- image: Image file (optional)
```

## Example Using Postman

1. **Create Vehicle with Image:**
   - Method: POST
   - URL: `http://localhost:8080/api/vehicles`
   - Body type: form-data
   - Add fields:
     - `vehicle` (text): 
       ```json
       {
         "customerId": 1,
         "model": "Toyota Camry",
         "color": "Blue",
         "vin": "1HGBH41JXMN109186",
         "licensePlate": "ABC-1234",
         "year": 2023,
         "registrationDate": "2023-01-15"
       }
       ```
     - `image` (file): Select your vehicle image

2. **Update Vehicle with Image:**
   - Method: PUT
   - URL: `http://localhost:8080/api/vehicles/1`
   - Body type: form-data
   - Add fields:
     - `vehicle` (text): VehicleDTO JSON
     - `image` (file): New vehicle image

## Image Specifications

- **Supported formats:** JPEG, PNG, GIF, WebP
- **Maximum file size:** 10MB (can be configured)
- **Automatic optimization:** Images are automatically resized to max 800x600 pixels
- **Storage location:** Images are stored in folder `ead-automobile/vehicles/`

## Features

✅ **Automatic image upload** to Cloudinary  
✅ **Image deletion** when vehicle is deleted  
✅ **Image update** - old image is automatically deleted  
✅ **Image optimization** - automatic resizing and quality optimization  
✅ **Secure storage** - images are served over HTTPS  

## Troubleshooting

### Issue: "Failed to upload image"
- Check your Cloudinary credentials are correct
- Verify your internet connection
- Check Cloudinary dashboard for quota limits

### Issue: "Invalid API key"
- Ensure environment variables are set correctly
- Restart your application after setting environment variables

### Issue: "Upload quota exceeded"
- Free tier has monthly limits (25GB storage, 25 credits/month)
- Consider upgrading your Cloudinary plan

## Security Notes

🔒 **Never commit credentials to Git**
- Add `.env` to `.gitignore`
- Use environment variables in production
- Rotate API credentials periodically

## Additional Resources

- [Cloudinary Documentation](https://cloudinary.com/documentation)
- [Cloudinary Java SDK](https://cloudinary.com/documentation/java_integration)
