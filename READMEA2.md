# Energy Management System – A2

This project was developed as part of the Distributed Systems course. It implements a distributed energy monitoring platform based on a microservices architecture, handling user management, device administration, consumption tracking, and inter-service synchronization through messaging.

## 1. Overview

The system provides:
- user authentication and management,
- device registration and administration,
- synchronization of device data between services via RabbitMQ,
- ingestion and aggregation of energy consumption measurements,
- frontend visualization for device and consumption data.

The architecture includes five main microservices, a RabbitMQ message broker, three PostgreSQL databases, a Traefik API gateway, and a device simulator used to generate measurement data.

---

## 2. Microservices

### 2.1 Auth-Service
- exposes APIs for user authentication and registration,
- generates and validates JWT access tokens,
- stores user credentials in its own database.

### 2.2 User-Service
- manages User entities,
- provides CRUD operations for administrators,
- is queried by Device-Service when assigning a device to a user.

### 2.3 Device-Service
- manages Device entities,
- allows creation, editing, deletion and assignment of devices,
- validates user existence before assigning a device,
- publishes synchronization events to RabbitMQ so that Monitoring-Service maintains a consistent device list.

It publishes messages into a single queue (`device.sync.queue`) using events:
- `CREATED`
- `DELETED`

---

## 3. Synchronization Mechanism (RabbitMQ)

### 3.1 Device-Service (Publisher)

Whenever a device is created or deleted, Device-Service publishes a JSON message to `device.sync.queue`.  
Each message contains:

eventType: CREATED | DELETED
id: <deviceId>
name: <deviceName>
maxConsumption: <value>



### 3.2 Monitoring-Service (Consumer)

Monitoring-Service listens to the same queue and:
- inserts the device into its local database on `CREATED`,
- removes it on `DELETED`.

This ensures both services maintain consistent device tables without tight coupling.

---

## 4. Monitoring-Service

Monitoring-Service is responsible for two main tasks:

### 4.1 Device Synchronization
Consumes messages from `device.sync.queue` through `DeviceSyncListener`.  
Each event results in inserting or deleting a corresponding entry in the `devices` table.

### 4.2 Energy Consumption Processing
Monitoring-Service also receives measurement messages through `device_measurements_queue`.  
For each measurement:
- it checks whether the device exists in its synchronized device table,
- normalizes the timestamp to the start of the hour,
- aggregates the consumption per hour in the `hourly_consumption` table,
- exposes endpoints for frontend visualization.

---

## 5. Device Simulator

The simulator sends periodic energy consumption measurements to the system.  
Its configuration is stored in `config.json`, allowing multiple devices to be defined without modifying the simulator code.

Example configuration:

{
"devices": [
{ "id": 10, "min": 0.5, "max": 2.5 },
{ "id": 11, "min": 0.2, "max": 1.8 }
],
"interval_seconds": 5
}



The simulator loads this configuration dynamically at startup.

---

## 6. Frontend

The frontend allows:
- user authentication,
- listing assigned devices,
- viewing daily and hourly consumption charts.

All requests are routed through Traefik, which acts as an API gateway for the system.

---

## 7. Technologies Used

- Java 17  
- Spring Boot (Web, Security, JPA)  
- RabbitMQ  
- Docker & Docker Compose  
- PostgreSQL  
- React  
- Traefik  
- Python (device simulator)

---

## 8. Running the Application

### 8.1 Clone the repository
git clone <repository-url>
cd DS2025_30641_Roatis_Vlad_a2



### 8.2 Start all services
docker compose up --build

yaml
Copiază codul

### 8.3 Access points
- Frontend: http://localhost  
- RabbitMQ Management UI: http://localhost:15672  
- Traefik Dashboard: http://localhost:8080  

---

## 9. Repository Structure

/auth-service
/user-service
/device-service
/monitoring-service
/frontend
/device-simulator-producer
docker-compose.yml



---

## 10. Notes

- Device synchronization is event-driven and decoupled via RabbitMQ.
- Monitoring-Service does not create devices automatically; it relies exclusively on synchronization events.
- The simulator configuration file enables flexible testing without hardcoding device IDs.