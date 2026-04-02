package com.casaaluna.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

/**
 * Main Spring Boot Application for Casa Aluna Backend
 * 
 * A vacation rental property management system that handles:
 * - Guest booking requests
 * - Admin approval workflow
 * - WhatsApp notifications
 * - Analytics event tracking
 * - Payment processing (Phase 2)
 * - Calendar synchronization (Phase 2)
 */
@SpringBootApplication
@EnableR2dbcRepositories
public class CasaAlunaBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CasaAlunaBackendApplication.class, args);
    }
}
