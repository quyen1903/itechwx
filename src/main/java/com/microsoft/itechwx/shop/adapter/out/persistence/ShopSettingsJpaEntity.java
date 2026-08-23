package com.microsoft.itechwx.shop.adapter.out.persistence;

import java.time.Instant;
import java.util.UUID;

import com.microsoft.itechwx.shop.domain.ShopSettings;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "shop_settings")
public class ShopSettingsJpaEntity {

    @Id
    @Column(name = "shop_id", nullable = false, updatable = false)
    private UUID shopId;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "timezone", nullable = false, length = 64)
    private String timezone;

    @Column(name = "language", nullable = false, length = 35)
    private String language;

    @Column(name = "theme", nullable = false, length = 32)
    private String theme;

    @Column(name = "email_notifications_enabled", nullable = false)
    private boolean emailNotificationsEnabled;

    @Column(name = "sms_notifications_enabled", nullable = false)
    private boolean smsNotificationsEnabled;

    @Column(name = "push_notifications_enabled", nullable = false)
    private boolean pushNotificationsEnabled;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected ShopSettingsJpaEntity() {}

    private ShopSettingsJpaEntity(ShopSettings settings) {
        this.shopId = settings.shopId();
        this.currency = settings.currency();
        this.timezone = settings.timezone();
        this.language = settings.language();
        this.theme = settings.theme();
        this.emailNotificationsEnabled = settings.emailNotificationsEnabled();
        this.smsNotificationsEnabled = settings.smsNotificationsEnabled();
        this.pushNotificationsEnabled = settings.pushNotificationsEnabled();
        this.createdAt = settings.createdAt();
        this.updatedAt = settings.updatedAt();
    }

    static ShopSettingsJpaEntity from(ShopSettings settings) {
        return new ShopSettingsJpaEntity(settings);
    }
}
