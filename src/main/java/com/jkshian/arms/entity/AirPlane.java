package com.jkshian.arms.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DynamicUpdate
@Table(name =  "airplane")
public class AirPlane {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int id;
    private String airline;
    private String flightNumber;
    private String aircraftModel;
    private String start;
    private String end;
    private String departureTime;
    private String arrivalTime;
    private int avlSeat;
    private double numOfKm;
    private double price;
}
