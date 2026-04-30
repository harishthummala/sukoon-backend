# Sukoon Backend

AI-powered mental wellness app backend.

## Tech Stack
- Java + Spring Boot
- PostgreSQL
- JPA/Hibernate
- REST APIs

## Features
- User registration and management
- Email/password and Google sign-in/sign-up
- CRUD operations
- PostgreSQL database integration

## Setup
1. Clone the repository
2. Configure PostgreSQL in application.properties
3. Configure `google.client.id` with your Google OAuth Web Client ID
4. Configure mail settings for password reset emails:

```bash
export MAIL_HOST=smtp.gmail.com
export MAIL_PORT=587
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-app-password
export APP_MAIL_FROM=your-email@gmail.com
export FRONTEND_RESET_PASSWORD_URL=http://localhost:3000/reset-password
```

5. Run: ./mvnw spring-boot:run

## Google Auth
Send the Google ID token from your frontend to:

```http
POST /api/auth/google
Content-Type: application/json

{
  "credential": "<google-id-token>"
}
```

The endpoint verifies the Google token, creates the user if needed, and returns the same response shape as `/api/auth/login`:

```json
{
  "token": "<app-jwt>",
  "user": {
    "id": 1,
    "email": "user@example.com",
    "name": "User Name"
  }
}
```

## Forgot Password
Request a reset link:

```http
POST /api/auth/forgot-password
Content-Type: application/json

{
  "email": "user@example.com"
}
```

Reset the password with the emailed token:

```http
POST /api/auth/reset-password
Content-Type: application/json

{
  "token": "<token-from-reset-link>",
  "password": "new-password"
}
```
