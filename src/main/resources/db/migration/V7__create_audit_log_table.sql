CREATE TABLE audit_log (
    id BIGSERIAL PRIMARY KEY,
    prescription_id BIGINT REFERENCES prescription(id),
    patient_id BIGINT NOT NULL,
    pharmacy_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);