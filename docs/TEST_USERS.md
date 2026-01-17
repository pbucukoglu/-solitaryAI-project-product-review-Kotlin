# 🧪 Test Users Guide

This document contains all test users for the Product Review Platform with their credentials and usage instructions.

---

## 👥 Available Test Users

### 🔴 Admin Users

#### 1. Super Admin
- **Email:** `admin@productreview.com`
- **Password:** `password`
- **Role:** Admin
- **Description:** System super admin user with all privileges

#### 2. Test Admin
- **Email:** `testadmin@example.com`
- **Password:** `password`
- **Role:** Admin
- **Description:** Test admin user for admin operations

### 🔵 Regular Users

#### 3. Regular User
- **Email:** `user@productreview.com`
- **Password:** `password`
- **Role:** User
- **Description:** Standard user with review add/edit permissions

#### 4. Test User
- **Email:** `test@example.com`
- **Password:** `password`
- **Role:** User
- **Description:** Test user for standard operations

#### 5. Test User 2
- **Email:** `testuser@example.com`
- **Password:** `password`
- **Role:** User
- **Description:** Additional test user

### 🟡 Migration User
- **Email:** `migrated@productreview.com`
- **Password:** `password`
- **Role:** User
- **Description:** User created during data migration

---

## 🔐 Login Test Steps

### Android Application
1. **Launch the application**
2. **Enter credentials** on login screen
3. **Successful login** → Product list displayed
4. **Failed login** → Error message shown

### API Test (Postman/cURL)
```bash
# Login Request
POST https://solitaryai-project-product-review-production.up.railway.app/api/auth/login
Content-Type: application/json

{
  "email": "admin@productreview.com",
  "password": "password"
}
```

**Successful Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "roles": ["ROLE_ADMIN"]
}
```

---

## 🎯 Role-Based Test Scenarios

### Admin Test Scenarios
1. **Login:** `admin@productreview.com` / `password`
2. **Product Management:** Settings → Admin Management
3. **Add Product:** Create new product
4. **Delete Product:** Delete existing product
5. **View Analytics:** Review statistics

### User Test Scenarios
1. **Login:** `test@example.com` / `password`
2. **Browse Products:** View product list
3. **Add Review:** Submit product review
4. **Edit Review:** Modify own review
5. **Helpful Vote:** Vote on reviews

---

## 🔍 Permission Matrix

| Feature | User | Admin |
|---------|------|-------|
| View products | ✅ | ✅ |
| Add reviews | ✅ | ✅ |
| Edit reviews | ✅ | ✅ |
| Delete reviews | ✅ | ✅ |
| Helpful votes | ✅ | ✅ |
| Add products | ❌ | ✅ |
| Delete products | ❌ | ✅ |
| Admin panel | ❌ | ✅ |
| Analytics | ❌ | ✅ |

---

## 🚨 Important Notes

### Password Information
- **All test users password:** **`password`**
- Passwords are BCrypt hashed in database
- Passwords should be changed in production for security

### Role Information
- **ROLE_USER:** Standard user permissions
- **ROLE_ADMIN:** All permissions + admin features
- Roles are encoded in JWT tokens

### Database Status
- Users are stored in Railway PostgreSQL database
- Located in `users` table
- Migration users were created during data transfer

---

## 🧪 Test Checklist

### ✅ Basic Tests
- [ ] Admin login successful
- [ ] User login successful
- [ ] Invalid password login fails
- [ ] Non-existent user login fails

### ✅ User Permission Tests
- [ ] View product list
- [ ] Add review
- [ ] Edit review
- [ ] Delete review
- [ ] Helpful votes

### ✅ Admin Permission Tests
- [ ] Add product
- [ ] Delete product
- [ ] Access admin panel
- [ ] View analytics

### ✅ UI Tests
- [ ] Admin Management section visible only to admin
- [ ] Admin features hidden from users
- [ ] Logout functionality works
- [ ] Token expired redirects to login

---

## 📞 Test Tools

### Postman Collection
```json
{
  "info": {
    "name": "Product Review API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Auth - Login",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"email\": \"admin@productreview.com\",\n  \"password\": \"password\"\n}"
        },
        "url": {
          "raw": "{{baseUrl}}/api/auth/login"
        }
      }
    }
  ]
}
```

### cURL Commands
```bash
# Admin Login
curl -X POST https://solitaryai-project-product-review-production.up.railway.app/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@productreview.com","password":"password"}'

# User Login
curl -X POST https://solitaryai-project-product-review-production.up.railway.app/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password"}'

# Get Products (Public)
curl -X GET https://solitaryai-project-product-review-production.up.railway.app/api/products

