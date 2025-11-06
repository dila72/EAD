# Postman Testing Guide for Appointment Assignment

## Setup

### Environment Variables
Create a Postman environment with these variables:
- `base_url`: `http://localhost:8080`
- `admin_token`: (Get from login as admin)
- `customer_token`: (Get from login as customer)
- `appointmentId`: (Will be set automatically)
- `employeeId`: (Will be set automatically)

---

## Test Flow

### 1. Login as Customer

**Request:**
```
POST {{base_url}}/api/auth/login
Content-Type: application/json

{
  "email": "customer@example.com",
  "password": "password123"
}
```

**Tests Script:**
```javascript
pm.test("Login successful", function() {
    pm.response.to.have.status(200);
});

var jsonData = pm.response.json();
pm.environment.set("customer_token", jsonData.token);
```

---

### 2. Customer Creates Appointment

**Request:**
```
POST {{base_url}}/api/appointments
Content-Type: application/json
Authorization: Bearer {{customer_token}}

{
  "service": "Oil Change, Brake Inspection, Tire Rotation",
  "vehicleNo": "ABC-1234",
  "vehicleId": "vehicle-uuid-123",
  "date": "2025-11-20",
  "startTime": "10:00",
  "endTime": "12:00"
}
```

**Tests Script:**
```javascript
pm.test("Appointment created with PENDING status", function() {
    pm.response.to.have.status(200);
    var jsonData = pm.response.json();
    pm.expect(jsonData.status).to.eql("PENDING");
    pm.expect(jsonData.employeeId).to.be.null;
    pm.environment.set("appointmentId", jsonData.appointmentId);
});
```

---

### 3. Customer Views Their Appointments

**Request:**
```
GET {{base_url}}/api/appointments
Authorization: Bearer {{customer_token}}
```

**Tests Script:**
```javascript
pm.test("Customer can view their appointments", function() {
    pm.response.to.have.status(200);
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.be.an('array');
});
```

---

### 4. Login as Admin

**Request:**
```
POST {{base_url}}/api/auth/login
Content-Type: application/json

{
  "email": "admin@example.com",
  "password": "Admin@123"
}
```

**Tests Script:**
```javascript
pm.test("Admin login successful", function() {
    pm.response.to.have.status(200);
});

var jsonData = pm.response.json();
pm.environment.set("admin_token", jsonData.token);
```

---

### 5. Admin Views Pending Appointments

**Request:**
```
GET {{base_url}}/api/appointments/pending
Authorization: Bearer {{admin_token}}
```

**Tests Script:**
```javascript
pm.test("Admin can view pending appointments", function() {
    pm.response.to.have.status(200);
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.be.an('array');
    
    if (jsonData.length > 0) {
        pm.expect(jsonData[0].status).to.eql("PENDING");
        console.log("Found " + jsonData.length + " pending appointments");
    }
});
```

---

### 6. Admin Checks Employee Availability

**Request:**
```
GET {{base_url}}/api/appointments/available-employees?date=2025-11-20
Authorization: Bearer {{admin_token}}
```

**Tests Script:**
```javascript
pm.test("Admin can view available employees", function() {
    pm.response.to.have.status(200);
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.be.an('array');
    
    if (jsonData.length > 0) {
        pm.expect(jsonData[0]).to.have.property('employeeId');
        pm.expect(jsonData[0]).to.have.property('employeeName');
        pm.expect(jsonData[0]).to.have.property('currentAppointmentCount');
        pm.expect(jsonData[0]).to.have.property('available');
        
        // Save first available employee ID
        var availableEmployee = jsonData.find(emp => emp.available);
        if (availableEmployee) {
            pm.environment.set("employeeId", availableEmployee.employeeId);
            console.log("Available employee found: " + availableEmployee.employeeName);
        }
    }
});
```

---

### 7. Admin Assigns Appointment to Employee

**Request:**
```
PUT {{base_url}}/api/appointments/{{appointmentId}}/assign
Content-Type: application/json
Authorization: Bearer {{admin_token}}

{
  "employeeId": {{employeeId}}
}
```

**Tests Script:**
```javascript
pm.test("Appointment assigned successfully", function() {
    pm.response.to.have.status(200);
    var jsonData = pm.response.json();
    pm.expect(jsonData.status).to.eql("UPCOMING");
    pm.expect(jsonData.employeeId).to.not.be.null;
    pm.expect(jsonData.employeeName).to.not.be.null;
    console.log("Assigned to: " + jsonData.employeeName);
});
```

---

### 8. Verify Assignment (Customer View)

**Request:**
```
GET {{base_url}}/api/appointments/{{appointmentId}}
Authorization: Bearer {{customer_token}}
```

**Tests Script:**
```javascript
pm.test("Customer can see assigned appointment", function() {
    pm.response.to.have.status(200);
    var jsonData = pm.response.json();
    pm.expect(jsonData.status).to.eql("UPCOMING");
    pm.expect(jsonData.employeeId).to.not.be.null;
    pm.expect(jsonData.employeeName).to.not.be.null;
});
```

---

## Security Tests

### Test 1: Customer Cannot Access Admin Endpoints

**Request:**
```
GET {{base_url}}/api/appointments/pending
Authorization: Bearer {{customer_token}}
```

**Expected Result:** 403 Forbidden

---

### Test 2: Customer Cannot Assign Appointments

