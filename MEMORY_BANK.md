# Casa Aluna Backend - Memory Bank

## Project Overview

**Project Name:** Casa Aluna Backend  
**Purpose:** Vacation rental property management system for Casa Aluna  
**Business Context:** A booking and property management platform that handles guest reservations, payment processing, calendar synchronization with major vacation rental platforms, and analytics tracking.

---

## Technology Stack

### Core Technologies
- **Language:** Java 21
- **Framework:** Spring Boot with WebFlux (Reactive Programming)
- **Database:** PostgreSQL
- **Build Tool:** Maven

### Key Characteristics
- **Reactive Architecture:** Using Spring WebFlux for non-blocking, asynchronous request handling
- **Modern Java:** Leveraging Java 21 features (virtual threads, pattern matching, records, etc.)
- **Relational Database:** PostgreSQL for transactional data and analytics storage

---

## Development Phases

### Phase 1: Core Booking & Notifications
**Status:** Initial Development

**Features:**
1. **Booking Request System**
   - Guest submits booking intent
   - Capture guest information and desired dates
   - Store booking requests in PostgreSQL

2. **Guest Validation Workflow**
   - Admin reviews booking intent
   - Admin approves or rejects before payment
   - Prevents unauthorized bookings

3. **WhatsApp Notifications**
   - Notify admin of new booking requests
   - Send confirmation to guests
   - Status updates throughout booking lifecycle

4. **Analytics Event Storage**
   - Track user behavior events
   - Store events in PostgreSQL
   - Foundation for future analytics dashboard

### Phase 2: Payments & Calendar Integration
**Status:** Planned

**Features:**
1. **PayU Payment Gateway Integration**
   - Process payments after admin approval
   - Handle payment confirmations
   - Manage refunds and cancellations

2. **Airbnb/Vrbo iCal Synchronization**
   - Import bookings from external platforms
   - Prevent double bookings
   - Sync availability calendars
   - Two-way calendar updates

3. **Full Booking Management**
   - Complete booking lifecycle management
   - Booking modifications and cancellations
   - Guest communication history
   - Booking status tracking

---

## Domain Models & Entities

### Core Entities

#### 1. BookingRequest
```
- id (UUID)
- guestName (String)
- guestEmail (String)
- guestPhone (String)
- checkInDate (LocalDate)
- checkOutDate (LocalDate)
- numberOfGuests (Integer)
- status (Enum: PENDING, APPROVED, REJECTED, PAID, CONFIRMED, CANCELLED)
- specialRequests (String)
- createdAt (Instant)
- updatedAt (Instant)
```

#### 2. Guest
```
- id (UUID)
- name (String)
- email (String)
- phone (String)
- whatsappNumber (String)
- country (String)
- documentType (String)
- documentNumber (String)
- createdAt (Instant)
```

#### 3. AdminApproval
```
- id (UUID)
- bookingRequestId (UUID)
- adminUserId (UUID)
- decision (Enum: APPROVED, REJECTED)
- notes (String)
- approvedAt (Instant)
```

#### 4. Payment
```
- id (UUID)
- bookingRequestId (UUID)
- amount (BigDecimal)
- currency (String)
- paymentMethod (String)
- payuTransactionId (String)
- status (Enum: PENDING, COMPLETED, FAILED, REFUNDED)
- paidAt (Instant)
```

#### 5. AnalyticsEvent
```
- id (UUID)
- eventType (String)
- eventData (JSONB)
- userId (UUID, nullable)
- sessionId (String)
- ipAddress (String)
- userAgent (String)
- timestamp (Instant)
```

#### 6. CalendarSync
```
- id (UUID)
- platform (Enum: AIRBNB, VRBO, BOOKING_COM)
- icalUrl (String)
- lastSyncedAt (Instant)
- syncStatus (Enum: SUCCESS, FAILED)
- errorMessage (String, nullable)
```

#### 7. ExternalBooking
```
- id (UUID)
- platform (String)
- externalBookingId (String)
- checkInDate (LocalDate)
- checkOutDate (LocalDate)
- guestName (String)
- syncedAt (Instant)
```

---

## API Design

### Phase 1 Endpoints

