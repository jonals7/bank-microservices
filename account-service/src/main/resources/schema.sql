-- ============================================
-- account-service schema
-- Account and Movement tables
-- ============================================

CREATE TABLE IF NOT EXISTS Account (
    id              BIGSERIAL PRIMARY KEY,
    account_number  VARCHAR(20)    NOT NULL UNIQUE,
    account_type    VARCHAR(20)    NOT NULL,
    initial_balance NUMERIC(15,2)  NOT NULL DEFAULT 0,
    current_balance NUMERIC(15,2)  NOT NULL DEFAULT 0,
    status          BOOLEAN        NOT NULL DEFAULT TRUE,
    customer_id     BIGINT         NOT NULL
);

CREATE TABLE IF NOT EXISTS Movement (
    id              BIGSERIAL PRIMARY KEY,
    date            TIMESTAMP      NOT NULL DEFAULT NOW(),
    movement_type   VARCHAR(10)    NOT NULL,
    amount          NUMERIC(15,2)  NOT NULL,
    balance         NUMERIC(15,2)  NOT NULL,
    account_id      BIGINT         NOT NULL REFERENCES Account(id)
);