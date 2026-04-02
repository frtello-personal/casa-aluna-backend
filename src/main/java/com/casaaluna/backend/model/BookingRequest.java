package com.casaaluna.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * BookingRequest entity representing a guest's booking request
 * 
 * This is the core entity for Phase 1, capturing all information
 * about a guest's intent to book the property.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("booking_requests")
public class BookingRequest {

    @Id
    private UUID id;

    private String guestName;
    
    private String guestEmail;
    
    private String guestPhone;
    
    private LocalDate checkInDate;
    
    private LocalDate checkOutDate;
    
    private Integer numberOfGuests;
    
    @Builder.Default
    private BookingStatus status = BookingStatus.PENDING;
    
    private String specialRequests;
    
    @Builder.Default
    private Instant createdAt = Instant.now();
    
    @Builder.Default
    private Instant updatedAt = Instant.now();
}
