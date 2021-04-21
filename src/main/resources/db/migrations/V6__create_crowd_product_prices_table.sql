create table crowd_product_prices (
    barcode VARCHAR(255),
    price DOUBLE NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    PRIMARY KEY (barcode, latitude, longitude)
);