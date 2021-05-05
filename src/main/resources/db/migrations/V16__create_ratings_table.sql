create table ratings (
        id SERIAL PRIMARY KEY,
        barcode VARCHAR(255) NOT NULL,
        rating INT NOT NULL
);