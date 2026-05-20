package com.jkshian.arms.config;

import com.jkshian.arms.User.Role;
import com.jkshian.arms.entity.AirPlane;
import com.jkshian.arms.entity.User;
import com.jkshian.arms.repo.PlaneRepo;
import com.jkshian.arms.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminBootstrapConfig {

    @Bean
    public CommandLineRunner createDefaultAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String adminEmail = "admin@jkshian.com";
            if (userRepository.findByEmail(adminEmail).isPresent()) {
                return;
            }

            User admin = User.builder()
                    .firstName("System")
                    .lastName("Admin")
                    .email(adminEmail)
                    .password(passwordEncoder.encode("Admin@123"))
                    .role(Role.ROLE_ADMIN)
                    .build();
            userRepository.save(admin);
        };
    }

    @Bean
    public CommandLineRunner createDefaultFlights(PlaneRepo planeRepo) {
        return args -> {
            ensureFlight(planeRepo, "IndiGo", "6E 201", "Airbus A320neo", "Colombo", "Jaffna", "06:10 AM", "07:22 AM", 60, 396.0, 3960000.0);
            ensureFlight(planeRepo, "Air India", "AI 118", "Airbus A321", "Colombo", "Kandy", "09:20 AM", "10:15 AM", 80, 115.0, 1450000.0);
            ensureFlight(planeRepo, "Vistara", "UK 441", "Boeing 787 Dreamliner", "Colombo", "Galle", "01:40 PM", "02:35 PM", 70, 126.0, 1720000.0);
            ensureFlight(planeRepo, "SriLankan", "UL 502", "Airbus A320", "Jaffna", "Colombo", "08:05 AM", "09:17 AM", 60, 396.0, 4050000.0);
            ensureFlight(planeRepo, "Air India", "AI 221", "Airbus A320", "Kandy", "Colombo", "11:10 AM", "12:00 PM", 80, 115.0, 1420000.0);
            ensureFlight(planeRepo, "IndiGo", "6E 330", "ATR 72", "Galle", "Colombo", "04:15 PM", "05:05 PM", 70, 126.0, 1690000.0);
            ensureFlight(planeRepo, "Vistara", "UK 710", "Airbus A320neo", "Matara", "Colombo", "05:50 PM", "06:55 PM", 45, 160.0, 2100000.0);
            ensureFlight(planeRepo, "SriLankan", "UL 880", "Airbus A321", "Colombo", "Matara", "07:30 PM", "08:35 PM", 45, 160.0, 2150000.0);
        };
    }

    private void ensureFlight(PlaneRepo planeRepo, String airline, String flightNumber, String aircraftModel, String start, String end, String departureTime, String arrivalTime, int seats, double km, double price) {
        if (planeRepo.findByFlightNumber(flightNumber).isPresent()) {
            return;
        }

        planeRepo.save(AirPlane.builder()
                .airline(airline)
                .flightNumber(flightNumber)
                .aircraftModel(aircraftModel)
                .start(start)
                .end(end)
                .departureTime(departureTime)
                .arrivalTime(arrivalTime)
                .avlSeat(seats)
                .numOfKm(km)
                .price(price)
                .build());
    }
}
