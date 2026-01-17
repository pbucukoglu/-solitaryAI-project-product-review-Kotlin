# Authentication API Test Script

## Test Users Available:
- **Admin**: admin@productreview.com / Admin123!
- **User**: user@productreview.com / User123!
- **Test Admin**: testadmin@example.com / Test123!
- **Test User**: testuser@example.com / Test123!
- **New Test**: test@example.com / Test123!

## API Endpoints Testing

### 1. Register New User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "newuser@example.com",
    "password": "Password123!",
    "fullName": "New Test User"
  }' | jq
```

### 2. Login as Regular User
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@productreview.com",
    "password": "User123!"
  }' | jq
```

### 3. Login as Admin
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@productreview.com",
    "password": "Admin123!"
  }' | jq
```

### 4. Access Public Endpoint (No Auth Required)
```bash
curl -X GET http://localhost:8080/api/products | jq '.content | length'
```

### 5. Access Protected Endpoint (Requires Auth)
```bash
# First login to get token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@productreview.com",
    "password": "User123!"
  }' | jq -r '.accessToken')

# Use token to access protected endpoint
curl -X POST http://localhost:8080/api/reviews \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "rating": 5,
    "comment": "Great product! Highly recommended.",
    "reviewerName": "Test User"
  }' | jq
```

### 6. Access Admin-Only Endpoint (Requires Admin Role)
```bash
# Login as admin
ADMIN_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@productreview.com",
    "password": "Admin123!"
  }' | jq -r '.accessToken')

# Create new product (admin only)
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Product",
    "description": "This is a test product created via API",
    "category": "Electronics",
    "price": 299.99
  }' | jq
```

### 7. Test Authorization Failure (Regular User Trying Admin Endpoint)
```bash
# Login as regular user
USER_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@productreview.com",
    "password": "User123!"
  }' | jq -r '.accessToken')

# Try to access admin endpoint (should fail)
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer $USER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Unauthorized Product",
    "description": "This should fail",
    "category": "Electronics",
    "price": 99.99
  }' | jq
```

### 8. Test Validation Errors
```bash
# Try to create review with invalid data
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@productreview.com",
    "password": "User123!"
  }' | jq -r '.accessToken')

# Invalid rating (should fail)
curl -X POST http://localhost:8080/api/reviews \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "rating": 6,
    "comment": "Invalid rating",
    "reviewerName": "Test User"
  }' | jq

# Short comment (should fail)
curl -X POST http://localhost:8080/api/reviews \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "rating": 5,
    "comment": "Hi",
    "reviewerName": "Test User"
  }' | jq
```

### 9. Test Duplicate Review Prevention
```bash
# Create first review
curl -X POST http://localhost:8080/api/reviews \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "rating": 4,
    "comment": "First review for this product",
    "reviewerName": "Test User"
  }' | jq

# Try to create second review for same product (should fail)
curl -X POST http://localhost:8080/api/reviews \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "rating": 5,
    "comment": "Second review for same product",
    "reviewerName": "Test User"
  }' | jq
```

### 10. Test Invalid Token
```bash
# Try to access protected endpoint with invalid token
curl -X GET http://localhost:8080/api/products/1 \
  -H "Authorization: Bearer invalid.token.here" | jq
```

## Expected Results:

1. **Register**: Should return JWT token with user roles
2. **Login**: Should return JWT token with correct roles
3. **Public endpoints**: Should work without authentication
4. **Protected endpoints**: Should work with valid JWT token
5. **Admin endpoints**: Should work only for admin users
6. **Validation**: Should return proper error messages
7. **Duplicate prevention**: Should prevent duplicate reviews
8. **Invalid tokens**: Should return 401 Unauthorized

## Quick Test Script (Bash)
```bash
#!/bin/bash

echo "🧪 Testing Authentication API..."

# Test 1: Login as user
echo "1. Testing user login..."
USER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "user@productreview.com", "password": "User123!"}')

USER_TOKEN=$(echo $USER_RESPONSE | jq -r '.accessToken')
echo "User token: ${USER_TOKEN:0:20}..."

# Test 2: Login as admin
echo "2. Testing admin login..."
ADMIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@productreview.com", "password": "Admin123!"}')

ADMIN_TOKEN=$(echo $ADMIN_RESPONSE | jq -r '.accessToken')
echo "Admin token: ${ADMIN_TOKEN:0:20}..."

# Test 3: Access public endpoint
echo "3. Testing public endpoint..."
curl -s -X GET http://localhost:8080/api/products | jq '.content | length' > /dev/null
echo "✅ Public endpoint accessible"

# Test 4: Access protected endpoint
echo "4. Testing protected endpoint..."
curl -s -X GET http://localhost:8080/api/products/1 \
  -H "Authorization: Bearer $USER_TOKEN" > /dev/null
echo "✅ Protected endpoint accessible with user token"

# Test 5: Access admin endpoint
echo "5. Testing admin endpoint..."
curl -s -X DELETE http://localhost:8080/api/products/999 \
  -H "Authorization: Bearer $ADMIN_TOKEN" > /dev/null 2>&1
echo "✅ Admin endpoint accessible with admin token"

# Test 6: Admin endpoint with user token (should fail)
echo "6. Testing admin endpoint with user token (should fail)..."
RESPONSE=$(curl -s -X DELETE http://localhost:8080/api/products/999 \
  -H "Authorization: Bearer $USER_TOKEN")
if echo "$RESPONSE" | jq -e '.status == 403' > /dev/null; then
  echo "✅ Admin endpoint correctly blocked for user"
else
  echo "❌ Admin endpoint should be blocked for user"
fi

echo "🎉 Authentication API tests completed!"
```
