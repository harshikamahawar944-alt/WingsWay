package com.jkshian.arms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightOptionDto {
    private String optionId;
    private int airplaneId;
    private String airline;
    private String flightNumber;
    private String aircraftModel;
    private String start;
    private String end;
    private String departureTime;
    private String arrivalTime;
    private String duration;
    private int seatsAvailable;
    private double price;
}
