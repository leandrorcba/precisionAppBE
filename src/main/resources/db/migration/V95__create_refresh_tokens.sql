CREATE TABLE refresh_tokens (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    token      VARCHAR(255) NOT NULL,
    id_user    INT          NOT NULL,
    expires_at DATETIME     NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_refresh_token (token),
    CONSTRAINT fk_refresh_token_user FOREIGN KEY (id_user) REFERENCES users (id_user) ON DELETE CASCADE
);
