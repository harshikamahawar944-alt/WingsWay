package com.jkshian.arms.repo;

import com.jkshian.arms.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepo extends JpaRepository<Booking,Integer> {
    List<Booking> findByUserEmailOrderByIdDesc(String userEmail);

}
