# Service Management API - Postman Testing Guide

## Prerequisites

1. Application should be running on `http://localhost:8080`
2. Database should be accessible
3. Admin user should exist in the system

## Step-by-Step Testing Guide

---

## Step 1: Login as Admin (Get JWT Token)

### Request
```
POST http://localhost:8080/api/auth/login
Content-Type: application/json
```

### Body (raw JSON)
```json
{
  "email": "admin@example.com",
  "password": "admin123"
}
```

### Expected Response
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "email": "admin@example.com",
  "role": "ADMIN"
}
```

**💡 Copy the `token` value - you'll need it for admin operations!**

---

## Step 2: Create a Service (Admin - No Image)

### Request
```
POST http://localhost:8080/api/admin/services
Content-Type: application/json
Authorization: Bearer <YOUR_ADMIN_TOKEN>
```

### Body (raw JSON)
```json
{
  "name": "Oil Change",
  "description": "Complete engine oil change with synthetic oil and filter replacement",
  "price": 50.00,
  "estimatedDurationMinutes": 30,
  "active": true
}
```

### Expected Response (201 Created)
```json
{
  "id": 1,
  "name": "Oil Change",
  "description": "Complete engine oil change with synthetic oil and filter replacement",
  "imageUrl": null,
  "price": 50.00,
  "estimatedDurationMinutes": 30,
  "active": true,
  "createdAt": "2025-11-06T12:00:00",
  "updatedAt": "2025-11-06T12:00:00"
}
```

---

## Step 3: Create a Service with Image (Admin)

### Request
```
POST http://localhost:8080/api/admin/services/with-image
Content-Type: multipart/form-data
Authorization: Bearer <YOUR_ADMIN_TOKEN>
```

### Body (form-data)
- **Key**: `service` | **Type**: Text
  ```json
  {
    "name": "Brake Service",
    "description": "Complete brake inspection, pad replacement, and rotor resurfacing",
    "price": 150.00,
    "estimatedDurationMinutes": 90,
    "active": true
  }
  ```

- **Key**: `image` | **Type**: File
  - Select an image file (JPG, PNG, etc.)

### Expected Response (201 Created)
```json
{
  "id": 2,
  "name": "Brake Service",
  "description": "Complete brake inspection, pad replacement, and rotor resurfacing",
  "imageUrl": "https://res.cloudinary.com/your-cloud/image/upload/v1699275000/ead-automobile/services/...",
  "price": 150.00,
  "estimatedDurationMinutes": 90,
  "active": true,
  "createdAt": "2025-11-06T12:05:00",
  "updatedAt": "2025-11-06T12:05:00"
}
```

---

## Step 4: Get All Services (Admin)

### Request
```
GET http://localhost:8080/api/admin/services
Authorization: Bearer <YOUR_ADMIN_TOKEN>
```

### Expected Response (200 OK)
```json
[
  {
    "id": 1,
    "name": "Oil Change",
    "description": "Complete engine oil change...",
    "imageUrl": null,
    "price": 50.00,
    "estimatedDurationMinutes": 30,
    "active": true,
    "createdAt": "2025-11-06T12:00:00",
    "updatedAt": "2025-11-06T12:00:00"
  },
  {
    "id": 2,
    "name": "Brake Service",
    "description": "Complete brake inspection...",
    "imageUrl": "https://res.cloudinary.com/...",
    "price": 150.00,
    "estimatedDurationMinutes": 90,
    "active": false,
    "createdAt": "2025-11-06T12:05:00",
    "updatedAt": "2025-11-06T12:05:00"
  }
]
```

**Note**: Shows both active and inactive services

---

## Step 5: Get Service by ID (Admin)

### Request
```
GET http://localhost:8080/api/admin/services/1
Authorization: Bearer <YOUR_ADMIN_TOKEN>
```

### Expected Response (200 OK)
```json
{
  "id": 1,
  "name": "Oil Change",
  "description": "Complete engine oil change...",
  "imageUrl": null,
  "price": 50.00,
  "estimatedDurationMinutes": 30,
  "active": true,
  "createdAt": "2025-11-06T12:00:00",
  "updatedAt": "2025-11-06T12:00:00"
}
```

---

## Step 6: Update Service (Admin - No Image Change)

### Request
```
PUT http://localhost:8080/api/admin/services/1
Content-Type: application/json
Authorization: Bearer <YOUR_ADMIN_TOKEN>
```

### Body (raw JSON)
```json
{
  "name": "Premium Oil Change",
  "description": "Complete engine oil change with full synthetic oil, premium filter, and 21-point inspection",
  "price": 75.00,
  "estimatedDurationMinutes": 45,
  "active": true
}
```

### Expected Response (200 OK)
```json
{
  "id": 1,
  "name": "Premium Oil Change",
  "description": "Complete engine oil change with full synthetic oil...",
  "imageUrl": null,
  "price": 75.00,
  "estimatedDurationMinutes": 45,
  "active": true,
  "createdAt": "2025-11-06T12:00:00",
  "updatedAt": "2025-11-06T12:15:00"
}
```

---

## Step 7: Update Service with New Image (Admin)

### Request
```
PUT http://localhost:8080/api/admin/services/2/with-image
Content-Type: multipart/form-data
Authorization: Bearer <YOUR_ADMIN_TOKEN>
```

### Body (form-data)
- **Key**: `service` | **Type**: Text
  ```json
  {
    "name": "Premium Brake Service",
    "description": "Complete brake system overhaul with premium pads and rotors",
    "price": 250.00,
    "estimatedDurationMinutes": 120,
    "active": true
  }
  ```

- **Key**: `image` | **Type**: File
  - Select a new image file

### Expected Response (200 OK)
The old image is automatically deleted from Cloudinary, and the new one is uploaded.

---

## Step 8: Toggle Service Status (Admin)

### Request
```
PATCH http://localhost:8080/api/admin/services/1/toggle-status
Authorization: Bearer <YOUR_ADMIN_TOKEN>
```

### Expected Response (200 OK)
```json
{
  "id": 1,
  "name": "Premium Oil Change",
  "description": "Complete engine oil change...",
  "imageUrl": null,
  "price": 75.00,
  "estimatedDurationMinutes": 45,
  "active": false,
  "createdAt": "2025-11-06T12:00:00",
  "updatedAt": "2025-11-06T12:20:00"
}
```

**Note**: The `active` field has been toggled

---

## Step 9: Browse Services (Customer/Public - No Auth)

### Request
```
GET http://localhost:8080/api/customer/services
```

**No Authorization header needed!**

### Expected Response (200 OK)
```json
[
  {
    "id": 2,
    "name": "Premium Brake Service",
    "description": "Complete brake system overhaul...",
    "imageUrl": "https://res.cloudinary.com/...",
    "price": 250.00,
    "estimatedDurationMinutes": 120,
    "active": true,
    "createdAt": "2025-11-06T12:05:00",
    "updatedAt": "2025-11-06T12:18:00"
  }
]
```

**Note**: Only shows active services (service ID 1 is inactive, so it won't appear)

---

## Step 10: Get Service Details (Customer/Public - No Auth)

### Request
```
GET http://localhost:8080/api/customer/services/2
```

**No Authorization header needed!**

### Expected Response (200 OK)
```json
{
  "id": 2,
  "name": "Premium Brake Service",
  "description": "Complete brake system overhaul...",
  "imageUrl": "https://res.cloudinary.com/...",
  "price": 250.00,
  "estimatedDurationMinutes": 120,
  "active": true,
  "createdAt": "2025-11-06T12:05:00",
  "updatedAt": "2025-11-06T12:18:00"
}
```

### Try accessing inactive service
```
GET http://localhost:8080/api/customer/services/1
```

**Expected Response**: 404 Not Found (because service ID 1 is inactive)

---

## Step 11: Delete Service (Admin)

### Request
```
DELETE http://localhost:8080/api/admin/services/1
Authorization: Bearer <YOUR_ADMIN_TOKEN>
```

### Expected Response (200 OK)
```
Service deleted successfully
```

**Note**: If the service has an image, it's automatically deleted from Cloudinary

---

## Common Error Responses

### 400 Bad Request
```json
{
  "message": "Service with name 'Oil Change' already exists"
}
```

### 401 Unauthorized
```json
{
  "message": "Unauthorized",
  "status": 401
}
```
**Solution**: Check if token is included and valid

### 403 Forbidden
```json
{
  "message": "Access Denied",
  "status": 403
}
```
**Solution**: User doesn't have ADMIN role

### 404 Not Found
```json
{
  "message": "Service not found with ID: 999"
}
```

---

## Postman Environment Variables

Create a Postman environment with these variables:

```
base_url: http://localhost:8080
admin_token: <paste your admin JWT token here>
```

Then use `{{base_url}}` and `{{admin_token}}` in your requests:
```
{{base_url}}/api/admin/services
Authorization: Bearer {{admin_token}}
```

---

## Testing Checklist

- [ ] Admin can login and get JWT token
- [ ] Admin can create service without image
- [ ] Admin can create service with image
- [ ] Admin can view all services (including inactive)
- [ ] Admin can view service by ID
- [ ] Admin can update service without changing image
- [ ] Admin can update service with new image
- [ ] Admin can toggle service status
- [ ] Admin can delete service
- [ ] Customer/Public can browse active services (no auth)
- [ ] Customer/Public can view active service details (no auth)
- [ ] Inactive services don't appear in customer endpoints
- [ ] Duplicate service names are rejected
- [ ] Images are uploaded to Cloudinary successfully
- [ ] Images are deleted when service is deleted or updated

---

## Tips

1. **Save your admin token**: After login, save the token in Postman environment for reuse
2. **Test image upload**: Use small image files (< 1MB) for faster testing
3. **Check Cloudinary**: Verify images are uploaded to your Cloudinary dashboard
4. **Test validation**: Try creating duplicate services to test validation
5. **Test authorization**: Try accessing admin endpoints without token to verify security

Enjoy testing! 🚀
