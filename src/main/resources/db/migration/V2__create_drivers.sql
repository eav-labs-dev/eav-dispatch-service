CREATE TABLE drivers (
    id UUID PRIMARY KEY,
    employee_number VARCHAR(32) NOT NULL UNIQUE,
    full_name VARCHAR(160) NOT NULL,
    phone_number VARCHAR(32) NOT NULL,
    license_number VARCHAR(64) NOT NULL UNIQUE,
    license_class VARCHAR(16) NOT NULL,
    license_expires_on DATE NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_drivers_active ON drivers (active);
