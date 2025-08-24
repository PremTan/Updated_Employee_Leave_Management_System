# Updated Employee Leave Management System

This is an **updated and improved version** of my previous Employee Leave Management project. Built with **Spring Boot, JWT, MySQL, and Email notifications**, it follows a **neatly layered architecture** and demonstrates real-world backend development.

## Features
- Employee registration, login & profile management
- JWT-based authentication with logout and token blacklisting
- Role-based access: Admin & Employee
- Leave application with overlap checks and leave balance validation
- Admin approval/rejection of leave requests
- Email notifications for admins and employees
- Global exception handling for robust error management

## Tech Stack
- Spring Boot
- Spring Security
- JPA / Hibernate
- MySQL
- JWT
- JavaMail
- ModelMapper

## Challenges Faced
- Implementing JWT token security and blacklist logic correctly for logout
- Preventing admin from approving/rejecting the same leave request multiple times
- Handling leave overlap issues when an employee applies for leave in between partially approved dates
- Scheduling tasks not working as expected despite multiple attempts

## Next Steps
- Add unit testing
- Improve email templates
- Build a React frontend

## How to Run
1. Clone the repository
2. Configure `application.properties` with your MySQL database credentials
3. Run the project using:
   ```bash
   mvn spring-boot:run
