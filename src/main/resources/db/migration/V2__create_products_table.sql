CREATE TABLE products (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                 VARCHAR(150) NOT NULL,
    sku                  VARCHAR(50)  NOT NULL,
    category_id          BIGINT,
    description          VARCHAR(500),
    quantity             INT NOT NULL DEFAULT 0,
    low_stock_threshold  INT NOT NULL DEFAULT 10,
    unit_price           DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    supplier             VARCHAR(100),
    created_at           DATETIME,
    updated_at           DATETIME,
    CONSTRAINT uq_products_sku UNIQUE (sku),
    CONSTRAINT fk_products_category FOREIGN KEY (category_id)
        REFERENCES categories(id) ON DELETE SET NULL,
    CONSTRAINT chk_products_quantity_nonneg CHECK (quantity >= 0),
    CONSTRAINT chk_products_threshold_nonneg CHECK (low_stock_threshold >= 0),
    CONSTRAINT chk_products_price_nonneg CHECK (unit_price >= 0)
) ENGINE=InnoDB;

CREATE INDEX idx_products_category_id ON products(category_id);
CREATE INDEX idx_products_name ON products(name);
CREATE INDEX idx_products_sku ON products(sku);