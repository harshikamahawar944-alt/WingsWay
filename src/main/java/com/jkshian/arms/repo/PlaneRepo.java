package com.jkshian.arms.repo;

import com.jkshian.arms.entity.AirPlane;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PlaneRepo extends JpaRepository<AirPlane,Integer> {

    @Query("SELECT  a FROM AirPlane a WHERE a.start = ?1 AND a.end = ?2")
    AirPlane findByStartAndEnd(String start,String end);

    Optional<AirPlane> findByFlightNumber(String flightNumber);

    Optional<AirPlane> findById(int id);
}
