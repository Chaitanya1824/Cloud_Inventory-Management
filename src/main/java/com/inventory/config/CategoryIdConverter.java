package com.inventory.config;

import com.inventory.model.Category;
import com.inventory.repository.CategoryRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * Allows Thymeleaf's th:field="*{category}" <select> (which submits the
 * category id as a plain string) to bind directly onto Product.category.
 * Spring Boot auto-registers any Converter bean into the web ConversionService.
 */
@Component
public class CategoryIdConverter implements Converter<String, Category> {

    private final CategoryRepository categoryRepository;

    public CategoryIdConverter(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category convert(@NonNull String source) {
        if (source.isBlank()) {
            return null;
        }
        try {
            Long id = Long.parseLong(source);
            return categoryRepository.findById(id).orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}