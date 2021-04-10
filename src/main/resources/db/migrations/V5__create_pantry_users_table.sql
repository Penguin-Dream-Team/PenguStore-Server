create table pantries_users (
    pantry_id BIGINT UNSIGNED,
        FOREIGN KEY (pantry_id) REFERENCES pantries(id) ON UPDATE CASCADE ON DELETE CASCADE ,
    user_id BIGINT UNSIGNED,
        FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE ,
    PRIMARY KEY (pantry_id, user_id)
);