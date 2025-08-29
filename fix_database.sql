-- 修复数据库 - 删除组织相关表并重新创建管理员表

-- 1. 删除组织相关表
DROP TABLE IF EXISTS org2admin CASCADE;
DROP TABLE IF EXISTS org2user CASCADE;
DROP TABLE IF EXISTS tb_org CASCADE;

-- 2. 删除并重新创建管理员表
DROP TABLE IF EXISTS tb_admin CASCADE;

CREATE TABLE tb_admin (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE INDEX idx_admin_username ON tb_admin (username);

-- 3. 插入管理员账号
INSERT INTO tb_admin (username, password) VALUES ('guanliyuan', '123456');

-- 4. 验证结果
SELECT '数据库修复完成！' as message;
SELECT '管理员账号: guanliyuan / 123456' as admin_info;
SELECT COUNT(*) as admin_count FROM tb_admin; 