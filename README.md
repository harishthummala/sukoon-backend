# Sukoon

**Sukoon** is a full-stack AI-powered mental wellbeing companion designed to provide a calm, responsive, and secure user experience.  
It combines a modern frontend with a production-ready Java backend, cloud deployment, containerization, and CI/CD automation.

**Live Frontend:** https://sukoon-frontend-v2.vercel.app  
**Live Backend API:** https://sukoonai.mooo.com/api

---

## What Sukoon Does

Sukoon is built to help users interact with a mental wellbeing assistant through a clean and intuitive interface backed by a scalable API architecture.  
The system is designed with a focus on performance, maintainability, security, and deployment readiness.

---

## Tech Stack

### Frontend
- TypeScript
- CSS
- Frontend hosted on Vercel

### Backend
- Java
- Spring Boot
- Spring Security
- JPA / Hibernate
- REST APIs
- PostgreSQL
- Docker
- Nginx

### Infrastructure / DevOps
- AWS EC2
- AWS RDS
- GitHub Actions
- Dockerized deployment
- Nginx reverse proxy

---

## Key Features

- Modern and responsive frontend experience
- Secure backend architecture using Spring Security
- RESTful API design
- PostgreSQL-backed persistence
- Cloud deployment on AWS
- Docker-based application packaging
- CI/CD automation using GitHub Actions
- Nginx configured for production traffic routing
- Separate frontend and backend deployments for cleaner scaling and maintainability

---

## Architecture Overview

- **Frontend** communicates with the backend through REST APIs
- **Backend** handles authentication, business logic, and data persistence
- **PostgreSQL** stores application data on AWS RDS
- **AWS EC2** runs the backend service in a production environment
- **Nginx** acts as the reverse proxy and supports production routing
- **GitHub Actions** automates deployment workflow

---

## Why This Project Matters

Sukoon is not just a CRUD app. It reflects real-world engineering decisions around:
- clean separation of concerns
- secure API design
- deployment on cloud infrastructure
- containerized delivery
- maintainable backend architecture

---

## Repository Structure

```bash
sukoon-backend/
sukoon-frontend-v2/
```

---
 
## Deployment

-Frontend deployed on Vercel
-Backend deployed on AWS EC2
-Database managed on AWS RDS
-Production routing handled through Nginx
-CI/CD automated with GitHub Actions
-Future Enhancements

---

## Advanced analytics dashboard

-Better user personalization
-Expanded AI-driven support flows
-Observability and structured logging improvements
-Rate limiting and stronger production hardening
