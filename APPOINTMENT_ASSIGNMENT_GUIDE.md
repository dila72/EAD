# Appointment Assignment Feature Guide

## Overview
This feature enables customers to create appointments with selected services, which are then displayed to admins who can assign them to available employees based on their workload and availability.

## Workflow

### 1. Customer Creates Appointment
- Customer selects the services they need
- Creates an appointment with date and time slot
- Appointment is created with `PENDING` status
- No employee is assigned yet

### 2. Admin Views Pending Appointments
- Admin can view all appointments with `PENDING` status
- Each appointment shows customer details, services, date, and time

### 3. Admin Checks Employee Availability
- Admin can query available employees for a specific date
- System shows each employee's current appointment count
- Employees are marked as available/unavailable based on workload (max 5 appointments per day)

### 4. Admin Assigns Appointment
- Admin assigns a pending appointment to a selected employee
- Appointment status changes from `PENDING` to `UPCOMING`
- Employee relationship is established

## API Endpoints

### Customer Endpoints

#### Create Appointment
```http
POST /api/appointments
Content-Type: application/json
Authorization: Bearer {customer_token}

{
  "service": "Oil Change, Tire Rotation",
  "vehicleNo": "ABC123",
  "vehicleId": "vehicle-uuid",
  "date": "2025-11-15",
  "startTime": "10:00",
  "endTime": "11:00"
}
```

**Response:** Returns created appointment with `PENDING` status

#### Get My Appointments
```http
GET /api/appointments
Authorization: Bearer {customer_token}
```

**Response:** List of all appointments for the authenticated customer

---

### Admin Endpoints

#### Get Pending Appointments
```http
GET /api/appointments/pending
Authorization: Bearer {admin_token}
```

**Response:**
```json
[
  {
    "appointmentId": "appt-uuid-123",
    "service": "Oil Change, Tire Rotation",
    "customerId": "customer-123",
    "vehicleId": "vehicle-456",
    "vehicleNo": "ABC123",
    "date": "2025-11-15",
    "startTime": "10:00",
    "endTime": "11:00",
    "status": "PENDING",
    "employeeId": null,
    "employeeName": null
  }
]
```

#### Get Available Employees
```http
GET /api/appointments/available-employees?date=2025-11-15
Authorization: Bearer {admin_token}
```

**Response:**
```json
[
  {
    "employeeId": 1,
    "employeeName": "John Smith",
    "email": "john.smith@company.com",
    "role": "EMPLOYEE",
    "currentAppointmentCount": 2,
    "available": true
  },
  {
    "employeeId": 2,
    "employeeName": "Jane Doe",
    "email": "jane.doe@company.com",
    "role": "EMPLOYEE",
    "currentAppointmentCount": 5,
    "available": false
  }
]
```

**Notes:**
- Employees with 5 or more appointments are marked as unavailable
- Count only includes non-cancelled appointments

#### Assign Appointment to Employee
```http
PUT /api/appointments/{appointmentId}/assign
Authorization: Bearer {admin_token}
Content-Type: application/json

{
  "employeeId": 1
}
```

**Response:**
```json
{
  "appointmentId": "appt-uuid-123",
  "service": "Oil Change, Tire Rotation",
  "customerId": "customer-123",
  "vehicleId": "vehicle-456",
  "vehicleNo": "ABC123",
  "date": "2025-11-15",
  "startTime": "10:00",
  "endTime": "11:00",
  "status": "UPCOMING",
  "employeeId": 1,
  "employeeName": "John Smith"
}
```

**Notes:**
- Status changes from `PENDING` to `UPCOMING`
- Employee information is populated

---

## Appointment Status Flow

```
PENDING → UPCOMING → COMPLETED
   ↓
CANCELLED
```

- **PENDING**: Newly created by customer, awaiting admin assignment
- **UPCOMING**: Assigned to employee, scheduled to happen
- **COMPLETED**: Service has been completed
- **CANCELLED**: Appointment was cancelled

---

## Database Changes

### AppointmentStatus Enum
Added new status: `PENDING`

### Appointment Entity
- Added `employee` relationship (ManyToOne)
- Employee can have multiple appointments

### New DTOs
1. **EmployeeAvailabilityDTO**: Shows employee workload and availability
2. **AssignAppointmentRequest**: Request body for assignment

---

## Business Logic

### Employee Availability Calculation
- Counts non-cancelled appointments for a specific date
- Maximum threshold: 5 appointments per day
- Available if current count < 5

### Assignment Rules
1. Appointment must be in `PENDING` status
2. Employee must exist in the system
3. Upon assignment:
   - Employee relationship is set
   - Status changes to `UPCOMING`

---

## Security

### Role-Based Access Control
- **Customer**: Can only create and view their own appointments
- **Admin**: Can view all appointments, pending list, employee availability, and assign appointments

### Endpoints Protected by @PreAuthorize
- `/api/appointments/pending` - Admin only
- `/api/appointments/available-employees` - Admin only
- `/api/appointments/{id}/assign` - Admin only

