create table products_users (
    product_id BIGINT UNSIGNED,
        FOREIGN KEY (product_id) REFERENCES products(id) ON UPDATE CASCADE ON DELETE CASCADE ,
    user_id BIGINT UNSIGNED,
        FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE ,
    PRIMARY KEY (product_id, user_id)
);