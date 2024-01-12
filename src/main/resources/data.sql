
-- 관리자
INSERT INTO app_user (username, password, role, email, provider, provider_id, created_time)
VALUES ('admin', '{bcrypt}$2a$10$3b8XfwlFQbHcT9v.GGiSGeIINMqyycHDCJHHUpn6V5xLZWsADsShO', 'ROLE_ADMIN', 'admin@example.com', 'manual', 'manual-admin', CURRENT_TIMESTAMP);
