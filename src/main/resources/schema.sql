DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS items CASCADE;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    name VARCHAR NOT NULL,
    email VARCHAR UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    name VARCHAR NOT NULL,
    description VARCHAR NOT NULL,
    is_available BOOLEAN NOT NULL,
    owner_id BIGINT NOT NULL,
    CONSTRAINT fk_items_to_users
            FOREIGN KEY(owner_id) REFERENCES users(id) ON DELETE CASCADE
);