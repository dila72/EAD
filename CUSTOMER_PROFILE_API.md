# Customer Profile Management API

## Overview
API endpoints for customers to view and edit their profile information. Customers can update their first name, last name, and phone number. Email and password cannot be changed through these endpoints.

## Authentication
All endpoints require JWT authentication. Include the JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

---

## Endpoints

### 1. Get My Profile
Get the profile of the currently authenticated customer.

**Endpoint:** `GET /api/customer/profile/me`

**Authentication:** Required (Customer role)

**Response (200 OK):**
```json
{
  "id": 1,
  "userId": 5,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "1234567890"
}
```

**Note:** Email is read-only and cannot be updated.

---

### 2. Update My Profile
Update the profile of the currently authenticated customer.

**Endpoint:** `PUT /api/customer/profile/me`

**Authentication:** Required (Customer role)

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "1234567890"
}
```

**Validation Rules:**
- `firstName`: Required, cannot be blank
- `lastName`: Required, cannot be blank
- `phoneNumber`: Optional, must be 10-15 digits if provided

**Response (200 OK):**
```json
{
  "id": 1,
  "userId": 5,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "1234567890"
}
```

**Error Responses:**

- **400 Bad Request** - Validation errors
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": {
    "firstName": "must not be blank",
    "phoneNumber": "must be between 10 and 15 digits"
  }
}
```

- **404 Not Found** - Customer profile not found
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Customer not found for user ID: 5"
}
```

---

### 3. Get Profile by User ID (Admin)
Get customer profile by user ID. Typically used by administrators.

**Endpoint:** `GET /api/customer/profile/user/{userId}`

**Authentication:** Required (Admin role recommended)

**Path Parameters:**
- `userId` (Long): The ID of the user

**Response (200 OK):**
```json
{
  "id": 1,
  "userId": 5,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "1234567890"
}
```

---

### 4. Get Profile by Customer ID (Admin)
Get customer profile by customer ID. Typically used by administrators.

**Endpoint:** `GET /api/customer/profile/{customerId}`

**Authentication:** Required (Admin role recommended)

**Path Parameters:**
- `customerId` (Long): The ID of the customer

**Response (200 OK):**
```json
{
  "id": 1,
  "userId": 5,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "1234567890"
}
```

---

### 5. Update Profile by User ID (Admin)
Update customer profile by user ID. Typically used by administrators.

**Endpoint:** `PUT /api/customer/profile/user/{userId}`

**Authentication:** Required (Admin role recommended)

**Path Parameters:**
- `userId` (Long): The ID of the user

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "1234567890"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "userId": 5,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "1234567890"
}
```

---

## Usage Examples

### Example 1: Customer Views Their Profile

**Request:**
```http
GET /api/customer/profile/me HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response:**
```json
{
  "id": 1,
  "userId": 5,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "1234567890"
}
```

---

### Example 2: Customer Updates Their Profile

**Request:**
```http
PUT /api/customer/profile/me HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Smith",
  "phoneNumber": "9876543210"
}
```

**Response:**
```json
{
  "id": 1,
  "userId": 5,
  "firstName": "John",
  "lastName": "Smith",
  "email": "john.doe@example.com",
  "phoneNumber": "9876543210"
}
```

---

### Example 3: Customer Updates Only Phone Number

**Request:**
```http
PUT /api/customer/profile/me HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "5555555555"
}
```

**Response:**
```json
{
  "id": 1,
  "userId": 5,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "5555555555"
}
```

---

## Integration with Frontend

### JavaScript/TypeScript Example

```javascript
// Get customer profile
async function getMyProfile() {
  const response = await fetch('http://localhost:8080/api/customer/profile/me', {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('jwt')}`,
      'Content-Type': 'application/json'
    }
  });
  return await response.json();
}

// Update customer profile
async function updateMyProfile(profileData) {
  const response = await fetch('http://localhost:8080/api/customer/profile/me', {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('jwt')}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      firstName: profileData.firstName,
      lastName: profileData.lastName,
      phoneNumber: profileData.phoneNumber
    })
  });
  
  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message || 'Failed to update profile');
  }
  
  return await response.json();
}

// Usage
try {
  const profile = await getMyProfile();
  console.log('Current profile:', profile);
  
  const updated = await updateMyProfile({
    firstName: 'John',
    lastName: 'Smith',
    phoneNumber: '9876543210'
  });
  console.log('Updated profile:', updated);
} catch (error) {
  console.error('Error:', error.message);
}
```

---

## Security Notes

1. **Authentication Required**: All endpoints require valid JWT authentication
2. **Email Protection**: Email cannot be changed through profile endpoints
3. **Password Protection**: Password cannot be changed through profile endpoints (use password reset flow)
4. **Customer Access**: Customers can only view/edit their own profile through `/me` endpoints
5. **Admin Access**: Admin endpoints (`/user/{userId}`) should be protected with admin role authorization

---

## Common Error Scenarios

### 1. Missing Authentication
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource"
}
```

### 2. Invalid Phone Number Format
```json
{
  "status": 400,
  "error": "Bad Request",
  "errors": {
    "phoneNumber": "must be between 10 and 15 digits"
  }
}
```

### 3. Missing Required Fields
```json
{
  "status": 400,
  "error": "Bad Request",
  "errors": {
    "firstName": "must not be blank",
    "lastName": "must not be blank"
  }
}
```

### 4. Customer Not Found
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Customer not found for email: john.doe@example.com"
}
```

---

## Testing with Postman/cURL

### cURL Example - Get Profile
```bash
curl -X GET http://localhost:8080/api/customer/profile/me \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### cURL Example - Update Profile
```bash
curl -X PUT http://localhost:8080/api/customer/profile/me \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Smith",
    "phoneNumber": "9876543210"
  }'
```

---

## Related APIs

- **Password Management**: Use `/api/auth/forgot-password` and `/api/auth/reset-password` endpoints
- **Vehicle Management**: Use `/api/vehicles` endpoints for managing customer vehicles
- **Appointments**: Use `/api/appointments` endpoints for managing customer appointments

---

## Change History

- **v1.0.0** - Initial release with profile view and edit functionality
  - GET /api/customer/profile/me
  - PUT /api/customer/profile/me
  - Admin endpoints for user/customer ID access
