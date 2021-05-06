create table ratings (
        user_id BIGINT UNSIGNED,
            FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE ,
        barcode VARCHAR(255) NOT NULL,
        rating INT NOT NULL,
        PRIMARY KEY(user_id, barcode)
);