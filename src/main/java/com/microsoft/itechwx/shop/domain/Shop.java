package com.microsoft.itechwx.shop.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import com.microsoft.itechwx.shop.domain.enums.ShopStatus;

public final class Shop {

    private final UUID id;
    private final String contactName;
    private final String businessName;
    private final String businessType;
    private final String taxId;
    private final String phone;
    private final String address;
    private final ShopStatus status;
    private final ShopMembership ownerMembership;
    private final ShopSettings settings;
    private final Instant createdAt;
    private final Instant updatedAt;

    private Shop(
        UUID id,
        String contactName,
        String businessName,
        String businessType,
        String taxId,
        String phone,
        String address,
        ShopStatus status,
        ShopMembership ownerMembership,
        ShopSettings settings,
        Instant createdAt,
        Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.contactName = requireText(contactName, "contactName", 2, 50);
        this.businessName = requireText(businessName, "businessName", 1, 100);
        this.businessType = optionalText(businessType, "businessType", 100);
        this.taxId = optionalText(taxId, "taxId", 50);
        this.phone = optionalText(phone, "phone", 32);
        this.address = optionalText(address, "address", 200);
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.ownerMembership = Objects.requireNonNull(ownerMembership, "ownerMembership must not be null");
        this.settings = Objects.requireNonNull(settings, "settings must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");

        if (!id.equals(ownerMembership.shopId()) || !id.equals(settings.shopId())) {
            throw new IllegalArgumentException("Shop children must belong to the shop");
        }
    }

    public static Shop register(
        UUID shopId,
        UUID ownerAccountId,
        String contactName,
        String businessName,
        String businessType,
        String taxId,
        String phone,
        String address,
        String currency,
        String timezone,
        String language,
        String theme,
        boolean emailNotificationsEnabled,
        boolean smsNotificationsEnabled,
        boolean pushNotificationsEnabled,
        Instant now
    ) {
        ShopMembership ownerMembership = ShopMembership.initialOwner(shopId, ownerAccountId, now);
        ShopSettings settings = ShopSettings.create(
            shopId,
            currency,
            timezone,
            language,
            theme,
            emailNotificationsEnabled,
            smsNotificationsEnabled,
            pushNotificationsEnabled,
            now
        );

        return new Shop(
            shopId,
            contactName,
            businessName,
            businessType,
            taxId,
            phone,
            address,
            ShopStatus.PENDING_REVIEW,
            ownerMembership,
            settings,
            now,
            now
        );
    }

    private static String requireText(
        String value,
        String field,
        int minimumLength,
        int maximumLength
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        String normalized = value.strip();
        if (normalized.length() < minimumLength || normalized.length() > maximumLength) {
            throw new IllegalArgumentException(field + " has an invalid length");
        }
        return normalized;
    }

    private static String optionalText(String value, String field, int maximumLength) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.strip();
        if (normalized.length() > maximumLength) {
            throw new IllegalArgumentException(field + " exceeds its maximum length");
        }
        return normalized;
    }

    public UUID id() {
        return id;
    }

    public String contactName() {
        return contactName;
    }

    public String businessName() {
        return businessName;
    }

    public String businessType() {
        return businessType;
    }

    public String taxId() {
        return taxId;
    }

    public String phone() {
        return phone;
    }

    public String address() {
        return address;
    }

    public ShopStatus status() {
        return status;
    }

    public ShopMembership ownerMembership() {
        return ownerMembership;
    }

    public ShopSettings settings() {
        return settings;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
