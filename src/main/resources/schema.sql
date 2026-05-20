CREATE DATABASE IF NOT EXISTS `Airline_Reservation_System`
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE `Airline_Reservation_System`;

DROP TABLE IF EXISTS `booking`;
DROP TABLE IF EXISTS `airplane`;
DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `first_name` VARCHAR(255) DEFAULT NULL,
    `last_name` VARCHAR(255) DEFAULT NULL,
    `email` VARCHAR(255) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `role` VARCHAR(50) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_email` (`email`),
    CONSTRAINT `chk_user_role` CHECK (`role` IN ('ROLE_USER', 'ROLE_ADMIN'))
);

CREATE TABLE `airplane` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `airline` VARCHAR(255) DEFAULT NULL,
    `flight_number` VARCHAR(255) DEFAULT NULL,
    `aircraft_model` VARCHAR(255) DEFAULT NULL,
    `start` VARCHAR(255) NOT NULL,
    `end` VARCHAR(255) NOT NULL,
    `departure_time` VARCHAR(50) DEFAULT NULL,
    `arrival_time` VARCHAR(50) DEFAULT NULL,
    `avl_seat` INT NOT NULL,
    `num_of_km` DOUBLE NOT NULL,
    `price` DOUBLE NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_airplane_flight_number` (`flight_number`),
    KEY `idx_airplane_route` (`start`, `end`),
    CONSTRAINT `chk_airplane_avl_seat` CHECK (`avl_seat` >= 0),
    CONSTRAINT `chk_airplane_num_of_km` CHECK (`num_of_km` > 0),
    CONSTRAINT `chk_airplane_price` CHECK (`price` >= 0)
);

