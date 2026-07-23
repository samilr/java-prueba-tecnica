CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE users (
    id                    UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    username              VARCHAR(50)  NOT NULL UNIQUE,
    email                 VARCHAR(150) NOT NULL UNIQUE,
    password              VARCHAR(100) NOT NULL,
    role                  VARCHAR(20)  NOT NULL DEFAULT 'USER',
    enabled               BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at            TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at            TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT chk_users_role CHECK (role IN ('ADMIN', 'USER'))
);

CREATE TABLE customers (
    id                    UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    name                  VARCHAR(100) NOT NULL,
    email                 VARCHAR(150) NOT NULL UNIQUE,
    phone                 VARCHAR(30),
    identification_number VARCHAR(20)  NOT NULL UNIQUE,
    status                VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    version               BIGINT       NOT NULL DEFAULT 0,
    created_at            TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at            TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT chk_customers_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

CREATE INDEX idx_customers_email ON customers (email);
CREATE INDEX idx_customers_identification_number ON customers (identification_number);
CREATE INDEX idx_customers_status ON customers (status);

CREATE TABLE addresses (
    id           UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id  UUID         NOT NULL,
    street       VARCHAR(200) NOT NULL,
    city         VARCHAR(100) NOT NULL,
    state        VARCHAR(100) NOT NULL,
    country      VARCHAR(100) NOT NULL DEFAULT 'República Dominicana',
    postal_code  VARCHAR(20),
    type         VARCHAR(20)  NOT NULL DEFAULT 'HOME',
    is_primary   BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT fk_addresses_customer FOREIGN KEY (customer_id)
        REFERENCES customers (id) ON DELETE CASCADE,
    CONSTRAINT chk_addresses_type CHECK (type IN ('HOME', 'WORK', 'BILLING', 'OTHER'))
);

CREATE INDEX idx_addresses_customer_id ON addresses (customer_id);

CREATE UNIQUE INDEX uq_addresses_one_primary_per_customer
    ON addresses (customer_id) WHERE is_primary = TRUE;
