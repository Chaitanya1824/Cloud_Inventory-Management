CREATE TABLE stock_transactions (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id         BIGINT NOT NULL,
    type               VARCHAR(20) NOT NULL,
    quantity_changed   INT NOT NULL,
    previous_quantity  INT NOT NULL,
    new_quantity       INT NOT NULL,
    note               VARCHAR(255),
    timestamp          DATETIME NOT NULL,
    CONSTRAINT fk_stock_tx_product FOREIGN KEY (product_id)
        REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT chk_stock_tx_type CHECK (type IN ('STOCK_IN', 'STOCK_OUT', 'ADJUSTMENT'))
) ENGINE=InnoDB;

CREATE INDEX idx_stock_tx_product_id ON stock_transactions(product_id);
CREATE INDEX idx_stock_tx_timestamp ON stock_transactions(timestamp);