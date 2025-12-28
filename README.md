# Online Ticket Booking System

A comprehensive backend system designed to manage online event ticket bookings. This application handles the complete lifecycle of a booking system, including user management, movie browsing, real-time seat selection, concurrent transaction processing, and asynchronous notifications.

The system is built using **Java Spring Boot** and is fully containerized with **Docker**.

---

## 🚀 Key Features

* **User Management**: Secure user registration and login with **JWT Authentication** and password hashing (BCrypt).
* **Event Discovery**: Browse movies and shows with high-performance caching using **Redis**.
* **Booking Engine**: Transactional booking process with **Pessimistic Locking** to prevent double-booking of seats during high concurrency.
* **Real-time Availability**: Check seat status (Available/Booked/Locked) in real-time.
* **Asynchronous Notifications**: Decoupled email notification simulation using **Apache Kafka**.
* **Containerization**: Full infrastructure setup (DB, Cache, Broker, App) using **Docker Compose**.

---

## 🛠️ Technology Stack

| Component | Technology | Version |
| :--- | :--- | :--- |
| **Language** | Java | 17 (Eclipse Temurin) |
| **Framework** | Spring Boot | 3.1.5 |
| **Database** | PostgreSQL | 15 (Alpine) |
| **Security** | Spring Security + JWT | - |
| **Caching** | Redis | Alpine |
| **Messaging** | Apache Kafka + Zookeeper | 7.5.0 (Confluent) |
| **Testing** | JUnit 5 + Mockito + Testcontainers | - |
| **Documentation** | Swagger / OpenAPI | 3.0 |
| **DevOps** | Docker & Docker Compose | 3.8 |

---

## 🏗️ System Architecture

The application follows a **Layered Architecture** interacting with external infrastructure services.

* **API Layer (Controllers)**: Handles HTTP requests and input validation.
* **Service Layer**: Contains business logic, including transaction management and locking strategies.
* **Data Access Layer**: Uses Spring Data JPA to interact with PostgreSQL.
* **Infrastructure**:
    * **PostgreSQL**: Persistent storage for Users, Events, and Bookings.
    * **Redis**: Caches `GET /api/events` requests to reduce database load.
    * **Kafka**: Handles `BookingCreatedEvent` messages asynchronously to trigger notifications.

---

## ⚙️ Setup & Installation

### Prerequisites
* Docker Desktop
* Java 17 (Optional, if running locally without Docker)

### Step-by-Step Run Guide

1.  **Clone the Repository**
    ```bash
    git clone [https://github.com/Sachin-nsk/TicketBookingSystem.git](https://github.com/Sachin-nsk/TicketBookingSystem.git)
    cd TicketBookingSystem
    ```

2.  **Build the Application**
    Compiles the Java code and creates the JAR file.
    ```bash
    # Linux/Mac
    ./mvnw clean package -DskipTests
    
    # Windows
    mvnw clean package -DskipTests
    ```

3.  **Start the Infrastructure**
    This command starts PostgreSQL, Redis, Kafka, Zookeeper, and the Backend Application.
    ```bash
    docker-compose up --build
    ```

4.  **Access the Application**
    * **API Base URL**: `http://localhost:8080`
    * **Swagger Documentation**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## 🔌 API Documentation

You can test all endpoints via Swagger UI. Below are the critical endpoints:

### 1. Authentication
* **Register**: `POST /api/users`
* **Login**: `POST /api/auth/login`
    * **Body**: `{ "email": "user@test.com", "password": "password" }`
    * **Response**: Returns a Bearer Token.

### 2. Event Browsing (Public)
* **Get All Events**: `GET /api/events` (Cached in Redis)
* **Get Show Seats**: `GET /api/shows/{showId}/seats`

### 3. Booking (Secured)
* **Create Booking**: `POST /api/bookings`
    * **Header**: `Authorization: Bearer <your_token>`
    * **Body**:
        ```json
        {
          "showId": 1,
          "showSeatIds": [1, 2]
        }
        ```

---

## 🧪 Testing Strategy

The project includes a robust testing suite.
### Running Tests
To run the full test suite:
```bash
./mvnw test
