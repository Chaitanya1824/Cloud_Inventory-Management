package com.inventory.repository;

import com.inventory.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySkuIgnoreCase(String sku);

    boolean existsBySkuIgnoreCase(String sku);

    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByNameContainingIgnoreCaseOrSkuContainingIgnoreCase(String name, String sku);

    // Products where current quantity has dropped to or below its own threshold
    @Query("SELECT p FROM Product p WHERE p.quantity <= p.lowStockThreshold AND p.quantity > 0")
    List<Product> findLowStockProducts();

    @Query("SELECT p FROM Product p WHERE p.quantity <= 0")
    List<Product> findOutOfStockProducts();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.quantity <= p.lowStockThreshold AND p.quantity > 0")
    long countLowStockProducts();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.quantity <= 0")
    long countOutOfStockProducts();

    @Query("SELECT COALESCE(SUM(p.unitPrice * p.quantity), 0) FROM Product p")
    java.math.BigDecimal calculateTotalInventoryValue();

    @Query("SELECT COALESCE(SUM(p.quantity), 0) FROM Product p")
    Long calculateTotalUnitsInStock();
}