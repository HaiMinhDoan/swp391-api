package com.devmam.taraacademyapi.service.impl.entities;

import com.devmam.taraacademyapi.exception.customize.CommonException;
import com.devmam.taraacademyapi.models.entities.Booking;
import com.devmam.taraacademyapi.models.entities.Ser;
import com.devmam.taraacademyapi.repository.BookingRepository;
import com.devmam.taraacademyapi.repository.SerRepository;
import com.devmam.taraacademyapi.service.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SerService extends BaseServiceImpl<Ser, Integer> {
    private static final Logger logger = LoggerFactory.getLogger(SerService.class);
    
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingService bookingService;

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public SerService(SerRepository repository) {
        super(repository);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        // Check if service exists
        if (!exists(id)) {
            CommonException exception = new CommonException("Service not found with ID: " + id);
            exception.setHttpStatus(HttpStatus.NOT_FOUND);
            throw exception;
        }

        // Find all bookings associated with this service
        List<Booking> bookings = bookingRepository.findByServiceId(id);
        
        // Delete all associated bookings first
        if (!bookings.isEmpty()) {
            logger.info("Deleting {} booking(s) associated with service ID: {}", bookings.size(), id);
            for (Booking booking : bookings) {
                bookingService.delete(booking.getId());
            }
            logger.info("Successfully deleted {} booking(s) for service ID: {}", bookings.size(), id);
        }

        // Now delete the service
        logger.info("Deleting service ID: {}", id);
        super.delete(id);
        logger.info("Successfully deleted service ID: {}", id);
    }
}