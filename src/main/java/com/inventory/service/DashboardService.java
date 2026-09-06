package com.inventory.service;

import com.inventory.model.Product;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final ProductService productService;
    private final CategoryService categoryService;

    public DashboardService(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    /** Number of products grouped by category name - powers the category distribution chart. */
    public Map<String, Long> getProductCountByCategory() {
        List<Product> products = productService.getAllProducts();
        Map<String, Long> result = new LinkedHashMap<>();
        for (Product p : products) {
            String categoryName = p.getCategory() != null ? p.getCategory().getName() : "Uncategorized";
            result.merge(categoryName, 1L, Long::sum);
        }
        return result;
    }

    /** Total stock value grouped by category name - powers the value-by-category chart. */
    public Map<String, BigDecimal> getInventoryValueByCategory() {
        List<Product> products = productService.getAllProducts();
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        for (Product p : products) {
            String categoryName = p.getCategory() != null ? p.getCategory().getName() : "Uncategorized";
            result.merge(categoryName, p.getTotalValue(), BigDecimal::add);
        }
        return result;
    }

    /** Breakdown of how many products fall in each stock-status bucket. */
    public Map<String, Long> getStockStatusBreakdown() {
        List<Product> products = productService.getAllProducts();
        Map<String, Long> breakdown = new LinkedHashMap<>();
        breakdown.put("IN_STOCK", 0L);
        breakdown.put("LOW_STOCK", 0L);
        breakdown.put("OUT_OF_STOCK", 0L);
        Map<String, Long> counts = products.stream()
                .collect(Collectors.groupingBy(Product::getStockStatus, Collectors.counting()));
        counts.forEach(breakdown::put);
        return breakdown;
    }

    public long getTotalCategories() {
        return categoryService.getAllCategories().size();
    }

    /** Top N products by total inventory value - useful for a simple analytics table. */
    public List<Product> getTopProductsByValue(int limit) {
        return productService.getAllProducts().stream()
                .sorted((a, b) -> b.getTotalValue().compareTo(a.getTotalValue()))
                .limit(limit)
                .toList();
    }
}