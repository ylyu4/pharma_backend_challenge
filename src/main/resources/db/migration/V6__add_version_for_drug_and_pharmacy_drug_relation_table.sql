ALTER TABLE drug ADD COLUMN version INT DEFAULT 0 NOT NULL;
ALTER TABLE pharmacy_drug_info ADD COLUMN version INT DEFAULT 0 NOT NULL;
