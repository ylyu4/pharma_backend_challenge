CREATE TABLE drug (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    manufacturer VARCHAR(255) NOT NULL,
    batch_number VARCHAR(255)NOT NULL,
    expiry_date DATE NOT NULL,
    stock INTEGER NOT NULL
);