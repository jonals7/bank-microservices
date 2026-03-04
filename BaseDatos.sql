-- ============================================================
-- BaseDatos.sql
-- Bank Microservices - Full Database Script
-- customer-service  → customerdb
-- account-service   → accountdb
-- ============================================================

-- ============================================================
-- DATABASE: customerdb (customer-service)
-- ============================================================

CREATE TABLE IF NOT EXISTS Person (
    id              BIGSERIAL       PRIMARY KEY,
    name            VARCHAR(100)    NOT NULL,
    gender          VARCHAR(10)     NOT NULL,
    age             INT             NOT NULL,
    identification  VARCHAR(20)     NOT NULL UNIQUE,
    address         VARCHAR(200)    NOT NULL,
    phone           VARCHAR(20)     NOT NULL
);

CREATE TABLE IF NOT EXISTS Consumer (
    id          BIGINT          PRIMARY KEY REFERENCES Person(id),
    password    VARCHAR(100)    NOT NULL,
    status      BOOLEAN         NOT NULL DEFAULT TRUE
);

-- ============================================================
-- Sample data - customerdb (Use Case 1)
-- ============================================================

INSERT INTO Person (name, gender, age, identification, address, phone) VALUES
    ('Jose Lema',         'Male',   30, '1001', 'Otavalo sn y principal',    '098254785'),
    ('Marianela Montalvo','Female', 28, '1002', 'Amazonas y NNUU',           '097548965'),
    ('Juan Osorio',       'Male',   35, '1003', '13 junio y Equinoccial',    '098874587');

INSERT INTO Consumer (id, password, status) VALUES
    (1, '1234', TRUE),
    (2, '5678', TRUE),
    (3, '1245', TRUE);

-- ============================================================
-- DATABASE: accountdb (account-service)
-- ============================================================

CREATE TABLE IF NOT EXISTS Account (
    id              BIGSERIAL       PRIMARY KEY,
    account_number  VARCHAR(20)     NOT NULL UNIQUE,
    account_type    VARCHAR(20)     NOT NULL,
    initial_balance NUMERIC(15,2)   NOT NULL DEFAULT 0,
    current_balance NUMERIC(15,2)   NOT NULL DEFAULT 0,
    status          BOOLEAN         NOT NULL DEFAULT TRUE,
    customer_id     BIGINT          NOT NULL
);

CREATE TABLE IF NOT EXISTS Movement (
    id              BIGSERIAL       PRIMARY KEY,
    date            TIMESTAMP       NOT NULL DEFAULT NOW(),
    movement_type   VARCHAR(10)     NOT NULL,
    amount          NUMERIC(15,2)   NOT NULL,
    balance         NUMERIC(15,2)   NOT NULL,
    account_id      BIGINT          NOT NULL REFERENCES Account(id)
);

-- ============================================================
-- Sample data - accountdb (Use Cases 2 & 3)
-- ============================================================

INSERT INTO Account (account_number, account_type, initial_balance, current_balance, status, customer_id) VALUES
    ('478758', 'Ahorro',    2000.00, 2000.00, TRUE, 1),
    ('225487', 'Corriente',  100.00,  100.00, TRUE, 2),
    ('495878', 'Ahorro',       0.00,    0.00, TRUE, 3),
    ('496825', 'Ahorro',     540.00,  540.00, TRUE, 2),
    ('585545', 'Corriente', 1000.00, 1000.00, TRUE, 1);

-- ============================================================
-- Sample movements (Use Case 4)
-- ============================================================

-- Account 478758 (Jose Lema) - Retiro de 575
INSERT INTO Movement (date, movement_type, amount, balance, account_id) VALUES
    ('2022-02-10 10:00:00', 'DEBIT', 575.00, 1425.00, 1);

UPDATE Account SET current_balance = 1425.00 WHERE account_number = '478758';

-- Account 225487 (Marianela) - Deposito de 600
INSERT INTO Movement (date, movement_type, amount, balance, account_id) VALUES
    ('2022-02-10 10:05:00', 'CREDIT', 600.00, 700.00, 2);

UPDATE Account SET current_balance = 700.00 WHERE account_number = '225487';

-- Account 495878 (Juan Osorio) - Deposito de 150
INSERT INTO Movement (date, movement_type, amount, balance, account_id) VALUES
    ('2022-02-10 10:10:00', 'CREDIT', 150.00, 150.00, 3);

UPDATE Account SET current_balance = 150.00 WHERE account_number = '495878';

-- Account 496825 (Marianela) - Retiro de 540
INSERT INTO Movement (date, movement_type, amount, balance, account_id) VALUES
    ('2022-02-08 10:00:00', 'DEBIT', 540.00, 0.00, 4);

UPDATE Account SET current_balance = 0.00 WHERE account_number = '496825';
