create table suggestions (
        user_id BIGINT UNSIGNED,
            FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE,
        rnumber VARCHAR(255),
            FOREIGN KEY (rnumber) REFERENCES products(barcode) ON UPDATE CASCADE ON DELETE CASCADE,
        cnumber VARCHAR(255),
            FOREIGN KEY (cnumber) REFERENCES products(barcode) ON UPDATE CASCADE ON DELETE CASCADE,
        cval INT NOT NULL,
        PRIMARY KEY (user_id, rnumber, cnumber)
);