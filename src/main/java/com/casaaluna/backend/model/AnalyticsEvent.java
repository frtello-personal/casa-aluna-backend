package com.casaaluna.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * AnalyticsEvent entity for tracking user behavior and system events
 * 
 * Stores events in a flexible format for analytics and monitoring.
 * The eventData field will store JSON data in PostgreSQL JSONB format.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("analytics_events")
public class AnalyticsEvent {

    @Id
    private UUID id;

    private String eventType;
    
    private String eventData;  // JSON string - will be stored as JSONB in PostgreSQL
    
    private UUID userId;  // Nullable - for authenticated events
    
    private String sessionId;
    
    private String ipAddress;
    
    private String userAgent;
    
    @Builder.Default
    private Instant timestamp = Instant.now();
}
