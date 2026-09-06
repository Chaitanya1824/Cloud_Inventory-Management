package com.inventory.service;

import com.inventory.model.Product;
import com.inventory.model.StockTransaction;
import com.inventory.model.TransactionType;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.StockTransactionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final StockTransactionRepository stockTransactionRepository;

    public ProductService(ProductRepository productRepository,
                          StockTransactionRepository stockTransactionRepository) {
        this.productRepository = productRepository;
        this.stockTransactionRepository = stockTransactionRepository;
    }

    // ---- Basic CRUD ----

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
    }

    @Transactional
    public Product saveProduct(Product product) {
        // Enforce unique SKU (case-insensitive), excluding the product's own row when editing
        productRepository.findBySkuIgnoreCase(product.getSku()).ifPresent(existing -> {
            if (!existing.getId().equals(product.getId())) {
                throw new IllegalArgumentException("A product with SKU '" + product.getSku() + "' already exists.");
            }
        });

        boolean isNew = product.getId() == null;
        Product saved = productRepository.save(product);

        // Log the initial stock as a STOCK_IN transaction so history is complete from day one
        if (isNew && saved.getQuantity() != null && saved.getQuantity() > 0) {
            logTransaction(saved, TransactionType.STOCK_IN, saved.getQuantity(), 0, saved.getQuantity(),
                    "Initial stock on product creation");
        }
        return saved;
    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    // ---- Search / filter for the inventory table ----

    public List<Product> searchProducts(String keyword, Long categoryId, String status) {
        List<Product> products;

        if (keyword != null && !keyword.isBlank()) {
            products = productRepository.findByNameContainingIgnoreCaseOrSkuContainingIgnoreCase(keyword, keyword);
        } else {
            products = productRepository.findAll();
        }

        if (categoryId != null) {
            products = products.stream()
                    .filter(p -> p.getCategory() != null && categoryId.equals(p.getCategory().getId()))
                    .toList();
        }

        if (status != null && !status.isBlank() && !status.equalsIgnoreCase("ALL")) {
            products = products.stream()
                    .filter(p -> p.getStockStatus().equalsIgnoreCase(status))
                    .toList();
        }

        return products;
    }

    // ---- Real-time inventory tracking ----

    /**
     * Adjusts stock for a product and records the movement. This is the single
     * entry point for all quantity changes so the audit trail (StockTransaction)
     * always stays in sync with Product.quantity.
     */
    @Transactional
    public Product adjustStock(Long productId, TransactionType type, int quantityChange, String note) {
        if (quantityChange <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        Product product = getProductById(productId);
        int previousQuantity = product.getQuantity() == null ? 0 : product.getQuantity();
        int newQuantity;

        switch (type) {
            case STOCK_IN -> newQuantity = previousQuantity + quantityChange;
            case STOCK_OUT -> {
                if (quantityChange > previousQuantity) {
                    throw new IllegalArgumentException(
                            "Cannot remove " + quantityChange + " units - only " + previousQuantity + " in stock.");
                }
                newQuantity = previousQuantity - quantityChange;
            }
            case ADJUSTMENT -> newQuantity = quantityChange; // treated as "set to" value
            default -> throw new IllegalArgumentException("Unknown transaction type: " + type);
        }

        product.setQuantity(newQuantity);
        Product saved = productRepository.save(product);

        int loggedChange = (type == TransactionType.ADJUSTMENT) ? (newQuantity - previousQuantity) : quantityChange;
        logTransaction(saved, type, loggedChange, previousQuantity, newQuantity, note);

        return saved;
    }

    private void logTransaction(Product product, TransactionType type, int changed,
                                int previousQuantity, int newQuantity, String note) {
        StockTransaction tx = new StockTransaction();
        tx.setProduct(product);
        tx.setType(type);
        tx.setQuantityChanged(changed);
        tx.setPreviousQuantity(previousQuantity);
        tx.setNewQuantity(newQuantity);
        tx.setNote(note);
        stockTransactionRepository.save(tx);
    }

    public List<StockTransaction> getTransactionHistoryForProduct(Long productId) {
        return stockTransactionRepository.findByProductIdOrderByTimestampDesc(productId);
    }

    public List<StockTransaction> getRecentTransactions(int limit) {
        return stockTransactionRepository.findAllByOrderByTimestampDesc(PageRequest.of(0, limit));
    }

    // ---- Alerts & analytics helpers ----

    public List<Product> getLowStockProducts() {
        return productRepository.findLowStockProducts();
    }

    public List<Product> getOutOfStockProducts() {
        return productRepository.findOutOfStockProducts();
    }

    public long countLowStock() {
        return productRepository.countLowStockProducts();
    }

    public long countOutOfStock() {
        return productRepository.countOutOfStockProducts();
    }

    public BigDecimal getTotalInventoryValue() {
        return productRepository.calculateTotalInventoryValue();
    }

    public Long getTotalUnitsInStock() {
        return productRepository.calculateTotalUnitsInStock();
    }

    public long countAllProducts() {
        return productRepository.count();
    }
}