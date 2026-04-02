package com.casaaluna.backend.repository;

import com.casaaluna.backend.model.Guest;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Reactive repository for Guest entities
 */
@Repository
public interface GuestRepository extends R2dbcRepository<Guest, UUID> {

    /**
     * Find guest by email
     */
    Mono<Guest> findByEmail(String email);

    /**
     * Find guest by phone number
     */
    Mono<Guest> findByPhone(String phone);

    /**
     * Check if guest exists by email
     */
    Mono<Boolean> existsByEmail(String email);
}