**Request:**
```
PUT {{base_url}}/api/appointments/{{appointmentId}}/assign
Content-Type: application/json
Authorization: Bearer {{customer_token}}

{
  "employeeId": 1
}
```

**Expected Result:** 403 Forbidden

---

### Test 3: Unauthorized Access

**Request:**
```
GET {{base_url}}/api/appointments/pending
```

**Expected Result:** 401 Unauthorized (no token provided)

---

## Edge Case Tests

### Test 1: Assign to Non-Existent Employee

**Request:**
```
PUT {{base_url}}/api/appointments/{{appointmentId}}/assign
Content-Type: application/json
Authorization: Bearer {{admin_token}}

{
  "employeeId": 99999
}
```

**Expected Result:** 500 with error message "Employee not found"

---

### Test 2: Assign Non-Existent Appointment

**Request:**
```
PUT {{base_url}}/api/appointments/invalid-appointment-id/assign
Content-Type: application/json
Authorization: Bearer {{admin_token}}

{
  "employeeId": 1
}
```

**Expected Result:** 500 with error message "Appointment not found"

---

### Test 3: Get Availability for Past Date

**Request:**
```
GET {{base_url}}/api/appointments/available-employees?date=2020-01-01
Authorization: Bearer {{admin_token}}
```

**Expected Result:** 200 with empty list or all employees available

---

## Complete Postman Collection JSON

You can import this into Postman:

```json
{
  "info": {
    "name": "Appointment Assignment API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Customer Flow",
      "item": [
        {
          "name": "Customer Login",
          "request": {
            "method": "POST",
            "header": [{"key": "Content-Type", "value": "application/json"}],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"email\": \"customer@example.com\",\n  \"password\": \"password123\"\n}"
            },
            "url": {
              "raw": "{{base_url}}/api/auth/login",
              "host": ["{{base_url}}"],
              "path": ["api", "auth", "login"]
            }
          }
        },
        {
          "name": "Create Appointment",
          "request": {
            "method": "POST",
            "header": [
              {"key": "Content-Type", "value": "application/json"},
              {"key": "Authorization", "value": "Bearer {{customer_token}}"}
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"service\": \"Oil Change, Brake Inspection\",\n  \"vehicleNo\": \"ABC-1234\",\n  \"vehicleId\": \"vehicle-123\",\n  \"date\": \"2025-11-20\",\n  \"startTime\": \"10:00\",\n  \"endTime\": \"12:00\"\n}"
            },
            "url": {
              "raw": "{{base_url}}/api/appointments",
              "host": ["{{base_url}}"],
              "path": ["api", "appointments"]
            }
          }
        }
      ]
    },
    {
      "name": "Admin Flow",
      "item": [
        {
          "name": "Admin Login",
          "request": {
            "method": "POST",
            "header": [{"key": "Content-Type", "value": "application/json"}],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"email\": \"admin@example.com\",\n  \"password\": \"Admin@123\"\n}"
            },
            "url": {
              "raw": "{{base_url}}/api/auth/login",
              "host": ["{{base_url}}"],
              "path": ["api", "auth", "login"]
            }
          }
        },
        {
          "name": "Get Pending Appointments",
          "request": {
            "method": "GET",
            "header": [
              {"key": "Authorization", "value": "Bearer {{admin_token}}"}
            ],
            "url": {
              "raw": "{{base_url}}/api/appointments/pending",
              "host": ["{{base_url}}"],
              "path": ["api", "appointments", "pending"]
            }
          }
        },
        {
          "name": "Get Available Employees",
          "request": {
            "method": "GET",
            "header": [
              {"key": "Authorization", "value": "Bearer {{admin_token}}"}
            ],
            "url": {
              "raw": "{{base_url}}/api/appointments/available-employees?date=2025-11-20",
              "host": ["{{base_url}}"],
              "path": ["api", "appointments", "available-employees"],
              "query": [{"key": "date", "value": "2025-11-20"}]
            }
          }
        },
        {
          "name": "Assign Appointment",
          "request": {
            "method": "PUT",
            "header": [
              {"key": "Content-Type", "value": "application/json"},
              {"key": "Authorization", "value": "Bearer {{admin_token}}"}
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"employeeId\": {{employeeId}}\n}"
            },
            "url": {
              "raw": "{{base_url}}/api/appointments/{{appointmentId}}/assign",
              "host": ["{{base_url}}"],
              "path": ["api", "appointments", "{{appointmentId}}", "assign"]
            }
          }
        }
      ]
    }
  ]
}
```

---

## Tips

1. **Run Tests in Order**: Execute requests sequentially as each depends on previous responses
2. **Use Collection Runner**: Run the entire collection to test the complete flow
3. **Check Console**: Use `console.log()` in test scripts to debug
4. **Environment Switching**: Create separate environments for dev, staging, and production

---

## Expected Results Summary

| Step | Status | Key Verification |
|------|--------|------------------|
| Customer Login | 200 | Token received |
| Create Appointment | 200 | Status = PENDING, employeeId = null |
| View Own Appointments | 200 | Array returned |
| Admin Login | 200 | Admin token received |
| Get Pending | 200 | Contains PENDING appointments |
| Get Available Employees | 200 | Shows appointment counts |
| Assign Appointment | 200 | Status = UPCOMING, employee assigned |
| Customer Views Updated | 200 | Can see assigned employee |

