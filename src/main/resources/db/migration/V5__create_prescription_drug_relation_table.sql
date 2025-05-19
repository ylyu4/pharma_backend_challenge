CREATE TABLE prescription_drug (
    prescription_id BIGINT NOT NULL REFERENCES prescription(id),
    drug_id BIGINT NOT NULL REFERENCES drug(id),
    quantity INT NOT NULL,
    PRIMARY KEY (prescription_id, drug_id)
);