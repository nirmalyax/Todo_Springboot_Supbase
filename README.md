# Todo Application with Spring Boot and Supabase

A robust, secure Todo application backend built with Spring Boot and integrated with Supabase PostgreSQL database.

## Features

- User registration and authentication (JWT based)
- CRUD operations for Todo items
- Status update functionality for Todos
- Search and filter Todo items by various criteria
- Due date tracking for Todo items
- Role-based authorization
- Secure API endpoints
- Row-level security to ensure users can only access their own data

## Technologies Used

- **Spring Boot 3.3.0**: Core framework
- **Spring Security**: Authentication and authorization
- **Spring Data JPA**: Database interaction
- **Supabase**: PostgreSQL database service
- **JWT (JSON Web Tokens)**: Secure authentication
- **Hibernate Validator**: Input validation
- **Lombok**: Reduce boilerplate code
- **Maven**: Dependency management and build tool
- **Java 17**: Programming language

## Prerequisites

- Java 17 or higher
- Maven 3.6.0 or higher
- Supabase account (https://supabase.com)
- Git (optional)
- IDE (IntelliJ IDEA, Eclipse, VSCode, etc.)

## Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/todo-supabase-spring.git
cd todo-supabase-spring
```

### 2. Supabase Configuration

1. Create a new Supabase project from [Supabase Dashboard](https://app.supabase.com)
2. Get your Supabase credentials:
   - Database URL
   - API Key
   - Database Password

3. Configure Row Level Security (RLS):
   
   In the Supabase SQL Editor, create RLS policies for todos table:

   ```sql
   -- Enable RLS
   ALTER TABLE todos ENABLE ROW LEVEL SECURITY;

   -- Create policy for users to only see their own todos
   CREATE POLICY "Users can view their own todos" ON todos 
     FOR SELECT 
     USING (auth.uid()::text = user_id::text);

   -- Create policy for users to insert their own todos
   CREATE POLICY "Users can insert their own todos" ON todos 
     FOR INSERT 
     WITH CHECK (auth.uid()::text = user_id::text);

   -- Create policy for users to update their own todos
   CREATE POLICY "Users can update their own todos" ON todos 
     FOR UPDATE 
     USING (auth.uid()::text = user_id::text);

   -- Create policy for users to delete their own todos
   CREATE POLICY "Users can delete their own todos" ON todos 
     FOR DELETE 
     USING (auth.uid()::text = user_id::text);
   ```

### 3. Application Configuration

1. Update `src/main/resources/application.properties` with your Supabase credentials:

```properties
# Database Configuration (Supabase PostgreSQL)
spring.datasource.url=jdbc:postgresql://YOUR_SUPABASE_HOST:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=YOUR_SUPABASE_PASSWORD
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# JWT Configuration
app.jwt.secret=your_jwt_secret_key_here
app.jwt.expiration=86400000
```

## API Documentation

### Authentication Endpoints

| Method | URL | Description | Request Body | Response |
|--------|-----|-------------|--------------|----------|
| POST | `/api/auth/register` | Register a new user | UserRegistrationRequest | UserResponse |
| POST | `/api/auth/login` | Login and get JWT token | UserLoginRequest | AuthResponse |
| GET | `/api/auth/check-username?username=xyz` | Check if username is available | - | Map<String, Boolean> |
| GET | `/api/auth/check-email?email=example@email.com` | Check if email is available | - | Map<String, Boolean> |

### Todo Endpoints

| Method | URL | Description | Request Body | Response |
|--------|-----|-------------|--------------|----------|
| POST | `/api/todos` | Create a new todo | TodoRequest | TodoResponse |
| GET | `/api/todos` | Get all todos with optional filters | - | List<TodoResponse> |
| GET | `/api/todos/{id}` | Get todo by ID | - | TodoResponse |
| PUT | `/api/todos/{id}` | Update a todo | TodoRequest | TodoResponse |
| PATCH | `/api/todos/{id}/status` | Update todo status | TodoStatusUpdateRequest | TodoResponse |
| DELETE | `/api/todos/{id}` | Delete a todo | - | HttpStatus.NO_CONTENT |
| GET | `/api/todos/status/{status}` | Get todos by status | - | List<TodoResponse> |
| GET | `/api/todos/due-date?fromDate=...&toDate=...` | Get todos by due date range | - | List<TodoResponse> |

## Security Information

- Uses JWT (JSON Web Tokens) for authentication
- Passwords are encrypted using BCrypt
- Spring Security is configured for secure endpoints
- CORS is enabled for cross-origin requests
- CSRF protection is disabled for stateless APIs
- Row Level Security in Supabase ensures data isolation

## Environment Variables

You can configure the following environment variables to override default settings:

| Variable | Description | Default |
|----------|-------------|---------|
| SPRING_DATASOURCE_URL | Supabase database URL | - |
| SPRING_DATASOURCE_USERNAME | Database username | postgres |
| SPRING_DATASOURCE_PASSWORD | Database password | - |
| APP_JWT_SECRET | Secret key for JWT tokens | - |
| APP_JWT_EXPIRATION | JWT token expiration time in milliseconds | 86400000 (24 hours) |
| SERVER_PORT | Server port | 8080 |

## Running the Application

### Using Maven

```bash
mvn spring-boot:run
```

### Using Java

```bash
mvn clean package
java -jar target/todo-supabase-spring-0.0.1-SNAPSHOT.jar
```

### Using Docker (if you create a Dockerfile)

```bash
docker build -t todo-supabase-spring .
docker run -p 8080:8080 todo-supabase-spring
```

## Testing

### Running Tests

```bash
mvn test
```

### Manual Testing with Postman/Curl

1. Register a user:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"password123"}'
```

2. Login and get JWT token:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'
```

3. Create a todo (using the JWT token from the login response):
```bash
curl -X POST http://localhost:8080/api/todos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"title":"Test Todo","description":"This is a test todo","status":"PENDING"}'
```

## Project Structure

```
src/main/java/com/example/todo/
├── TodoSupabaseSpringApplication.java      # Application entry point
├── controller/                             # REST controllers
│   ├── AuthController.java                 # Authentication endpoints 
│   ├── TodoController.java                 # Todo endpoints
│   └── RestExceptionHandler.java           # Global exception handler
├── dto/                                    # Data Transfer Objects
│   ├── AuthResponse.java                   # Authentication response
│   ├── TodoRequest.java                    # Todo creation/update request
│   ├── TodoResponse.java                   # Todo response
│   ├── TodoSearchCriteria.java             # Todo search parameters
│   ├── TodoStatusUpdateRequest.java        # Todo status update
│   ├── UserLoginRequest.java               # User login request
│   ├── UserRegistrationRequest.java        # User registration request
│   └── UserResponse.java                   # User response
├── entity/                                 # JPA entities
│   ├── Todo.java                           # Todo entity
│   ├── TodoStatus.java                     # Todo status enum
│   └── User.java                           # User entity
├── repository/                             # Data repositories
│   ├── TodoRepository.java                 # Todo data access
│   └── UserRepository.java                 # User data access
├── security/                               # Security configuration
│   ├── JwtAuthenticationFilter.java        # JWT authentication filter
│   ├── JwtTokenProvider.java               # JWT utility
│   └── SecurityConfig.java                 # Security configuration
└── service/                                # Business logic
    ├── TodoService.java                    # Todo service interface
    ├── TodoServiceImpl.java                # Todo service implementation
    ├── UserDetailsServiceImpl.java         # UserDetails service
    ├── UserService.java                    # User service interface
    └── UserServiceImpl.java                # User service implementation
```

## Contributing

1. Fork the repository
2. Create a new branch (`git checkout -b feature/your-feature`)
3. Make your changes
4. Commit your changes (`git commit -m 'Add some feature'`)
5. Push to the branch (`git push origin feature/your-feature`)
6. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

