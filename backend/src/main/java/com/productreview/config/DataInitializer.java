package com.productreview.config;

import com.productreview.entity.Product;
import com.productreview.repository.ProductRepository;
import com.productreview.util.ProductNameUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;

@Configuration
@Profile("dev")
public class DataInitializer {
    
    private final ProductRepository productRepository;

    public DataInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    @Bean
    public CommandLineRunner initData() {
        return args -> {
            if (productRepository.count() == 0) {
                // Electronics
                Product p1 = new Product();
                p1.setName(ProductNameUtil.normalizeProductName("iPhone 15 Pro"));
                p1.setDescription("Latest iPhone with A17 Pro chip, titanium design, and advanced camera system.");
                p1.setCategory("Electronics");
                p1.setPrice(new BigDecimal("999.99"));
                productRepository.save(p1);
                
                Product p2 = new Product();
                p2.setName(ProductNameUtil.normalizeProductName("Samsung Galaxy S24"));
                p2.setDescription("Flagship Android phone with AI features and stunning display.");
                p2.setCategory("Electronics");
                p2.setPrice(new BigDecimal("899.99"));
                productRepository.save(p2);
                
                Product p3 = new Product();
                p3.setName(ProductNameUtil.normalizeProductName("MacBook Pro 16\""));
                p3.setDescription("Powerful laptop with M3 chip, perfect for professionals.");
                p3.setCategory("Electronics");
                p3.setPrice(new BigDecimal("2499.99"));
                productRepository.save(p3);
                
                // Clothing
                Product p4 = new Product();
                p4.setName(ProductNameUtil.normalizeProductName("Nike Air Max 90"));
                p4.setDescription("Classic sneakers with comfortable cushioning and timeless design.");
                p4.setCategory("Clothing");
                p4.setPrice(new BigDecimal("120.00"));
                productRepository.save(p4);
                
                Product p5 = new Product();
                p5.setName(ProductNameUtil.normalizeProductName("Levi's 501 Jeans"));
                p5.setDescription("Original fit jeans, iconic straight leg design.");
                p5.setCategory("Clothing");
                p5.setPrice(new BigDecimal("89.99"));
                productRepository.save(p5);
                
                // Books
                Product p6 = new Product();
                p6.setName(ProductNameUtil.normalizeProductName("The Pragmatic Programmer"));
                p6.setDescription("Your journey to mastery in software development.");
                p6.setCategory("Books");
                p6.setPrice(new BigDecimal("39.99"));
                productRepository.save(p6);
                
                Product p7 = new Product();
                p7.setName(ProductNameUtil.normalizeProductName("Clean Code"));
                p7.setDescription("A Handbook of Agile Software Craftsmanship.");
                p7.setCategory("Books");
                p7.setPrice(new BigDecimal("49.99"));
                productRepository.save(p7);
                
                // Home & Kitchen
                Product p8 = new Product();
                p8.setName(ProductNameUtil.normalizeProductName("Dyson V15 Detect"));
                p8.setDescription("Cordless vacuum with laser technology and powerful suction.");
                p8.setCategory("Home & Kitchen");
                p8.setPrice(new BigDecimal("749.99"));
                productRepository.save(p8);
                
                Product p9 = new Product();
                p9.setName(ProductNameUtil.normalizeProductName("Instant Pot Duo"));
                p9.setDescription("7-in-1 pressure cooker, slow cooker, rice cooker, and more.");
                p9.setCategory("Home & Kitchen");
                p9.setPrice(new BigDecimal("99.99"));
                productRepository.save(p9);
                
                // Sports & Outdoors
                Product p10 = new Product();
                p10.setName(ProductNameUtil.normalizeProductName("Yoga Mat Premium"));
                p10.setDescription("Non-slip yoga mat with carrying strap, 6mm thickness.");
                p10.setCategory("Sports & Outdoors");
                p10.setPrice(new BigDecimal("34.99"));
                productRepository.save(p10);
            }
        };
    }
}