#### Booking Requests
```
POST   /api/v1/bookings/requests          - Create booking request
GET    /api/v1/bookings/requests          - List all booking requests (admin)
GET    /api/v1/bookings/requests/{id}     - Get booking request details
PUT    /api/v1/bookings/requests/{id}     - Update booking request
DELETE /api/v1/bookings/requests/{id}     - Cancel booking request
```

#### Admin Approval
```
POST   /api/v1/admin/bookings/{id}/approve  - Approve booking request
POST   /api/v1/admin/bookings/{id}/reject   - Reject booking request
GET    /api/v1/admin/bookings/pending       - Get pending approvals
```

#### Analytics
```
POST   /api/v1/analytics/events           - Track analytics event
GET    /api/v1/analytics/events           - Query analytics events (admin)
```

#### Notifications (Internal)
```
POST   /api/v1/notifications/whatsapp     - Send WhatsApp notification
```

### Phase 2 Endpoints

#### Payments
```
POST   /api/v1/payments/initiate          - Initiate PayU payment
POST   /api/v1/payments/webhook           - PayU webhook callback
GET    /api/v1/payments/{id}              - Get payment status
POST   /api/v1/payments/{id}/refund       - Process refund
```

#### Calendar Sync
```
POST   /api/v1/calendar/sync              - Trigger manual sync
GET    /api/v1/calendar/availability      - Check availability
POST   /api/v1/calendar/sources           - Add iCal source
GET    /api/v1/calendar/sources           - List iCal sources
DELETE /api/v1/calendar/sources/{id}      - Remove iCal source
```

---

## Integration Points

### 1. WhatsApp Integration (Phase 1)
**Options:**
- **Twilio WhatsApp API** (Recommended)
- **WhatsApp Business API**
- **Meta Cloud API**

**Use Cases:**
- New booking request notifications to admin
- Booking approval/rejection notifications to guests
- Payment reminders
- Check-in/check-out reminders

**Implementation Considerations:**
- Store WhatsApp message templates
- Handle message delivery status
- Implement retry logic for failed messages
- Rate limiting and quota management

### 2. PayU Payment Gateway (Phase 2)
**Integration Type:** REST API

**Key Flows:**
1. Payment initiation after admin approval
2. Redirect guest to PayU payment page
3. Handle payment confirmation webhook
4. Update booking status based on payment result

**Security:**
- API key management
- Webhook signature verification
- PCI compliance considerations

### 3. iCal Synchronization (Phase 2)
**Platforms:**
- Airbnb
- Vrbo
- Booking.com (future)

**Sync Strategy:**
- Scheduled polling (every 15-30 minutes)
- Parse iCal format (RFC 5545)
- Detect conflicts with existing bookings
- Block dates on Casa Aluna calendar

**Data Mapping:**
- Extract check-in/check-out dates
- Parse guest information (if available)
- Handle timezone conversions

---

## Database Schema Considerations

### PostgreSQL Features to Leverage
1. **JSONB for Analytics Events**
   - Flexible schema for diverse event types
   - Efficient querying with GIN indexes

2. **Date Range Types**
   - Use `daterange` for booking periods
   - Efficient overlap detection for availability checks

3. **Enums for Status Fields**
   - Type-safe status management
   - Database-level validation

4. **Indexes**
   - B-tree indexes on foreign keys
   - GIN indexes on JSONB columns
   - Composite indexes for common queries

5. **Constraints**
   - Check constraints for date validation (check-out > check-in)
   - Unique constraints for external booking IDs
   - Foreign key constraints for referential integrity

### Sample Schema Patterns
```sql
-- Booking availability check using daterange
CREATE INDEX idx_bookings_date_range ON bookings 
USING GIST (daterange(check_in_date, check_out_date));

-- Analytics event JSONB indexing
CREATE INDEX idx_analytics_event_data ON analytics_events 
USING GIN (event_data);

-- Composite index for common queries
CREATE INDEX idx_bookings_status_dates ON booking_requests 
(status, check_in_date, check_out_date);
```

---

## Architecture Patterns

### Reactive Programming with WebFlux
**Key Concepts:**
- Non-blocking I/O operations
- Backpressure handling
- Reactive streams (Mono, Flux)

**Benefits:**
- Better resource utilization
- Improved scalability
- Efficient handling of external API calls

