# Digital Document Indexing (DDI)

Digital Document Indexing (DDI) is a Spring Boot-based application designed to provide efficient indexing and retrieval of digital documents. It offers features such as document storage, indexing, search capabilities, and metadata management. The project aims to streamline document management processes for organizations and individuals.

## Table of Contents

- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Running the Application](#running-the-application)
    - [Development Environment](#development-environment)
    - [Production Environment (TBD)](#production-environment-tbd)
    - [Run .Jar file after being built by using maven](#run-jar-file-after-being-built-by-using-maven)
- [API Documentation](#api-documentation)

## Features

- **Document Storage**: Store and manage digital documents securely.
- **Indexing**: Efficiently index documents for quick retrieval.
- **Search**: Full-text search functionality to find documents easily.
- **Metadata Management**: Manage metadata associated with each document.
- **Role-Based Access Control**: Secure access based on user roles.

## Prerequisites

- **Java 21** or higher
- **Maven 3.9.8** or higher
- **Git** (for version control)

## Installation

1. **Clone the repository**:
   ```sh
   git clone https://github.com/trinhvinhphat2003/capstone-backend.git
   cd capstone-backend
2. **Install dependencies and build the project**:
    ```sh
   mvn clean install
## Running the Application

### Development Environment

To run the application in the development environment, follow these steps:
1. **Run Docker Compose**:
   Before starting the application, ensure that the necessary services (MySQL, Redis, and RabbitMQ) are running. Use Docker Compose to set up these services:

   ```sh
   docker-compose -f docker-compose.dev.yml up -d
2. **Run the application**:
   ```sh
   ./mvnw spring-boot:run --define spring-boot.run.arguments="--spring.profiles.active=dev"
### Development Environment Integrating With Front-End

To run the application in the development environment, follow these steps:
1. **Build scss server**:
   ```sh
   docker build -f Dockerfile.frontend -t trinhvinhphat2003/capstone-backend-server-fe .
2. **Push scss server**:
   ```sh
   docker push trinhvinhphat2003/capstone-backend-server-fe:latest
3. **Build mobile socket server**:
   ```sh
   docker build -t trinhvinhphat2003/mobile-socket-capstone-server .
4. **Build mobile socket server**:
   ```sh
   docker push trinhvinhphat2003/mobile-socket-capstone-server:latest
5. **Run Docker Compose**:
   This is what FE team have to do to run server:

   ```sh
   docker-compose -f docker-compose.fe.yml up -d
### Production Environment (TBD)

To run the application in the production environment, follow these steps:
1. **Run the application**:
   ```sh
   ./mvnw spring-boot:run --define spring-boot.run.arguments="--spring.profiles.active=prod"
### Run .Jar file after being built by using maven
1. **Run with /dev/ profile**:
   ```shell
   java -jar gym-management-system-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
2. **Run with /prod/ profile**:
   ```sh
   java -jar gym-management-system-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
## API Documentation

The project includes Swagger for API documentation. Swagger provides an interactive interface to explore and test the API endpoints.

To access the API documentation:

1. **Start the Application**:
   Ensure the application is running. For development, use the `dev` profile:
   ```sh
   ./mvnw spring-boot:run --define spring-boot.run.arguments="--spring.profiles.active=dev"
2. **Open the Swagger UI**:
   Navigate to http://localhost:8080/swagger-ui.html in your web browser. This will display the Swagger UI where you can view the available API endpoints, their descriptions, and try out requests interactively.
3. **Explore Endpoints**:
   Use the Swagger UI to browse through the various endpoints, view request parameters, and see example responses. This interactive documentation helps you understand how to interact with the API and test its functionality.