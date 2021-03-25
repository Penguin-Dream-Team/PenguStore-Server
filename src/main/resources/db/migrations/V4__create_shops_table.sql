create table shops (
    shop_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    location_x FLOAT NOT NULL,
    location_y FLOAT NOT NULL
);