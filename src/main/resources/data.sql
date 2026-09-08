-- Authors
INSERT INTO authors (id, first_name, last_name)
VALUES
    (1, 'J.K.', 'Rowling'),
    (2, 'Roald', 'Dahl'),
    (3, 'Annie M.G.', 'Schmidt')
    ON CONFLICT (id) DO NOTHING;


-- Books
INSERT INTO books (
    id,
    isbn,
    title,
    publisher,
    publication_year,
    category,
    description,
    cover_image_path,
    author_id
)
VALUES
    (
        1,
        '978-0-7475-3269-9',
        'Harry Potter en de Steen der Wijzen',
        'Bloomsbury',
        1997,
        'Fantasy',
        'Het eerste boek in de Harry Potter serie',
        NULL,
        1
    ),
    (
        2,
        '978-0-7475-3849-3',
        'Harry Potter en de Geheime Kamer',
        'Bloomsbury',
        1998,
        'Fantasy',
        'Het tweede boek in de Harry Potter serie',
        NULL,
        1
    ),
    (
        3,
        '978-0-14-028720-7',
        'Sjakie en de Chocoladefabriek',
        'Penguin Books',
        1964,
        'Kinderboek',
        'Een magisch verhaal over een chocoladefabriek',
        NULL,
        2
    ),
    (
        4,
        '978-90-254-0001-1',
        'Pluk van de Petteflet',
        'Van Goor',
        1971,
        'Kinderboek',
        'Het avontuur van Pluk en zijn kraantjepuk',
        NULL,
        3
    )
    ON CONFLICT (id) DO NOTHING;


-- BookCopies
INSERT INTO book_copies (id, barcode, status, book_id)
VALUES
    (1, 'BC-001', 'AVAILABLE', 1),
    (2, 'BC-002', 'LOANED', 1),
    (3, 'BC-003', 'AVAILABLE', 2),
    (4, 'BC-004', 'AVAILABLE', 3),
    (5, 'BC-005', 'AVAILABLE', 4)
    ON CONFLICT (id) DO NOTHING;


-- Customers
INSERT INTO customers (id, keycloak_id, first_name, last_name, email, phone_number)
VALUES
    (
     1,
     '977d1973-d0dc-45df-bd3c-f9823611acee',
     'Admin',
     'Beheerder',
     'beheerder@boekenbeheer.nl',
     '0612345678'),
    (
     2,
     '9ac95bc8-f0df-4a89-95bf-89d100ee4c61',
     'Test',
     'Klant',
     'klant@boekenbeheer.nl',
     '0698765432'
    )
ON CONFLICT (id) DO NOTHING;


-- LibraryCards
INSERT INTO library_cards (
    id,
    card_number,
    issue_date,
    expiration_date,
    active,
    customer_id
)
VALUES
    (
        1,
        'LC-001',
        '2026-01-01',
        '2027-01-01',
        true,
        1
    ),
    (
        2,
        'LC-002',
        '2026-01-01',
        '2027-01-01',
        true,
        2
    )
    ON CONFLICT (id) DO NOTHING;


-- Loans
INSERT INTO loans (
    id,
    loan_date,
    due_date,
    return_date,
    customer_id,
    book_copy_id
)
VALUES
    (
        1,
        '2026-08-01',
        '2026-08-15',
        '2026-08-14',
        2,
        3
    ),
    (
        2,
        '2026-09-01',
        '2026-09-15',
        NULL,
        2,
        2
    )
    ON CONFLICT (id) DO NOTHING;


-- Sequences bijwerken
SELECT setval('authors_id_seq', (SELECT MAX(id) FROM authors));
SELECT setval('books_id_seq', (SELECT MAX(id) FROM books));
SELECT setval('book_copies_id_seq', (SELECT MAX(id) FROM book_copies));
SELECT setval('customers_id_seq', (SELECT MAX(id) FROM customers));
SELECT setval('library_cards_id_seq', (SELECT MAX(id) FROM library_cards));
SELECT setval('loans_id_seq',  (SELECT MAX(id) FROM loans));