CREATE TABLE audit_log_drug_info (
    audit_log_id BIGINT NOT NULL REFERENCES audit_log(id),
    drug_id BIGINT NOT NULL REFERENCES drug(id),
    quantity_requested INT NOT NULL,
    quantity_dispensed INT NOT NULL,
    PRIMARY KEY (audit_log_id, drug_id)
);