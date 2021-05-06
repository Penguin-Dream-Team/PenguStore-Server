create table suggestions (
        shopping_list_id BIGINT UNSIGNED,
            FOREIGN KEY (shopping_list_id) REFERENCES shopping_list(id) ON UPDATE CASCADE ON DELETE CASCADE ,
        row_number VARCHAR(255),
            FOREIGN KEY (row_number) REFERENCES products(barcode) ON UPDATE CASCADE ON DELETE CASCADE,
        col_number VARCHAR(255),
            FOREIGN KEY (col_number) REFERENCES products(barcode) ON UPDATE CASCADE ON DELETE CASCADE,
        cell_val INT NOT NULL,
        PRIMARY KEY (shopping_list_id, row_number, col_number)
);