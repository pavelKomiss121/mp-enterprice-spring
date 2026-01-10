-- Create Events Table
CREATE TABLE events (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    total_seats INT NOT NULL,
    booked_seats INT DEFAULT 0,
    version BIGINT DEFAULT 0
);

-- Create Seats Table
CREATE TABLE seats (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT REFERENCES events(id),
    seat_number VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL,
    UNIQUE (event_id, seat_number)
);

-- Create Bookings Table
CREATE TABLE bookings (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT REFERENCES events(id),
    user_id BIGINT,
    seat_id BIGINT REFERENCES seats(id),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

-- Indexes
CREATE INDEX idx_seats_event_id ON seats (event_id);
CREATE INDEX idx_seats_status ON seats (status);
CREATE INDEX idx_bookings_user_id ON bookings (user_id);
CREATE INDEX idx_bookings_event_id ON bookings (event_id);

