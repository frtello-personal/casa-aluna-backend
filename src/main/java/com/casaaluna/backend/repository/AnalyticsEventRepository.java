package com.casaaluna.backend.repository;

import com.casaaluna.backend.model.AnalyticsEvent;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Reactive repository for AnalyticsEvent entities
 */
@Repository
public interface AnalyticsEventRepository extends R2dbcRepository<AnalyticsEvent, UUID> {

    /**
     * Find events by type
     */
    Flux<AnalyticsEvent> findByEventType(String eventType);

    /**
     * Find events by session ID
     */
    Flux<AnalyticsEvent> findBySessionId(String sessionId);

    /**
     * Find events by user ID
     */
    Flux<AnalyticsEvent> findByUserId(UUID userId);

    /**
     * Find recent events
     */
    @Query("SELECT * FROM analytics_events WHERE timestamp >= :since ORDER BY timestamp DESC LIMIT :limit")
    Flux<AnalyticsEvent> findRecentEvents(Long since, int limit);

    /**
     * Count events by type
     */
    Mono<Long> countByEventType(String eventType);
}
