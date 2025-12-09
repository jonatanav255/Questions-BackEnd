# Backend Development Log - Questions Game App

## Project Overview
Backend REST API for the Questions Game application. Provides data persistence, business logic, and API endpoints for managing questions, categories, and practice sessions.

## Tech Stack
- **Framework**: Spring Boot 3.4.0
- **Language**: Java 17
- **Build Tool**: Maven
- **Database**: PostgreSQL 15.3
- **ORM**: Hibernate (via Spring Data JPA)
- **Additional Libraries**:
  - Lombok (reduce boilerplate code)
  - Spring Validation (bean validation)

## Architecture

### Project Structure
```
Questions-BackEnd/
├── src/main/java/com/questions/backend/
│   ├── controller/
│   │   └── HealthController.java           # Health check endpoints
│   └── QuestionsBackendApplication.java    # Main Spring Boot application
├── src/main/resources/
│   └── application.properties              # Configuration (uses env variables)
├── src/test/java/
│   └── QuestionsBackendApplicationTests.java
├── .env                                    # Environment variables (gitignored)
├── .env.example                            # Template for environment variables
├── pom.xml                                 # Maven dependencies
├── .gitignore                              # Excludes logs, .env, target/, etc.
└── BACKEND_LOG.md                          # This file
```

### Database Schema (Planned)
Based on frontend requirements from DEV_LOG.md:

```sql
-- Categories table
CREATE TABLE categories (
  id UUID PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  color VARCHAR(50) NOT NULL,
  icon VARCHAR(255),
  question_count INTEGER DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Questions table
CREATE TABLE questions (
  id UUID PRIMARY KEY,
  question TEXT NOT NULL,
  answer TEXT NOT NULL,
  code_snippet TEXT,
  tags TEXT[],
  category_id UUID REFERENCES categories(id),
  difficulty VARCHAR(20) NOT NULL CHECK (difficulty IN ('beginner', 'intermediate', 'senior')),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Practice sessions table
CREATE TABLE practice_sessions (
  id UUID PRIMARY KEY,
  user_id UUID,  -- For future multi-user support
  category_id UUID REFERENCES categories(id),
  difficulty VARCHAR(20) NOT NULL,
  question_ids UUID[],
  score INTEGER,
  total INTEGER,
  started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  completed_at TIMESTAMP
);

-- Session answers table
CREATE TABLE session_answers (
  id UUID PRIMARY KEY,
  session_id UUID REFERENCES practice_sessions(id),
  question_id UUID REFERENCES questions(id),
  user_answer TEXT,
  self_rating VARCHAR(20) CHECK (self_rating IN ('correct', 'incorrect')),
  answered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Configuration

### Security: Environment Variables
Database credentials are stored in environment variables (NOT committed to git):

**Setup:**
1. Copy `.env.example` to `.env`
2. Fill in your actual database credentials in `.env`
3. The `.env` file is gitignored and will never be committed

**Files:**
- `.env` - Your actual credentials (gitignored)
- `.env.example` - Template for other developers (committed to git)

### Database Connection (application.properties)
```properties
# Uses environment variables with fallback defaults
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/questions_db}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:}
spring.jpa.hibernate.ddl-auto=update
```

### Server Settings
- **Port**: 8080
- **Context Path**: /
- **JPA**: Auto-update schema enabled (development mode)
- **SQL Logging**: Enabled for debugging

### Lombok Configuration (VS Code)

**Why Lombok?** Reduces boilerplate code (getters, setters, constructors, etc.)

**VS Code Setup:**
The project includes VS Code configuration to prevent Lombok annotation processor warnings:

1. **`.vscode/settings.json`** - Suppresses the annotation processor warning
2. **`.vscode/extensions.json`** - Recommends the Lombok extension

**First Time Setup:**
When you open this project in VS Code, you'll see a notification to install recommended extensions. Click "Install All" to get the Lombok extension (`GabrielBB.vscode-lombok`).

**Manual Installation:**
```bash
code --install-extension GabrielBB.vscode-lombok
```

Then reload VS Code: `Cmd+Shift+P` → "Developer: Reload Window"

**No Warning Guarantee:** With these settings, Lombok works perfectly without the `NoClassDefFoundError` warning.

## Setup & Installation

### Prerequisites
- Java 17 or higher
- PostgreSQL 15+
- Maven 3.8+ (or use included Maven wrapper)

### Initial Setup (2025-12-08)

1. **Created Spring Boot project** using Spring Initializr
   - Spring Boot 3.4.0 (latest stable)
   - Dependencies: Web, Data JPA, PostgreSQL, Validation, Lombok

2. **Created PostgreSQL database**
   ```bash
   createdb questions_db
   ```

3. **Configured environment variables** (Security)
   - Created `.env` file with database credentials
   - Created `.env.example` as template for other developers
   - Added `.env` to `.gitignore` to prevent credential exposure

4. **Configured application.properties**
   - Database connection using environment variables
   - JPA/Hibernate configuration
   - Debug logging enabled

5. **First successful run**
   ```bash
   ./mvnw clean spring-boot:run
   ```
   - Application started on port 8080
   - Successfully connected to PostgreSQL
   - Hibernate initialized with no entities (expected)

### Build & Run

**Using Maven Wrapper (recommended):**
```bash
./mvnw clean spring-boot:run
```

**Using installed Maven:**
```bash
mvn clean spring-boot:run
```

**Build JAR:**
```bash
./mvnw clean package
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

