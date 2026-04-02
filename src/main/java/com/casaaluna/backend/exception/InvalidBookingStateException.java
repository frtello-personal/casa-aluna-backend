package com.casaaluna.backend.exception;

/**
 * Exception thrown when a booking operation is invalid for the current state
 */
public class InvalidBookingStateException extends BookingException {
    
    public InvalidBookingStateException(String message) {
        super(message);
    }
}
