create table smart_sorting (
        row_number BIGINT UNSIGNED,
            FOREIGN KEY (row_number) REFERENCES products(id) ON UPDATE CASCADE ON DELETE CASCADE,
        col_number BIGINT UNSIGNED,
            FOREIGN KEY (col_number) REFERENCES products(id) ON UPDATE CASCADE ON DELETE CASCADE,
        cell_val INT NOT NULL,
        PRIMARY KEY (row_number, col_number)
);