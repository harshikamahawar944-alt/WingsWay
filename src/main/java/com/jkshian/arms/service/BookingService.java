package com.jkshian.arms.service;


import com.jkshian.arms.dto.BookingDto;
import com.jkshian.arms.entity.AirPlane;
import com.jkshian.arms.entity.Booking;
import com.jkshian.arms.repo.BookingRepo;
import com.jkshian.arms.repo.PlaneRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final PlaneRepo planeRepo;
    private final BookingRepo bookingRepo;


    public List<Booking> getAllBooking() {
        return bookingRepo.findAll();
    }

    public long countBookings() {
        return bookingRepo.count();
    }

    public List<Booking> getBookingsForUser(String userEmail) {
        return bookingRepo.findByUserEmailOrderByIdDesc(userEmail);
    }


    public ResponseEntity<Double> checkPrice(BookingDto bookingdto) {
        if (bookingdto.getBnumofseat() <= 0) {
            return ResponseEntity.badRequest().build();
        }
        AirPlane findPlane =planeIsAvilable(bookingdto);
        if(findPlane!=null){
              bookingdto.setPrice(calculatePrice(findPlane.getNumOfKm(),bookingdto.getBnumofseat()));
            return ResponseEntity.ok(bookingdto.getPrice());
        }
        return ResponseEntity.notFound().build();
    }

    private AirPlane planeIsAvilable(BookingDto bookingdto){
        if (bookingdto.getFlightNumber() != null && !bookingdto.getFlightNumber().isBlank()) {
            AirPlane byFlightNumber = planeRepo.findByFlightNumber(bookingdto.getFlightNumber()).orElse(null);
            if (byFlightNumber != null) {
                return byFlightNumber;
            }
        }
        AirPlane findPlane = planeRepo.findByStartAndEnd(bookingdto.getBstart(),bookingdto.getBend());
        if(findPlane == null){
            return null;
        }
        return findPlane;
    }

    private double calculatePrice(double numOfKm, int bnumOfseat) {
        double pricePerKm = 10000.00;
        return numOfKm*bnumOfseat*pricePerKm;
    }

    public ResponseEntity<String> addBooking(BookingDto bookingdto) {
        return addBooking(bookingdto, null);
    }

    @Transactional
    public ResponseEntity<String> addBooking(BookingDto bookingdto, String userEmail) {
        if (bookingdto.getBnumofseat() <= 0) {
            return ResponseEntity.badRequest().body("Seat count must be greater than zero");
        }
        AirPlane findPlane =planeIsAvilable(bookingdto);
        Booking booking = new Booking();
        if(findPlane != null){
            double finalPrice = bookingdto.getPrice() > 0 ? bookingdto.getPrice() : calculatePrice(findPlane.getNumOfKm(),bookingdto.getBnumofseat());
            booking.setBookingReference(
                    bookingdto.getBookingReference() != null && !bookingdto.getBookingReference().isBlank()
                            ? bookingdto.getBookingReference()
                            : "JK" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase()
            );
            booking.setBStart(bookingdto.getBstart());
            booking.setBEnd(bookingdto.getBend());
            booking.setBNumOfseat(bookingdto.getBnumofseat());
            booking.setUserEmail(userEmail);
            booking.setAirline(bookingdto.getAirline() != null ? bookingdto.getAirline() : findPlane.getAirline());
            booking.setFlightNumber(bookingdto.getFlightNumber() != null ? bookingdto.getFlightNumber() : findPlane.getFlightNumber());
            booking.setAircraftModel(bookingdto.getAircraftModel() != null ? bookingdto.getAircraftModel() : findPlane.getAircraftModel());
            booking.setDepartureTime(bookingdto.getDepartureTime() != null ? bookingdto.getDepartureTime() : findPlane.getDepartureTime());
            booking.setArrivalTime(bookingdto.getArrivalTime() != null ? bookingdto.getArrivalTime() : findPlane.getArrivalTime());
            booking.setClassType(bookingdto.getClassType());
            booking.setSeatNumbers(bookingdto.getSeatNumbers());
            booking.setPrice(finalPrice);

           if(findPlane.getAvlSeat() - bookingdto.getBnumofseat() >= 0){
               bookingRepo.save(booking);
               findPlane.setAvlSeat(findPlane.getAvlSeat() - bookingdto.getBnumofseat());
               planeRepo.save(findPlane);
           }else {
              return ResponseEntity.status(401).body("Seats Are not Available");
           }
           return ResponseEntity.ok(booking.getBookingReference());
        }else {
            return ResponseEntity.status(401).body("The plane is not found");
        }
    }
}
