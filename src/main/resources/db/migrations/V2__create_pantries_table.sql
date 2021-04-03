create table pantries  (
    pantry_id SERIAL PRIMARY KEY,
    code  VARCHAR (255) NOT NULL UNIQUE,
    name VARCHAR  (255) NOT NULL,
    latitude FLOAT NOT NULL,
    longitude FLOAT NOT NULL
);