package com.casaaluna.backend.repository;

import com.casaaluna.backend.model.BookingRequest;
import com.casaaluna.backend.model.BookingStatus;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Reactive repository for BookingRequest entities
 * 
 * Provides reactive database operations using R2DBC.
 * All methods return Mono (single result) or Flux (multiple results).
 */
@Repository
public interface BookingRequestRepository extends R2dbcRepository<BookingRequest, UUID> {

    /**
     * Find all booking requests by status
     */
    Flux<BookingRequest> findByStatus(BookingStatus status);

    /**
     * Find booking requests by guest email
     */
    Flux<BookingRequest> findByGuestEmail(String guestEmail);

    /**
     * Find booking requests that overlap with given date range
     * Used for availability checking
     */
    @Query("SELECT * FROM booking_requests " +
           "WHERE status IN ('APPROVED', 'PAID', 'CONFIRMED') " +
           "AND (check_in_date <= :checkOutDate AND check_out_date >= :checkInDate)")
    Flux<BookingRequest> findOverlappingBookings(LocalDate checkInDate, LocalDate checkOutDate);

    /**
     * Count pending booking requests
     */
    Mono<Long> countByStatus(BookingStatus status);

    /**
     * Find booking requests created after a specific date
     */
    @Query("SELECT * FROM booking_requests WHERE created_at >= :since ORDER BY created_at DESC")
    Flux<BookingRequest> findRecentBookings(Long since);
}
