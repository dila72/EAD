# RAG Chatbot - Quick Reference

## API Endpoint
```
POST http://localhost:8080/api/chat
```

## Request Format
```json
{
  "message": "Your question here",
  "customerId": "optional-customer-id",
  "conversationId": "optional-conversation-uuid"
}
```

## Response Format
```json
{
  "reply": "AI response",
  "conversationId": "uuid",
  "timestamp": 1699315200000,
  "success": true,
  "error": null
}
```

## Supported Questions

| Category | Example Questions |
|----------|------------------|
| **Services** | "What services do you offer?", "Tell me about oil change" |
| **Pricing** | "How much is tire rotation?", "What are your prices?" |
| **Scheduling** | "What time slots are available?", "When can I book?" |
| **Appointments** | "Show my appointments" (needs customerId) |
| **Duration** | "How long does brake service take?" |
| **Staff** | "Who are your technicians?" |
| **Contact** | "What are your business hours?", "How to contact?" |

## Quick Test Commands

### Health Check
```bash
curl http://localhost:8080/api/chat/health
```

### Ask a Question
```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "What services do you offer?"}'
```

### With Customer ID
```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Show my appointments", "customerId": "customer-123"}'
```

## JavaScript Example
```javascript
async function chat(message, customerId = null) {
  const res = await fetch('http://localhost:8080/api/chat', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ message, customerId })
  });
  return await res.json();
}

// Usage
const response = await chat("What services do you offer?");
console.log(response.reply);
```

## Configuration
Add to `application.properties`:
```properties
gemini.api.key=YOUR_API_KEY
```

Get API key: https://makersuite.google.com/app/apikey

## Key Classes

| Class | Purpose |
|-------|---------|
| `ChatController` | REST endpoint |
| `ChatbotService` | Orchestration |
| `RAGService` | Context retrieval |
| `GeminiService` | AI generation |
| `ChatRequest` | Request DTO |
| `ChatResponse` | Response DTO |

## Run Application
```bash
cd d:\EAD\ead-automobile
.\mvnw.cmd spring-boot:run
```

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Empty responses | Add data to database (services, slots) |
| API errors | Check Gemini API key in application.properties |
| CORS errors | Already enabled with `@CrossOrigin` |
| Slow responses | Normal - AI processing takes 2-5 seconds |

## Data Sources

RAG retrieves context from:
- ✅ `services` table - Service info, prices, duration
- ✅ `service_slots` table - Available time slots
- ✅ `appointments` table - Customer appointments
- ✅ `employees` table - Staff information
- ✅ `users` table - Employee names, emails

## How It Works
```
Question → Intent Detection → Database Query → Context Building → AI Prompt → Gemini API → Response
```

## Documentation Files
- **RAG_CHATBOT_GUIDE.md** - Complete implementation guide
- **RAG_IMPLEMENTATION_SUMMARY.md** - Implementation summary
- **RAG_CHATBOT_QUICKREF.md** - This quick reference
