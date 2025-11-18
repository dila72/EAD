# Service Management API Documentation

## Overview
This implementation provides a complete CRUD API for managing predefined services that customers can select when booking appointments. Admins can create, view, update, delete, and toggle the status of services, including uploading images for each service.

## Architecture

### Components Created

1. **Entity**: `Service.java`
   - Located: `src/main/java/com/example/ead_backend/model/entity/Service.java`
   - Fields: id, name, description, imageUrl, imagePublicId, price, estimatedDurationMinutes, active, timestamps

2. **DTO**: `ServiceDTO.java`
   - Located: `src/main/java/com/example/ead_backend/dto/ServiceDTO.java`
   - Transfers service data without exposing internal Cloudinary details

3. **Repository**: `ServiceRepository.java`
   - Located: `src/main/java/com/example/ead_backend/repository/ServiceRepository.java`
   - JPA repository with custom queries for active services

4. **Mapper**: `ServiceMapper.java`
   - Located: `src/main/java/com/example/ead_backend/mapper/ServiceMapper.java`
   - MapStruct interface for entity-DTO conversion

5. **Service Layer**: 
   - Interface: `ServiceService.java`
   - Implementation: `ServiceServiceImpl.java`
   - Located: `src/main/java/com/example/ead_backend/service/`

6. **Controller**: `ServiceController.java`
   - Located: `src/main/java/com/example/ead_backend/controller/ServiceController.java`
   - REST endpoints with admin authorization

## API Endpoints

The API is organized by role-based routing:
- **Admin endpoints**: `/api/admin/services` - Full CRUD operations (requires ADMIN role)
- **Customer endpoints**: `/api/customer/services` - Read-only active services (no authentication required for browsing)

---

## Admin Endpoints

### 1. Create Service (without image)
**Admin only**
```http
POST /api/admin/services
Content-Type: application/json
Authorization: Bearer <admin-token>

{
  "name": "Oil Change",
  "description": "Complete engine oil change with filter replacement",
  "price": 50.00,
  "estimatedDurationMinutes": 30,
  "active": true
}
```

**Response**: 
```json
{
  "id": 1,
  "name": "Oil Change",
  "description": "Complete engine oil change with filter replacement",
  "imageUrl": null,
  "price": 50.00,
  "estimatedDurationMinutes": 30,
  "active": true,
  "createdAt": "2025-11-06T10:30:00",
  "updatedAt": "2025-11-06T10:30:00"
}
```

### 2. Create Service (with image)
**Admin only**
```http
POST /api/admin/services/with-image
Content-Type: multipart/form-data
Authorization: Bearer <admin-token>

Form Data:
- service: {"name":"Oil Change","description":"Complete engine oil change","price":50.00,"estimatedDurationMinutes":30,"active":true}
- image: [binary file]
```

**Response**: Same as above, but with `imageUrl` populated

### 3. Get All Services (including inactive)
**Admin only**
```http
GET /api/admin/services
Authorization: Bearer <admin-token>
```

**Response**:
```json
[
  {
    "id": 1,
    "name": "Oil Change",
    "description": "Complete engine oil change",
    "imageUrl": "https://res.cloudinary.com/...",
    "price": 50.00,
    "estimatedDurationMinutes": 30,
    "active": true,
    "createdAt": "2025-11-06T10:30:00",
    "updatedAt": "2025-11-06T10:30:00"
  },
  {
    "id": 2,
    "name": "Brake Service",
    "description": "Brake inspection and replacement",
    "imageUrl": "https://res.cloudinary.com/...",
    "price": 150.00,
    "estimatedDurationMinutes": 90,
    "active": false
  }
]
```

### 4. Get Service by ID (Admin view)
**Admin only**
```http
GET /api/admin/services/{id}
Authorization: Bearer <admin-token>
```

### 5. Update Service (without changing image)
**Admin only**
```http
PUT /api/admin/services/{id}
Content-Type: application/json
Authorization: Bearer <admin-token>

{
  "name": "Premium Oil Change",
  "description": "Complete engine oil change with synthetic oil",
  "price": 75.00,
  "estimatedDurationMinutes": 45,
  "active": true
}
```

### 6. Update Service (with new image)
**Admin only**
```http
PUT /api/admin/services/{id}/with-image
Content-Type: multipart/form-data
Authorization: Bearer <admin-token>

Form Data:
- service: {"name":"Premium Oil Change","description":"...","price":75.00,"estimatedDurationMinutes":45,"active":true}
- image: [binary file]
```

### 7. Toggle Service Status
**Admin only** - Toggle between active/inactive
```http
PATCH /api/admin/services/{id}/toggle-status
Authorization: Bearer <admin-token>
```

