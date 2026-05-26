CREATE TABLE IF NOT EXISTS accounts (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100),
    balance NUMERIC(15,2),
    created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS transactions (
    id BIGSERIAL PRIMARY KEY,
    from_account_id BIGINT,
    to_account_id BIGINT,
    amount NUMERIC(15,2),
    type VARCHAR(20),
    created_at TIMESTAMP
);
