package com.example.e_commerce_backend.repository;

import com.example.e_commerce_backend.ecommerce.Product;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductRepository extends JpaRepository<Product, Long> {

}