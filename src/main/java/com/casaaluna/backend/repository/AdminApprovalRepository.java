package com.casaaluna.backend.repository;

import com.casaaluna.backend.model.AdminApproval;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Reactive repository for AdminApproval entities
 */
@Repository
public interface AdminApprovalRepository extends R2dbcRepository<AdminApproval, UUID> {

    /**
     * Find approval by booking request ID
     */
    Mono<AdminApproval> findByBookingRequestId(UUID bookingRequestId);

    /**
     * Find all approvals by admin user ID
     */
    Flux<AdminApproval> findByAdminUserId(UUID adminUserId);

    /**
     * Check if approval exists for a booking request
     */
    Mono<Boolean> existsByBookingRequestId(UUID bookingRequestId);
}
