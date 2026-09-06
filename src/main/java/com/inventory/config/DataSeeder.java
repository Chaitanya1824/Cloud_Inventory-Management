package com.inventory.config;

import com.inventory.model.Category;
import com.inventory.model.Product;
import com.inventory.repository.CategoryRepository;
import com.inventory.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Seeds a handful of demo categories/products the first time the app runs
 * against an empty database, so Phase 1 is immediately explorable.
 * Safe to leave in for Phase 2/3; it only inserts when tables are empty.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public DataSeeder(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            return; // already seeded
        }

        Category electronics = categoryRepository.save(new Category("Electronics", "Electronic devices and accessories"));
        Category grocery = categoryRepository.save(new Category("Grocery", "Packaged food and daily essentials"));
        Category stationery = categoryRepository.save(new Category("Stationery", "Office and school supplies"));
        Category furniture = categoryRepository.save(new Category("Furniture", "Home and office furniture"));

        productRepository.save(product("Wireless Mouse", "ELEC-001", electronics,
                "Ergonomic 2.4GHz wireless mouse", 45, 15, new BigDecimal("499.00"), "Logitech Distributors"));
        productRepository.save(product("USB-C Charger 20W", "ELEC-002", electronics,
                "Fast charging USB-C wall adapter", 8, 10, new BigDecimal("799.00"), "Anker India"));
        productRepository.save(product("Bluetooth Headphones", "ELEC-003", electronics,
                "Over-ear noise cancelling headphones", 0, 5, new BigDecimal("2499.00"), "Boat Lifestyle"));

        productRepository.save(product("Basmati Rice 5kg", "GRO-001", grocery,
                "Premium long-grain basmati rice", 120, 20, new BigDecimal("650.00"), "Local Wholesale Mart"));
        productRepository.save(product("Sunflower Oil 1L", "GRO-002", grocery,
                "Refined sunflower cooking oil", 6, 15, new BigDecimal("180.00"), "Local Wholesale Mart"));

        productRepository.save(product("A4 Paper Ream (500 sheets)", "STA-001", stationery,
                "70 GSM white copier paper", 60, 20, new BigDecimal("320.00"), "JK Paper Supplies"));
        productRepository.save(product("Ballpoint Pen (Box of 10)", "STA-002", stationery,
                "Blue ink ballpoint pens", 3, 10, new BigDecimal("120.00"), "Cello Distributors"));

        productRepository.save(product("Office Chair - Mesh Back", "FUR-001", furniture,
                "Adjustable height ergonomic office chair", 12, 5, new BigDecimal("5999.00"), "Featherlite Ltd"));
        productRepository.save(product("Study Table", "FUR-002", furniture,
                "Compact wooden study table", 0, 5, new BigDecimal("3499.00"), "Nilkamal Ltd"));
    }

    private Product product(String name, String sku, Category category, String description,
                            int quantity, int threshold, BigDecimal price, String supplier) {
        Product p = new Product();
        p.setName(name);
        p.setSku(sku);
        p.setCategory(category);
        p.setDescription(description);
        p.setQuantity(quantity);
        p.setLowStockThreshold(threshold);
        p.setUnitPrice(price);
        p.setSupplier(supplier);
        return p;
    }
}