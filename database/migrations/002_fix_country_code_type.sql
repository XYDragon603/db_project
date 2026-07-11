BEGIN;

ALTER TABLE medication_catalog
    DROP CONSTRAINT IF EXISTS medication_catalog_country_code_fkey;

ALTER TABLE medication_catalog
    ALTER COLUMN country_code TYPE VARCHAR(2)
    USING trim(country_code)::VARCHAR(2);

ALTER TABLE countries
    ALTER COLUMN country_code TYPE VARCHAR(2)
    USING trim(country_code)::VARCHAR(2);

ALTER TABLE medication_catalog
    ADD CONSTRAINT medication_catalog_country_code_fkey
    FOREIGN KEY (country_code) REFERENCES countries(country_code);

COMMIT;
