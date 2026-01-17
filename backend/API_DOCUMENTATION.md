# Product Review API Documentation

## Authentication Endpoints

### Register User
```bash
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe"
}
```

**Response:**
```json
HTTP 201 Created
"User registered successfully with email: user@example.com"
```

### Login
```bash
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response:**
```json
HTTP 200 OK
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "roles": ["ROLE_USER"],
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe"
}
```

### Get Current User
```bash
GET /api/auth/me
Authorization: Bearer <token>
```

## Product Endpoints

### Get All Products (Public)
```bash
GET /api/products?page=0&size=10&sortBy=reviewCount&sortDir=DESC
```

### Get Product by ID (Public)
```bash
GET /api/products/1
```

### Create Product (Admin Only)
```bash
POST /api/products
Authorization: Bearer <admin-token>
Content-Type: application/json

{
  "name": "Product Name",
  "description": "Product description",
  "category": "Electronics",
  "price": 99.99,
  "imageUrls": ["https://example.com/image.jpg"]
}
```

### Delete Product (Admin Only)
```bash
DELETE /api/products/1
Authorization: Bearer <admin-token>
```

## Review Endpoints

### Create Review (Authenticated Users)
```bash
POST /api/reviews
Authorization: Bearer <token>
Content-Type: application/json

{
  "productId": 1,
  "comment": "Great product! Highly recommended.",
  "rating": 5
}
```

### Update Review (Authenticated Users)
```bash
PUT /api/reviews/1
Authorization: Bearer <token>
Content-Type: application/json

{
  "comment": "Updated review text",
  "rating": 4
}
```

### Delete Review (Authenticated Users)
```bash
DELETE /api/reviews/1
Authorization: Bearer <token>
```

### Get Reviews by Product (Public)
```bash
GET /api/reviews/product/1?page=0&size=10&sortBy=createdAt&sortDir=DESC
```

### Toggle Helpful Vote (Authenticated Users)
```bash
POST /api/reviews/1/helpful
Authorization: Bearer <token>
```

## Error Response Format

All errors follow this consistent format:

```json
{
  "timestamp": "2024-01-17T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "details": [
    "rating: Rating must be between 1 and 5",
    "comment: Comment must be at least 10 characters long"
  ]
}
```

## HTTP Status Codes

- **200** - Success
- **201** - Created
- **400** - Bad Request (validation errors)
- **401** - Unauthorized (authentication required)
- **403** - Forbidden (insufficient permissions)
- **404** - Not Found
- **409** - Conflict (duplicate review)
- **500** - Internal Server Error

## Default Users

The application seeds two default users:

**Admin User:**
- Email: `admin@productreview.com`
- Password: `admin123`
- Roles: `ROLE_ADMIN`

**Regular User:**
- Email: `user@productreview.com`
- Password: `user123`
- Roles: `ROLE_USER`

## Security Headers

For protected endpoints, include the JWT token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

## Validation Rules

### Review Creation
- Rating: Must be between 1 and 5
- Comment: Minimum 10 characters, maximum 2000 characters
- Product: Must exist
- Duplicate: One review per user per product

### User Registration
- Email: Valid email format, must be unique
- Password: Minimum 6 characters, maximum 40 characters
- First/Last Name: Maximum 50 characters each
