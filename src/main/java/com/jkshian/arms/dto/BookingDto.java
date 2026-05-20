package com.jkshian.arms.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private int id;
    private String bstart;
    private String bend;
    private int bnumofseat;
    private double price;
    private String classType;
    private String seatNumbers;
    private String airline;
    private String flightNumber;
    private String aircraftModel;
    private String departureTime;
    private String arrivalTime;
    private String bookingReference;
}
