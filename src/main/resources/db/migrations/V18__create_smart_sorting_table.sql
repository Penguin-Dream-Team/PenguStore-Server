create table smart_sorting (
        shopping_list_id BIGINT UNSIGNED,
            FOREIGN KEY (shopping_list_id) REFERENCES shopping_list(id) ON UPDATE CASCADE ON DELETE CASCADE ,
        rnumber VARCHAR(255),
            FOREIGN KEY (rnumber) REFERENCES products(barcode) ON UPDATE CASCADE ON DELETE CASCADE,
        cnumber VARCHAR(255),
            FOREIGN KEY (cnumber) REFERENCES products(barcode) ON UPDATE CASCADE ON DELETE CASCADE,
        cval INT NOT NULL,
        PRIMARY KEY (shopping_list_id, rnumber, cnumber)
);