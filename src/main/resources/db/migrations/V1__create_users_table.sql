create table users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(16) UNIQUE NOT NULL,
    email VARCHAR (255) NOT NULL,
    password VARCHAR  (255) NOT NULL,
    guest BOOLEAN NOT NULL
);