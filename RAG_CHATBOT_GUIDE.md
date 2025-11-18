# RAG Chatbot Implementation Guide

## Overview

A **Retrieval-Augmented Generation (RAG)** chatbot has been successfully implemented in the EAD Automobile Service Center application. This chatbot uses AI to provide intelligent responses by retrieving relevant information from the database and generating contextual answers using Google's Gemini AI.

## Architecture

### Components

1. **ChatController** (`controller/ChatController.java`)
   - REST API endpoint: `POST /api/chat`
   - Handles incoming chat requests
   - Returns structured responses with conversation tracking

2. **ChatbotService** (`service/ChatbotService.java`)
   - Orchestrates the RAG workflow
   - Builds prompts with system instructions
   - Manages error handling

3. **RAGService** (`service/RAGService.java`)
   - **Core RAG component** - retrieves relevant context from database
   - Implements intent detection
   - Formats context for AI consumption
   - Retrieves data from multiple sources:
     - Available services and pricing
     - Time slots for appointments
     - Customer appointments
     - Employee information
     - Contact details

4. **GeminiService** (`service/GeminiService.java`)
   - Integrates with Google Gemini AI API
   - Sends prompts and receives AI-generated responses
   - Handles API communication via REST

5. **DTOs** (`dto/ChatRequest.java`, `dto/ChatResponse.java`)
   - Structured request/response formats
   - Support for conversation tracking
   - Customer identification for personalized responses

## How RAG Works

### Traditional Chatbot vs RAG Chatbot

**Traditional Chatbot:**
```
User Question → AI Model → Generic Response
```

**RAG Chatbot:**
```
User Question → Context Retrieval (Database) → AI Model + Context → Accurate Response
```

### RAG Workflow

1. **User sends a question** via `/api/chat` endpoint
2. **Intent Detection**: RAGService analyzes keywords to determine what information is needed
3. **Context Retrieval**: Retrieves relevant data from database:
   - Services, prices, descriptions
   - Available appointment slots
   - Customer's appointments (if customer ID provided)
   - Employee information
   - Contact details
4. **Prompt Building**: Combines context with system instructions
5. **AI Generation**: Sends enriched prompt to Gemini AI
6. **Response**: Returns accurate, contextual answer to user

## API Endpoints

### POST `/api/chat`

Send a message to the chatbot and receive an AI-generated response.

**Request Body:**
```json
{
  "message": "What services do you offer?",
  "customerId": "customer-id-optional",
  "conversationId": "uuid-optional"
}
```

**Response:**
```json
{
  "reply": "We offer the following services: Oil Change ($50, 30 minutes), Tire Rotation ($40, 45 minutes)...",
  "conversationId": "uuid",
  "timestamp": 1699315200000,
  "success": true,
  "error": null
}
```

**Error Response:**
```json
{
  "reply": null,
  "conversationId": null,
  "timestamp": 1699315200000,
  "success": false,
  "error": "Unable to process your request. Please try again later."
}
```

### GET `/api/chat/health`

Check if the chatbot service is running.

**Response:**
```text
Chatbot service is running
```

## Supported Query Types

The RAG chatbot can intelligently answer questions about:

### 1. Services
- "What services do you offer?"
- "How much does an oil change cost?"
- "What types of repairs do you do?"
- "Tell me about your services"

### 2. Pricing
- "How much is a tire rotation?"
- "What are your prices?"
- "Is your service expensive?"
- "Service costs?"

### 3. Appointments & Scheduling
- "What time slots are available?"
- "When can I book an appointment?"
- "Show me available times"
- "What are my appointments?" (requires customerId)

### 4. Duration
- "How long does an oil change take?"
- "What's the duration for brake service?"
- "Time required for inspection?"

### 5. Staff
- "Who are your technicians?"
- "Tell me about your mechanics"
- "Who will work on my car?"

### 6. Contact & Location
- "How can I contact you?"
- "What are your business hours?"
- "Where are you located?"

## Configuration

### Required Configuration (`application.properties`)

```properties
# Gemini AI API Key
gemini.api.key=YOUR_GEMINI_API_KEY
```

**To get a Gemini API key:**
1. Visit: https://makersuite.google.com/app/apikey
2. Sign in with Google account
3. Click "Create API Key"
4. Copy the key and add to `application.properties`

## Testing the Chatbot

### Using cURL

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "What services do you offer?"
  }'
```

### Using Postman

1. **Method**: POST
2. **URL**: `http://localhost:8080/api/chat`
3. **Headers**: 
   - `Content-Type: application/json`
4. **Body** (raw JSON):
```json
{
  "message": "What are your available time slots?",
  "customerId": "optional-customer-id"
}
```

### Using JavaScript/Frontend

