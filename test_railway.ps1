# PowerShell script for testing Railway deployment

Write-Host "🧪 Testing Railway Deployment..." -ForegroundColor Green

$RAILWAY_URL = "https://solitaryai-project-product-review-production.up.railway.app"
Write-Host "📍 Testing Railway URL: $RAILWAY_URL" -ForegroundColor Yellow

# Test 1: Health check
Write-Host "1. Testing health endpoint..." -ForegroundColor Cyan
try {
    $response = Invoke-WebRequest -Uri "$RAILWAY_URL/actuator/health" -UseBasicParsing -TimeoutSec 10
    if ($response.StatusCode -eq 200) {
        Write-Host "✅ Health endpoint working" -ForegroundColor Green
    } else {
        Write-Host "❌ Health endpoint failed - Status: $($response.StatusCode)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ Health endpoint failed - $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: Public products endpoint
Write-Host "2. Testing public products endpoint..." -ForegroundColor Cyan
try {
    $productsResponse = Invoke-RestMethod -Uri "$RAILWAY_URL/api/products" -UseBasicParsing -TimeoutSec 10
    if ($productsResponse.content) {
        $productCount = $productsResponse.content.Count
        Write-Host "✅ Products endpoint working - $productCount products found" -ForegroundColor Green
    } else {
        Write-Host "❌ Products endpoint failed - No content found" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ Products endpoint failed - $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

# Test 3: Auth endpoint
Write-Host "3. Testing auth endpoint..." -ForegroundColor Cyan
try {
    $authBody = @{
        email = "user@productreview.com"
        password = "User123!"
    } | ConvertTo-Json
    
    $authResponse = Invoke-RestMethod -Uri "$RAILWAY_URL/api/auth/login" -Method POST -Body $authBody -ContentType "application/json" -UseBasicParsing -TimeoutSec 10
    
    if ($authResponse.accessToken) {
        Write-Host "✅ Auth endpoint working" -ForegroundColor Green
        $token = $authResponse.accessToken
        Write-Host "   Token received: $($token.Substring(0, [Math]::Min(20, $token.Length)))..." -ForegroundColor Gray
    } else {
        Write-Host "❌ Auth endpoint failed - No token in response" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ Auth endpoint failed - $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
        try {
            $errorBody = $_.Exception.Response.GetResponseStream()
            $reader = New-Object System.IO.StreamReader($errorBody)
            $errorText = $reader.ReadToEnd()
            Write-Host "Error response: $errorText" -ForegroundColor Red
        } catch {
            Write-Host "Could not read error response" -ForegroundColor Red
        }
    }
}

# Test 4: Simple connectivity test
Write-Host "4. Testing basic connectivity..." -ForegroundColor Cyan
try {
    $response = Invoke-WebRequest -Uri "$RAILWAY_URL/api/products" -UseBasicParsing -TimeoutSec 5
    Write-Host "✅ Basic connectivity working - Status: $($response.StatusCode)" -ForegroundColor Green
} catch {
    Write-Host "❌ Basic connectivity failed - $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "🎉 Railway testing completed!" -ForegroundColor Green

# Wait for user input before closing
Read-Host "Press Enter to exit"
