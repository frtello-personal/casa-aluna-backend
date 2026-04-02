package com.casaaluna.backend.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * DTO for creating/updating booking requests
 * Uses Java 21 record for immutability and conciseness
 */
public record BookingRequestDto(
    
    @NotBlank(message = "Guest name is required")
    @Size(min = 2, max = 100, message = "Guest name must be between 2 and 100 characters")
    String guestName,
    
    @NotBlank(message = "Guest email is required")
    @Email(message = "Invalid email format")
    String guestEmail,
    
    @NotBlank(message = "Guest phone is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    String guestPhone,
    
    @NotNull(message = "Check-in date is required")
    @Future(message = "Check-in date must be in the future")
    LocalDate checkInDate,
    
    @NotNull(message = "Check-out date is required")
    @Future(message = "Check-out date must be in the future")
    LocalDate checkOutDate,
    
    @NotNull(message = "Number of guests is required")
    @Min(value = 1, message = "At least 1 guest is required")
    @Max(value = 20, message = "Maximum 20 guests allowed")
    Integer numberOfGuests,
    
    @Size(max = 500, message = "Special requests must not exceed 500 characters")
    String specialRequests
) {
    /**
     * Custom validation to ensure check-out is after check-in
     */
    public BookingRequestDto {
        if (checkInDate != null && checkOutDate != null && !checkOutDate.isAfter(checkInDate)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }
    }
}
