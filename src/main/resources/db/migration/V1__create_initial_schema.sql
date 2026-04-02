-- Casa Aluna Backend - Initial Database Schema
-- Phase 1: Booking Requests, Guests, Admin Approvals, and Analytics Events

-- Create booking_requests table
CREATE TABLE booking_requests (
    id UUID PRIMARY KEY,
    guest_name VARCHAR(100) NOT NULL,
    guest_email VARCHAR(255) NOT NULL,
    guest_phone VARCHAR(20) NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    number_of_guests INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    special_requests TEXT,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,
    
    -- Constraints
    CONSTRAINT chk_dates CHECK (check_out_date > check_in_date),
    CONSTRAINT chk_guests CHECK (number_of_guests > 0 AND number_of_guests <= 20),
    CONSTRAINT chk_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'PAID', 'CONFIRMED', 'CANCELLED'))
);

-- Create guests table
CREATE TABLE guests (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20),
    whatsapp_number VARCHAR(20),
    country VARCHAR(100),
    document_type VARCHAR(50),
    document_number VARCHAR(100),
    created_at BIGINT NOT NULL
);

-- Create admin_approvals table
CREATE TABLE admin_approvals (
    id UUID PRIMARY KEY,
    booking_request_id UUID NOT NULL,
    admin_user_id UUID NOT NULL,
    decision VARCHAR(20) NOT NULL,
    notes TEXT,
    approved_at BIGINT NOT NULL,
    
    -- Foreign key
    CONSTRAINT fk_booking_request FOREIGN KEY (booking_request_id) 
        REFERENCES booking_requests(id) ON DELETE CASCADE,
    
    -- Constraints
    CONSTRAINT chk_decision CHECK (decision IN ('APPROVED', 'REJECTED')),
    CONSTRAINT unique_booking_approval UNIQUE (booking_request_id)
);

-- Create analytics_events table
CREATE TABLE analytics_events (
    id UUID PRIMARY KEY,
    event_type VARCHAR(100) NOT NULL,
    event_data JSONB,
    user_id UUID,
    session_id VARCHAR(255),
    ip_address VARCHAR(45),
    user_agent TEXT,
    timestamp BIGINT NOT NULL
);

-- Create indexes for better query performance

-- Booking requests indexes
CREATE INDEX idx_booking_requests_status ON booking_requests(status);
CREATE INDEX idx_booking_requests_guest_email ON booking_requests(guest_email);
CREATE INDEX idx_booking_requests_dates ON booking_requests(check_in_date, check_out_date);
CREATE INDEX idx_booking_requests_created_at ON booking_requests(created_at);

-- Guests indexes
CREATE INDEX idx_guests_email ON guests(email);
CREATE INDEX idx_guests_phone ON guests(phone);

-- Admin approvals indexes
CREATE INDEX idx_admin_approvals_booking_id ON admin_approvals(booking_request_id);
CREATE INDEX idx_admin_approvals_admin_user_id ON admin_approvals(admin_user_id);

-- Analytics events indexes
CREATE INDEX idx_analytics_events_type ON analytics_events(event_type);
CREATE INDEX idx_analytics_events_user_id ON analytics_events(user_id);
CREATE INDEX idx_analytics_events_session_id ON analytics_events(session_id);
CREATE INDEX idx_analytics_events_timestamp ON analytics_events(timestamp);
CREATE INDEX idx_analytics_events_data ON analytics_events USING GIN(event_data);

-- Comments for documentation
COMMENT ON TABLE booking_requests IS 'Stores guest booking requests for Casa Aluna property';
COMMENT ON TABLE guests IS 'Stores guest information for communication and future bookings';
COMMENT ON TABLE admin_approvals IS 'Tracks admin approval/rejection decisions for booking requests';
COMMENT ON TABLE analytics_events IS 'Stores analytics events for tracking user behavior and system events';

COMMENT ON COLUMN booking_requests.status IS 'Booking lifecycle status: PENDING, APPROVED, REJECTED, PAID, CONFIRMED, CANCELLED';
COMMENT ON COLUMN analytics_events.event_data IS 'JSONB field for flexible event data storage';
