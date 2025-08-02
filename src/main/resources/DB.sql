CREATE DATABASE platform;

CREATE SCHEMA activity;
CREATE TABLE tb_org (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    description TEXT
);
CREATE INDEX idx_org_name ON tb_org (name);
CREATE TABLE tb_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255),
    create_time TIMESTAMP,
    working_hours BIGINT,
    school_id VARCHAR(255),
    name VARCHAR(255)
);
CREATE INDEX idx_user_name ON tb_user (name);
CREATE TABLE tb_admin (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255)
);
CREATE INDEX idx_admin_username ON tb_admin (username);
CREATE TABLE tb_activity (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    description TEXT,
    location VARCHAR(255),
    status VARCHAR(255),
    image_url VARCHAR(255),
    type VARCHAR(255),
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    start_to_get_time TIMESTAMP,
    end_to_get_time TIMESTAMP,
    create_time TIMESTAMP
);
CREATE INDEX idx_activity_name ON tb_activity (name);
CREATE TABLE tb_notification (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255),
    content TEXT,
    create_time TIMESTAMP
);
CREATE INDEX idx_notification_title ON tb_notification (title);
CREATE TABLE tb_log (
    id BIGSERIAL PRIMARY KEY,
    ip VARCHAR(255),
    method VARCHAR(255),
    request TEXT,
    exception TEXT,
    time TIMESTAMP,
    result TEXT,
    status INTEGER
);
CREATE INDEX idx_log_method ON tb_log (method);
CREATE TABLE org2admin (
    id BIGSERIAL PRIMARY KEY,
    org_id BIGINT REFERENCES tb_org(id),
    admin_id BIGINT REFERENCES tb_admin(id)
);
CREATE TABLE org2user (
    id BIGSERIAL PRIMARY KEY,
    org_id BIGINT REFERENCES tb_org(id),
    user_id BIGINT REFERENCES tb_user(id)
);
CREATE TABLE volunteer (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES tb_user(id),
    activity_id BIGINT REFERENCES tb_activity(id),
    status INT
);
CREATE TABLE activity_character(
    id BIGSERIAL PRIMARY KEY,
    activity_id BIGINT REFERENCES tb_activity(id),
    character_name VARCHAR,
    volume BIGINT
);
