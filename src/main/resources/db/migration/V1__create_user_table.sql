CREATE TABLE user (
                      id INT AUTO_INCREMENT PRIMARY KEY,
                      account_id VARCHAR(100) NULL,
                      name VARCHAR(50) NULL,
                      token CHAR(36) NULL,
                      gmt_create BIGINT NULL,
                      gmt_modified BIGINT NULL,
                      bio VARCHAR(256) NULL
);