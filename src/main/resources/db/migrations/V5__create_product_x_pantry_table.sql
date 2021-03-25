create table product_x_pantry (
    pantry_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    have_qty INT NOT NULL,
    want_qty INT NOT NULL,
    PRIMARY KEY (pantry_id, product_id)
);