# Add Product (Admin Only)
curl -X POST https://solitaryai-project-product-review-production.up.railway.app/api/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"name":"Test Product","description":"Test Description","category":"Electronics","price":99.99,"imageUrls":[]}'
```

---

## 📅 Last Update

- **Date:** 2026-01-17
- **Version:** v1.0.0
- **Status:** Production ready
- **User count:** 6 test users
- **Roles:** 2 Admin, 4 User
- **Password:** `password`
- **Rol:** User
- **Açıklama:** Test işlemleri için standart kullanıcı

#### 5. Test User 2
- **Email:** `testuser@example.com`
- **Password:** `password`
- **Rol:** User
- **Açıklama:** Ek test kullanıcısı

### 🟡 Migration User
- **Email:** `migrated@productreview.com`
- **Password:** `password`
- **Rol:** User
- **Açıklama:** Veri migrasyonu sırasında oluşturulan kullanıcı

## 🔐 Login Test Adımları

### Android Uygulaması
1. **Uygulamayı aç**
2. **Login ekranında** email ve şifre gir
3. **Başarılı giriş** → Product listesi görüntülenir
4. **Başarısız giriş** → Hata mesajı gösterilir

### API Test (Postman/cURL)
```bash
# Login Request
POST https://solitaryai-project-product-review-production.up.railway.app/api/auth/login
Content-Type: application/json

{
  "email": "admin@productreview.com",
  "password": "password"
}
```

**Başarılı Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "roles": ["ROLE_ADMIN"]
}
```

## 🎯 Rol Bazlı Test Senaryoları

### Admin Test Senaryoları
1. **Login:** `admin@productreview.com` / `password`
2. **Product Management:** Settings → Admin Management
3. **Add Product:** Yeni ürün oluşturma
4. **Delete Product:** Mevcut ürün silme
5. **View Analytics:** İstatistikleri görüntüleme

### User Test Senaryoları
1. **Login:** `test@example.com` / `password`
2. **Browse Products:** Ürün listesini gezme
3. **Add Review:** Ürün değerlendirmesi yapma
4. **Edit Review:** Kendi yorumunu düzenleme
5. **Helpful Vote:** Yorumlara helpful oyu verme

## 🔍 Yetki Matrisi

| Özellik | User | Admin |
|---------|------|-------|
| Ürünleri görüntüleme | ✅ | ✅ |
| Review ekleme | ✅ | ✅ |
| Review düzenleme | ✅ | ✅ |
| Review silme | ✅ | ✅ |
| Helpful oyları | ✅ | ✅ |
| Ürün ekleme | ❌ | ✅ |
| Ürün silme | ❌ | ✅ |
| Admin paneli | ❌ | ✅ |
| Analytics | ❌ | ✅ |

## 🚨 Önemli Notlar

### Şifre Bilgisi
- Tüm test kullanıcılarının şifresi: **`password`**
- Şifreler BCrypt ile hash'lenmiş durumda
- Güvenlik nedeniyle şifreler production'da değiştirilmelidir

### Rol Bilgisi
- **ROLE_USER:** Standart kullanıcı yetkileri
- **ROLE_ADMIN:** Tüm yetkiler + admin özellikleri
- Roller JWT token'da encode edilir

### Database Durumu
- Kullanıcılar Railway PostgreSQL'de saklı
- `users` tablosunda mevcut durumda
- Migration user'lar veri taşıma işlemi için kullanılmış

## 🧪 Test Checklist

### ✅ Temel Testler
- [ ] Admin login başarılı
- [ ] User login başarılı
- [ ] Yanlış şifre ile login başarısız
- [ ] Mevcut olmayan kullanıcı ile login başarısız

### ✅ User Yetki Testleri
- [ ] Product listesi görüntüleme
- [ ] Review ekleme
- [ ] Review düzenleme
- [ ] Review silme
- [ ] Helpful oyları

### ✅ Admin Yetki Testleri
- [ ] Product ekleme
- [ ] Product silme
- [ ] Admin paneli erişimi
- [ ] Analytics görüntüleme

### ✅ UI Testleri
- [ ] Admin Management section sadece admin'de görünür
- [ ] User'de admin özellikleri görünmez
- [ ] Logout çalışır
- [ ] Token expired durumunda login'a yönlendirme

## 📞 Test Araçları

### Postman Collection
```json
{
  "info": {
    "name": "Product Review API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Auth - Login",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"email\": \"admin@productreview.com\",\n  \"password\": \"password\"\n}"
        },
        "url": {
          "raw": "{{baseUrl}}/api/auth/login"
        }
      }
    }
  ]
}
```

### cURL Komutları
```bash
# Admin Login
curl -X POST https://solitaryai-project-product-review-production.up.railway.app/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@productreview.com","password":"password"}'

# User Login
curl -X POST https://solitaryai-project-product-review-production.up.railway.app/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password"}'

# Get Products (Public)
curl -X GET https://solitaryai-project-product-review-production.up.railway.app/api/products

# Add Product (Admin Only)
curl -X POST https://solitaryai-project-product-review-production.up.railway.app/api/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"name":"Test Product","description":"Test Description","category":"Electronics","price":99.99,"imageUrls":[]}'
```

---

## 📅 Son Güncelleme

- **Tarih:** 2026-01-17
- **Version:** v1.0.0
- **Durum:** Production hazır
- **Kullanıcı sayısı:** 6 test kullanıcı
- **Roller:** 2 Admin, 4 User
