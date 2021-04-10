create table shopping_list_users (
    shopping_list_id BIGINT UNSIGNED,
        FOREIGN KEY (shopping_list_id) REFERENCES shopping_list(id) ON UPDATE CASCADE ON DELETE CASCADE ,
    user_id BIGINT UNSIGNED,
        FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE ,
    PRIMARY KEY (shopping_list_id, user_id)
);