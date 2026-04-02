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
 * Guest entity representing a customer
 * 
 * Stores guest information for future bookings and communication.
 * Can be linked to multiple booking requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("guests")
public class Guest {

    @Id
    private UUID id;

    private String name;
    
    private String email;
    
    private String phone;
    
    private String whatsappNumber;
    
    private String country;
    
    private String documentType;
    
    private String documentNumber;
    
    @Builder.Default
    private Instant createdAt = Instant.now();
}