```javascript
async function askChatbot(message, customerId = null) {
  try {
    const response = await fetch('http://localhost:8080/api/chat', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        message: message,
        customerId: customerId,
        conversationId: sessionStorage.getItem('conversationId') || null
      })
    });
    
    const data = await response.json();
    
    if (data.success) {
      // Store conversation ID for continuity
      sessionStorage.setItem('conversationId', data.conversationId);
      console.log('Chatbot:', data.reply);
      return data.reply;
    } else {
      console.error('Error:', data.error);
      return null;
    }
  } catch (error) {
    console.error('Request failed:', error);
    return null;
  }
}

// Example usage
askChatbot("What services do you offer?");
askChatbot("Show me my appointments", "customer-123");
```

## Features

### ✅ Intelligent Intent Detection
Automatically detects what the user is asking about and retrieves relevant context.

### ✅ Multi-Source Data Retrieval
Pulls information from:
- Services table
- ServiceSlot table
- Appointments table
- Employees table
- And more...

### ✅ Personalized Responses
When customer ID is provided, retrieves customer-specific appointment information.

### ✅ Conversation Tracking
Supports conversation IDs for maintaining chat context across multiple messages.

### ✅ Error Handling
Graceful error handling with user-friendly error messages.

### ✅ Structured Responses
Returns formatted JSON with success/error status and timestamps.

### ✅ CORS Enabled
Supports cross-origin requests for frontend integration.

## Best Practices

### 1. Always Provide Context
The RAG system works best when it has relevant data in the database:
- Keep services up to date
- Maintain accurate slot availability
- Ensure appointment statuses are current

### 2. Customer Identification
For personalized responses about appointments, pass the customer ID:
```json
{
  "message": "What are my upcoming appointments?",
  "customerId": "customer-123"
}
```

### 3. Clear Questions
Users should ask clear, specific questions for best results:
- ✅ "What is the price of an oil change?"
- ❌ "Tell me everything"

### 4. Handle Errors
Always check the `success` field in responses:
```javascript
if (response.success) {
  // Handle successful response
} else {
  // Show error message to user
  console.error(response.error);
}
```

## Advanced: Extending the RAG System

### Adding New Data Sources

To add new data sources to the RAG context:

1. **Update RAGService.java**:
```java
private String getNewDataContext() {
    // Query your repository
    List<NewEntity> entities = newRepository.findAll();
    
    // Format the data
    StringBuilder sb = new StringBuilder("=== NEW DATA ===\n");
    for (NewEntity entity : entities) {
        sb.append(entity.getInfo()).append("\n");
    }
    return sb.toString();
}
```

2. **Add to buildContext method**:
```java
if (containsKeywords(lowerQuestion, "new", "keyword")) {
    context.append(getNewDataContext()).append("\n\n");
}
```

### Improving Intent Detection

Enhance keyword matching in `buildContext()`:
```java
if (containsKeywords(lowerQuestion, "new", "keywords", "here")) {
    // Retrieve specific context
}
```

## Troubleshooting

### Issue: "Unable to generate response"
**Solution**: Check that your Gemini API key is valid in `application.properties`

### Issue: Empty or generic responses
**Solution**: Ensure database has relevant data (services, slots, etc.)

### Issue: CORS errors from frontend
**Solution**: The controller has `@CrossOrigin(origins = "*")` enabled. For production, specify allowed origins.

### Issue: Slow responses
**Solution**: 
- Optimize database queries
- Add caching for frequently accessed data
- Consider connection pooling for Gemini API

## Security Considerations

### Production Deployment:

1. **Secure API Key**: Store Gemini API key in environment variables, not in source code
2. **Rate Limiting**: Implement rate limiting to prevent API abuse
3. **Authentication**: Add authentication to chatbot endpoint if needed
4. **CORS**: Restrict CORS origins to your frontend domain
5. **Input Validation**: Already implemented - validates message is not empty
6. **Error Messages**: Avoid exposing internal errors to users

## Performance

- **Average Response Time**: 2-5 seconds (depends on context size and API latency)
- **Context Size**: Dynamically adjusted based on question
- **Concurrent Requests**: Supports multiple simultaneous users
- **Scalability**: Stateless design allows horizontal scaling

## Future Enhancements

Potential improvements:
- [ ] Conversation history storage in database
- [ ] Multi-language support
- [ ] Voice input/output
- [ ] Sentiment analysis
- [ ] Appointment booking through chat
- [ ] Caching for common questions
- [ ] Analytics dashboard for chat metrics
- [ ] Integration with WhatsApp/Telegram
- [ ] Proactive notifications

## Summary

The RAG chatbot implementation successfully combines:
- **Retrieval**: Smart context extraction from database
- **Augmentation**: Enriching AI prompts with relevant data
- **Generation**: AI-powered natural language responses

This approach ensures the chatbot provides **accurate, up-to-date, and contextual** answers based on your actual business data, rather than generic AI responses.
