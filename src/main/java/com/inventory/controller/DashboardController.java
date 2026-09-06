package com.inventory.controller;

import com.inventory.service.DashboardService;
import com.inventory.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final ProductService productService;
    private final DashboardService dashboardService;

    public DashboardController(ProductService productService, DashboardService dashboardService) {
        this.productService = productService;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalProducts", productService.countAllProducts());
        model.addAttribute("totalCategories", dashboardService.getTotalCategories());
        model.addAttribute("totalUnits", productService.getTotalUnitsInStock());
        model.addAttribute("totalValue", productService.getTotalInventoryValue());
        model.addAttribute("lowStockCount", productService.countLowStock());
        model.addAttribute("outOfStockCount", productService.countOutOfStock());

        model.addAttribute("lowStockProducts", productService.getLowStockProducts());
        model.addAttribute("outOfStockProducts", productService.getOutOfStockProducts());
        model.addAttribute("recentTransactions", productService.getRecentTransactions(8));

        model.addAttribute("categoryDistribution", dashboardService.getProductCountByCategory());
        model.addAttribute("stockStatusBreakdown", dashboardService.getStockStatusBreakdown());

        return "dashboard";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("totalProducts", productService.countAllProducts());
        model.addAttribute("totalValue", productService.getTotalInventoryValue());
        model.addAttribute("totalUnits", productService.getTotalUnitsInStock());
        model.addAttribute("categoryDistribution", dashboardService.getProductCountByCategory());
        model.addAttribute("valueByCategory", dashboardService.getInventoryValueByCategory());
        model.addAttribute("stockStatusBreakdown", dashboardService.getStockStatusBreakdown());
        model.addAttribute("topProducts", dashboardService.getTopProductsByValue(10));
        return "reports";
    }
}