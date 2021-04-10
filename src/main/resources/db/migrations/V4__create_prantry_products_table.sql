create table pantry_products (
    pantry_id BIGINT UNSIGNED,
        FOREIGN KEY (pantry_id) REFERENCES pantries(id) ON UPDATE CASCADE ON DELETE CASCADE ,
    product_id BIGINT UNSIGNED,
        FOREIGN KEY (product_id) REFERENCES products(id) ON UPDATE CASCADE ON DELETE CASCADE ,
    have_qty INT NOT NULL,
    want_qty INT NOT NULL,
    PRIMARY KEY (pantry_id, product_id)
);