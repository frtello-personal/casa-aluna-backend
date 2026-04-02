package com.casaaluna.backend.controller;

import com.casaaluna.backend.dto.ApprovalRequestDto;
import com.casaaluna.backend.dto.BookingRequestDto;
import com.casaaluna.backend.model.BookingRequest;
import com.casaaluna.backend.model.BookingStatus;
import com.casaaluna.backend.service.BookingRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * REST controller for booking request endpoints
 * 
 * All endpoints are reactive, returning Mono or Flux wrapped in ResponseEntity
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingRequestController {

    private final BookingRequestService bookingRequestService;

    /**
     * Create a new booking request
     * POST /api/v1/bookings/requests
     */
    @PostMapping("/requests")
    public Mono<ResponseEntity<BookingRequest>> createBookingRequest(
            @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        log.info("Received booking request from: {}", bookingRequestDto.guestEmail());
        
        return bookingRequestService.createBookingRequest(bookingRequestDto)
            .map(booking -> ResponseEntity.status(HttpStatus.CREATED).body(booking));
    }

    /**
     * Get all booking requests
     * GET /api/v1/bookings/requests
     */
    @GetMapping("/requests")
    public Flux<BookingRequest> getAllBookingRequests(
            @RequestParam(required = false) BookingStatus status) {
        log.info("Fetching booking requests with status filter: {}", status);
        
        if (status != null) {
            return bookingRequestService.getBookingRequestsByStatus(status);
        }
        return bookingRequestService.getAllBookingRequests();
    }

    /**
     * Get booking request by ID
     * GET /api/v1/bookings/requests/{id}
     */
    @GetMapping("/requests/{id}")
    public Mono<ResponseEntity<BookingRequest>> getBookingRequestById(@PathVariable UUID id) {
        log.info("Fetching booking request: {}", id);
        
        return bookingRequestService.getBookingRequestById(id)
            .map(ResponseEntity::ok);
    }

    /**
     * Cancel a booking request
     * DELETE /api/v1/bookings/requests/{id}
     */
    @DeleteMapping("/requests/{id}")
    public Mono<ResponseEntity<BookingRequest>> cancelBookingRequest(@PathVariable UUID id) {
        log.info("Cancelling booking request: {}", id);
        
        return bookingRequestService.cancelBookingRequest(id)
            .map(ResponseEntity::ok);
    }

    /**
     * Check availability for dates
     * POST /api/v1/bookings/check-availability
     */
    @PostMapping("/check-availability")
    public Mono<ResponseEntity<AvailabilityResponse>> checkAvailability(
            @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        log.info("Checking availability for dates: {} to {}", 
            bookingRequestDto.checkInDate(), bookingRequestDto.checkOutDate());
        
        return bookingRequestService.checkAvailability(bookingRequestDto)
            .map(available -> ResponseEntity.ok(new AvailabilityResponse(available)));
    }

    /**
     * Get pending booking requests (admin endpoint)
     * GET /api/v1/bookings/admin/pending
     */
    @GetMapping("/admin/pending")
    public Flux<BookingRequest> getPendingBookingRequests() {
        log.info("Fetching pending booking requests for admin review");
        return bookingRequestService.getPendingBookingRequests();
    }

    /**
     * Approve a booking request (admin endpoint)
     * POST /api/v1/bookings/admin/{id}/approve
     */
    @PostMapping("/admin/{id}/approve")
    public Mono<ResponseEntity<BookingRequest>> approveBookingRequest(
            @PathVariable UUID id,
            @Valid @RequestBody ApprovalRequestDto approvalDto) {
        log.info("Admin approving booking request: {}", id);
        
        return bookingRequestService.approveBookingRequest(id, approvalDto)
            .map(ResponseEntity::ok);
    }

    /**
     * Reject a booking request (admin endpoint)
     * POST /api/v1/bookings/admin/{id}/reject
     */
    @PostMapping("/admin/{id}/reject")
    public Mono<ResponseEntity<BookingRequest>> rejectBookingRequest(
            @PathVariable UUID id,
            @Valid @RequestBody ApprovalRequestDto approvalDto) {
        log.info("Admin rejecting booking request: {}", id);
        
        return bookingRequestService.rejectBookingRequest(id, approvalDto)
            .map(ResponseEntity::ok);
    }

    /**
     * Availability response record
     */
    public record AvailabilityResponse(boolean available) {}
}
