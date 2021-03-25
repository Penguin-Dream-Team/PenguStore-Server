create table products (
    product_id SERIAL PRIMARY KEY,
    barcode VARCHAR(255) NOT NULL,
    review_score DOUBLE ,
    review_number INT NOT NULL,
    name VARCHAR  (255) NOT NULL
);