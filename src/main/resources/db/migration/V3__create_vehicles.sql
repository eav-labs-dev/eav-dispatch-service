CREATE TABLE vehicles (
    id UUID PRIMARY KEY,
    registration_number VARCHAR(32) NOT NULL UNIQUE,
    make VARCHAR(80) NOT NULL,
    model VARCHAR(80) NOT NULL,
    type VARCHAR(24) NOT NULL,
    max_payload_kg INTEGER NOT NULL CHECK (max_payload_kg > 0),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_vehicles_active ON vehicles (active);
