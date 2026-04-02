package com.casaaluna.backend.model;

/**
 * Booking request status enumeration
 * 
 * Lifecycle:
 * PENDING -> APPROVED -> PAID -> CONFIRMED
 *         -> REJECTED
 *         -> CANCELLED (at any stage)
 */
public enum BookingStatus {
    PENDING,      // Initial state when guest submits booking request
    APPROVED,     // Admin has approved the booking request
    REJECTED,     // Admin has rejected the booking request
    PAID,         // Guest has completed payment (Phase 2)
    CONFIRMED,    // Booking is fully confirmed
    CANCELLED     // Booking has been cancelled
}
