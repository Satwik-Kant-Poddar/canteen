# 🔐 Login & Authentication — Developer Guide

This document explains the login and authentication system used in the Canteen Meal Planner.

---

## Overview

The application uses **simple session-based authentication**. Users log in with a username and password, and their identity is stored in the HTTP session. There is no JWT, OAuth, or Spring Security form login — this is intentionally kept minimal.

---

## User Table

Users are stored in the `app_user` table (H2 in-memory database). The table is auto-populated on startup via `DataInitializer.java`.

### Default Users

| Username       | Employee ID | Role       | Password   |
|---------------|-------------|-----------|------------|
| `admin`       | 100         | ADMIN     | `admin123` |
| `john.doe`    | 1           | EMPLOYEE  | `pass123`  |
| `jane.smith`  | 2           | EMPLOYEE  | `pass123`  |
| `mike.wilson` | 3           | EMPLOYEE  | `pass123`  |
| `sara.jones`  | 4           | EMPLOYEE  | `pass123`  |
| `raj.kumar`   | 5           | EMPLOYEE  | `pass123`  |

> **Note:** Passwords are stored in plaintext for simplicity. In production, use bcrypt or another hashing algorithm.

---

## Roles

| Role       | Access                                      |
|-----------|---------------------------------------------|
| `ADMIN`    | Can access the Admin Dashboard (`/admin`)   |
| `EMPLOYEE` | Can access the Employee Dashboard (`/`)     |

- An **ADMIN** user who tries to access `/` will be allowed (they can view the employee dashboard too).
- An **EMPLOYEE** user who tries to access `/admin` will be **redirected to `/`**.

---

## Authentication Flow

```
1. User navigates to any page (/ or /admin)
2. DashboardController checks session for "loggedInUser"
3. If no session → redirect to /login
4. User enters username + password on /login
5. AuthController validates against UserRepository
6. If valid → store User in session, redirect to role-appropriate page
7. If invalid → show error on login page
8. Logout → /logout clears session, redirects to /login
```

### Session Key

The logged-in user object is stored in the session under the key:

```
loggedInUser
```

You can access it in any controller via:

```java
User user = (User) session.getAttribute("loggedInUser");
```

---

## Key Files

| File                        | Purpose                                        |
|----------------------------|------------------------------------------------|
| `domain/User.java`         | JPA entity for users                           |
| `domain/Role.java`         | Enum: ADMIN, EMPLOYEE                          |
| `repository/UserRepository.java` | Spring Data JPA repository for users      |
| `config/DataInitializer.java`    | Seeds dummy user data on startup          |
| `controller/AuthController.java` | Handles /login, /logout endpoints         |
| `controller/DashboardController.java` | Session checks + role-based routing   |
| `templates/login.html`     | Login page template                            |

---

## Adding New Users

To add a new user, edit `DataInitializer.java`:

```java
userRepository.save(new User("new.user", 6L, "password", Role.EMPLOYEE));
```

Parameters:
1. `username` — unique login name
2. `employeeId` — unique Long ID (used in meal selections)
3. `password` — plaintext password
4. `role` — `Role.ADMIN` or `Role.EMPLOYEE`

---

## Security Configuration

`SecurityConfig.java` keeps all HTTP requests open (`permitAll()`). Authentication is handled **manually** at the controller level via session checks, not through Spring Security's filter chain.

This means:
- `/login` and `/logout` are accessible without a session
- `/` and `/admin` check for a valid session in the controller
- API endpoints (`/api/meals/*`) remain open (no session check)

---

## How to Test

1. Start the application: `mvn spring-boot:run`
2. Navigate to `http://localhost:8080/` — you'll be redirected to `/login`
3. Login as an employee: `john.doe` / `pass123`
4. You'll see the employee dashboard with your employee ID pre-filled
5. Logout and login as admin: `admin` / `admin123`
6. You'll see the admin dashboard

---

## Future Improvements

- [ ] Hash passwords with BCrypt
- [ ] Spring Security integration with proper filter chain
- [ ] JWT token-based authentication for API endpoints
- [ ] User management CRUD (add/edit/delete users from admin panel)
- [ ] Password reset functionality
- [ ] Session timeout configuration
