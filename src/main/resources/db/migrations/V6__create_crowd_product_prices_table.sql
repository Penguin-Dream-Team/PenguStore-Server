create table crowd_product_prices (
    barcode VARCHAR(255),
    price DOUBLE NOT NULL,
    latitude FLOAT NOT NULL,
    longitude FLOAT NOT NULL,
    PRIMARY KEY (barcode, latitude, longitude)
);