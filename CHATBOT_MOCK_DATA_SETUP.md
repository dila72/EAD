# Chatbot Mock Data Setup

## Overview

This document explains how to set up mock data for the RAG Chatbot functionality.

## Mock Data Includes:

- **8 Services** (Oil Change, Brake Inspection, Tire Rotation, etc.)
- **23 Time Slots** spread across 3 days (today, tomorrow, and day after tomorrow)

## Loading Mock Data

### Option 1: Using pgAdmin, DBeaver, or any PostgreSQL Client

Copy and paste the following SQL commands directly into your PostgreSQL client:

#### Step 1: Insert Services

```sql
INSERT INTO services (id, name, description, price, estimated_duration_minutes, active, created_at, updated_at)
VALUES
(1, 'Oil Change', 'Complete engine oil change with filter replacement', 49.99, 30, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Brake Inspection', 'Comprehensive brake system inspection and testing', 79.99, 45, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Tire Rotation', 'Professional tire rotation and balance', 39.99, 30, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Engine Diagnostic', 'Complete engine diagnostic scan and analysis', 99.99, 60, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'AC Service', 'Air conditioning system service and recharge', 129.99, 90, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'Battery Replacement', 'Battery testing and replacement service', 149.99, 45, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 'Transmission Service', 'Transmission fluid change and inspection', 199.99, 120, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 'Wheel Alignment', 'Four-wheel computerized alignment', 89.99, 60, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;
```

#### Step 2: Insert Time Slots

```sql
INSERT INTO service_slot (id, date, time, available)
VALUES
-- Today's slots
(1, TO_CHAR(CURRENT_DATE, 'YYYY-MM-DD'), '09:00 AM', true),
(2, TO_CHAR(CURRENT_DATE, 'YYYY-MM-DD'), '10:00 AM', true),
(3, TO_CHAR(CURRENT_DATE, 'YYYY-MM-DD'), '11:00 AM', true),
(4, TO_CHAR(CURRENT_DATE, 'YYYY-MM-DD'), '01:00 PM', false),
(5, TO_CHAR(CURRENT_DATE, 'YYYY-MM-DD'), '02:00 PM', true),
(6, TO_CHAR(CURRENT_DATE, 'YYYY-MM-DD'), '03:00 PM', true),
(7, TO_CHAR(CURRENT_DATE, 'YYYY-MM-DD'), '04:00 PM', true),

-- Tomorrow's slots
(8, TO_CHAR(CURRENT_DATE + INTERVAL '1 day', 'YYYY-MM-DD'), '09:00 AM', true),
(9, TO_CHAR(CURRENT_DATE + INTERVAL '1 day', 'YYYY-MM-DD'), '10:00 AM', true),
(10, TO_CHAR(CURRENT_DATE + INTERVAL '1 day', 'YYYY-MM-DD'), '11:00 AM', true),
(11, TO_CHAR(CURRENT_DATE + INTERVAL '1 day', 'YYYY-MM-DD'), '12:00 PM', true),
(12, TO_CHAR(CURRENT_DATE + INTERVAL '1 day', 'YYYY-MM-DD'), '01:00 PM', true),
(13, TO_CHAR(CURRENT_DATE + INTERVAL '1 day', 'YYYY-MM-DD'), '02:00 PM', true),
(14, TO_CHAR(CURRENT_DATE + INTERVAL '1 day', 'YYYY-MM-DD'), '03:00 PM', true),
(15, TO_CHAR(CURRENT_DATE + INTERVAL '1 day', 'YYYY-MM-DD'), '04:00 PM', true),

-- Day after tomorrow's slots
(16, TO_CHAR(CURRENT_DATE + INTERVAL '2 days', 'YYYY-MM-DD'), '09:00 AM', true),
(17, TO_CHAR(CURRENT_DATE + INTERVAL '2 days', 'YYYY-MM-DD'), '10:00 AM', true),
(18, TO_CHAR(CURRENT_DATE + INTERVAL '2 days', 'YYYY-MM-DD'), '11:00 AM', true),
(19, TO_CHAR(CURRENT_DATE + INTERVAL '2 days', 'YYYY-MM-DD'), '12:00 PM', true),
(20, TO_CHAR(CURRENT_DATE + INTERVAL '2 days', 'YYYY-MM-DD'), '01:00 PM', true),
(21, TO_CHAR(CURRENT_DATE + INTERVAL '2 days', 'YYYY-MM-DD'), '02:00 PM', true),
(22, TO_CHAR(CURRENT_DATE + INTERVAL '2 days', 'YYYY-MM-DD'), '03:00 PM', true),
(23, TO_CHAR(CURRENT_DATE + INTERVAL '2 days', 'YYYY-MM-DD'), '04:00 PM', true)
ON CONFLICT (id) DO NOTHING;
```

