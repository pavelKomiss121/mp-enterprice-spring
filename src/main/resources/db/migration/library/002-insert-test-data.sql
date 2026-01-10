-- Liquibase formatted SQL
-- changeset library:002-insert-test-data

-- Insert categories
INSERT INTO categories (name, description) VALUES
('Fiction', 'Fiction books'),
('Science', 'Scientific books'),
('History', 'Historical books'),
('Technology', 'Technology books');

-- Insert authors
INSERT INTO authors (first_name, last_name, biography, birth_date) VALUES
('John', 'Doe', 'Famous author', '1970-01-15'),
('Jane', 'Smith', 'Award-winning writer', '1980-05-20'),
('Robert', 'Brown', 'Bestselling author', '1975-11-30');

-- Insert users
INSERT INTO users (email, first_name, last_name, phone, registered_at) VALUES
('john@example.com', 'John', 'Reader', '+1234567890', NOW()),
('jane@example.com', 'Jane', 'Bookworm', '+0987654321', NOW());

-- Insert books
INSERT INTO books (isbn, title, description, publication_year, pages, category_id, status, created_at)
VALUES
('978-0-123456-78-9', 'Introduction to Science', 'A beginner guide to science', 2020, 300, 2, 'AVAILABLE', NOW()),
('978-0-987654-32-1', 'History of the World', 'Comprehensive history book', 2018, 500, 3, 'AVAILABLE', NOW()),
('978-0-111111-11-1', 'Tech Innovations', 'Latest in technology', 2023, 250, 4, 'AVAILABLE', NOW());

-- Link books with authors
INSERT INTO book_authors (book_id, author_id)
SELECT b.id, a.id
FROM books b, authors a
WHERE b.isbn = '978-0-123456-78-9' AND a.last_name = 'Doe'
UNION ALL
SELECT b.id, a.id
FROM books b, authors a
WHERE b.isbn = '978-0-987654-32-1' AND a.last_name = 'Smith'
UNION ALL
SELECT b.id, a.id
FROM books b, authors a
WHERE b.isbn = '978-0-111111-11-1' AND a.last_name = 'Brown';

