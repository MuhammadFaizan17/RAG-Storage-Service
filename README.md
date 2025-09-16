# 🚀 RAG Chat Storage Microservice

A fast, secure, and scalable backend for storing chat histories from RAG (Retrieval-Augmented Generation) chatbot systems.

---

## ✨ Key Features
- **Chat Sessions:** Create, rename, favorite, delete, and paginate sessions
- **Messages:** Store messages with sender, content, and context
- **User Management:** Email-based user identification
- **API Security:** API key authentication & rate limiting
- **Health Checks:** `/api/health` endpoint
- **OpenAPI Docs:** Interactive Swagger UI
- **CORS & Logging:** Secure CORS, centralized logging
- **Global Exception Handler:** Standardized error responses across all endpoints
- **Dockerized:** One-command setup with Docker Compose
- **Database Migrations:** Managed with Liquibase

---

## 🛠️ Tech Stack
- **Java 21** · Spring Boot · Spring Security · JPA · Hibernate
- **PostgreSQL** · Liquibase · PgAdmin
- **Bucket4j** (rate limiting) · OpenAPI/Swagger
- **JUnit 5** · Mockito · Spring Boot Test
- **Docker** · Docker Compose

---

## ⚡ Quick Start
1. **Clone & Configure**
   ```bash
   git clone <repo-url>
   cd service
   cp .env.example .env
   # Edit .env for your secrets
   ```
2. **Run with Docker**
   ```bash
   docker compose up -d
   ```
3. **Access Services**
   - API: [http://localhost:8080](http://localhost:8080)
   - Docs: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
   - PgAdmin: [http://localhost:5050](http://localhost:5050)

---

## 🔒 Authentication
All endpoints (except health/docs) require:
```
X-API-Key: your-api-key
```

---

## 📚 API Endpoints
- **Users:**
  - `POST /api/users` — Create user
  - `GET /api/users/{id}` — Get user
- **Sessions:**
  - `POST /api/chat/sessions` — Create session
  - `PUT /api/chat/sessions/{id}` — Update session
  - `GET /api/chat/sessions` — List sessions (pagination)
  - `DELETE /api/chat/sessions/{id}` — Delete session
  - `GET /api/chat/sessions?favorite=true` — Favorite sessions
- **Messages:**
  - `POST /api/chat/sessions/{id}/messages` — Add message
  - `GET /api/chat/sessions/{id}/messages` — Get messages (pagination)
- **Session Chat (Context):**
  - `GET /api/session-chat/{id}/messages` — Paginated messages
  - `POST /api/session-chat/{id}/add-message` — Add message with context
- **Health:**
  - `GET /api/health` — Health check

---

## 🗄️ Database
- **Tables:** users, chat_sessions, chat_messages, session_chat
- **Features:** UUID PKs, timestamps, cascading deletes, JSONB, full-text search
- **Migrations:** Managed by Liquibase (see `src/main/resources/db/changelog`)

---

## 🚦 Rate Limiting
- Default: 100 requests/minute per API key
- Configurable via `.env`

---

## 🧪 Testing & Development
- **Run tests:** `./mvnw test`
- **Integration tests:** `./mvnw verify`
- **Local dev:** `./mvnw spring-boot:run`
- **DB migrations:** Liquibase (`./mvnw liquibase:update`)

---

## 🛡️ Security & Best Practices
- No hardcoded secrets
- Input validation & error handling
- Secure headers & CORS
- No sensitive data in logs
- **Sonar Compliance:** Code meets quality standards for maintainability, reliability, and security

---

## 🏁 Production Tips
- Use secure passwords & API keys
- Enable HTTPS
- Tune rate limits
- Minimum 2GB RAM, Java 17+, PostgreSQL 12+

---

## 📦 Docker Services
- `app`: Spring Boot API
- `postgres`: Database
- `pgadmin`: DB management

---

## 📄 Environment Variables
See `.env.example` for all required variables.

---

## 📞 Need Help?
Open an issue or contact the RAG Service Team!
