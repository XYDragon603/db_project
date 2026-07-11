BEGIN;

CREATE TABLE IF NOT EXISTS countries (
    country_code VARCHAR(2) PRIMARY KEY,
    country_name VARCHAR(100) NOT NULL UNIQUE,
    regulatory_authority VARCHAR(150) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS medication_catalog (
    catalog_id BIGSERIAL PRIMARY KEY,
    country_code VARCHAR(2) NOT NULL REFERENCES countries(country_code),
    generic_name VARCHAR(150) NOT NULL,
    dosage_form VARCHAR(50) NOT NULL,
    strength VARCHAR(100) NOT NULL,
    prescription_required BOOLEAN NOT NULL DEFAULT FALSE,
    catalog_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
        CHECK (catalog_status IN ('ACTIVE', 'INACTIVE')),
    CONSTRAINT uq_catalog_local_product
        UNIQUE (country_code, generic_name, dosage_form, strength)
);

CREATE TABLE IF NOT EXISTS medication_brands (
    brand_id BIGSERIAL PRIMARY KEY,
    catalog_id BIGINT NOT NULL REFERENCES medication_catalog(catalog_id),
    brand_name VARCHAR(150) NOT NULL,
    manufacturer VARCHAR(150),
    local_registration_code VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_catalog_brand UNIQUE (catalog_id, brand_name)
);

ALTER TABLE medications
    ADD COLUMN IF NOT EXISTS catalog_id BIGINT REFERENCES medication_catalog(catalog_id);

CREATE INDEX IF NOT EXISTS idx_catalog_country_generic
    ON medication_catalog(country_code, lower(generic_name));
CREATE INDEX IF NOT EXISTS idx_medication_brands_catalog
    ON medication_brands(catalog_id);

INSERT INTO countries (country_code, country_name, regulatory_authority) VALUES
    ('TH', 'Thailand', 'Thai Food and Drug Administration'),
    ('CN', 'China', 'National Medical Products Administration'),
    ('US', 'United States', 'U.S. Food and Drug Administration')
ON CONFLICT (country_code) DO NOTHING;

INSERT INTO medication_catalog (catalog_id, country_code, generic_name, dosage_form, strength, prescription_required) VALUES
    (1, 'TH', 'Metformin', 'Tablet', '500mg', TRUE),
    (2, 'TH', 'Paracetamol', 'Tablet', '500mg', FALSE),
    (3, 'TH', 'Amlodipine', 'Tablet', '5mg', TRUE),
    (4, 'CN', 'Metformin', 'Tablet', '500mg', TRUE),
    (5, 'CN', 'Atorvastatin', 'Tablet', '10mg', TRUE),
    (6, 'CN', 'Vitamin C', 'Tablet', '500mg', FALSE),
    (7, 'US', 'Metformin', 'Tablet', '500mg', TRUE),
    (8, 'US', 'Atorvastatin', 'Tablet', '10mg', TRUE),
    (9, 'US', 'Lisinopril', 'Tablet', '5mg', TRUE),
    (10, 'US', 'Ibuprofen', 'Tablet', '200mg', FALSE)
ON CONFLICT (country_code, generic_name, dosage_form, strength) DO NOTHING;

INSERT INTO medication_brands (brand_id, catalog_id, brand_name, manufacturer, local_registration_code) VALUES
    (1, 1, 'Metformin GPO', 'Government Pharmaceutical Organization', 'TH-DEMO-001'),
    (2, 2, 'Sara', 'Thai Nakorn Patana', 'TH-DEMO-002'),
    (3, 3, 'Amlopine', 'Berlin Pharmaceutical', 'TH-DEMO-003'),
    (4, 4, 'Metformin Demo CN', 'Demo Manufacturer CN', 'CN-DEMO-001'),
    (5, 5, 'Atorvastatin Demo CN', 'Demo Manufacturer CN', 'CN-DEMO-002'),
    (6, 6, 'Vitamin C Demo CN', 'Demo Manufacturer CN', 'CN-DEMO-003'),
    (7, 7, 'Glucophage', 'Demo Manufacturer US', 'US-DEMO-001'),
    (8, 8, 'Lipitor', 'Demo Manufacturer US', 'US-DEMO-002'),
    (9, 9, 'Zestril', 'Demo Manufacturer US', 'US-DEMO-003'),
    (10, 10, 'Advil', 'Demo Manufacturer US', 'US-DEMO-004')
ON CONFLICT (catalog_id, brand_name) DO NOTHING;

SELECT setval(pg_get_serial_sequence('medication_catalog', 'catalog_id'), COALESCE((SELECT MAX(catalog_id) FROM medication_catalog), 1), true);
SELECT setval(pg_get_serial_sequence('medication_brands', 'brand_id'), COALESCE((SELECT MAX(brand_id) FROM medication_brands), 1), true);

COMMIT;
