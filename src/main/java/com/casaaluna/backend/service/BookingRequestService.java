package com.casaaluna.backend.service;

import com.casaaluna.backend.dto.ApprovalRequestDto;
import com.casaaluna.backend.dto.BookingRequestDto;
import com.casaaluna.backend.exception.BookingNotFoundException;
import com.casaaluna.backend.exception.InvalidBookingStateException;
import com.casaaluna.backend.model.*;
import com.casaaluna.backend.repository.AdminApprovalRepository;
import com.casaaluna.backend.repository.BookingRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

/**
 * Service layer for booking request business logic
 * 
 * All methods are reactive, returning Mono or Flux
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookingRequestService {

    private final BookingRequestRepository bookingRequestRepository;
    private final AdminApprovalRepository adminApprovalRepository;

    /**
     * Create a new booking request
     */
    @Transactional
    public Mono<BookingRequest> createBookingRequest(BookingRequestDto dto) {
        log.info("Creating booking request for guest: {}", dto.guestEmail());
        
        BookingRequest bookingRequest = BookingRequest.builder()
            .id(UUID.randomUUID())
            .guestName(dto.guestName())
            .guestEmail(dto.guestEmail())
            .guestPhone(dto.guestPhone())
            .checkInDate(dto.checkInDate())
            .checkOutDate(dto.checkOutDate())
            .numberOfGuests(dto.numberOfGuests())
            .specialRequests(dto.specialRequests())
            .status(BookingStatus.PENDING)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

        return bookingRequestRepository.save(bookingRequest)
            .doOnSuccess(saved -> log.info("Booking request created with id: {}", saved.getId()))
            .doOnError(error -> log.error("Error creating booking request", error));
    }

    /**
     * Get booking request by ID
     */
    public Mono<BookingRequest> getBookingRequestById(UUID id) {
        log.debug("Fetching booking request with id: {}", id);
        return bookingRequestRepository.findById(id)
            .switchIfEmpty(Mono.error(new BookingNotFoundException(id)));
    }

    /**
     * Get all booking requests
     */
    public Flux<BookingRequest> getAllBookingRequests() {
        log.debug("Fetching all booking requests");
        return bookingRequestRepository.findAll();
    }

    /**
     * Get booking requests by status
     */
    public Flux<BookingRequest> getBookingRequestsByStatus(BookingStatus status) {
        log.debug("Fetching booking requests with status: {}", status);
        return bookingRequestRepository.findByStatus(status);
    }

    /**
     * Get pending booking requests (for admin review)
     */
    public Flux<BookingRequest> getPendingBookingRequests() {
        log.debug("Fetching pending booking requests");
        return bookingRequestRepository.findByStatus(BookingStatus.PENDING);
    }

    /**
     * Approve a booking request
     */
    @Transactional
    public Mono<BookingRequest> approveBookingRequest(UUID bookingId, ApprovalRequestDto approvalDto) {
        log.info("Approving booking request: {}", bookingId);
        
        return bookingRequestRepository.findById(bookingId)
            .switchIfEmpty(Mono.error(new BookingNotFoundException(bookingId)))
            .flatMap(booking -> {
                // Validate booking is in PENDING state
                if (booking.getStatus() != BookingStatus.PENDING) {
                    return Mono.error(new InvalidBookingStateException(
                        "Booking must be in PENDING state to be approved. Current state: " + booking.getStatus()
                    ));
                }

                // Update booking status
                booking.setStatus(BookingStatus.APPROVED);
                booking.setUpdatedAt(Instant.now());

                // Create approval record
                AdminApproval approval = AdminApproval.builder()
                    .id(UUID.randomUUID())
                    .bookingRequestId(bookingId)
                    .adminUserId(approvalDto.adminUserId())
                    .decision(ApprovalDecision.APPROVED)
                    .notes(approvalDto.notes())
                    .approvedAt(Instant.now())
                    .build();

                // Save both booking and approval
                return bookingRequestRepository.save(booking)
                    .flatMap(savedBooking -> adminApprovalRepository.save(approval)
                        .thenReturn(savedBooking))
                    .doOnSuccess(saved -> log.info("Booking request approved: {}", bookingId));
            });
    }

    /**
     * Reject a booking request
     */
    @Transactional
    public Mono<BookingRequest> rejectBookingRequest(UUID bookingId, ApprovalRequestDto approvalDto) {
        log.info("Rejecting booking request: {}", bookingId);
        
        return bookingRequestRepository.findById(bookingId)
            .switchIfEmpty(Mono.error(new BookingNotFoundException(bookingId)))
            .flatMap(booking -> {
                // Validate booking is in PENDING state
                if (booking.getStatus() != BookingStatus.PENDING) {
                    return Mono.error(new InvalidBookingStateException(
                        "Booking must be in PENDING state to be rejected. Current state: " + booking.getStatus()
                    ));
                }

                // Update booking status
                booking.setStatus(BookingStatus.REJECTED);
                booking.setUpdatedAt(Instant.now());

                // Create approval record
                AdminApproval approval = AdminApproval.builder()
                    .id(UUID.randomUUID())
                    .bookingRequestId(bookingId)
                    .adminUserId(approvalDto.adminUserId())
                    .decision(ApprovalDecision.REJECTED)
                    .notes(approvalDto.notes())
                    .approvedAt(Instant.now())
                    .build();

                // Save both booking and approval
                return bookingRequestRepository.save(booking)
                    .flatMap(savedBooking -> adminApprovalRepository.save(approval)
                        .thenReturn(savedBooking))
                    .doOnSuccess(saved -> log.info("Booking request rejected: {}", bookingId));
            });
    }

    /**
     * Cancel a booking request
     */
    @Transactional
    public Mono<BookingRequest> cancelBookingRequest(UUID bookingId) {
        log.info("Cancelling booking request: {}", bookingId);
        
        return bookingRequestRepository.findById(bookingId)
            .switchIfEmpty(Mono.error(new BookingNotFoundException(bookingId)))
            .flatMap(booking -> {
                booking.setStatus(BookingStatus.CANCELLED);
                booking.setUpdatedAt(Instant.now());
                return bookingRequestRepository.save(booking)
                    .doOnSuccess(saved -> log.info("Booking request cancelled: {}", bookingId));
            });
    }

    /**
     * Check availability for given dates
     */
    public Mono<Boolean> checkAvailability(BookingRequestDto dto) {
        log.debug("Checking availability for dates: {} to {}", dto.checkInDate(), dto.checkOutDate());
        
        return bookingRequestRepository
            .findOverlappingBookings(dto.checkInDate(), dto.checkOutDate())
            .hasElements()
            .map(hasOverlap -> !hasOverlap)  // Available if no overlapping bookings
            .doOnSuccess(available -> log.debug("Availability check result: {}", available));
    }
}
