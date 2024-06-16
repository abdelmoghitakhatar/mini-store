package com.small.store.controllers;

import com.small.store.models.Product;
import com.small.store.models.ProductDTO;
import com.small.store.repositories.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/products")
public class productController {

    private final ProductRepository productRepository;

    public productController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("")
    public String getAllProducts(Model model){
        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);
        return "products/index";
    }

    @GetMapping("/create")
    public String createProductPage(Model model){
        ProductDTO productDTO = new ProductDTO();
        model.addAttribute("productDTO", productDTO);
        return "products/createProduct";
    }

    @PostMapping("/create")
    public String createNewProduct(@Valid @ModelAttribute ProductDTO productDTO, BindingResult result){

        int x = 3;
        System.out.println(x);
        if(productDTO.getFile().isEmpty()){
            result.addError(
                    new FieldError("productDTO", "file", "The image file is required")
            );
        }
        if (result.hasErrors()){
            return "products/createProduct";
        }

        return "redirect:/products";
    }
}
