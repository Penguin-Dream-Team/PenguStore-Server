create table products (
    id SERIAL PRIMARY KEY,
    barcode VARCHAR(255) UNIQUE,
    name VARCHAR(255)
);