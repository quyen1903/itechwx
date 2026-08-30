CREATE TABLE accounts (
    id RAW(16) NOT NULL,
    account_type VARCHAR2(32 CHAR) NOT NULL,
    status VARCHAR2(32 CHAR) NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_accounts PRIMARY KEY (id),
    CONSTRAINT ck_accounts_type
        CHECK (account_type IN ('USER', 'SHOP', 'ADMIN', 'SUPER_ADMIN')),
    CONSTRAINT ck_accounts_status
        CHECK (status IN ('PENDING_VERIFICATION', 'ACTIVE', 'SUSPENDED', 'LOCKED', 'DELETED')),
    CONSTRAINT ck_accounts_time CHECK (updated_at >= created_at)
);

CREATE TABLE account_authentications (
    id RAW(16) NOT NULL,
    account_id RAW(16) NOT NULL,
    email VARCHAR2(320 CHAR) NOT NULL,
    username VARCHAR2(64 CHAR),
    password_hash VARCHAR2(512 CHAR) NOT NULL,
    auth_method VARCHAR2(32 CHAR) NOT NULL,
    email_verified_at TIMESTAMP(6) WITH TIME ZONE,
    failed_login_attempts NUMBER(10, 0) DEFAULT 0 NOT NULL,
    last_login_at TIMESTAMP(6) WITH TIME ZONE,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_account_authentications PRIMARY KEY (id),
    CONSTRAINT fk_account_authentications_account
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
    CONSTRAINT uq_account_authentications_account UNIQUE (account_id),
    CONSTRAINT uq_account_authentications_email UNIQUE (email),
    CONSTRAINT uq_account_authentications_username UNIQUE (username),
    CONSTRAINT ck_account_authentications_email_normalized
        CHECK (email = LOWER(TRIM(email)) AND LENGTH(email) BETWEEN 3 AND 320),
    CONSTRAINT ck_account_authentications_username
        CHECK (
            username IS NULL
            OR (
                username = LOWER(TRIM(username))
                AND LENGTH(username) BETWEEN 3 AND 64
                AND REGEXP_LIKE(username, '^[a-z0-9._-]+$')
            )
        ),
    CONSTRAINT ck_account_authentications_hash
        CHECK (LENGTH(password_hash) BETWEEN 20 AND 512),
    CONSTRAINT ck_account_authentications_method CHECK (auth_method = 'EMAIL_PASSWORD'),
    CONSTRAINT ck_account_authentications_attempts CHECK (failed_login_attempts >= 0),
    CONSTRAINT ck_account_authentications_time CHECK (updated_at >= created_at)
);

CREATE TABLE shops (
    id RAW(16) NOT NULL,
    contact_name VARCHAR2(50 CHAR) NOT NULL,
    business_name VARCHAR2(100 CHAR) NOT NULL,
    business_type VARCHAR2(100 CHAR),
    tax_id VARCHAR2(50 CHAR),
    phone VARCHAR2(32 CHAR),
    address VARCHAR2(200 CHAR),
    status VARCHAR2(32 CHAR) NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_shops PRIMARY KEY (id),
    CONSTRAINT ck_shops_contact_name
        CHECK (contact_name = TRIM(contact_name) AND LENGTH(contact_name) BETWEEN 2 AND 50),
    CONSTRAINT ck_shops_business_name
        CHECK (business_name = TRIM(business_name) AND LENGTH(business_name) BETWEEN 1 AND 100),
    CONSTRAINT ck_shops_business_type
        CHECK (
            business_type IS NULL
            OR (business_type = TRIM(business_type) AND LENGTH(business_type) BETWEEN 1 AND 100)
        ),
    CONSTRAINT ck_shops_tax_id
        CHECK (tax_id IS NULL OR (tax_id = TRIM(tax_id) AND LENGTH(tax_id) BETWEEN 1 AND 50)),
    CONSTRAINT ck_shops_phone
        CHECK (phone IS NULL OR (phone = TRIM(phone) AND LENGTH(phone) BETWEEN 1 AND 32)),
    CONSTRAINT ck_shops_address
        CHECK (address IS NULL OR (address = TRIM(address) AND LENGTH(address) BETWEEN 1 AND 200)),
    CONSTRAINT ck_shops_status
        CHECK (status IN ('PENDING_REVIEW', 'ACTIVE', 'SUSPENDED', 'CLOSED')),
    CONSTRAINT ck_shops_time CHECK (updated_at >= created_at)
);

CREATE TABLE shop_memberships (
    shop_id RAW(16) NOT NULL,
    account_id RAW(16) NOT NULL,
    role VARCHAR2(32 CHAR) NOT NULL,
    status VARCHAR2(32 CHAR) NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_shop_memberships PRIMARY KEY (shop_id, account_id),
    CONSTRAINT fk_shop_memberships_shop
        FOREIGN KEY (shop_id) REFERENCES shops(id) ON DELETE CASCADE,
    CONSTRAINT fk_shop_memberships_account
        FOREIGN KEY (account_id) REFERENCES accounts(id),
    CONSTRAINT ck_shop_memberships_role CHECK (role IN ('OWNER', 'ADMIN', 'STAFF')),
    CONSTRAINT ck_shop_memberships_status CHECK (status IN ('ACTIVE', 'SUSPENDED', 'REVOKED')),
    CONSTRAINT ck_shop_memberships_time CHECK (updated_at >= created_at)
);

CREATE INDEX ix_shop_memberships_active_account
    ON shop_memberships (
        CASE WHEN status = 'ACTIVE' THEN account_id END,
        CASE WHEN status = 'ACTIVE' THEN shop_id END
    );

CREATE UNIQUE INDEX uq_shop_memberships_active_owner
    ON shop_memberships (
        CASE WHEN role = 'OWNER' AND status = 'ACTIVE' THEN shop_id END
    );

CREATE TABLE shop_settings (
    shop_id RAW(16) NOT NULL,
    currency VARCHAR2(3 CHAR) NOT NULL,
    timezone VARCHAR2(64 CHAR) NOT NULL,
    language VARCHAR2(35 CHAR) NOT NULL,
    theme VARCHAR2(32 CHAR) NOT NULL,
    email_notifications_enabled BOOLEAN NOT NULL,
    sms_notifications_enabled BOOLEAN NOT NULL,
    push_notifications_enabled BOOLEAN NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_shop_settings PRIMARY KEY (shop_id),
    CONSTRAINT fk_shop_settings_shop
        FOREIGN KEY (shop_id) REFERENCES shops(id) ON DELETE CASCADE,
    CONSTRAINT ck_shop_settings_currency
        CHECK (currency = UPPER(TRIM(currency)) AND REGEXP_LIKE(currency, '^[A-Z]{3}$')),
    CONSTRAINT ck_shop_settings_timezone
        CHECK (timezone = TRIM(timezone) AND LENGTH(timezone) BETWEEN 1 AND 64),
    CONSTRAINT ck_shop_settings_language
        CHECK (language = TRIM(language) AND LENGTH(language) BETWEEN 2 AND 35),
    CONSTRAINT ck_shop_settings_theme
        CHECK (theme = TRIM(theme) AND LENGTH(theme) BETWEEN 1 AND 32),
    CONSTRAINT ck_shop_settings_time CHECK (updated_at >= created_at)
);
