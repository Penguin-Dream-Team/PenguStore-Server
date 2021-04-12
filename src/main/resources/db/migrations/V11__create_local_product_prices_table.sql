create table local_product_prices (
    product_id BIGINT UNSIGNED,
        FOREIGN KEY (product_id) REFERENCES products(id) ON UPDATE CASCADE ON DELETE CASCADE ,
    price DOUBLE NOT NULL,
    latitude FLOAT NOT NULL,
    longitude FLOAT NOT NULL,
    PRIMARY KEY (product_id, latitude, longitude)
);