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
 * AdminApproval entity representing an admin's decision on a booking request
 * 
 * Tracks the approval/rejection workflow for booking requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("admin_approvals")
public class AdminApproval {

    @Id
    private UUID id;

    private UUID bookingRequestId;
    
    private UUID adminUserId;
    
    private ApprovalDecision decision;
    
    private String notes;
    
    @Builder.Default
    private Instant approvedAt = Instant.now();
}