**Example Pattern:**
```java
public Mono<BookingRequest> createBookingRequest(BookingRequestDto dto) {
    return bookingRepository.save(toEntity(dto))
        .flatMap(booking -> notificationService.notifyAdmin(booking)
            .thenReturn(booking))
        .flatMap(booking -> analyticsService.trackEvent("booking_created", booking)
            .thenReturn(booking));
}
```

### Layered Architecture
```
┌─────────────────────────────────────┐
│     Controllers (REST Endpoints)     │
├─────────────────────────────────────┤
│     Services (Business Logic)        │
├─────────────────────────────────────┤
│     Repositories (Data Access)       │
├─────────────────────────────────────┤
│     PostgreSQL Database              │
└─────────────────────────────────────┘
```

### Cross-Cutting Concerns
- **Security:** JWT authentication, role-based access control
- **Logging:** Structured logging with correlation IDs
- **Error Handling:** Global exception handlers
- **Validation:** Bean validation with custom validators
- **Monitoring:** Actuator endpoints, metrics

---

## Security Considerations

### Authentication & Authorization
1. **Admin Endpoints**
   - JWT-based authentication
   - Role: ADMIN required
   - Secure approval workflow

2. **Guest Endpoints**
   - Public booking request creation
   - Token-based booking access (view/modify own bookings)

3. **Webhook Endpoints**
   - Signature verification (PayU)
   - IP whitelisting where applicable

### Data Protection
- Encrypt sensitive guest information
- PCI compliance for payment data (handled by PayU)
- GDPR considerations for guest data
- Secure storage of API keys and credentials

### Rate Limiting
- Prevent abuse of public endpoints
- Protect against DDoS attacks
- Implement per-IP rate limits

---

## Analytics Strategy

### Event Types to Track
1. **User Journey Events**
   - `page_view` - Track page visits
   - `booking_form_started` - User begins booking form
   - `booking_form_completed` - User submits booking request
   - `booking_form_abandoned` - User leaves without completing

2. **Booking Lifecycle Events**
   - `booking_created` - New booking request
   - `booking_approved` - Admin approves
   - `booking_rejected` - Admin rejects
   - `payment_initiated` - Payment process started
   - `payment_completed` - Payment successful
   - `booking_confirmed` - Final confirmation

3. **System Events**
   - `calendar_synced` - iCal sync completed
   - `notification_sent` - WhatsApp notification sent
   - `api_error` - API errors for monitoring

### Analytics Queries
- Conversion funnel analysis
- Booking approval rates
- Average time to approval
- Popular booking dates
- Guest demographics
- Revenue tracking

---

## Development Guidelines

### Code Organization
```
src/main/java/com/casaaluna/backend/
├── config/              # Configuration classes
├── controller/          # REST controllers
├── service/             # Business logic
├── repository/          # Data access
├── model/               # Domain entities
├── dto/                 # Data transfer objects
├── exception/           # Custom exceptions
├── security/            # Security configuration
├── integration/         # External API clients
│   ├── whatsapp/
│   ├── payu/
│   └── ical/
└── util/                # Utility classes
```

### Testing Strategy
1. **Unit Tests**
   - Service layer logic
   - Utility functions
   - Validation logic

2. **Integration Tests**
   - Repository tests with test containers
   - API endpoint tests
   - External integration tests (mocked)

3. **End-to-End Tests**
   - Complete booking flow
   - Payment processing
   - Calendar sync scenarios

### Error Handling Patterns
```java
// Custom exception hierarchy
BookingException
├── BookingNotFoundException
├── BookingAlreadyApprovedException
├── InvalidBookingDatesException
└── PaymentFailedException

// Global exception handler
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BookingNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleNotFound(BookingNotFoundException ex) {
        // Return appropriate error response
    }
}
```

### Logging Standards
- Use SLF4J with Logback
- Include correlation IDs for request tracing
- Log levels: ERROR (system issues), WARN (business issues), INFO (key events), DEBUG (detailed flow)
- Structured logging for analytics

---

## Environment Configuration

### Application Profiles
1. **dev** - Local development
2. **test** - Testing environment
3. **staging** - Pre-production
4. **prod** - Production

