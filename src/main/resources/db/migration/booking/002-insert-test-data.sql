-- Insert Test Events
INSERT INTO events (name, total_seats, booked_seats, version) VALUES
('Spring Conference 2026', 100, 0, 0),
('Java Meetup', 50, 0, 0),
('DevOps Summit', 200, 0, 0);

-- Insert Test Seats for Event 1
INSERT INTO seats (event_id, seat_number, status) VALUES
(1, 'A1', 'AVAILABLE'),
(1, 'A2', 'AVAILABLE'),
(1, 'A3', 'AVAILABLE'),
(1, 'B1', 'AVAILABLE'),
(1, 'B2', 'AVAILABLE');

-- Insert Test Seats for Event 2
INSERT INTO seats (event_id, seat_number, status) VALUES
(2, 'R1', 'AVAILABLE'),
(2, 'R2', 'AVAILABLE'),
(2, 'R3', 'AVAILABLE');

