package com.devmam.taraacademyapi.repository;

import com.devmam.taraacademyapi.models.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer>, JpaSpecificationExecutor<Booking> {
    /**
     * Find all bookings by service ID
     */
    List<Booking> findByServiceId(Integer serviceId);
    
    /**
     * Count bookings by service ID
     */
    long countByServiceId(Integer serviceId);
}