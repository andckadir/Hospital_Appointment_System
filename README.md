🏥 Hospital Appointment System

A console-based Hospital Appointment Management System developed in Java as part of the CME 2210 - Object Oriented Analysis and Design course at Dokuz Eylul University.

This system allows patients to book appointments, doctors to manage schedules, and administrators to maintain records in a structured multi-layered architecture.

📌 Project Overview

The Hospital Appointment System is designed to digitize and simplify hospital appointment processes.

It provides:

Online appointment booking

Appointment cancellation and rescheduling

Role-based access control

Doctor schedule management

Patient record viewing

Notification handling

The system follows Object-Oriented Design principles and a layered architecture structure.

🎯 Objectives

Digitize the appointment scheduling process

Reduce hospital congestion

Minimize administrative errors

Improve usability for patients and doctors

Maintain secure and structured patient records

🏗️ System Architecture

The project follows a 3-layered architecture:

1️⃣ Presentation Layer

Console-based user interface

Role-based dynamic menus

User interaction handling

Main Class:
Test

2️⃣ Application Layer (Business Logic)

Appointment booking validation

Time conflict prevention

Role-based access control

Notification triggering

Authentication logic

Main Services:

AppointmentService

AuthenticationService

3️⃣ Data Access Layer

Simulated database implementation

CRUD operations

Interface-based abstraction

Main Components:

IDatabase

Database (Singleton)

IPatientDatabase

IDoctorDatabase

📂 Project Structure
src/
│
├── Appointment/
│   └── Appointment.java
│
├── databasePackage/
│   ├── IDatabase.java
│   └── Database.java
│
├── servicePackage/
│   ├── AppointmentService.java
│   └── AuthenticationService.java
│
├── userPackage/
│   ├── Patient.java
│   ├── Doctor.java
│   ├── DoctorBuilder.java
│   └── BuilderFactory.java
│
├── ConsoleMenu.java
│
└── Test.java


👥 User Roles
🧑 Patient

Register / Login

Book appointment

Cancel appointment

View appointment history

👨‍⚕️ Doctor

Login

View appointment calendar

Cancel appointment

View patient information

🛠 Administrator

Manage doctor records

Manage patient records

🔐 Non-Functional Requirements

Performance: Appointment list loads under 1 second

Scalability: Supports 1000+ users

Security: Encrypted login handling

Availability: 24/7 access

Maintainability: Modular and layered structure

Usability: Clear and role-based navigation

🧠 Design Principles Used

Encapsulation

Polymorphism

Interfaces

Layered Architecture

Builder Design Pattern

Singleton Pattern

🧪 Testing

The system was tested through:

Manual unit test scenarios

Functional flow testing via ConsoleMenu

Edge case validation:

Double booking prevention

Invalid login attempts

Appointment conflict checks

All core functionalities work as expected:

Booking

Cancellation

Login

Listing appointments

📈 Future Improvements

GUI implementation (JavaFX / Web-based UI)

Calendar view for appointments

Real database integration (MySQL / PostgreSQL)

SMS / Email notification system

Multi-language support

Mobile compatibility
