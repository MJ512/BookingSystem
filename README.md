# 🎬 BookMyShow – Movie Booking Backend API

A RESTful backend for a movie ticket booking system built with **Java 21**, **Jakarta EE (JAX-RS / Jersey)**, and **PostgreSQL**.

Users can register, log in, browse movies and shows, book seats, cancel bookings, and manage their profile.

---

## 📐 Architecture

```
src/main/java/org/bookmyshow/
├── controller/         # JAX-RS REST endpoints
│   ├── booking/        # BookingController
│   └── user/           # RegistrationController, UserLoginController, UserDashboardController
├── service/            # Business logic layer
│   ├── booking/        # BookingService
│   └── user/           # RegistrationService, UserLoginService, UserDashboardService
├── repository/         # Interfaces + abstract base
│   └── impl/           # Concrete JDBC implementations
├── model/              # Plain Java domain objects (User, Booking, Movie, …)
├── validation/         # Input validation (pattern + booking rules)
├── datasource/         # HikariCP connection pool
├── config/             # ConfigLoader (reads config.properties)
├── util/               # HashPassword (BCrypt)
└── exception/          # BookingException (RuntimeException)
```

**Stack:** Java 21 · Jakarta EE 10 · Jersey 4 · HikariCP · PostgreSQL · BCrypt · SLF4J + Logback · Maven

---

## 🚀 Getting Started

### Prerequisites

| Tool | Version |
|------|---------|
| Java | 21+ |
| Maven | 3.8+ |
| PostgreSQL | 14+ |
| Tomcat | 10+ |

### 1. Clone the repo

```bash
git clone https://github.com/MJ512/BookingSystem.git
cd BookingSystem
```

### 2. Configure the database

Copy the example config and fill in your credentials:

```bash
cp src/main/resources/config.properties.example src/main/resources/config.properties
```

Edit `config.properties`:

```properties
db.url=jdbc:postgresql://localhost:5432/bookingsystemdb
db.username=your_db_user
db.password=your_db_password
```

> ⚠️ `config.properties` is in `.gitignore` — never commit real credentials.

### 3. Build

```bash
mvn clean package
```

### 4. Deploy

Copy the generated WAR to Tomcat's webapps directory:

```bash
cp target/BookMyShow-1.0-SNAPSHOT.war $TOMCAT_HOME/webapps/BookMyShow.war
```

The API will be available at: `http://localhost:8080/BookMyShow/api/`

---

## 📡 API Reference

### User

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/register` | Register a new user |
| `POST` | `/api/login` | Login (starts session) |
| `POST` | `/api/users/logout` | Logout (invalidates session) |
| `GET`  | `/api/users/history/{user_id}` | Get booking history |
| `PUT`  | `/api/users/update?user_id=&password=` | Update profile |
| `PUT`  | `/api/users/change-password?user_id=&oldPassword=&newPassword=` | Change password |

### Booking

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/booking` | Book seats for a show |
| `DELETE` | `/api/booking/cancel/{user_id}/{id}` | Cancel a booking |

### Example: Register

```json
POST /api/register
{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "9876543210",
  "password": "Secret@123"
}
```

### Example: Book a Ticket

```json
POST /api/booking
{
  "userId": 1,
  "movieShowId": 5,
  "seatIds": [12, 13],
  "isConfirmed": true
}
```

**Password requirements:** minimum 8 characters, at least one uppercase, one lowercase, one digit, one special character.

---

## 🗄️ Database Schema (Key Tables)

```sql
users         (id, name, email, phone, password)
movie         (id, title, certificate, language, genre, duration, release_date, is_playing)
theater       (id, name, address_id)
screen        (id, theater_id, capacity)
movie_show    (id, movie_id, theater_id, screen_id, start_time)
show_seats    (id, show_id, seat_id, is_booked)
booking       (id, user_id, movie_show_id, booking_time, is_confirmed)
booked_seats  (booking_id, seat_id)
```

---

## 🔒 Security Notes

- Passwords are hashed with **BCrypt** before storage
- Session-based authentication via `HttpSession`
- Input validated with regex patterns before processing
- **JWT-based auth is planned** (see To-Do)

---

## ✅ To-Do / Roadmap

- [ ] Replace session auth with **JWT tokens**
- [ ] Add **role-based access** (User vs Theater Owner)
- [ ] Theater owner API (schedule shows, manage screens)
- [ ] Payment integration (Razorpay / Stripe)
- [ ] Add unit and integration tests (JUnit 5 + Mockito)
- [ ] Migrate to environment variables for all config (Docker-ready)
- [ ] Admin dashboard API

---

## 🤝 Contributing

1. Fork the repo
2. Create a branch: `git checkout -b feature/your-feature`
3. Commit: `git commit -m "feat: describe your change"`
4. Push: `git push origin feature/your-feature`
5. Open a Pull Request

---

## 📄 License

MIT License — see [LICENSE](LICENSE) for details.
