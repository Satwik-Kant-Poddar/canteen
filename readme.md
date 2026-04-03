# 🍽️ Canteen Meal Management System

A backend-first Spring Boot application to manage daily meal selections for employees in an office canteen. The system allows employees to select meals for specific dates, supports bulk selections, enforces business rules (like lock periods), and is designed to be easily extended with authentication and a frontend UI.

---

## 🚀 Tech Stack

* **Java 17**
* **Spring Boot**
* **Spring Web (REST APIs)**
* **Spring Data JPA**
* **H2 Database (in-memory / local dev)**
* **Hibernate**
* **Maven**

---

## 📂 Project Structure

```
com.office.canteen
│
├── CanteenApplication.java
│
├── config
│   ├── SecurityConfig.java
│   └── WebConfig.java              (future: CORS, formatters, etc.)
│
├── controller
│   ├── HealthController.java
│   └── MealSelectionController.java
│
├── domain
│   ├── entity
│   │   └── MealSelection.java
│   │
│   └── enum
│       └── MealType.java
│
├── dto
│   ├── request
│   │   ├── MealSelectionRequestDTO.java
│   │   └── MealRangeRequestDTO.java
│   │
│   └── response
│       └── MealSelectionDTO.java
│
├── exception
│   ├── GlobalExceptionHandler.java
│   └── MealSelectionLockedException.java
│
├── mapper
│   └── MealSelectionMapper.java
│
├── repository
│   └── MealSelectionRepository.java
│
├── service
│   ├── MealSelectionService.java
│   └── impl                       (optional, enterprise-ready)
│       └── MealSelectionServiceImpl.java
│
└── util                            (future-safe)
    └── DateUtils.java
   
```

---

## 🧠 Core Concepts

### Meal Types

| Value     | Meaning                                           |
| --------- | ------------------------------------------------- |
| `VEG`     | Vegetarian meal                                   |
| `NON_VEG` | Non-vegetarian meal                               |
| `NO_MEAL` | Employee explicitly chose to skip meal            |
| `NONE`    | No record / no choice made (backend-only meaning) |

> `NONE` is **not selected by the user** — it represents missing data.

---

## 🔐 Business Rules

* ❌ Meal date cannot be in the past
* 🔒 Meal selection is locked **7 days before the meal date**
* ✅ One meal per employee per date (unique constraint)
* 🔁 Bulk selection is transactional (all succeed or all fail)

---

## 🌐 API Documentation

### Base URL

```
http://localhost:8080/api/meals
```

---

### 1️⃣ Select / Update Meal (Single Day)

**Endpoint**

```
POST /api/meals
```

**Request Body**

```json
{
  "employeeId": 1,
  "mealDate": "2026-02-01",
  "mealType": "VEG"
}
```

**Response**

```json
{
  "employeeId": 1,
  "mealDate": "2026-02-01",
  "mealType": "VEG"
}
```

---

### 2️⃣ Get Meal Selection for a Date

**Endpoint**

```
GET /api/meals?employeeId=1&mealDate=2026-02-01
```

**If record exists**

```json
{
  "employeeId": 1,
  "mealDate": "2026-02-01",
  "mealType": "NON_VEG"
}
```

**If no record exists**

```json
{
  "employeeId": 1,
  "mealDate": "2026-02-01",
  "mealType": "NONE"
}
```

---

### 3️⃣ Bulk Meal Selection

**Endpoint**

```
POST /api/meals/bulk
```

**Request Body**

```json
[
  {
    "employeeId": 1,
    "mealDate": "2026-02-01",
    "mealType": "VEG"
  },
  {
    "employeeId": 1,
    "mealDate": "2026-02-02",
    "mealType": "NO_MEAL"
  }
]
```

**Behavior**

* Wrapped in `@Transactional`
* If **any entry fails**, nothing is saved

---

## ⚠️ Error Handling

Handled globally via `@RestControllerAdvice`

### Example: Meal Locked

```json
{
  "message": "Meal selection is locked 7 days before the date",
  "timestamp": "2026-01-20T10:15:30"
}
```

### Validation Error

```json
{
  "mealDate": "Meal date must not be null"
}
```

---

## 🗄️ Database

### H2 Console

```
http://localhost:8080/h2-console
```

**Default Settings**

* JDBC URL: `jdbc:h2:mem:canteen-db`
* Username: `sa`
* Password: *(empty)*

### Table

```
meal_selection
```

Unique constraint:

```
(employee_id, meal_date)
```

---

## 🔁 Transactions

* `@Transactional` is applied at **Service layer**
* Bulk operations are atomic
* Read-only methods may use `@Transactional(readOnly = true)`

---

## 🔐 Security

Currently:

* CSRF disabled
* All endpoints are open

Planned:

* Login via employee email
* Spring Security + JWT

---

## 🧭 Future Roadmap

* ✅ Backend APIs
* 🔜 Authentication & Authorization
* 🔜 React / Web UI
* 🔜 Admin dashboard
* 🔜 Proper DB (PostgreSQL / MySQL)
* 🔜 Audit logs & reports

---

## 🧑‍💻 Developer Notes

* DTOs are used for both request & response (intentionally)
* `NONE` is backend-derived, not user input
* Service layer owns business rules
* Clean separation of concerns

---

## ▶️ How to Run

```bash
mvn spring-boot:run
```

---

Happy coding 🚀
This project is designed not just to work, but to **teach real-world backend engineering practices**.
