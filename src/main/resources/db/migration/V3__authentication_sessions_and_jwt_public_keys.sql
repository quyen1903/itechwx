CREATE TABLE account_profiles (
    account_id RAW(16) NOT NULL,
    name VARCHAR2(50 CHAR) NOT NULL,
    avatar VARCHAR2(2048 CHAR),
    phone VARCHAR2(32 CHAR),
    address VARCHAR2(200 CHAR),
    timezone VARCHAR2(64 CHAR),
    language VARCHAR2(35 CHAR),
    is_active BOOLEAN NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_account_profiles PRIMARY KEY (account_id),
    CONSTRAINT fk_account_profiles_account
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
    CONSTRAINT ck_account_profiles_time CHECK (updated_at >= created_at)
);

CREATE TABLE device_sessions (
    id RAW(16) NOT NULL,
    account_authentication_id RAW(16) NOT NULL,
    device_id RAW(16) NOT NULL,
    device_name VARCHAR2(200 CHAR),
    last_login_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    is_active BOOLEAN NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_device_sessions PRIMARY KEY (id),
    CONSTRAINT fk_device_sessions_authentication
        FOREIGN KEY (account_authentication_id)
        REFERENCES account_authentications(id) ON DELETE CASCADE,
    CONSTRAINT ck_device_sessions_time CHECK (updated_at >= created_at)
);

CREATE INDEX ix_device_sessions_authentication
    ON device_sessions (account_authentication_id, is_active);

CREATE TABLE refresh_tokens (
    id RAW(16) NOT NULL,
    device_session_id RAW(16) NOT NULL,
    token_hash VARCHAR2(64 CHAR) NOT NULL,
    expires_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    revoked_at TIMESTAMP(6) WITH TIME ZONE,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_refresh_tokens PRIMARY KEY (id),
    CONSTRAINT fk_refresh_tokens_session
        FOREIGN KEY (device_session_id)
        REFERENCES device_sessions(id) ON DELETE CASCADE,
    CONSTRAINT uq_refresh_tokens_hash UNIQUE (token_hash),
    CONSTRAINT ck_refresh_tokens_time CHECK (expires_at > created_at),
    CONSTRAINT ck_refresh_tokens_revoked
        CHECK (revoked_at IS NULL OR revoked_at >= created_at)
);

CREATE INDEX ix_refresh_tokens_active_session
    ON refresh_tokens (device_session_id, revoked_at, expires_at);

CREATE TABLE jwt_public_keys (
    kid VARCHAR2(36 CHAR) NOT NULL,
    device_session_id RAW(16) NOT NULL,
    algorithm VARCHAR2(16 CHAR) NOT NULL,
    public_key_der BLOB NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    expires_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_jwt_public_keys PRIMARY KEY (kid),
    CONSTRAINT fk_jwt_public_keys_session
        FOREIGN KEY (device_session_id)
        REFERENCES device_sessions(id) ON DELETE CASCADE,
    CONSTRAINT ck_jwt_public_keys_algorithm CHECK (algorithm = 'RS256'),
    CONSTRAINT ck_jwt_public_keys_time CHECK (expires_at > created_at)
);

CREATE INDEX ix_jwt_public_keys_session
    ON jwt_public_keys (device_session_id);

CREATE INDEX ix_jwt_public_keys_expiry
    ON jwt_public_keys (expires_at);
