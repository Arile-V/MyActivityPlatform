-- 清理数据库并重新创建（去掉所有组织相关内容）
-- 删除可能存在的旧表
DROP TABLE IF EXISTS org2admin CASCADE;
DROP TABLE IF EXISTS org2user CASCADE;
DROP TABLE IF EXISTS tb_org CASCADE;
DROP TABLE IF EXISTS tb_admin CASCADE;
DROP TABLE IF EXISTS tb_user CASCADE;
DROP TABLE IF EXISTS tb_activity CASCADE;
DROP TABLE IF EXISTS tb_notification CASCADE;
DROP TABLE IF EXISTS tb_log CASCADE;
DROP TABLE IF EXISTS volunteer CASCADE;
DROP TABLE IF EXISTS activity_character CASCADE;

-- 重新创建核心表
CREATE TABLE tb_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    working_hours BIGINT DEFAULT 0,
    school_id VARCHAR(255),
    name VARCHAR(255)
);
CREATE INDEX idx_user_name ON tb_user (name);

CREATE TABLE tb_admin (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);
CREATE INDEX idx_admin_username ON tb_admin (username);

CREATE TABLE tb_activity (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    location VARCHAR(255),
    status VARCHAR(255) DEFAULT 'pending',
    image_url VARCHAR(255),
    type VARCHAR(255),
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    start_to_get_time TIMESTAMP,
    end_to_get_time TIMESTAMP,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_activity_name ON tb_activity (name);

CREATE TABLE tb_notification (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255),
    content TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_notification_title ON tb_notification (title);

CREATE TABLE tb_log (
    id BIGSERIAL PRIMARY KEY,
    ip VARCHAR(255),
    method VARCHAR(255),
    request TEXT,
    exception TEXT,
    time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    result TEXT,
    status INTEGER
);
CREATE INDEX idx_log_method ON tb_log (method);

CREATE TABLE volunteer (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES tb_user(id) ON DELETE CASCADE,
    activity_id BIGINT REFERENCES tb_activity(id) ON DELETE CASCADE,
    status INT DEFAULT 0
);

CREATE TABLE activity_character(
    id BIGSERIAL PRIMARY KEY,
    activity_id BIGINT REFERENCES tb_activity(id) ON DELETE CASCADE,
    character_name VARCHAR(255),
    volume BIGINT DEFAULT 1
);

-- 插入测试管理员账号
INSERT INTO tb_admin (username, password) VALUES ('guanliyuan', '123456');

-- 插入测试用户账号
INSERT INTO tb_user (username, email, name, school_id) VALUES 
('testuser', 'test@example.com', '测试用户', 'TEST001');

-- 插入测试活动
INSERT INTO tb_activity (name, description, location, type, status) VALUES 
('社区清洁日', '参与社区环境清洁活动', '社区中心', '环保', 'active');

-- 显示创建结果
SELECT '数据库初始化完成！' as message;
SELECT '管理员账号: guanliyuan / 123456' as admin_info;
SELECT COUNT(*) as admin_count FROM tb_admin;
SELECT COUNT(*) as user_count FROM tb_user;
SELECT COUNT(*) as activity_count FROM tb_activity; 