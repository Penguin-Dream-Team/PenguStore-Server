create table shop_x_product (
    shop_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    price DOUBLE NOT NULL,
    PRIMARY KEY (shop_id, product_id)
);