###Option 2: Using the SQL File
If your PostgreSQL client supports running SQL files, use the `data.sql` file located at:

```
src/main/resources/data.sql
```

### Option 3: Manual Loading via psql (if available)

```bash
psql -U postgres -d auto-mobile-3 -f src/main/resources/data.sql
```

## Testing the Chatbot

Once the mock data is loaded, you can test the chatbot with these sample questions:

### Test Questions:

1. **Services Query:**

```json
{
  "message": "What services do you offer?",
  "conversationId": "test-001"
}
```

2. **Pricing Query:**

```json
{
  "message": "How much does an oil change cost?",
  "conversationId": "test-002"
}
```

3. **Available Slots:**

```json
{
  "message": "When are you available for appointments?",
  "conversationId": "test-003"
}
```

4. **Service Duration:**

```json
{
  "message": "How long does a brake inspection take?",
  "conversationId": "test-004"
}
```

5. **General Info:**

```json
{
  "message": "Tell me about your service center",
  "conversationId": "test-005"
}
```

## API Endpoint

**POST** `http://localhost:8080/api/chat`

### Request Headers:

```
Content-Type: application/json
```

### Request Body:

```json
{
  "message": "Your question here",
  "customerId": "optional-customer-id",
  "conversationId": "optional-conversation-id"
}
```

### Expected Response:

```json
{
  "reply": "AI-generated response based on RAG context",
  "conversationId": "same-or-generated-id",
  "timestamp": 1762468757096,
  "success": true,
  "error": null
}
```

## Important Notes

### Gemini API Configuration

The chatbot uses Google's Gemini AI API. Make sure your API key is correctly configured in `application.properties`:

```properties
gemini.api.key=YOUR_ACTUAL_API_KEY_HERE
```

### Database Tables Required

The chatbot queries these tables:

- `services` - Service information (name, price, duration, description)
- `service_slot` - Available appointment slots
- `appointments` - Customer appointments (optional, for personalized responses)
- `users` and `employees` - Staff information (optional)

## Troubleshooting

### Error: "Unable to process your request"

**Possible causes:**

1. **Gemini API Key invalid** - Check your API key in `application.properties`
2. **Database connection issue** - Verify PostgreSQL is running
3. **Mock data not loaded** - Run the `data.sql` script
4. **API version mismatch** - The code now uses `/v1/` instead of `/v1beta/`

### Error: "Message cannot be empty"

**Cause:** Typo in JSON field name. Use `"message"` not `"messege"`

### No services or slots returned

**Cause:** Mock data not loaded. Execute the `data.sql` script in your database.

## Mock Data Structure

### Services Table

| Field                      | Type    | Example                         |
| -------------------------- | ------- | ------------------------------- |
| id                         | BIGINT  | 1                               |
| name                       | VARCHAR | "Oil Change"                    |
| description                | TEXT    | "Complete engine oil change..." |
| price                      | DECIMAL | 49.99                           |
| estimated_duration_minutes | INTEGER | 30                              |
| active                     | BOOLEAN | true                            |

### Service Slots Table

| Field     | Type    | Example      |
| --------- | ------- | ------------ |
| id        | BIGINT  | 1            |
| date      | VARCHAR | "2025-11-07" |
| time      | VARCHAR | "09:00 AM"   |
| available | BOOLEAN | true         |

## Next Steps

1. Load the mock data using one of the methods above
2. Start the Spring Boot application
3. Test the chatbot using Postman or any API client
4. Check the console logs for detailed error messages if issues occur
