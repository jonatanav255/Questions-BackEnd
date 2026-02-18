# Questions Game - Backend API

A RESTful API backend for a flashcard-style questions game application, built with Spring Boot 3.4.0.

## Tech Stack

- **Framework**: Spring Boot 3.4.0
- **Language**: Java 17
- **Database**: PostgreSQL 15
- **ORM**: Spring Data JPA / Hibernate 6.6.2
- **Build Tool**: Maven (with Maven Wrapper)
- **Port**: 8080

## Features

### ✅ Implemented APIs

#### 1. Categories API (`/api/categories`)
- Create, read, update, delete categories
- Auto-tracking of question counts per category
- Validation prevents deletion of categories with questions

#### 2. Tags API (`/api/tags`)
- Create, read, update, delete tags
- Case-insensitive tag name handling (normalized to lowercase)
- Auto-create tags when creating questions (getOrCreate pattern)

#### 3. Questions API (`/api/questions`) - **NEW!**
- Full CRUD operations for questions
- Pagination support for all list endpoints
- Advanced filtering:
  - By category
  - By difficulty level (BEGINNER, INTERMEDIATE, SENIOR)
  - By tags
  - Combined filters (category + difficulty)
- Random question selection for practice sessions
- Question counting with filters
- Automatic category question count management
- Many-to-many relationship with tags
- Code snippet support for technical questions

## Quick Start

### Prerequisites

- Java 17 or higher
- PostgreSQL 15
- Maven (or use included Maven Wrapper)

### Database Setup

1. Create a PostgreSQL database:
```sql
CREATE DATABASE questions_db;
```

2. Create a `.env` file in the project root:
```env
DB_URL=jdbc:postgresql://localhost:5432/questions_db
DB_USERNAME=postgres
DB_PASSWORD=your_password
```

### Running the Application

```bash
# Build the project
./mvnw clean install

# Run the application
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`

### Testing the API

Check health:
```bash
curl http://localhost:8080/api/health
```

Get all categories:
```bash
curl http://localhost:8080/api/categories
```

Create a question:
```bash
curl -X POST http://localhost:8080/api/questions \
  -H "Content-Type: application/json" \
  -d '{
    "question": "What is polymorphism?",
    "answer": "Polymorphism allows objects to be treated as instances of their parent class.",
    "difficulty": "INTERMEDIATE",
    "categoryId": "your-category-uuid-here",
    "tags": ["oop", "java"]
  }'
```

## API Documentation

### Complete API Reference

See **[FRONTEND_INTEGRATION_GUIDE.md](./FRONTEND_INTEGRATION_GUIDE.md)** for comprehensive API documentation including:
- All endpoints with examples
- Request/response formats
- Query parameters
- Validation rules
- Error handling
- TypeScript type definitions
- Frontend integration guide

### Key Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/categories` | GET | Get all categories |
| `/api/categories` | POST | Create a category |
| `/api/tags` | GET | Get all tags |
| `/api/questions` | GET | Get questions (paginated, filterable) |
| `/api/questions` | POST | Create a question |
| `/api/questions/{id}` | GET | Get single question |
| `/api/questions/random` | GET | Get random questions for practice |
| `/api/questions/count` | GET | Get question counts |

## Database Schema

### Tables

**categories**
- `id` (UUID, PK)
- `name` (VARCHAR 100, NOT NULL)
- `color` (VARCHAR 50, NOT NULL)
- `icon` (VARCHAR 255)
- `question_count` (INTEGER, NOT NULL, default 0)
- `created_at` (TIMESTAMP, NOT NULL)

**tags**
- `id` (UUID, PK)
- `name` (VARCHAR 50, NOT NULL, UNIQUE)
- `created_at` (TIMESTAMP, NOT NULL)

**questions**
- `id` (UUID, PK)
- `question` (VARCHAR 1000, NOT NULL)
- `answer` (VARCHAR 5000, NOT NULL)
- `code_snippet` (VARCHAR 10000)
- `difficulty` (ENUM: BEGINNER, INTERMEDIATE, SENIOR)
- `category_id` (UUID, FK → categories)
- `created_at` (TIMESTAMP, NOT NULL)