### Configuration Properties
```yaml
# application.yml structure
spring:
  r2dbc:
    url: # PostgreSQL connection
  security:
    jwt:
      secret: # JWT signing key
      
integration:
  whatsapp:
    api-key: # WhatsApp API credentials
    base-url: # API endpoint
  payu:
    merchant-id: # PayU credentials
    api-key:
    base-url:
  calendar:
    sync-interval: # Sync frequency
    
analytics:
  enabled: true
  batch-size: 100
```

---

## Deployment Considerations

### Infrastructure
- **Container:** Docker
- **Orchestration:** Kubernetes (optional)
- **Database:** Managed PostgreSQL (AWS RDS, Google Cloud SQL, etc.)
- **Secrets Management:** Environment variables, AWS Secrets Manager, or similar

### CI/CD Pipeline
1. Code commit
2. Run tests
3. Build Docker image
4. Push to container registry
5. Deploy to environment
6. Run smoke tests

### Monitoring & Observability
- Application metrics (Spring Boot Actuator)
- Database performance monitoring
- API response times
- Error rates and alerting
- Log aggregation (ELK stack, CloudWatch, etc.)

---

## Future Enhancements

### Potential Features
1. **Multi-property Support**
   - Manage multiple vacation rental properties
   - Property-specific calendars and pricing

2. **Dynamic Pricing**
   - Seasonal pricing rules
   - Last-minute discounts
   - Demand-based pricing

3. **Guest Portal**
   - Self-service booking management
   - Digital check-in/check-out
   - House manual and local recommendations

4. **Advanced Analytics Dashboard**
   - Revenue reports
   - Occupancy rates
   - Guest insights
   - Booking trends

5. **Automated Messaging**
   - Pre-arrival instructions
   - Post-checkout reviews
   - Personalized recommendations

6. **Channel Manager Integration**
   - Unified calendar across all platforms
   - Automated pricing sync
   - Review aggregation

---

## Key Decision Log

### Technology Choices

**Why Spring WebFlux?**
- Non-blocking I/O for external API calls (WhatsApp, PayU, iCal)
- Better resource utilization
- Scalability for future growth
- Modern reactive programming paradigm

**Why PostgreSQL?**
- ACID compliance for booking transactions
- Rich data types (JSONB for analytics, daterange for bookings)
- Mature ecosystem and tooling
- Strong consistency guarantees

**Why Java 21?**
- Long-term support (LTS)
- Virtual threads for improved concurrency
- Modern language features
- Strong enterprise ecosystem

### Architectural Decisions

**Admin Approval Before Payment**
- Prevents spam bookings
- Allows property owner to vet guests
- Reduces payment processing fees for rejected bookings
- Provides quality control

**Analytics in PostgreSQL**
- Simplifies infrastructure (single database)
- Sufficient for initial scale
- Easy to query with SQL
- Can migrate to dedicated analytics DB later if needed

**iCal Sync vs. Direct API Integration**
- iCal is universally supported
- No need for platform-specific API integrations
- Simpler implementation
- Trade-off: Less real-time, more polling

---

## Contact & Resources

### Documentation
- Spring Boot: https://spring.io/projects/spring-boot
- Spring WebFlux: https://docs.spring.io/spring-framework/reference/web/webflux.html
- PostgreSQL: https://www.postgresql.org/docs/
- PayU API: [PayU Developer Documentation]
- Twilio WhatsApp: https://www.twilio.com/docs/whatsapp

### Development Team
- Repository: https://github.com/frtello-personal/casa-aluna-backend.git
- Project Lead: [To be filled]
- Backend Developer: [To be filled]

---

## Glossary

- **Booking Request:** Initial guest inquiry to reserve dates
- **Admin Approval:** Property owner's decision to accept/reject a booking
- **iCal:** Internet Calendaring format (RFC 5545) for calendar data exchange
- **WebFlux:** Spring's reactive web framework
- **Reactive Programming:** Programming paradigm focused on asynchronous data streams
- **JSONB:** PostgreSQL's binary JSON data type
- **PayU:** Payment gateway provider
- **Channel Manager:** System that synchronizes listings across multiple booking platforms

---

**Last Updated:** April 1, 2026  
**Version:** 1.0  
**Status:** Initial Memory Bank Creation
