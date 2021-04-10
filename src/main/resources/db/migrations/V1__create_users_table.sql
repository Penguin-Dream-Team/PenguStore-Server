create table users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(16) UNIQUE NOT NULL,
    email VARCHAR (255) NULL,
    password VARCHAR (255) NOT NULL
);