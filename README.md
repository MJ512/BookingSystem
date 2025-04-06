# BookingSystemAPI

**BookingSystemAPI** is a Spring Boot-based backend application for managing movie ticket bookings. It supports user registration, login, and allows users to book, cancel, and modify their bookings.

The project is under active development and follows a modular and scalable design.

---

## Current Features

### 1. User Functionality

- User registration and login
- Book movie tickets for available shows
- Cancel existing bookings
- Modify bookings

---

## To-Do (In Progress)

### Theater Owner Functionality (Planned)
- Implement theater owner login
- Create API for scheduling shows (assigning movies to screens)
- Create API for modifying showtimes
- Restrict management access to authenticated theater owners

### Authentication and Access Control
- Secure APIs using token/session-based authentication (e.g., JWT)
- Encrypt and hash stored passwords
- Add role-based access control (User vs. Theater Owner)

### Payment Integration
- Design `Payment` entity and add to booking flow
- Create `PaymentController` and `PaymentService`
- (Optional) Integrate third-party gateways like Stripe or Razorpay

### Database & ORM Improvements
- Transition from simple repository usage to full ORM abstraction
- Add support for PostgreSQL/MySQL in addition to H2
- Optimize database access using custom queries and transactions

---

## API Overview

| Endpoint                          | Description                                   |
|----------------------------------|-----------------------------------------------|
| `POST /api/users/register`       | Register a new user                           |
| `POST /api/users/login`          | Login for existing user                       |
| `POST /api/bookings`             | Book a ticket for a show                      |
| `PUT /api/bookings/{id}`         | Modify an existing booking                    |
| `DELETE /api/bookings/{id}`      | Cancel a booking                              |

> Endpoints related to theater owner login and show management are planned and not yet implemented.

---

## Technology Stack

- Java 21
- Spring Boot
- Spring Data JPA
- H2 / MySQL (configurable)
- RESTful APIs

---

## Future Enhancements

- Admin dashboard for managing theaters, screens, and owners
- Seat reservation and availability system
- Full role-based access for users and theater owners
- UI dashboard for both users and theater owners

---

## Notes

This backend service is a work in progress and is structured to support future expansion such as online payments, real-time seat selection, and role-based access control.

