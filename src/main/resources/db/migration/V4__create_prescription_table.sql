CREATE TABLE prescription (
    id BIGSERIAL PRIMARY KEY,
    pharmacy_id BIGINT NOT NULL REFERENCES pharmacy(id),
    patient_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);