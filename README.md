# Evergreen Hotel Reservation Website

Full-stack hotel booking app for **Visual Studio Code**, built with HTML, CSS, vanilla JavaScript, Java 17, Spring Boot, Maven, and MySQL.

## Requirements

- JDK 17 or newer
- MySQL Server 8.x
- Visual Studio Code with the **Extension Pack for Java** (recommended)
- Maven (or use the Maven wrapper if you add one locally)

## Run in Visual Studio Code

1. Install Java and MySQL, then open this repository folder in VS Code.
2. Start the MySQL service. The default configuration connects as `root` with an empty password and creates the `hotel_reservation` database automatically. If that is not your local MySQL setup, set the environment variables below.
3. Open the VS Code terminal in the project folder and run:

   ```bash
   mvn spring-boot:run
   ```

4. Open **http://localhost:8080** in your browser. The frontend and REST API are served by the same Spring Boot app.

Alternatively, build a jar with `mvn clean package` and run it using `java -jar target/hotel-reservation-1.0.0.jar`.

## Configure MySQL credentials

Default connection:

- Database: `hotel_reservation` (created automatically if MySQL permits it)
- Username: `root`
- Password: empty

Set `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` in the environment to use another local MySQL account. Example for PowerShell:

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your-mysql-password"
mvn spring-boot:run
```

Example for macOS/Linux:

```bash
export DB_USERNAME=root
export DB_PASSWORD='your-mysql-password'
mvn spring-boot:run
```

If automatic database creation is disabled for your MySQL account, create it once in MySQL:

```sql
CREATE DATABASE hotel_reservation;
```

Hibernate creates the tables on first launch, and the app inserts six sample rooms if the rooms table is empty.

## Features

- Responsive hotel landing page and room cards
- Search by room style and date range
- Check date-specific room availability
- Create reservations with guest name and automatic total calculation
- View and cancel reservations
- Prevent overlapping reservations for the same room

## REST API

| Method | Endpoint | Purpose |
|---|---|---|
| `GET` | `/api/rooms?checkIn=2026-12-01&checkOut=2026-12-03&category=Deluxe` | Search rooms; `category` is optional |
| `GET` | `/api/reservations` | List reservations |
| `POST` | `/api/reservations` | Create reservation |
| `DELETE` | `/api/reservations/{id}` | Cancel reservation |

Example request body:

```json
{
  "guestName": "Alex Morgan",
  "roomNumber": 201,
  "checkInDate": "2026-12-01",
  "checkOutDate": "2026-12-03"
}
```

Dates use ISO format (`yyyy-MM-dd`). Check-out is exclusive for availability calculations; a guest checking out on a date does not block a booking that begins that same date.