CREATE TABLE `booking` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `booking_reference` VARCHAR(255) DEFAULT NULL,
    `b_start` VARCHAR(255) NOT NULL,
    `b_end` VARCHAR(255) NOT NULL,
    `user_email` VARCHAR(255) DEFAULT NULL,
    `airline` VARCHAR(255) DEFAULT NULL,
    `flight_number` VARCHAR(255) DEFAULT NULL,
    `aircraft_model` VARCHAR(255) DEFAULT NULL,
    `departure_time` VARCHAR(50) DEFAULT NULL,
    `arrival_time` VARCHAR(50) DEFAULT NULL,
    `class_type` VARCHAR(50) DEFAULT NULL,
    `seat_numbers` VARCHAR(255) DEFAULT NULL,
    `b_num_ofseat` INT NOT NULL,
    `price` DOUBLE NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_booking_reference` (`booking_reference`),
    KEY `idx_booking_user_email` (`user_email`),
    KEY `idx_booking_route` (`b_start`, `b_end`),
    CONSTRAINT `chk_booking_b_num_ofseat` CHECK (`b_num_ofseat` > 0),
    CONSTRAINT `chk_booking_price` CHECK (`price` >= 0)
);

INSERT INTO `airplane`
    (`airline`, `flight_number`, `aircraft_model`, `start`, `end`, `departure_time`, `arrival_time`, `avl_seat`, `num_of_km`, `price`)
VALUES
    ('IndiGo', '6E 201', 'Airbus A320neo', 'Colombo', 'Jaffna', '06:10 AM', '07:22 AM', 60, 396.0, 3960000.0),
    ('Air India', 'AI 118', 'Airbus A321', 'Colombo', 'Kandy', '09:20 AM', '10:15 AM', 80, 115.0, 1450000.0),
    ('Vistara', 'UK 441', 'Boeing 787 Dreamliner', 'Colombo', 'Galle', '01:40 PM', '02:35 PM', 70, 126.0, 1720000.0),
    ('SriLankan', 'UL 502', 'Airbus A320', 'Jaffna', 'Colombo', '08:05 AM', '09:17 AM', 60, 396.0, 4050000.0),
    ('Air India', 'AI 221', 'Airbus A320', 'Kandy', 'Colombo', '11:10 AM', '12:00 PM', 80, 115.0, 1420000.0),
    ('IndiGo', '6E 330', 'ATR 72', 'Galle', 'Colombo', '04:15 PM', '05:05 PM', 70, 126.0, 1690000.0),
    ('Vistara', 'UK 710', 'Airbus A320neo', 'Matara', 'Colombo', '05:50 PM', '06:55 PM', 45, 160.0, 2100000.0),
    ('SriLankan', 'UL 880', 'Airbus A321', 'Colombo', 'Matara', '07:30 PM', '08:35 PM', 45, 160.0, 2150000.0),
    ('Air India', 'AI 302', 'Boeing 787 Dreamliner', 'Delhi', 'New York', '02:15 AM', '08:40 AM', 78, 11760.0, 5199999.9),
    ('Air India', 'AI 303', 'Boeing 787 Dreamliner', 'New York', 'Delhi', '12:30 PM', '01:45 PM', 78, 11760.0, 5199999.9),
    ('IndiGo', '6E 512', 'Airbus A320neo', 'Chennai', 'Hyderabad', '10:00 AM', '11:20 AM', 130, 520.0, 200000.0),
    ('IndiGo', '6E 513', 'Airbus A320neo', 'Hyderabad', 'Chennai', '12:10 PM', '01:30 PM', 130, 520.0, 200000.0),
    ('Vistara', 'UK 881', 'Airbus A321', 'Kolkata', 'Pune', '06:00 AM', '08:45 AM', 140, 1570.0, 350000.0),
    ('Vistara', 'UK 882', 'Airbus A321', 'Pune', 'Kolkata', '09:30 AM', '12:15 PM', 140, 1570.0, 350000.0),
    ('Akasa Air', 'QP 140', 'Boeing 737 MAX', 'Bangalore', 'Kochi', '07:35 AM', '08:40 AM', 99, 360.0, 150000.0),
    ('Akasa Air', 'QP 141', 'Boeing 737 MAX', 'Kochi', 'Bangalore', '09:30 AM', '10:35 AM', 99, 360.0, 150000.0),
    ('Emirates', 'EK 511', 'Boeing 777', 'Delhi', 'Dubai', '02:00 PM', '05:30 PM', 200, 2200.0, 1200000.0),
    ('Emirates', 'EK 512', 'Boeing 777', 'Dubai', 'Delhi', '06:15 PM', '09:45 PM', 200, 2200.0, 1200000.0),
    ('Air India', 'AI 161', 'Boeing 787 Dreamliner', 'Delhi', 'London', '03:45 PM', '08:30 PM', 300, 6700.0, 4500000.0),
    ('Air India', 'AI 162', 'Boeing 787 Dreamliner', 'London', 'Delhi', '09:15 PM', '02:00 AM', 300, 6700.0, 4500000.0),
    ('Vistara', 'UK 305', 'Airbus A321', 'Chennai', 'Singapore', '06:00 AM', '11:30 AM', 150, 3800.0, 2500000.0),
    ('Vistara', 'UK 306', 'Airbus A321', 'Singapore', 'Chennai', '12:15 PM', '05:45 PM', 150, 3800.0, 2500000.0),
    ('IndiGo', '6E 901', 'Airbus A320neo', 'Mumbai', 'Goa', '05:30 PM', '06:45 PM', 110, 129.99, 180000.0),
    ('IndiGo', '6E 902', 'Airbus A320neo', 'Goa', 'Mumbai', '07:30 PM', '08:45 PM', 110, 129.99, 180000.0),
    ('SpiceJet', 'SG 703', 'Boeing 737', 'Ahmedabad', 'Jaipur', '07:20 AM', '08:40 AM', 68, 540.0, 209999.98),
    ('SpiceJet', 'SG 704', 'Boeing 737', 'Jaipur', 'Ahmedabad', '08:30 AM', '09:50 AM', 68, 540.0, 209999.98);

-- Users are stored with BCrypt-encoded passwords.
-- Create users through the application registration endpoints,
-- or insert BCrypt hashes manually if you want seed accounts.
