# Energy Management System – Distributed Systems Assignment 1

**Author:** Roatiș Vlad – Group 30641  
**Assignment A1 – Request–Reply Communication**

---

## Overview

This project implements a **Distributed Energy Management System** composed of multiple independent microservices, each running in its own Docker container and exposed through a **Traefik API Gateway**.

The application supports two types of users:

### Administrator:
- CRUD operations on users  
- CRUD operations on devices  
- Assign devices to users  

### Client:
- View all devices assigned to their account  

Authentication is handled via a dedicated **Auth Service**, using **JWT tokens**.

---

## System Components

### 2.1  Frontend (React)
- Login & Register pages  
- Role-based navigation  
- Admin dashboard for managing users and devices  
- Client dashboard with device list  

---

### 2.2  Microservices

####  Auth Service
- Register new users  
- Login  
- JWT generation & validation  

####  User Service
- CRUD operations on users  
- PostgreSQL storage  

####  Device Service
- CRUD operations on devices  
- Assign devices to users  
- PostgreSQL storage  

---

### 2.3  API Gateway (Traefik)
- Single entry point for all microservices  
- URL-based routing (`/auth`, `/users`, `/devices`)  
- Proxying REST requests to internal services  

---

## 🛠 Technologies Used

- **Java 17**
- **Spring Boot 3**
- **Spring Data JPA**
- **PostgreSQL**
- **Docker & Docker Compose**
- **Traefik reverse proxy**
- **ReactJS**
- **JWT Authentication**


---

## ▶️ How to Run the Project

### 4.1 Prerequisites

Install:
- Docker Desktop  
- Node.js (if you want to run frontend locally)  
- Git  

### 4.2 Build & Run via Docker (recommended)

Run these commands **in the main project folder**:

```bash
docker-compose down -v
docker-compose up --build
```

### Access points via Traefik Gateway

| Service | URL |
|--------|------|
| Traefik Dashboard | http://localhost:8080 |
| Auth Service | http://localhost/auth |
| User Service | http://localhost/users |
| Device Service | http://localhost/devices |
| Frontend UI | http://localhost |

---

## 🗄 Database Configuration

Each microservice uses its own **PostgreSQL database**:

| Service | Database |
|---------|-----------|
| Auth Service | `auth_db` |
| User Service | `users_db` |
| Device Service | `devices_db` |

Internal docker hostnames configured in `docker-compose.yml`:
- `auth-postgres-db`
- `user-postgres-db`
- `device-postgres-db`

---

##  How to Test

1. Start services:  
   ```bash
   docker compose up
   ```

2. Open frontend:  
   **http://localhost**


3. Test scenario:
   - Create an admin user  
   - Login as admin  
   - Create a user  
   - Create a device  
   - Assign device → user  
   - Login with non-admin user  
   - View assigned devices  

---


This project was developed as part of the **Distributed Systems** course at **UTCN**.
