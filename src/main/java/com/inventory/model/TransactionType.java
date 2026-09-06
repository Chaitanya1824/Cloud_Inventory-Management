package com.inventory.model;

/**
 * Represents the type of a stock movement recorded against a product.
 */
public enum TransactionType {
    STOCK_IN,   // Goods received / added to inventory
    STOCK_OUT,  // Goods sold / removed from inventory
    ADJUSTMENT  // Manual correction (e.g. after a stock count)
}