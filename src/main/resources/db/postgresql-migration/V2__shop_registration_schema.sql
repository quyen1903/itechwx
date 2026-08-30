CREATE TABLE accounts (
    id UUID NOT NULL,
    account_type VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT pk_accounts PRIMARY KEY (id),
    CONSTRAINT ck_accounts_type
        CHECK (account_type IN ('USER', 'SHOP', 'ADMIN', 'SUPER_ADMIN')),
    CONSTRAINT ck_accounts_status
        CHECK (status IN ('PENDING_VERIFICATION', 'ACTIVE', 'SUSPENDED', 'LOCKED', 'DELETED')),
    CONSTRAINT ck_accounts_time CHECK (updated_at >= created_at)
);

CREATE TABLE account_authentications (
    id UUID NOT NULL,
    account_id UUID NOT NULL,
    email VARCHAR(320) NOT NULL,
    username VARCHAR(64),
    password_hash VARCHAR(512) NOT NULL,
    auth_method VARCHAR(32) NOT NULL,
    email_verified_at TIMESTAMPTZ,
    failed_login_attempts INTEGER NOT NULL DEFAULT 0,
    last_login_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT pk_account_authentications PRIMARY KEY (id),
    CONSTRAINT fk_account_authentications_account
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
    CONSTRAINT uq_account_authentications_account UNIQUE (account_id),
    CONSTRAINT uq_account_authentications_email UNIQUE (email),
    CONSTRAINT uq_account_authentications_username UNIQUE (username),
    CONSTRAINT ck_account_authentications_email_normalized
        CHECK (email = LOWER(BTRIM(email)) AND CHAR_LENGTH(email) BETWEEN 3 AND 320),
    CONSTRAINT ck_account_authentications_username
        CHECK (
            username IS NULL
            OR (
                username = LOWER(BTRIM(username))
                AND CHAR_LENGTH(username) BETWEEN 3 AND 64
                AND username ~ '^[a-z0-9._-]+$'
            )
        ),
    CONSTRAINT ck_account_authentications_hash
        CHECK (CHAR_LENGTH(password_hash) BETWEEN 20 AND 512),
    CONSTRAINT ck_account_authentications_method CHECK (auth_method = 'EMAIL_PASSWORD'),
    CONSTRAINT ck_account_authentications_attempts CHECK (failed_login_attempts >= 0),
    CONSTRAINT ck_account_authentications_time CHECK (updated_at >= created_at)
);

CREATE TABLE shops (
    id UUID NOT NULL,
    contact_name VARCHAR(50) NOT NULL,
    business_name VARCHAR(100) NOT NULL,
    business_type VARCHAR(100),
    tax_id VARCHAR(50),
    phone VARCHAR(32),
    address VARCHAR(200),
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT pk_shops PRIMARY KEY (id),
    CONSTRAINT ck_shops_contact_name
        CHECK (contact_name = BTRIM(contact_name) AND CHAR_LENGTH(contact_name) BETWEEN 2 AND 50),
    CONSTRAINT ck_shops_business_name
        CHECK (business_name = BTRIM(business_name) AND CHAR_LENGTH(business_name) BETWEEN 1 AND 100),
    CONSTRAINT ck_shops_business_type
        CHECK (
            business_type IS NULL
            OR (business_type = BTRIM(business_type) AND CHAR_LENGTH(business_type) BETWEEN 1 AND 100)
        ),
    CONSTRAINT ck_shops_tax_id
        CHECK (tax_id IS NULL OR (tax_id = BTRIM(tax_id) AND CHAR_LENGTH(tax_id) BETWEEN 1 AND 50)),
    CONSTRAINT ck_shops_phone
        CHECK (phone IS NULL OR (phone = BTRIM(phone) AND CHAR_LENGTH(phone) BETWEEN 1 AND 32)),
    CONSTRAINT ck_shops_address
        CHECK (address IS NULL OR (address = BTRIM(address) AND CHAR_LENGTH(address) BETWEEN 1 AND 200)),
    CONSTRAINT ck_shops_status
        CHECK (status IN ('PENDING_REVIEW', 'ACTIVE', 'SUSPENDED', 'CLOSED')),
    CONSTRAINT ck_shops_time CHECK (updated_at >= created_at)
);

CREATE TABLE shop_memberships (
    shop_id UUID NOT NULL,
    account_id UUID NOT NULL,
    role VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT pk_shop_memberships PRIMARY KEY (shop_id, account_id),
    CONSTRAINT fk_shop_memberships_shop
        FOREIGN KEY (shop_id) REFERENCES shops(id) ON DELETE CASCADE,
    CONSTRAINT fk_shop_memberships_account
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE RESTRICT,
    CONSTRAINT ck_shop_memberships_role CHECK (role IN ('OWNER', 'ADMIN', 'STAFF')),
    CONSTRAINT ck_shop_memberships_status CHECK (status IN ('ACTIVE', 'SUSPENDED', 'REVOKED')),
    CONSTRAINT ck_shop_memberships_time CHECK (updated_at >= created_at)
);

CREATE INDEX ix_shop_memberships_active_account
    ON shop_memberships (account_id, shop_id)
    WHERE status = 'ACTIVE';

CREATE UNIQUE INDEX uq_shop_memberships_active_owner
    ON shop_memberships (shop_id)
    WHERE role = 'OWNER' AND status = 'ACTIVE';

CREATE TABLE shop_settings (
    shop_id UUID NOT NULL,
    currency VARCHAR(3) NOT NULL,
    timezone VARCHAR(64) NOT NULL,
    language VARCHAR(35) NOT NULL,
    theme VARCHAR(32) NOT NULL,
    email_notifications_enabled BOOLEAN NOT NULL,
    sms_notifications_enabled BOOLEAN NOT NULL,
    push_notifications_enabled BOOLEAN NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT pk_shop_settings PRIMARY KEY (shop_id),
    CONSTRAINT fk_shop_settings_shop
        FOREIGN KEY (shop_id) REFERENCES shops(id) ON DELETE CASCADE,
    CONSTRAINT ck_shop_settings_currency
        CHECK (currency = UPPER(BTRIM(currency)) AND currency ~ '^[A-Z]{3}$'),
    CONSTRAINT ck_shop_settings_timezone
        CHECK (timezone = BTRIM(timezone) AND CHAR_LENGTH(timezone) BETWEEN 1 AND 64),
    CONSTRAINT ck_shop_settings_language
        CHECK (language = BTRIM(language) AND CHAR_LENGTH(language) BETWEEN 2 AND 35),
    CONSTRAINT ck_shop_settings_theme
        CHECK (theme = BTRIM(theme) AND CHAR_LENGTH(theme) BETWEEN 1 AND 32),
    CONSTRAINT ck_shop_settings_time CHECK (updated_at >= created_at)
);
