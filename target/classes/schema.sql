CREATE TABLE IF NOT EXISTS operation (
    id BIGSERIAL PRIMARY KEY,
    date DATE NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    description VARCHAR(50) NOT NULL,
    kind SMALLINT NOT NULL,
    account SMALLINT NOT NULL,
    storned BOOLEAN
);
