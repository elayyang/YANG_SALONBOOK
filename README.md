# SalonBook: Multi-Service Appointment Scheduling and Client Booking System

## Project Description

SalonBook is a Java-based console application designed to help salon personnel manage clients, salon services, and appointments. It uses an SQLite database to store and manage information efficiently.

## Objectives

- Organize client information.
- Manage salon services and their prices.
- Schedule and manage client appointments.
- Provide user registration and login.
- Support role-based access for authorized users.
- Generate monthly appointment summaries.
- Record system activities.

## User Roles

- **Administrator** – Manages account approvals and system operations.
- **Receptionist** – Handles client information, services, and appointments.
- **Salon Staff** – Accesses the salon management functions available to their role.

## Technologies Used

- Java 17
- IntelliJ IDEA
- Apache Maven
- SQLite
- SQLite JDBC Driver

## Main Features

1. User registration and login
2. Account approval
3. Client management (Add, View, Update, Delete)
4. Salon service management (Add, View, Update, Delete)
5. Appointment booking and viewing
6. Appointment status management
7. Monthly appointment summary
8. Activity log monitoring

## Project Structure

```text
SalonBook/
├── src/
│   └── main/
│       └── java/
│           ├── dao/
│           ├── model/
│           ├── ui/
│           └── Main.java
├── pom.xml
├── README.md
└── salonbook.db
```

## Database

SalonBook uses SQLite to store user accounts, client records, salon services, appointments, and activity logs. Database tables are initialized when the application starts.

## How to Run the Project

1. Install Java Development Kit (JDK) 17 or a compatible version.
2. Install IntelliJ IDEA.
3. Open the project in IntelliJ IDEA.
4. Allow Maven to download the required dependencies.
5. Run `Main.java`.
6. Register an account if needed.
7. Ask an administrator to approve the account before logging in.

## How to Use

1. Register an account.
2. Log in using an active account.
3. Select the available menu options based on your role.
4. Manage clients, services, and appointments.
5. View monthly reports and activity logs when permitted.
6. Log out when finished.

## Project Purpose

This project was developed for the PF201 course to demonstrate Java programming, layered package architecture, database connectivity using SQLite, CRUD operations, and the use of PreparedStatement for database queries.

## Developer

Developed as an academic project for PF201.
