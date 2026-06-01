# Airline Reservation Management System

The Airline Reservation Management System is a web-based application that streamlines the process of flight booking and reservation management. It offers a convenient interface for users to search and book flights, check available seats, manage reservations, and handle cancellations. The system also provides airline administrators with tools to manage flights, seat availability, and passenger information.

![Screenshot (289)](https://github.com/ImeshaDilshani/Airline-Reservation-Management-System/assets/93858302/28855982-352a-4b09-a0a5-d7aa8bcf6c5f)


## Features
- User Registration and Login: Passengers can create accounts and log in to access the reservation system.
- Flight Search: Users can search for available flights based on their preferred destinations and travel dates.
- Seat Availability: The system displays seat availability for each flight, allowing users to choose their seats during booking.
- Booking and Reservation: Passengers can book flights and manage their reservations.
- Admin Dashboard: Airline administrators have access to a dashboard to manage flights, seat availability, and user bookings.
- Flight Management: Admins can add, update, or remove flights from the system.
- Reservation Management: Airline staff can manage passenger reservations, including cancellations and modifications.

## Technologies Used
- Frontend: HTML, CSS, JavaScript
- Backend: Java Spring Boot
- Database: H2

### Clone the Repository
```bash
git clone https://github.com/ImeshaDilshani/Airline-Reservation-Management-System.git
cd Airline-Reservation-Management-System
```
### Database Setup
The app uses a file-based H2 database by default. Hibernate creates and updates the tables automatically, and the app seeds a default admin account plus starter flights on first run.

### Backend Setup
- Configure the database connection in `src/main/resources/application.properties`.
- Start the app from the project root with Maven, not with `javac`:

```bash
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

- The app uses port `8080` locally by default and automatically uses Render's `PORT` value in production.
- In VS Code, run the `ArmsApplication` launch configuration. Do not run `ArmsApplication.java` as a single current file, because Spring Boot dependencies are loaded through Maven.

### Render Deployment
The app can run on Render without an external database because it uses H2. By default, the database file is created at `./data/arms` inside the running service.

For data that survives redeploys, add a Render persistent disk and set this environment variable:

```bash
H2_DB_PATH=/var/data/arms
```

Remove any old MySQL environment variables from Render, especially `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD`. Spring Boot gives environment variables higher priority than `application.properties`, so old values can override the H2 configuration.

If you do not attach a persistent disk, Render may lose the H2 database file on redeploy. The default login seeded by the app is:

```text
Email: admin@jkshian.com
Password: Admin@123
```

## Access the Application
Open your web browser and visit `http://localhost:8080` to access the Airline Reservation Management System.


