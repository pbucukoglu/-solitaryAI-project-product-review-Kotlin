#!/bin/bash

echo "🧪 Testing Railway Deployment..."

RAILWAY_URL="https://solitaryai-project-product-review-production.up.railway.app"

echo "📍 Testing Railway URL: $RAILWAY_URL"

# Test 1: Health check
echo "1. Testing health endpoint..."
curl -f -s "$RAILWAY_URL/actuator/health" > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo "✅ Health endpoint working"
else
    echo "❌ Health endpoint failed"
fi

# Test 2: Public products endpoint
echo "2. Testing public products endpoint..."
PRODUCTS_RESPONSE=$(curl -s "$RAILWAY_URL/api/products")
if echo "$PRODUCTS_RESPONSE" | jq -e '.content' > /dev/null 2>&1; then
    PRODUCT_COUNT=$(echo "$PRODUCTS_RESPONSE" | jq '.content | length // 0')
    echo "✅ Products endpoint working - $PRODUCT_COUNT products found"
else
    echo "❌ Products endpoint failed"
    echo "Response: $PRODUCTS_RESPONSE"
fi

# Test 3: Auth endpoint
echo "3. Testing auth endpoint..."
AUTH_RESPONSE=$(curl -s -X POST "$RAILWAY_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email": "user@productreview.com", "password": "User123!"}')

if echo "$AUTH_RESPONSE" | jq -e '.accessToken' > /dev/null 2>&1; then
    echo "✅ Auth endpoint working"
    TOKEN=$(echo "$AUTH_RESPONSE" | jq -r '.accessToken')
    echo "   Token received: ${TOKEN:0:20}..."
else
    echo "❌ Auth endpoint failed"
    echo "Response: $AUTH_RESPONSE"
fi

# Test 4: CORS headers
echo "4. Testing CORS..."
CORS_RESPONSE=$(curl -s -H "Origin: https://example.com" -H "Access-Control-Request-Method: GET" -X OPTIONS "$RAILWAY_URL/api/products")
if echo "$CORS_RESPONSE" | grep -i "access-control-allow-origin" > /dev/null; then
    echo "✅ CORS headers present"
else
    echo "❌ CORS headers missing"
fi

echo "🎉 Railway testing completed!"
