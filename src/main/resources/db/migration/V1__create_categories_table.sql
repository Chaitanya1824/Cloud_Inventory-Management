CREATE TABLE categories (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    CONSTRAINT uq_categories_name UNIQUE (name)
) ENGINE=InnoDB;