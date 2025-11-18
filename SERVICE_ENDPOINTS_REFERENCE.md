# Service Management Endpoints - Quick Reference

## Endpoint Structure Overview

```
/api/admin/services         → Admin: Full CRUD operations (requires ADMIN role)
/api/customer/services      → Customer/Public: Browse active services (no auth required)
```

---

## Admin Endpoints (`/api/admin/services`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/admin/services` | Create service (JSON) |
| `POST` | `/api/admin/services/with-image` | Create service with image (multipart) |
| `GET` | `/api/admin/services` | Get all services (including inactive) |
| `GET` | `/api/admin/services/{id}` | Get service by ID |
| `PUT` | `/api/admin/services/{id}` | Update service (JSON) |
| `PUT` | `/api/admin/services/{id}/with-image` | Update service with image (multipart) |
| `PATCH` | `/api/admin/services/{id}/toggle-status` | Toggle active/inactive status |
| `DELETE` | `/api/admin/services/{id}` | Delete service |

**Authorization**: Requires `Bearer <admin-token>` with `ROLE_ADMIN`

---

## Customer Endpoints (`/api/customer/services`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/customer/services` | Get all active services (no auth required) |
| `GET` | `/api/customer/services/{id}` | Get active service details by ID (no auth required) |

**Authorization**: None required - publicly accessible for browsing

**Note**: Only returns active services (inactive services will return 404)

**Use Case**: 
- Website visitors can browse available services
- Customers can view services before/during appointment booking
- Frontend can display service catalog without requiring login

---

## Request/Response Examples

### 1. Admin Creates Service

**Request:**
```http
POST /api/admin/services
Authorization: Bearer <admin-token>
Content-Type: application/json

{
  "name": "Oil Change",
  "description": "Full synthetic oil change",
  "price": 50.00,
  "estimatedDurationMinutes": 30,
  "active": true
}
```

**Response:**
```json
{
  "id": 1,
  "name": "Oil Change",
  "description": "Full synthetic oil change",
  "imageUrl": null,
  "price": 50.00,
  "estimatedDurationMinutes": 30,
  "active": true,
  "createdAt": "2025-11-06T11:00:00",
  "updatedAt": "2025-11-06T11:00:00"
}
```

### 2. Customer Views Available Services

**Request:**
```http
GET /api/customer/services
```

**Response:**
```json
[
  {
    "id": 1,
    "name": "Oil Change",
    "description": "Full synthetic oil change",
    "imageUrl": "https://res.cloudinary.com/...",
    "price": 50.00,
    "estimatedDurationMinutes": 30,
    "active": true,
    "createdAt": "2025-11-06T11:00:00",
    "updatedAt": "2025-11-06T11:00:00"
  },
  {
    "id": 2,
    "name": "Brake Service",
    "description": "Brake inspection and replacement",
    "imageUrl": "https://res.cloudinary.com/...",
    "price": 150.00,
    "estimatedDurationMinutes": 90,
    "active": true,
    "createdAt": "2025-11-06T11:05:00",
    "updatedAt": "2025-11-06T11:05:00"
  }
]
```

---

## Security & Authorization

| Role | Endpoint Pattern | Access Level |
|------|-----------------|--------------|
| `ADMIN` | `/api/admin/services/**` | Full CRUD + status management |
| Customer/Public | `/api/customer/services/**` | Read-only (active services, no auth required) |

---

## Benefits of Role-Based Routing

✅ **Clear separation** of admin vs customer functionality  
✅ **Better security** - easy to identify privileged endpoints  
✅ **Easier maintenance** - grouped by role responsibilities  
✅ **Intuitive API structure** - follows RESTful best practices  
✅ **Scalable** - easy to add employee or other role endpoints later  

Example: Add employee endpoints in the future:
```
/api/employee/services → Employee-specific service views
```