## API Endpoints

### Health Check (✅ Implemented)
```
GET    /api/health       # Health check with status, version, and timestamp
GET    /api/health/ping  # Simple ping endpoint (returns "pong")
```

**Example Response:**
```json
{
  "status": "UP",
  "message": "Questions Backend API is running",
  "timestamp": "2025-12-08T20:09:49.399904",
  "version": "0.0.1-SNAPSHOT"
}
```

**Test:**
```bash
curl http://localhost:8080/api/health
curl http://localhost:8080/api/health/ping
```

### Categories (Planned)
```
GET    /api/categories              # List all categories
GET    /api/categories/{id}         # Get category by ID
POST   /api/categories              # Create new category
PUT    /api/categories/{id}         # Update category
DELETE /api/categories/{id}         # Delete category
```

### Questions
```
GET    /api/questions                           # List all questions (with filters)
GET    /api/questions/{id}                      # Get question by ID
GET    /api/questions/category/{categoryId}     # Questions by category
GET    /api/questions/difficulty/{level}        # Questions by difficulty
POST   /api/questions                           # Create new question
PUT    /api/questions/{id}                      # Update question
DELETE /api/questions/{id}                      # Delete question
```

### Practice Sessions
```
POST   /api/practice/start          # Create session, return questions
GET    /api/practice/{id}           # Get session details
POST   /api/practice/{id}/answer    # Submit answer + rating
POST   /api/practice/{id}/complete  # Mark session complete
GET    /api/practice/{id}/results   # Get session results
GET    /api/practice/history        # Get user's past sessions (future)
```

## Current Status

### Completed (2025-12-08)
- ✅ Spring Boot 3.4.0 project initialized
- ✅ PostgreSQL database created (`questions_db`)
- ✅ Application configuration setup
- ✅ Database connection verified
- ✅ Application runs successfully
- ✅ Maven build configured
- ✅ .gitignore configured (excludes DEV_LOG.md, environment files)
- ✅ Environment variables for database credentials (security)
- ✅ Health check endpoints implemented (`/api/health`, `/api/health/ping`)
- ✅ Lombok configured with VS Code settings (no warnings)

### Application Startup Log
```
Tomcat started on port 8080 (http)
Started QuestionsBackendApplication in 1.329 seconds
Database version: PostgreSQL 15.3
Hibernate ORM core version 6.6.2.Final
```

## Architecture Decisions

Based on frontend requirements and scalability needs:

### Data Persistence
- **✅ Session Persistence**: Store practice sessions in database
  - **Why**: Enable history tracking, analytics, resume capability
  - **Benefits**: Users can review past sessions, track progress over time, practice wrong answers again
  - **Alternative considered**: Stateless backend (rejected - limits features severely)

### Database Design
- **✅ Category Deletion**: Prevent deletion if category has questions
  - **Why**: Data integrity - avoid orphaned questions
  - **Behavior**: Return 409 Conflict error with message

- **✅ Pagination**: Implement pagination for question lists
  - **Page size**: 20 items per page
  - **Why**: Performance - avoid loading hundreds of questions at once

- **✅ Tags Storage**: Separate Tag table with many-to-many relationship
  - **Why**: Normalized approach enables tag management, autocomplete, usage statistics
  - **Schema**: Tag entity + QuestionTag join table
  - **Benefits**: Query all unique tags, show tag usage counts, prevent duplicates

---

## Development Approach

Our implementation philosophy for this project:

### Core Principles
- ✅ **Learn as we go** - Understand each piece before moving to the next
- ✅ **Production quality** - Clean code, validation, proper error handling
- ✅ **Documentation** - Keep BACKEND_LOG.md current with what's completed
- ✅ **Testable** - Each feature tested and working before moving on
- ✅ **No overwhelm** - One feature at a time, fully implemented

### Implementation Strategy
1. Build one complete feature (Entity → Repository → Service → Controller)
2. Test it thoroughly with curl/Postman
3. Document it in BACKEND_LOG.md
4. Move to next feature

**Why this approach?**
- Ensures production-ready code at every step
- Allows time for learning and understanding
- Makes debugging easier (smaller surface area)
- Provides working milestones

---

## Implementation Roadmap

### Phase 1: Core Data APIs

#### 1.1 Category API
- [x] Create Category entity
- [x] Create CategoryRepository
- [x] Create CategoryService (with question count logic)
- [ ] Create CategoryController
- [ ] Test CRUD operations
- [ ] **API Endpoints**: GET, POST, PUT, DELETE `/api/categories`

#### 1.2 Tag API
- [ ] Create Tag entity
- [ ] Create TagRepository
- [ ] Create TagService
- [ ] Create TagController
- [ ] Test CRUD operations
- [ ] **API Endpoints**: GET, POST `/api/tags`

