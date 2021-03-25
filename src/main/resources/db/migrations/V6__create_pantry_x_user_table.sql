create table pantry_x_user (
    pantry_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (pantry_id, user_id)
);