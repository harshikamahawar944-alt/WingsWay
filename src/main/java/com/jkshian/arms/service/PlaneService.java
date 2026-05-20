package com.jkshian.arms.service;


import com.jkshian.arms.dto.Planedto;
import com.jkshian.arms.dto.FlightOptionDto;
import com.jkshian.arms.dto.RouteSuggestionDto;
import com.jkshian.arms.entity.AirPlane;
import com.jkshian.arms.repo.PlaneRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PlaneService {
    private  final PlaneRepo planeRepo;
    private  final ModelMapper modelMapper;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);


    public ResponseEntity<String> createNewPlane(Planedto planedto){
      AirPlane airPlane = new AirPlane();
      airPlane.setAirline(planedto.getAirline());
      airPlane.setFlightNumber(planedto.getFlightNumber());
      airPlane.setAircraftModel(planedto.getAircraftModel());
      airPlane.setStart(planedto.getStart());
      airPlane.setEnd(planedto.getEnd());
      airPlane.setDepartureTime(planedto.getDepartureTime());
      airPlane.setArrivalTime(planedto.getArrivalTime());
      airPlane.setAvlSeat(planedto.getAvlSeat());
      airPlane.setNumOfKm(planedto.getNumOfKm());
      airPlane.setPrice(planedto.getPrice());
       if(planedto.getFlightNumber() != null && planeRepo.findByFlightNumber(planedto.getFlightNumber()).isPresent()){
           return ResponseEntity.status(401).body("Flight number already exists");
       }else {
           planeRepo.save(airPlane);
           return ResponseEntity.status(200).body("Sccessfully added");
       }
    }

    public ResponseEntity<String> updatePlane(Planedto planedto) {
       AirPlane existOne = planeRepo.findById(planedto.getId()).orElse(null);
       if(existOne == null){
           return ResponseEntity.status(404).body("The Plane does not alrady exist");
       }
       existOne.setAirline(planedto.getAirline());
       existOne.setFlightNumber(planedto.getFlightNumber());
       existOne.setAircraftModel(planedto.getAircraftModel());
       existOne.setStart(planedto.getStart());
       existOne.setEnd(planedto.getEnd());
       existOne.setDepartureTime(planedto.getDepartureTime());
       existOne.setArrivalTime(planedto.getArrivalTime());
       existOne.setNumOfKm(planedto.getNumOfKm());
       existOne.setAvlSeat(planedto.getAvlSeat());
       existOne.setPrice(planedto.getPrice());
       planeRepo.save(existOne);
       return ResponseEntity.status(200).body("Sccessfully Updated");
    }

    public Planedto getPlaneById(int id) {
        AirPlane existOne =  planeRepo.findById(id).orElseThrow();
        return modelMapper.map(existOne,Planedto.class);
    }


    public List<Planedto> getAllPlane() {
        List<AirPlane> airPlanes = planeRepo.findAll();
        return modelMapper.map(airPlanes, new TypeToken<List<Planedto>>(){}.getType());
    }

    public long countPlanes() {
        return planeRepo.count();
    }

    public ResponseEntity<String> deletePlaneById(int id) {
        Planedto existOne = getPlaneById(id);
        planeRepo.delete(modelMapper.map(existOne,AirPlane.class));
        return ResponseEntity.status(200).body(" The AirPlane  is  Sccessfully Deleted");
    }

    public List<RouteSuggestionDto> getRouteSuggestions(String query) {
        String normalizedQuery = normalize(query);
        return planeRepo.findAll().stream()
                .flatMap(plane -> Stream.of(plane.getStart(), plane.getEnd()))
                .filter(city -> city != null && !city.isBlank())
                .map(String::trim)
                .distinct()
                .filter(city -> normalizedQuery.isBlank() || city.toLowerCase(Locale.ENGLISH).contains(normalizedQuery))
                .sorted()
                .limit(8)
                .map(city -> RouteSuggestionDto.builder()
                        .city(city)
                        .label(city + " flights")
                        .build())
                .collect(Collectors.toList());
    }

    public List<FlightOptionDto> searchFlightOptions(String start, String end, int seatsRequested) {
        String normalizedStart = normalize(start);
        String normalizedEnd = normalize(end);
        if (normalizedStart.isBlank() || normalizedEnd.isBlank() || seatsRequested <= 0) {
            return List.of();
        }

        return planeRepo.findAll().stream()
                .filter(plane -> plane.getStart() != null && plane.getEnd() != null)
                .filter(plane -> plane.getStart().equalsIgnoreCase(normalizedStart) && plane.getEnd().equalsIgnoreCase(normalizedEnd))
                .filter(plane -> plane.getAvlSeat() >= seatsRequested)
                .map(this::mapToFlightOption)
                .sorted(Comparator.comparingDouble(FlightOptionDto::getPrice))
                .collect(Collectors.toList());
    }

    private FlightOptionDto mapToFlightOption(AirPlane plane) {
        long durationMinutes = calculateDurationMinutes(plane);
        return FlightOptionDto.builder()
                .optionId(String.valueOf(plane.getId()))
                .airplaneId(plane.getId())
                .airline(defaultString(plane.getAirline(), "Airline"))
                .flightNumber(defaultString(plane.getFlightNumber(), "FL-" + plane.getId()))
                .aircraftModel(defaultString(plane.getAircraftModel(), "Airbus A320"))
                .start(plane.getStart())
                .end(plane.getEnd())
                .departureTime(defaultString(plane.getDepartureTime(), "08:00 AM"))
                .arrivalTime(defaultString(plane.getArrivalTime(), "09:00 AM"))
                .duration((durationMinutes / 60) + "h " + (durationMinutes % 60) + "m")
                .seatsAvailable(plane.getAvlSeat())
                .price(plane.getPrice() > 0 ? plane.getPrice() : Math.round(plane.getNumOfKm() * 10000.00))
                .build();
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ENGLISH);
    }

    private String defaultString(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value;
    }

    private long calculateDurationMinutes(AirPlane plane) {
        try {
            if (plane.getDepartureTime() != null && plane.getArrivalTime() != null) {
                LocalTime departure = LocalTime.parse(plane.getDepartureTime().toUpperCase(Locale.ENGLISH), TIME_FORMATTER);
                LocalTime arrival = LocalTime.parse(plane.getArrivalTime().toUpperCase(Locale.ENGLISH), TIME_FORMATTER);
                long minutes = java.time.Duration.between(departure, arrival).toMinutes();
                if (minutes > 0) {
                    return minutes;
                }
            }
        } catch (Exception ignored) {
        }
        return Math.max(55L, Math.round((plane.getNumOfKm() / 650.0) * 60) + 35);
    }

}
