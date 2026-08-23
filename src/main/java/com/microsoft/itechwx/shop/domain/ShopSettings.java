package com.microsoft.itechwx.shop.domain;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public final class ShopSettings {

    private final UUID shopId;
    private final String currency;
    private final String timezone;
    private final String language;
    private final String theme;
    private final boolean emailNotificationsEnabled;
    private final boolean smsNotificationsEnabled;
    private final boolean pushNotificationsEnabled;
    private final Instant createdAt;
    private final Instant updatedAt;

    private ShopSettings(
        UUID shopId,
        String currency,
        String timezone,
        String language,
        String theme,
        boolean emailNotificationsEnabled,
        boolean smsNotificationsEnabled,
        boolean pushNotificationsEnabled,
        Instant createdAt,
        Instant updatedAt
    ) {
        this.shopId = Objects.requireNonNull(shopId, "shopId must not be null");
        this.currency = normalizeCurrency(currency);
        this.timezone = normalizeTimezone(timezone);
        this.language = normalizeLanguage(language);
        this.theme = requireText(theme, "theme", 32).toLowerCase(Locale.ROOT);
        this.emailNotificationsEnabled = emailNotificationsEnabled;
        this.smsNotificationsEnabled = smsNotificationsEnabled;
        this.pushNotificationsEnabled = pushNotificationsEnabled;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }

    static ShopSettings create(
        UUID shopId,
        String currency,
        String timezone,
        String language,
        String theme,
        boolean emailNotificationsEnabled,
        boolean smsNotificationsEnabled,
        boolean pushNotificationsEnabled,
        Instant now
    ) {
        return new ShopSettings(
            shopId,
            currency,
            timezone,
            language,
            theme,
            emailNotificationsEnabled,
            smsNotificationsEnabled,
            pushNotificationsEnabled,
            now,
            now
        );
    }

    private static String normalizeCurrency(String value) {
        String normalized = requireText(value, "currency", 3).toUpperCase(Locale.ROOT);
        if (normalized.length() != 3) {
            throw new IllegalArgumentException("currency must be a three-letter ISO code");
        }
        try {
            Currency.getInstance(normalized);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("currency must be a supported ISO code", exception);
        }
        return normalized;
    }

    private static String normalizeTimezone(String value) {
        String normalized = requireText(value, "timezone", 64);
        try {
            return ZoneId.of(normalized).getId();
        } catch (RuntimeException exception) {
            throw new IllegalArgumentException("timezone must be a valid zone ID", exception);
        }
    }

    private static String normalizeLanguage(String value) {
        String normalized = requireText(value, "language", 35);
        Locale locale = Locale.forLanguageTag(normalized);
        if (locale.getLanguage().isBlank()) {
            throw new IllegalArgumentException("language must be a valid language tag");
        }
        return locale.toLanguageTag();
    }

    private static String requireText(String value, String field, int maximumLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        String normalized = value.strip();
        if (normalized.length() > maximumLength) {
            throw new IllegalArgumentException(field + " exceeds its maximum length");
        }
        return normalized;
    }

    public UUID shopId() {
        return shopId;
    }

    public String currency() {
        return currency;
    }

    public String timezone() {
        return timezone;
    }

    public String language() {
        return language;
    }

    public String theme() {
        return theme;
    }

    public boolean emailNotificationsEnabled() {
        return emailNotificationsEnabled;
    }

    public boolean smsNotificationsEnabled() {
        return smsNotificationsEnabled;
    }

    public boolean pushNotificationsEnabled() {
        return pushNotificationsEnabled;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
