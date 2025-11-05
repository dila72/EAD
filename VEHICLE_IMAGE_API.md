# Vehicle Image Upload API - Quick Reference

## Setup Required

### 1. Set Environment Variables
Before running the application, set these environment variables:

**PowerShell:**
```powershell
$env:CLOUDINARY_CLOUD_NAME="your-cloud-name"
$env:CLOUDINARY_API_KEY="your-api-key"
$env:CLOUDINARY_API_SECRET="your-api-secret"
```

**Or update `application.properties` directly (not recommended for production)**

## API Endpoints

### 1. Create Vehicle WITHOUT Image (JSON)
```http
POST http://localhost:8080/api/vehicles
Content-Type: application/json

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

### 2. Create Vehicle WITH Image (Multipart Form Data)
```http
POST http://localhost:8080/api/vehicles
Content-Type: multipart/form-data

Form Fields:
- vehicle: {
    "customerId": 1,
    "model": "Toyota Camry",
    "color": "Blue",
    "vin": "1HGBH41JXMN109186",
    "licensePlate": "ABC-1234",
    "year": 2023,
    "registrationDate": "2023-01-15"
  }
- image: [Select Image File]
```

### 3. Update Vehicle WITHOUT Image (JSON)
```http
PUT http://localhost:8080/api/vehicles/1
Content-Type: application/json

{
  "customerId": 1,
  "model": "Honda Accord",
  "color": "Red",
  "vin": "1HGBH41JXMN109186",
  "licensePlate": "XYZ-5678",
  "year": 2024,
  "registrationDate": "2024-01-15"
}
```

### 4. Update Vehicle WITH Image (Multipart Form Data)
```http
PUT http://localhost:8080/api/vehicles/1
Content-Type: multipart/form-data

Form Fields:
- vehicle: { ... vehicle data ... }
- image: [Select New Image File]
```

### 5. Get Vehicle by ID (includes imageUrl)
```http
GET http://localhost:8080/api/vehicles/1
```

Response:
```json
{
  "id": 1,
  "customerId": 1,
  "model": "Toyota Camry",
  "color": "Blue",
  "vin": "1HGBH41JXMN109186",
  "licensePlate": "ABC-1234",
  "year": 2023,
  "registrationDate": "2023-01-15",
  "imageUrl": "https://res.cloudinary.com/your-cloud/image/upload/v1234567890/ead-automobile/vehicles/abc123.jpg"
}
```

### 6. Get All Vehicles for a Customer
```http
GET http://localhost:8080/api/vehicles/customer/1
```

### 7. Delete Vehicle (also deletes image from Cloudinary)
```http
DELETE http://localhost:8080/api/vehicles/1
```

## Testing with Postman

### Create/Update with Image:
1. Select request method (POST or PUT)
2. Enter URL
3. Go to **Body** tab
4. Select **form-data**
5. Add two fields:
   - Key: `vehicle` | Type: Text | Value: `{"customerId": 1, "model": "Toyota", ...}`
   - Key: `image` | Type: File | Value: Select your image file

## Features Implemented ✅

- ✅ Upload vehicle images to Cloudinary
- ✅ Automatic image optimization (800x600 max, auto quality)
- ✅ Update vehicle image (old image is automatically deleted)
- ✅ Delete vehicle image when vehicle is deleted
- ✅ Secure HTTPS image URLs
- ✅ Images organized in folder: `ead-automobile/vehicles/`
- ✅ Environment variable configuration for security

## Database Changes

New columns added to `vehicles` table:
- `image_url` (VARCHAR 500) - Cloudinary image URL
- `image_public_id` (VARCHAR 255) - Cloudinary public ID for deletion

## Notes

- Image upload is **optional** for create/update operations
- Old images are automatically deleted when updating or deleting vehicles
- Images are automatically resized to max 800x600 pixels
- Supported formats: JPEG, PNG, GIF, WebP
- Free Cloudinary tier: 25GB storage, 25 credits/month
