create table smart_sorting (
        row_number VARCHAR(255),
            FOREIGN KEY (row_number) REFERENCES products(barcode) ON UPDATE CASCADE ON DELETE CASCADE,
        col_number VARCHAR(255),
            FOREIGN KEY (col_number) REFERENCES products(barcode) ON UPDATE CASCADE ON DELETE CASCADE,
        cell_val INT NOT NULL,
        PRIMARY KEY (row_number, col_number)
);