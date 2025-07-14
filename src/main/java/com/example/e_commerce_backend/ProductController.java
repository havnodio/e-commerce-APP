package com.example.e_commerce_backend;

import com.example.e_commerce_backend.ecommerce.Product;
import com.example.e_commerce_backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Controller
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    // Show products with pagination
    @GetMapping("/products-page")
    public String showProducts(Model model,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(required = false) String keyword) {
        int pageSize = 5;
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Product> productPage;

        if (keyword != null && !keyword.isEmpty()) {
            productPage = productRepository.findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(keyword, keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else {
            productPage = productRepository.findAll(pageable);
        }

        model.addAttribute("productPage", productPage);
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("product", new Product());

        return "products";
    }


    // Add a product (with optional image)
    @PostMapping("/products-page")
    public String addProduct(@ModelAttribute Product product,
                             @RequestParam(value = "image", required = false) MultipartFile image) {
        if (image != null && !image.isEmpty()) {
            String filename = saveImage(image);
            product.setImageFilename(filename);
        }
        productRepository.save(product);
        return "redirect:/products-page";
    }

    // Delete a product
    @PostMapping("/products-page/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        return "redirect:/products-page";
    }

    // Display edit form
    @GetMapping("/products-page/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        return "edit-product";
    }

    // Handle update form submission
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

    private String saveImage(MultipartFile image) {
        try {
            String uploadDir = "uploads/";
            String originalFilename = image.getOriginalFilename();
            String uniqueFilename = System.currentTimeMillis() + "_" + originalFilename;
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return uniqueFilename;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
