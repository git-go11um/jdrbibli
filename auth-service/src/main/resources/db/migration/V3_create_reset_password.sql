ALTER TABLE users ADD COLUMN reset_password_code VARCHAR(255);

ALTER TABLE users ADD COLUMN reset_password_code_expiration BIGINT;