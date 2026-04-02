package com.casaaluna.backend.exception;

import java.util.UUID;

/**
 * Exception thrown when a booking request is not found
 */
public class BookingNotFoundException extends BookingException {
    
    public BookingNotFoundException(UUID id) {
        super("Booking request not found with id: " + id);
    }
    
    public BookingNotFoundException(String message) {
        super(message);
    }
}
