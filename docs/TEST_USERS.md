# 🧪 Test Users Guide

Bu doküman, Product Review Platform'daki mevcut test kullanıcılarının bilgilerini ve kullanım talimatlarını içerir.

## 👥 Mevcut Test Kullanıcıları

### 🔴 Admin Kullanıcıları

#### 1. Super Admin
- **Email:** `admin@productreview.com`
- **Password:** `password`
- **Rol:** Admin
- **Açıklama:** Sistem süper admin kullanıcısı, tüm yetkilere sahip

#### 2. Test Admin
- **Email:** `testadmin@example.com`
- **Password:** `password`
- **Rol:** Admin
- **Açıklama:** Test işlemleri için admin kullanıcısı

### 🔵 Regular Kullanıcıları

#### 3. Regular User
- **Email:** `user@productreview.com`
- **Password:** `password`
- **Rol:** User
- **Açıklama:** Standart kullanıcı, review ekleme/düzenleme yetkileri

#### 4. Test User
- **Email:** `test@example.com`
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