---

## Frontend Integration Guide

### Customer Flow
1. **Create Appointment Page**
   - Service selection (multi-select or checkboxes)
   - Vehicle selection
   - Date/time picker
   - Submit → POST to `/api/appointments`
   - Show success message with PENDING status

2. **My Appointments Page**
   - GET from `/api/appointments`
   - Display list with status badges
   - Show "Pending Assignment" for PENDING status
   - Show assigned employee name for UPCOMING status

### Admin Flow
1. **Pending Appointments Dashboard**
   - GET from `/api/appointments/pending`
   - Display table/cards with appointment details
   - Each appointment has "Assign" button

2. **Assignment Modal/Dialog**
   - Opens when admin clicks "Assign"
   - GET from `/api/appointments/available-employees?date={appointmentDate}`
   - Display employees with availability indicators
   - Color-code: Green (available), Red (unavailable)
   - Show current appointment count
   - Select employee and confirm
   - PUT to `/api/appointments/{id}/assign`

3. **Success Handling**
   - Show confirmation message
   - Update appointment list
   - Remove from pending list
   - Show in upcoming appointments

---

## Example Frontend Code Snippets

### React - Fetch Pending Appointments
```javascript
const fetchPendingAppointments = async () => {
  const response = await fetch('/api/appointments/pending', {
    headers: {
      'Authorization': `Bearer ${adminToken}`
    }
  });
  const data = await response.json();
  setPendingAppointments(data);
};
```

### React - Get Available Employees
```javascript
const fetchAvailableEmployees = async (appointmentDate) => {
  const response = await fetch(
    `/api/appointments/available-employees?date=${appointmentDate}`,
    {
      headers: {
        'Authorization': `Bearer ${adminToken}`
      }
    }
  );
  const data = await response.json();
  setEmployees(data);
};
```

### React - Assign Appointment
```javascript
const assignAppointment = async (appointmentId, employeeId) => {
  const response = await fetch(
    `/api/appointments/${appointmentId}/assign`,
    {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${adminToken}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ employeeId })
    }
  );
  
  if (response.ok) {
    const updatedAppointment = await response.json();
    // Update UI
    showSuccessMessage(`Assigned to ${updatedAppointment.employeeName}`);
    refreshPendingList();
  }
};
```

---

## Testing

### Test Scenarios

1. **Customer Creates Appointment**
   - Verify status is PENDING
   - Verify employeeId is null

2. **Admin Views Pending Appointments**
   - Verify only PENDING appointments are returned
   - Verify admin authentication is required

3. **Admin Gets Available Employees**
   - Verify appointment counts are correct
   - Verify availability flag logic
   - Test with different dates

4. **Admin Assigns Appointment**
   - Verify status changes to UPCOMING
   - Verify employee relationship is set
   - Verify error handling for invalid employee/appointment

5. **Authorization Tests**
   - Verify customers can't access admin endpoints
   - Verify customers can't view other customers' appointments

---

## Error Handling

### Common Errors

1. **Appointment Not Found**
   - Status: 404
   - Message: "Appointment not found with id {id}"

2. **Employee Not Found**
   - Status: 404
   - Message: "Employee not found with id {id}"

3. **Unauthorized Access**
   - Status: 403
   - Message: "Not authorized to view/update this appointment"

4. **Invalid Status**
   - Status: 400
   - Message: "Appointment is not in PENDING status"

---

## Future Enhancements

1. **Smart Assignment Algorithm**
   - Auto-suggest best available employee based on:
     - Service type
     - Current workload
     - Skills/specialization
     - Location

2. **Notifications**
   - Notify customer when appointment is assigned
   - Notify employee of new assignment

3. **Calendar Integration**
   - Visual calendar view for admins
   - Drag-and-drop assignment

4. **Employee Skills Matching**
   - Match appointments to employees with relevant skills
   - Service-specific employee filtering

5. **Appointment Conflict Detection**
   - Check for time slot conflicts
   - Prevent double-booking

---

## Troubleshooting

### Issue: Admin can't see pending appointments
- Check if admin role is properly configured
- Verify JWT token contains ROLE_ADMIN
- Check if @PreAuthorize is working

### Issue: Employee availability always shows 0
- Verify appointment status is not CANCELLED
- Check date parameter format (ISO 8601: YYYY-MM-DD)
- Verify employee IDs in database

### Issue: Assignment fails
- Check if appointment is in PENDING status
- Verify employee exists in database
- Check transaction boundaries in service layer

---

## Summary

This implementation provides a complete workflow for appointment management:
1. ✅ Customer creates appointments (PENDING status)
2. ✅ Admin views all pending appointments
3. ✅ Admin checks employee availability by date
4. ✅ Admin assigns appointments to employees
5. ✅ Status updates automatically (PENDING → UPCOMING)
6. ✅ Role-based access control
7. ✅ Employee workload tracking

All endpoints are secured, tested, and ready for frontend integration.
