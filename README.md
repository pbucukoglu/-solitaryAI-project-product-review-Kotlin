# 📱 Product Review Application

**Backend:** Spring Boot  
**Mobile App:** Android (Jetpack Compose)  
**Authentication:** JWT (JSON Web Tokens)
**Database:** PostgreSQL (Railway)

---

## 📌 Project Overview

The **Product Review Application** is a full-stack system that allows users to browse products, submit reviews, rate products, and view aggregated feedback through a modern mobile interface. The project features **JWT-based authentication**, **role-based authorization**, **admin management capabilities**, and **AOP validation**.

---

## 🎯 Features

### 🔐 Authentication & Authorization
- **JWT-based login system** with secure token management
- **Role-based access control**: Admin vs User permissions
- **Secure API endpoints** with method-level security
- **Token expiration and refresh** mechanisms

### 📱 Mobile Application (Android)
- **Modern UI** built with Jetpack Compose
- **MVVM architecture** with Hilt dependency injection
- **Offline-first design** with local data caching
- **Responsive design** for various screen sizes

### 🖥️ Backend API (Spring Boot)
- **RESTful API** with comprehensive CRUD operations
- **PostgreSQL database** with optimized queries
- **AOP validation** for business rules
- **Global exception handling** with consistent error responses

### 👥 Admin Management
- **Product management**: Create, update, delete products
- **User analytics**: Review statistics and insights
- **Content moderation**: Review approval workflows

---

## 🚀 Quick Start

### Prerequisites
- **Java 17+**
- **Android Studio** with Kotlin support
- **PostgreSQL** (Railway deployment)
- **Git** for version control

### Backend Setup
```bash
# Clone the repository
git clone https://github.com/pbucukoglu/-solitaryAI-project-product-review-Kotlin.git
cd -solitaryAI-project-product-review-Kotlin

# Run backend
cd backend
mvn spring-boot:run
```

### Android Setup
```bash
# Clone the repository
git clone https://github.com/pbucukoglu/-solitaryAI-project-product-review-Kotlin.git
cd -solitaryAI-project-product-review-Kotlin/android-app

# Build and install APK
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 📚 Documentation

- [**Setup Guide**](SETUP_GUIDE.md) - Complete installation instructions
- [**API Documentation**](docs/API_DOCUMENTATION.md) - REST API reference
- [**Android Development**](docs/ANDROID_DEVELOPMENT.md) - Mobile app guide
- [**Admin Guide**](docs/ADMIN_GUIDE.md) - Admin features documentation
- [**Testing Guide**](docs/TEST_USERS.md) - Test users and scenarios

---

## 🛡️ Security

### Authentication Flow
1. **User Registration**: `/api/auth/register` - Create new user account
2. **User Login**: `/api/auth/login` - Authenticate and receive JWT token
3. **Token Validation**: Automatic JWT validation on protected endpoints
4. **Authorization**: Role-based access control for API endpoints

### Security Features
- **Password Hashing** with BCrypt
- **JWT Token Security** with configurable expiration
- **CORS Configuration** for cross-origin requests
- **SQL Injection Prevention** with JPA/Hibernate
- **Input Validation** with comprehensive error handling

---

## 📊 Database Schema

### Core Tables
- **users**: User accounts with roles and authentication data
- **products**: Product information with metadata
- **reviews**: User reviews with ratings and helpful votes
- **categories**: Product categorization system

### Relationships
- **Users → Reviews**: One-to-many relationship
- **Products → Reviews**: One-to-many relationship
- **Categories → Products**: One-to-many relationship

---

## 🔧 Technologies Used

### Backend Stack
- **Spring Boot 3.2.0** - Application framework
- **Spring Security 6** - Authentication and authorization
- **Spring Data JPA** - Database access layer
- **PostgreSQL** - Primary database
- **JWT (jjwt)** - Token-based authentication
- **AspectJ** - AOP validation
- **Maven** - Build and dependency management

### Android Stack
- **Jetpack Compose** - Modern UI toolkit
- **Hilt** - Dependency injection
- **Retrofit** - HTTP client
- **Room** - Local database (if needed)
- **DataStore** - Secure data storage
- **Navigation Compose** - Navigation component

### DevOps
- **Railway** - Cloud deployment platform
- **GitHub Actions** - CI/CD pipeline
- **Docker** - Containerization
- **PostgreSQL** - Database hosting

---

## 📱 Project Structure

```
├── backend/                 # Spring Boot backend
│   ├── src/main/java/       # Java source code
│   ├── src/main/resources/    # Configuration files
│   └── pom.xml              # Maven dependencies
├── android-app/             # Android application
│   ├── app/src/main/         # Kotlin source code
│   ├── app/build.gradle.kts   # Android build configuration
│   └── app/src/main/res/     # Android resources
└── docs/                   # Documentation
    ├── API_DOCUMENTATION.md
    ├── TEST_USERS.md
    └── TROUBLESHOOTING.md
```

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏‍♂️ Maintainers

- **Backend Development**: Spring Boot, JWT, PostgreSQL
- **Android Development**: Jetpack Compose, Hilt, Retrofit
- **DevOps**: Railway deployment, CI/CD pipelines

---

## 📞 Support

For questions, issues, or contributions, please open an issue on the [GitHub repository](https://github.com/pbucukoglu/-solitaryAI-project-product-review-Kotlin/issues).

---

*Last updated: January 2026*
  - View products
  - Submit reviews and ratings
  - Browse existing reviews
- Demonstrate clean architecture and separation of concerns

---

## 🧩 Core Features

### 🛒 Product Management
- Retrieve a list of products
- View product details:
  - Name
  - Description
  - Category
  - Price
  - Average rating
- Backend supports pagination and sorting

### ⭐ Review & Rating System
- Users can:
  - Submit a text-based review
  - Rate products on a numeric scale (e.g., 1–5)
- Display:
  - Average rating per product
  - Total review count
  - Review history

### 📊 Aggregation & Insights
- Backend calculates:
  - Average ratings
  - Review counts
- Optimized for read-heavy access patterns

### 📱 Mobile Experience (React Native)
- Cross-platform support (iOS & Android)
- Key screens:
  - Product List
  - Product Details
  - Add Review
- Reusable UI components
- API-driven data rendering
- Loading and error states handled gracefully

---

## 🏗️ Architecture

### Backend (Spring Boot)
- RESTful API architecture
- Layered structure:
  - Controller
  - Service
  - Repository
- JPA / Hibernate for ORM
- PostgreSQL (or H2 for local development)
- DTO-based request/response models
- Input validation (ratings range, review length, etc.)

### Mobile App (React Native)
- Functional components with hooks
- API integration using `fetch` or `axios`
- Local state management
- Environment-based API configuration

---

## 🚫 Out of Scope
- Authentication & authorization
- User accounts or roles
- Payments or checkout
- Admin dashboards

---

## 🧪 Testing & Quality
- Unit tests for service and repository layers
- Integration tests for REST endpoints
- Validation and error handling
- Consistent API response formats

---

## 💡 Why This Project

This project demonstrates:
- Strong **Spring Boot backend fundamentals**
- Clean **REST API design**
- Practical **React Native mobile development**
- Scalable architecture patterns used in real-world applications
