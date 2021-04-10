create table crowd_product_images (
    id SERIAL PRIMARY KEY,
    barcode VARCHAR(255),
    image_url VARCHAR(255) NOT NULL
);