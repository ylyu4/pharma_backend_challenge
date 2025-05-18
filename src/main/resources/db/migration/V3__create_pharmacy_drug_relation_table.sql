CREATE TABLE pharmacy_drug_info (
    pharmacy_id BIGINT REFERENCES pharmacy(id),
    drug_id BIGINT REFERENCES drug(id),
    max_allocation_amount INT NOT NULL,
    dispensing_amount INT NOT NULL,
    PRIMARY KEY (pharmacy_id, drug_id)
);