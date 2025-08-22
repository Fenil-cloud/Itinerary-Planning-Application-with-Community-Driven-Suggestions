# 🧳 Itinerary Planning Microservices

This repository contains a **Java Spring Boot microservices-based Itinerary Planning System** that allows users to plan trips, get community-driven suggestions, vote on polls, interact with AI for trip assistance, and more.  
The system follows a **distributed microservice architecture** with dedicated services for authentication, trip management, community engagement, notifications, and API gateway management.

---

## 🚀 Features

- 🔑 **Authentication & User Management**
  - Register, Login, Logout
  - Reset password
- ✈️ **Trip Management**
  - Add, Update, Search Trips
- 💡 **Community Suggestions**
  - Get trip suggestions from community
  - Like & Dislike suggestions
- 🗳 **Polls & Voting**
  - Create Polls
  - Vote on suggestions
- 🤖 **AI Integration**
  - Ask AI for trip planning or recommendations
- 🔔 **Notifications**
  - Real-time notifications for user activity

---

## 🏗 Microservices Overview

| Service Name                  | Description |
|-------------------------------|-------------|
| **Auth-Service**              | Handles user authentication, registration, login, and password reset |
| **Trip-Service**              | Manages trip creation, update, and search functionality |
| **Community-Suggestions-Service** | Provides community-driven trip suggestions, likes, and dislikes |
| **Voting-And-Poll-Service**   | Enables polls and voting for trip-related suggestions |
| **Notification-Service**      | Sends notifications for important events (trip updates, votes, etc.) |
| **Gateway-Service**           | API Gateway for routing and security |
| **Config-Server**             | Centralized configuration management (without OpenAI key committed) |
| **Service-Registry**          | Eureka service registry for service discovery |

---

## ⚙️ Tech Stack

- **Java 17+**
- **Spring Boot 3+**
- **Spring Cloud** (Eureka, Config, Gateway)
- **Spring Security + JWT**
- **Spring Data JPA / Hibernate**
- **MySQL / PostgreSQL**
- **Kafka / RabbitMQ** (for event-driven communication, optional)
- **Docker** (for containerization)

---

