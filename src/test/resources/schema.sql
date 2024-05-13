CREATE TABLE `user` (
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE,
    name VARCHAR(255),
    create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_uuid ON `user`(uuid);

CREATE TYPE session_status AS ENUM ('ACTIVE', 'ENDED');

CREATE TABLE `session` (
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE,
    session_name VARCHAR(255),
    status session_status NOT NULL,
    admin_id BIGINT NOT NULL,
    create_datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    end_datetime TIMESTAMP,
    FOREIGN KEY (admin_id) REFERENCES `user`(id)
);

CREATE INDEX idx_session_uuid ON `session`(uuid);

CREATE TYPE member_status AS ENUM ('INVITED', 'JOINED');

CREATE TABLE sessionuser (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    session_id BIGINT NOT NULL,
    status MEMBER_STATUS NOT NULL,
    FOREIGN KEY (user_id) REFERENCES `user`(id),
    FOREIGN KEY (session_id) REFERENCES `session`(id)
);

CREATE TABLE submission (
    id SERIAL PRIMARY KEY,
    session_user_id BIGINT NOT NULL,
    place_name VARCHAR(255) NOT NULL,
    selected BOOLEAN DEFAULT FALSE,
    create_datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (session_user_id) REFERENCES sessionuser(id)
);