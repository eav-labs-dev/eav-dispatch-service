ALTER TABLE shipments ADD COLUMN driver_id UUID;
ALTER TABLE shipments ADD COLUMN vehicle_id UUID;

ALTER TABLE shipments
    ADD CONSTRAINT fk_shipments_driver FOREIGN KEY (driver_id) REFERENCES drivers (id);
ALTER TABLE shipments
    ADD CONSTRAINT fk_shipments_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicles (id);

CREATE INDEX idx_shipments_driver ON shipments (driver_id);
CREATE INDEX idx_shipments_vehicle ON shipments (vehicle_id);

CREATE TABLE shipment_status_history (
    id UUID PRIMARY KEY,
    shipment_id UUID NOT NULL,
    previous_status VARCHAR(32),
    new_status VARCHAR(32) NOT NULL,
    note VARCHAR(500),
    changed_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_status_history_shipment
        FOREIGN KEY (shipment_id) REFERENCES shipments (id) ON DELETE CASCADE,
    CONSTRAINT ck_status_history_previous CHECK (
        previous_status IS NULL OR previous_status IN (
            'CREATED', 'ASSIGNED', 'IN_TRANSIT', 'DELIVERED', 'CANCELLED'
        )
    ),
    CONSTRAINT ck_status_history_new CHECK (
        new_status IN ('CREATED', 'ASSIGNED', 'IN_TRANSIT', 'DELIVERED', 'CANCELLED')
    )
);

CREATE INDEX idx_status_history_shipment_time
    ON shipment_status_history (shipment_id, changed_at);
