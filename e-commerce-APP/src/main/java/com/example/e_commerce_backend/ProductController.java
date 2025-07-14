package com.example.e_commerce_backend;

import com.example.e_commerce_backend.ecommerce.Product;
import com.example.e_commerce_backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    // ✅ Show products and the add form
    @GetMapping("/products-page")
    public String showProducts(Model model) {
        model.addAttribute("products", productRepository.findAll());
        model.addAttribute("product", new Product());
        return "products"; // products.html
    }

    // ✅ Add a product
    @PostMapping("/products-page")
    public String addProduct(@ModelAttribute Product product) {
        productRepository.save(product);
        return "redirect:/products-page";
    }

    // ✅ Delete a product
    @PostMapping("/products-page/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        return "redirect:/products-page";
    }

    // ✅ Display edit form
    @GetMapping("/products-page/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        return "edit-product"; // edit-product.html
    }

    // ✅ Handle update form submission
    @PostMapping("/products-page/update/{id}")
    public String updateProduct(@PathVariable Long id, @ModelAttribute Product product) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));

        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setCategory(product.getCategory());

        productRepository.save(existingProduct);

        return "redirect:/products-page";
    }
}
