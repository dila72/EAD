# Appointment Assignment - Quick Reference Card

## 🎯 Quick Overview
Customer creates appointment → Admin views pending → Admin checks availability → Admin assigns to employee

---

## 📡 API Endpoints

### Customer Endpoints
```
POST   /api/appointments                    Create appointment (→ PENDING)
GET    /api/appointments                    Get my appointments
GET    /api/appointments/{id}               Get specific appointment
PUT    /api/appointments/{id}               Update appointment
DELETE /api/appointments/{id}               Delete appointment
```

### Admin Endpoints (Requires ROLE_ADMIN)
```
GET    /api/appointments/pending                            Get all pending appointments
GET    /api/appointments/available-employees?date={date}    Get employee availability
PUT    /api/appointments/{id}/assign                        Assign to employee
```

---

## 📦 Request/Response Examples

### Create Appointment (Customer)
```json
POST /api/appointments
{
  "service": "Oil Change, Brake Check",
  "vehicleNo": "ABC-1234",
  "vehicleId": "vehicle-id",
  "date": "2025-11-20",
  "startTime": "10:00",
  "endTime": "12:00"
}

Response: { "appointmentId": "...", "status": "PENDING", ... }
```

### Get Pending Appointments (Admin)
```json
GET /api/appointments/pending

Response: [
  {
    "appointmentId": "...",
    "status": "PENDING",
    "employeeId": null,
    "employeeName": null,
    ...
  }
]
```

### Get Available Employees (Admin)
```json
GET /api/appointments/available-employees?date=2025-11-20

Response: [
  {
    "employeeId": 1,
    "employeeName": "John Doe",
    "email": "john@example.com",
    "role": "EMPLOYEE",
    "currentAppointmentCount": 2,
    "available": true
  }
]
```

### Assign Appointment (Admin)
```json
PUT /api/appointments/{appointmentId}/assign
{
  "employeeId": 1
}

Response: { "appointmentId": "...", "status": "UPCOMING", "employeeId": 1, ... }
```

---

## 🔄 Status Flow
```
PENDING → UPCOMING → COMPLETED
   ↓
CANCELLED
```

---

## 🔑 Authentication Headers
```
Authorization: Bearer {JWT_TOKEN}
```

---

## 📊 Business Rules

| Rule | Value |
|------|-------|
| Max appointments per employee per day | 5 |
| Default status for new appointments | PENDING |
| Status after assignment | UPCOMING |
| Availability threshold | < 5 appointments |

---

## 🎨 Frontend State Management

### Customer View
```javascript
// Show status badge
if (appointment.status === 'PENDING') {
  badge = <Badge color="yellow">Awaiting Assignment</Badge>
} else if (appointment.status === 'UPCOMING') {
  badge = <Badge color="blue">Assigned to {appointment.employeeName}</Badge>
}
```

### Admin View
```javascript
// Check availability color
const color = employee.available ? 'green' : 'red'
const text = `${employee.employeeName} (${employee.currentAppointmentCount}/5)`
```

---

## ⚠️ Error Handling

| Error | Status | Action |
|-------|--------|--------|
| Appointment not found | 404 | Show "Appointment not found" |
| Employee not found | 404 | Show "Employee not found" |
| Unauthorized | 403 | Redirect to login |
| Invalid date format | 400 | Show validation error |

---

## 🧪 Testing Checklist

- [ ] Customer can create appointment (PENDING status)
- [ ] Customer can view their appointments
- [ ] Admin can view pending appointments
- [ ] Admin can check employee availability
- [ ] Admin can assign appointment
- [ ] Status changes to UPCOMING after assignment
- [ ] Employee info appears in appointment
- [ ] Customer cannot access admin endpoints
- [ ] Unauthorized users get 401/403 errors

---

## 🐛 Troubleshooting

**Problem:** "403 Forbidden" on admin endpoints  
**Solution:** Check JWT token has ROLE_ADMIN

**Problem:** Employee availability shows 0 for all  
**Solution:** Check date parameter format (YYYY-MM-DD)

**Problem:** Assignment fails  
**Solution:** Verify appointment is PENDING and employee exists

**Problem:** Customer sees other customers' appointments  
**Solution:** Check authentication in controller

---

## 📁 Key Files

| File | Purpose |
|------|---------|
| `AppointmentController.java` | API endpoints |
| `AppointmentService.java` | Business logic interface |
| `AppointmentServiceImpl.java` | Implementation |
| `AppointmentRepository.java` | Database queries |
| `AppointmentDTO.java` | Data transfer object |
| `EmployeeAvailabilityDTO.java` | Availability response |

---

## 🚀 Getting Started

1. **Build:** `.\mvnw.cmd clean compile`
2. **Run:** `.\mvnw.cmd spring-boot:run`
3. **Test:** Import Postman collection from `APPOINTMENT_ASSIGNMENT_POSTMAN.md`
4. **Integrate:** Follow examples in `APPOINTMENT_ASSIGNMENT_GUIDE.md`

---

## 💡 Pro Tips

1. **Cache availability data** - Employee availability doesn't change frequently
2. **Add loading states** - Assignment can take a few seconds
3. **Show success messages** - Confirm assignment completion
4. **Add filters** - Filter by date, status, employee
5. **Implement notifications** - Notify customer when assigned
6. **Validate dates** - Don't allow past dates
7. **Add confirmation dialogs** - Before assignment
8. **Show appointment details** - In assignment modal

---

## 📞 Quick Links

- Full Guide: `APPOINTMENT_ASSIGNMENT_GUIDE.md`
- Postman Tests: `APPOINTMENT_ASSIGNMENT_POSTMAN.md`
- Summary: `APPOINTMENT_ASSIGNMENT_SUMMARY.md`

---

**Last Updated:** November 6, 2025  
**Version:** 1.0
