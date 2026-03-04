-- ============================================
-- customer-service schema
-- Person and Customer tables
-- ============================================

CREATE TABLE IF NOT EXISTS Person (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    gender      VARCHAR(10)  NOT NULL,
    age         INT          NOT NULL,
    identification VARCHAR(20) NOT NULL UNIQUE,
    address     VARCHAR(200) NOT NULL,
    phone       VARCHAR(20)  NOT NULL
);

CREATE TABLE IF NOT EXISTS Customer (
    id          BIGINT PRIMARY KEY REFERENCES person(id),
    password    VARCHAR(100) NOT NULL,
    status      BOOLEAN NOT NULL DEFAULT TRUE
);
