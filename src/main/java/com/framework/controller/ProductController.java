package com.framework.controller;

import com.framework.annotation.Controller;

@Controller
public class ProductController {
    private String[] products = {"Laptop", "Smartphone", "Tablet"};
    
    public String getProducts() {
        return "Liste des produits : " + String.join(", ", products);
    }
    
    public String getProductDetails(String id) {
        return "Détails du produit : " + id;
    }
    
    public String searchProducts(String query) {
        return "Recherche de produits pour : " + query;
    }
}
