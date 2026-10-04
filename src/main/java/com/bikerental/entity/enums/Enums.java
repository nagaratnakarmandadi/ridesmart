package com.bikerental.entity.enums;

public class Enums {
    public enum RoleName {
        ROLE_CUSTOMER,
        ROLE_ADMIN
    }

    public enum VerificationStatus {
        NOT_VERIFIED,
        PENDING,
        VERIFIED,
        REJECTED
    }

    public enum DocumentType {
        AADHAAR,
        DRIVING_LICENSE,
        SELFIE,
        ADDRESS_PROOF
    }

    public enum DocumentStatus {
        NOT_SUBMITTED,
        SUBMITTED,
        UNDER_REVIEW,
        VERIFIED,
        REJECTED,
        EXPIRED
    }

    public enum BikeStatus {
        AVAILABLE,
        BOOKED,
        INSPECTION,
        RENTED,
        RETURN_PENDING_INSPECTION,
        MAINTENANCE,
        BLOCKED
    }

    public enum BookingStatus {
        REQUESTED,
        PENDING_DOCUMENTS,
        DOCUMENTS_SUBMITTED,
        UNDER_VERIFICATION,
        APPROVED,
        REJECTED,
        PAYMENT_PENDING,
        CONFIRMED,
        READY_FOR_PICKUP,
        ACTIVE,
        RETURN_PENDING,
        COMPLETED,
        CANCELLED,
        EXPIRED
    }

    public enum RentalStatus {
        READY_FOR_PICKUP,
        ACTIVE,
        RETURN_PENDING,
        INSPECTION,
        BILLING,
        PAYMENT_DUE,
        COMPLETED
    }

    public enum InspectionType {
        PRE_RENTAL,
        POST_RENTAL
    }

    public enum AgreementStatus {
        DRAFT,
        PENDING_SIGNATURE,
        SIGNED,
        ACTIVE,
        COMPLETED
    }

    public enum PaymentType {
        BOOKING_PAYMENT,
        FINAL_BALANCE,
        EXTRA_CHARGE,
        SECURITY_DEPOSIT,
        REFUND
    }

    public enum PaymentMethod {
        CASH,
        UPI
    }

    public enum PaymentStatus {
        PENDING,
        SUBMITTED,
        VERIFIED,
        PAID,
        FAILED,
        REJECTED,
        REFUNDED,
        CASH_PENDING,
        BALANCE_PENDING
    }

    public enum DamageStatus {
        REPORTED,
        UNDER_REVIEW,
        CONFIRMED,
        DISPUTED,
        RESOLVED
    }

    public enum FuelLevel {
        EMPTY,
        QUARTER,
        HALF,
        THREE_QUARTERS,
        FULL
    }
}