#### 1.3 Question API (Most Complex)
- [ ] Create Question entity (with Category & Tag relationships)
- [ ] Create QuestionRepository (with pagination)
- [ ] Create QuestionService
- [ ] Create QuestionController
- [ ] Test CRUD + pagination
- [ ] **API Endpoints**: GET (paginated), POST, PUT, DELETE `/api/questions`

---

### Phase 2: Practice System

#### 2.1 Practice Session Entities
- [ ] Create PracticeSession entity
- [ ] Create SessionAnswer entity
- [ ] Create repositories for both
- [ ] Test database persistence

#### 2.2 Practice API
- [ ] Create PracticeSessionService (with question selection algorithm)
- [ ] Create PracticeController
- [ ] Test complete practice workflow
- [ ] **API Endpoints**:
  - `POST /api/practice/start` - Start session, return 20 random questions
  - `POST /api/practice/{id}/answer` - Submit answer with self-rating
  - `POST /api/practice/{id}/complete` - Finish session, calculate score
  - `GET /api/practice/{id}/results` - Get results with correct/incorrect breakdown

---

### Phase 3: Configuration & Polish

#### 3.1 Security & CORS
- [ ] Configure CORS for `http://localhost:3000` (frontend)
- [ ] Add request validation (`@Valid`)
- [ ] Create global exception handler (`@ControllerAdvice`)

#### 3.2 Error Handling
- [ ] Handle EntityNotFoundException → 404
- [ ] Handle validation errors → 400
- [ ] Handle constraint violations → 409
- [ ] Custom exception for "Category has questions" → 409

#### 3.3 Testing & Documentation
- [ ] Write integration tests for each API
- [ ] Add API documentation (Swagger/OpenAPI) - optional
- [ ] Performance testing with pagination

---

## Features Completed

### Infrastructure (2025-12-08)
- ✅ Spring Boot 3.4.0 project initialized
- ✅ PostgreSQL database created (`questions_db`)
- ✅ Maven build configured
- ✅ Environment variables for database credentials (security)
- ✅ .gitignore configured (excludes logs, .env, target/)
- ✅ Lombok removed (using plain Java for clarity)

### API Endpoints (2025-12-08)
- ✅ Health check API: `GET /api/health` and `GET /api/health/ping`

### Category API (2025-12-08)
- ✅ **Category Entity** created with validation annotations
  - Fields: id (UUID), name, color, icon, questionCount, createdAt
  - Validation: @NotBlank, @Size constraints
  - Auto-generated UUID and timestamp
- ✅ **CategoryRepository** created
  - Extends JpaRepository for basic CRUD
  - Custom methods: `findByName()`, `existsByName()`
- ✅ **CategoryService** created with business logic
  - CRUD operations: getAllCategories(), getCategoryById(), createCategory(), updateCategory(), deleteCategory()
  - Duplicate name prevention (checks existsByName before create/update)
  - Delete protection (prevents deletion if category has questions)
  - Question count management: incrementQuestionCount(), decrementQuestionCount()
  - Uses @Transactional for database safety

### In Progress
- 🚧 **Next**: CategoryController (REST API endpoints)

---

## Development Notes

### Design Decisions
- **UUID for IDs**: Better for distributed systems and prevents sequential ID guessing
- **Hibernate ddl-auto=update**: Convenient for development, should be changed to `validate` or `none` in production
- **SQL logging enabled**: Helps with debugging during development
- **No Lombok**: Using plain Java for better IDE compatibility and code clarity

### Frontend Integration
This backend is designed to work with the Next.js frontend documented in `DEV_LOG.md`:
- Frontend runs on Next.js 16 with TypeScript
- Dark theme UI with Tailwind CSS
- Expects JSON responses from REST API
- Will need CORS configuration to allow requests from frontend domain

### Testing Strategy
- **Unit Tests**: Service layer business logic
- **Integration Tests**: Repository layer database operations
- **API Tests**: Controller endpoints with MockMvc
- **E2E Tests**: Full request/response cycle with test database

## Production Readiness Checklist (Future)
- [ ] Change `ddl-auto` to `validate` or use migrations (Flyway/Liquibase)
- [x] Externalize sensitive configuration (use environment variables)
- [ ] Add comprehensive error handling
- [ ] Implement request/response logging
- [ ] Add health check endpoints (Spring Actuator)
- [ ] Configure connection pooling appropriately
- [ ] Add database indexes for performance
- [ ] Implement rate limiting
- [ ] Add API versioning
- [ ] Configure HTTPS
- [ ] Add monitoring and metrics
- [ ] Implement caching where appropriate
- [ ] Write API documentation
- [ ] Set up CI/CD pipeline

## Notes
- Database credentials are stored in `.env` file (gitignored) using environment variables
- Current configuration is optimized for local development
- SQL logging will be disabled in production for performance
- Application is stateless and ready for horizontal scaling
- Spring Boot will load environment variables from `.env` file automatically in development