**question_tags** (join table)
- `question_id` (UUID, FK → questions)
- `tag_id` (UUID, FK → tags)

### Indexes
- `idx_tag_name` on tags.name
- `idx_question_category` on questions.category_id
- `idx_question_difficulty` on questions.difficulty

## Architecture

```
src/main/java/com/questions/backend/
├── controller/         # REST endpoints
│   ├── HealthController
│   ├── CategoryController
│   ├── TagController
│   └── QuestionController
├── service/           # Business logic
│   ├── CategoryService
│   ├── TagService
│   └── QuestionService
├── repository/        # Data access layer (JPA)
│   ├── CategoryRepository
│   ├── TagRepository
│   └── QuestionRepository
├── entity/            # JPA entities
│   ├── Category
│   ├── Tag
│   ├── Question
│   └── DifficultyLevel (enum)
├── dto/              # Data transfer objects
│   ├── QuestionRequest
│   ├── QuestionResponse
│   └── ErrorResponse
└── exception/        # Custom exceptions + global handler
    ├── GlobalExceptionHandler
    ├── ResourceNotFoundException
    ├── DuplicateResourceException
    └── ResourceConflictException
```

## Error Handling

All errors follow a consistent JSON structure:

```json
{
  "timestamp": "2026-02-18T14:43:12.029160",
  "status": 404,
  "error": "Not Found",
  "message": "Question not found with id: 'abc-123'",
  "path": "/api/questions/abc-123"
}
```

**HTTP Status Codes:**
- `200 OK` - Successful GET/PUT
- `201 Created` - Successful POST
- `204 No Content` - Successful DELETE
- `400 Bad Request` - Validation errors
- `404 Not Found` - Resource not found
- `409 Conflict` - Duplicate or constraint violation
- `500 Internal Server Error` - Unexpected errors

## Configuration

### application.properties

```properties
# Database
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/questions_db}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:}

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### CORS Setup (for frontend integration)

Add to `application.properties`:
```properties
spring.web.cors.allowed-origins=http://localhost:3000
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allowed-headers=*
```

## Development

### Build Commands

```bash
# Clean build
./mvnw clean install

# Run tests
./mvnw test

# Skip tests
./mvnw clean install -DskipTests

# Run application
./mvnw spring-boot:run

# Package as JAR
./mvnw package
```

### Code Style

- Uses Jakarta Validation (@NotBlank, @Size, @Valid)
- Service layer is @Transactional
- Read-only queries use @Transactional(readOnly = true)
- Repository methods follow Spring Data JPA naming conventions
- Custom queries use @Query for complex operations

## Project Status

### ✅ Completed
- [x] Categories API (full CRUD)
- [x] Tags API (full CRUD)
- [x] Questions API (full CRUD)
- [x] Pagination support
- [x] Advanced filtering
- [x] Random question selection
- [x] Automatic tag creation
- [x] Question count tracking
- [x] Comprehensive error handling
- [x] Input validation
- [x] Database relationships (ManyToOne, ManyToMany)

### 🚧 Planned (Future Enhancements)
- [ ] Practice session tracking (persist sessions to DB)
- [ ] User authentication & authorization
- [ ] Question review/rating system
- [ ] Statistics and analytics
- [ ] Swagger/OpenAPI documentation
- [ ] Integration tests
- [ ] Flyway database migrations
- [ ] Docker containerization

## Frontend Integration

This backend is designed to work with the Next.js 16 frontend located at:
`/Users/jonatanav255/Documents/GitHub/Questions-FrontEnd`

See **[FRONTEND_INTEGRATION_GUIDE.md](./FRONTEND_INTEGRATION_GUIDE.md)** for detailed integration instructions, TypeScript types, and implementation examples.

## License

This project is for educational purposes.

## Contributors

- Backend developed with Claude Code
- Spring Boot 3.4.0 framework
- PostgreSQL database