### 8. Delete Service
**Admin only** - Deletes service and its image from Cloudinary
```http
DELETE /api/admin/services/{id}
Authorization: Bearer <admin-token>
```

**Response**: `"Service deleted successfully"`

---

## Customer Endpoints

### 9. Get Active Services
**Public/Customer** - List all active services (no auth required for browsing, auth optional for booking)
```http
GET /api/customer/services
```

**Response**:
```json
[
  {
    "id": 1,
    "name": "Oil Change",
    "description": "Complete engine oil change",
    "imageUrl": "https://res.cloudinary.com/...",
    "price": 50.00,
    "estimatedDurationMinutes": 30,
    "active": true,
    "createdAt": "2025-11-06T10:30:00",
    "updatedAt": "2025-11-06T10:30:00"
  }
]
```

### 10. Get Service Details by ID
**Public/Customer** - View details of a specific active service (no auth required)
```http
GET /api/customer/services/{id}
```

**Note**: Returns 404 if service is inactive

## Features

### Image Management
- Images are uploaded to **Cloudinary** cloud storage
- Automatic deletion when service is deleted or updated
- Images stored in folder: `ead-automobile/services`
- Automatic image optimization (max 800x600px, auto quality)

### Validation
- Service names must be unique (case-insensitive)
- All required fields validated
- Duplicate name prevention on create and update

### Security
- **Admin endpoints** (`/api/admin/services`) protected with `@PreAuthorize("hasRole('ADMIN')")`
- **Customer endpoints** (`/api/customer/services`) publicly accessible for browsing services (no auth required)
- Role-based routing for clear separation of concerns
- CORS enabled for cross-origin requests

### Status Management
- Services can be active or inactive
- Inactive services won't appear in customer-facing endpoints
- Easy toggle between states without deleting data

## Database Schema

The `services` table will be automatically created with the following structure:

```sql
CREATE TABLE services (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL UNIQUE,
  description VARCHAR(1000),
  image_url VARCHAR(255),
  image_public_id VARCHAR(255),
  price DECIMAL(10, 2),
  estimated_duration_minutes INTEGER,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP
);
```

## Testing

### Using Postman or cURL

1. **Login as Admin** (get JWT token):
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"adminpassword"}'
```

2. **Create a Service (Admin)**:
```bash
curl -X POST http://localhost:8080/api/admin/services \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN" \
  -d '{
    "name": "Brake Service",
    "description": "Complete brake inspection and pad replacement",
    "price": 150.00,
    "estimatedDurationMinutes": 90,
    "active": true
  }'
```

3. **Upload Service with Image (Admin)**:
```bash
curl -X POST http://localhost:8080/api/admin/services/with-image \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN" \
  -F 'service={"name":"Tire Rotation","description":"Four-wheel tire rotation","price":40.00,"estimatedDurationMinutes":20,"active":true}' \
  -F 'image=@/path/to/tire-service.jpg'
```

4. **Get All Services (Admin)**:
```bash
curl http://localhost:8080/api/admin/services \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```

5. **Get Active Services (Customer - no auth needed)**:
```bash
curl http://localhost:8080/api/customer/services
```

6. **Get Service Details (Customer - no auth needed)**:
```bash
curl http://localhost:8080/api/customer/services/1
```

## Integration with Appointments

Currently, the `Appointment` entity has a `service` field as a String. To integrate with the new Service management:

### Option 1: Keep String field
- Customers select from predefined services
- Store the service name as a string in appointments
- Simple but loses relational benefits

### Option 2: Add Service relationship (recommended)
Modify `Appointment.java`:
```java
@ManyToOne
@JoinColumn(name = "service_id")
private Service service;
```

This would require updating:
- AppointmentDTO to include serviceId
- Appointment creation logic to link to Service entity
- Database migration

## Error Handling

All endpoints include proper error handling:
- **400 Bad Request**: Invalid input data
- **401 Unauthorized**: Missing or invalid JWT token
- **403 Forbidden**: User doesn't have admin role
- **404 Not Found**: Service with given ID doesn't exist
- **409 Conflict**: Service name already exists
- **500 Internal Server Error**: Server error (e.g., Cloudinary upload failure)

## Notes

- All admin operations require `ROLE_ADMIN` in the JWT token
- Images are optimized automatically (max 800x600, auto quality)
- Service names are unique and case-insensitive
- Timestamps (createdAt, updatedAt) are managed automatically
- Image deletion from Cloudinary is handled automatically
- If image upload fails, the service won't be created/updated

## Next Steps

To complete the integration:
1. Run the application to create the database table
2. Test all endpoints with Postman
3. Consider linking Appointment entity to Service entity (optional)
4. Add frontend UI for admin service management
5. Display service images and details on customer appointment booking page
