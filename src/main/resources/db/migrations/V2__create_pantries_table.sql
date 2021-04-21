create table pantries  (
    id SERIAL PRIMARY KEY,
    code  VARCHAR (255) NOT NULL UNIQUE,
    name  VARCHAR (255) NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    color VARCHAR(12) NOT NULL
);