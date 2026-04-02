package com.casaaluna.backend.dto;

import com.casaaluna.backend.model.ApprovalDecision;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * DTO for admin approval/rejection requests
 */
public record ApprovalRequestDto(
    
    @NotNull(message = "Admin user ID is required")
    UUID adminUserId,
    
    @NotNull(message = "Decision is required")
    ApprovalDecision decision,
    
    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    String notes
) {
}
