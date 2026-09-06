package com.inventory.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Core inventory item.
 *
 * Stock status is derived (not stored) from quantity vs lowStockThreshold,
 * so it is always consistent with the current quantity - this is what
 * powers "real-time" stock status / low stock alerts without needing a
 * separate background job to keep a stored flag in sync.
 */
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name is required")
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String name;

    @NotBlank(message = "SKU is required")
    @Size(max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String sku;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    @Size(max = 500)
    @Column(length = 500)
    private String description;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    @Column(nullable = false)
    private Integer quantity = 0;

    @NotNull(message = "Low stock threshold is required")
    @Min(value = 0, message = "Threshold cannot be negative")
    @Column(name = "low_stock_threshold", nullable = false)
    private Integer lowStockThreshold = 10;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price cannot be negative")
    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice = BigDecimal.ZERO;

    @Size(max = 100)
    private String supplier;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Product() {
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ---- Derived / helper properties (not persisted) ----

    @Transient
    public String getStockStatus() {
        if (quantity == null || quantity <= 0) {
            return "OUT_OF_STOCK";
        } else if (lowStockThreshold != null && quantity <= lowStockThreshold) {
            return "LOW_STOCK";
        } else {
            return "IN_STOCK";
        }
    }

    @Transient
    public String getStockStatusLabel() {
        return switch (getStockStatus()) {
            case "OUT_OF_STOCK" -> "Out of Stock";
            case "LOW_STOCK" -> "Low Stock";
            default -> "In Stock";
        };
    }

    @Transient
    public String getStockStatusBadgeClass() {
        return switch (getStockStatus()) {
            case "OUT_OF_STOCK" -> "bg-danger";
            case "LOW_STOCK" -> "bg-warning text-dark";
            default -> "bg-success";
        };
    }

    @Transient
    public BigDecimal getTotalValue() {
        if (unitPrice == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    // ---- Getters / Setters ----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getLowStockThreshold() {
        return lowStockThreshold;
    }

    public void setLowStockThreshold(Integer lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}