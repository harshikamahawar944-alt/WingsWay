package com.jkshian.arms.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "booking")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String bookingReference;
    private String bStart;
    private String bEnd;
    private String userEmail;
    private String airline;
    private String flightNumber;
    private String aircraftModel;
    private String departureTime;
    private String arrivalTime;
    private String classType;
    private String seatNumbers;
    private int bNumOfseat;
    private double price;
}
