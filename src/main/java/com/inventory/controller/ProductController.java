package com.inventory.controller;

import com.inventory.model.Product;
import com.inventory.model.TransactionType;
import com.inventory.service.CategoryService;
import com.inventory.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    // ---- Inventory table (list, search, filter) ----

    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) Long categoryId,
                       @RequestParam(required = false) String status,
                       Model model) {
        model.addAttribute("products", productService.searchProducts(keyword, categoryId, status));
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedStatus", status);
        return "products/list";
    }

    // ---- Add / Edit ----

    @GetMapping("/new")
    public String newForm(Model model) {
        Product product = new Product();
        product.setLowStockThreshold(10);
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("isEdit", false);
        return "products/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("isEdit", true);
        return "products/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("product") Product product,
                       BindingResult result,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("isEdit", product.getId() != null);
            return "products/form";
        }
        try {
            productService.saveProduct(product);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Product '" + product.getName() + "' saved successfully.");
            return "redirect:/products";
        } catch (Exception e) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("isEdit", product.getId() != null);
            model.addAttribute("errorMessage", e.getMessage());
            return "products/form";
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("successMessage", "Product deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not delete product: " + e.getMessage());
        }
        return "redirect:/products";
    }

    // ---- Real-time stock adjustment (add stock / remove stock / correction) ----

    @GetMapping("/{id}/stock")
    public String stockForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("history", productService.getTransactionHistoryForProduct(id));
        return "products/stock-form";
    }

    @PostMapping("/{id}/stock")
    public String adjustStock(@PathVariable Long id,
                              @RequestParam TransactionType type,
                              @RequestParam int quantity,
                              @RequestParam(required = false) String note,
                              RedirectAttributes redirectAttributes) {
        try {
            productService.adjustStock(id, type, quantity, note);
            redirectAttributes.addFlashAttribute("successMessage", "Stock updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/products/" + id + "/stock";
    }
}