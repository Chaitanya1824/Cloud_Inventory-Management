package com.inventory.service;

import com.inventory.model.Category;
import com.inventory.repository.CategoryRepository;
import com.inventory.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));
    }

    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        long productCount = productRepository.countByCategoryId(id);
        if (productCount > 0) {
            throw new IllegalStateException(
                    "Cannot delete category '" + category.getName() + "' - it still has "
                            + productCount + " product(s) assigned to it.");
        }
        categoryRepository.deleteById(id);
    }

    public boolean nameExists(String name) {
        return categoryRepository.existsByNameIgnoreCase(name);
    }

    public long getProductCount(Long categoryId) {
        return productRepository.countByCategoryId(categoryId);
    }
}