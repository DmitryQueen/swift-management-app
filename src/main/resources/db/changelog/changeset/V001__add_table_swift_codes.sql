CREATE TABLE swift_codes
(
    id             BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    address        VARCHAR(255) NOT NULL,
    bank_name      VARCHAR(255) NOT NULL,
    country_iso2   VARCHAR(2)   NOT NULL,
    country_name   VARCHAR(50)  NOT NULL,
    is_headquarter BOOLEAN      NOT NULL,
    swift_code     VARCHAR(11)  NOT NULL
);

CREATE UNIQUE INDEX swift_codes_idx
ON swift_codes (swift_code);

CREATE INDEX country_iso2_idx
ON swift_codes (country_iso2)