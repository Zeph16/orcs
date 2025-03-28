# College Management System – Microservices Architecture

## Overview

This project is a **microservices-based** college management system designed for scalability, maintainability, and resilience. The system is decomposed into multiple independent services, each responsible for a core function, ensuring flexibility, fault isolation, and ease of deployment. The architecture follows industry-standard design patterns to facilitate **high availability, observability, and security** while enabling **seamless integration with future systems**.

Key components

- **Microservices-Based Architecture** – Independently deployable, domain-focused services.
   
- **Spring Boot & Spring Cloud** – Robust, production-grade microservices framework.
   
- **Event-Driven Communication** – Asynchronous messaging with RabbitMQ.

- **Resilient & Scalable** – Circuit breakers, load balancing, and service discovery.
   
- **Centralized Security** – OAuth 2.0-based authentication via Keycloak.
   
- **Observability & Monitoring** – Distributed tracing and system health tracking with Zipkin, Prometheus, and Grafana.
   

## Microservices Overview

Each microservice operates independently, with **dedicated databases** and **stateless API interactions**, allowing for modular updates and scaling.

### Student-Service

- Manages student records, personal details, and status.
    
- Exposes REST APIs for CRUD operations.
    

### Curriculum-Service

- Handles courses, subjects, prerequisites, and academic structures.
    
- API for retrieving course catalogs and requirements.
    

### Enrollment-Service

- Manages student course enrollments, drops, and status tracking.
    
- Integrates with **Payment-Service** to validate payments.
    

### Payment-Service

- Handles student payments via **Chapa payment gateway**.
    
- Isolates finances from the rest of the system and supports credit balance management when students drop courses.
    

### Notification-Service

- Asynchronous notification system using RabbitMQ.
    
- System alerts for enrollment, payments, and account updates.
    

### API-Gateway (Backend For Frontend - BFF)

- Acts as the single entry point for all microservices.
    
- Implements **Spring Cloud Gateway** for API routing and security.
    

## Architectural Design Decisions

### Service Decomposition & Domain-Driven Design

The system follows a **domain-driven decomposition**, where each microservice represents a well-defined **bounded context**. This avoids **tight coupling** and lays the foundation for modularity.

- Why separate services?
    
    - Avoid monolithic scaling issues.
        
    - Services can evolve independently.
        
    - Enables polyglot persistence if needed in the future.
        
- Why independent databases?
    
    - Avoids cross-service transactions (anti-pattern in microservices).
        
    - Each service owns its data for autonomy.

### Communication Strategy: REST APIs & RabbitMQ

Inter-service communication is **stateless** and uses a combination of **synchronous REST APIs** and **asynchronous event-driven messaging**.

- **REST APIs for Request-Response:**
    
    - Used for retrieving data in a synchronous manner. In this project `Feignclient` provides an abstraction over direct api calls.
        
    - Example: `Enrollment-Service` queries `Student-Service` for student status.
        
- **RabbitMQ for Asynchronous Messaging:**
    
    - Used for event-driven workflows.
        
    - Example: When a student is registered, an event is published for `Notification-Service` to approve the send a notification.

### Service Discovery with Eureka

- Each service registers itself with a **Eureka Discovery Server**.
    
- Enables dynamic service lookup instead of hardcoded service URLs.
    
- Supports **load balancing** for high availability.

### Resilience with Circuit Breaker Pattern (Resilience4J)

- Prevents cascading failures by cutting off **unhealthy services**.
    
- If `Payment-Service` fails, `Enrollment-Service` doesn't hang.

### Security Architecture: OAuth 2.0 & OpenID Connect (Keycloak)

#### Why Keycloak?

- Provides built-in OAuth 2.0 & OpenID Connect support.
    
- Handles **Single Sign-On (SSO)** for all college systems.
    
- Supports **PKCE (Proof Key for Code Exchange)**, which is what a frontend web client would use to connect to this app.
    

#### Authentication Strategy

- **All authentication is centralized in Keycloak**.
    
- The **Gateway-Service** enforces token validation.
    
- **No inter-service authentication** – services trust each other within the private network.
    

#### Inter-Service Communication & Security

- **No security for inter-service communication** (inside private network).

- **Token propagation via Gateway-Service**: Prevents direct microservice exposure.

## Observability & Monitoring

### Distributed Tracing with Zipkin

- Tracks inter-service requests, debugging latency issues.
    

### Metrics Collection with Prometheus

- Each service exposes **metrics endpoints** for Prometheus to scrape.
    

### Dashboard Visualization with Grafana

- Provides a real-time overview of system health & API performance.


## Deployment Strategy

### Network Security & DMZ Considerations

- **Public-facing services** (Gateway and Keycloak) are placed in a **DMZ**.
    
- Internal microservices are inside a **secure private network**.
    
- **No inter-service authentication** inside the private network.
    

### Centralized Configuration with Spring Cloud Config

- Configurations stored in a **Git-backed Spring Cloud Config Server**.
    
- Allows real-time **config updates** without redeploying services.
    

### Infrastructure Setup

- **Docker Compose** for local development.
    
- **Kubernetes** for production deployment.
    
- **Separate namespaces** for each environment (Dev, Staging, Prod) in the configuration.


## Decision making

### When This Is a Good Idea ✅

- If the system is expected to grow significantly in users, services, or features
    
- If multiple teams are available to work on different functionalities
    
- If there’s a need to introduce different databases or programming languages
    
- If distinct business functionalities (students, curriculum, payments, etc.) need to evolve separately

### When to Avoid ❌
    
- If time-to-market is a priority
    
- If there is a small development team
    
- If adequate decomposition can't be achieved and services need to communicate frequently (chattiness)
    
- If there is a lack of DevOps maturity (CI/CD pipelines, container orchestration...)
