create table local_product_images (
    id SERIAL PRIMARY KEY,
    product_id BIGINT UNSIGNED,
        FOREIGN KEY (product_id) REFERENCES products(id) ON UPDATE CASCADE ON DELETE CASCADE ,
    image_url VARCHAR(255) NOT NULL
);