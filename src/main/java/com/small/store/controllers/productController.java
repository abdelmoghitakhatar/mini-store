package com.small.store.controllers;

import com.small.store.models.Product;
import com.small.store.models.ProductDTO;
import com.small.store.repositories.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/products")
public class productController {

    private final ProductRepository productRepository;
    private final String DIRECTORY = "public/images/";

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

        if(productDTO.getFile().isEmpty()){
            result.addError(
                    new FieldError("productDTO", "file", "The image file is required")
            );
        }
        if (result.hasErrors()){
            return "products/createProduct";
        }

        MultipartFile imageFile = productDTO.getFile();
        Date currentDate = new Date();
        String imageName =
                currentDate.toString().replace(' ', '_').replace(':', '_') +
                        "_" + imageFile.getOriginalFilename();
        try {

            Path path = Paths.get(DIRECTORY);

            if(!Files.exists(path)){
                Files.createDirectories(path);
            }

            InputStream inputStream = imageFile.getInputStream();
            Files.copy(inputStream, path.resolve(imageName), StandardCopyOption.REPLACE_EXISTING);

        } catch(Exception e){
            System.out.println("Exception" + e.getMessage());
        }

        Product product = new Product();

        product.setName(productDTO.getName());
        product.setBrand(productDTO.getBrand());
        product.setCategory(productDTO.getCategory());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setImageFileName(imageName);
        product.setCreationDate(currentDate);

        productRepository.save(product);


        return "redirect:/products";
    }

    @GetMapping("/delete")
    public String deleteProduct(@RequestParam("id") Long id){
        if(id == null){
            return "redirect:/products";
        }

        Optional<Product> productOptional = productRepository.findById(id);

        try {
            Path path = Paths.get(DIRECTORY);
            Files.delete(path.resolve(productOptional.get().getImageFileName()));
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

        productRepository.deleteById(id);
        return "redirect:/products";
    }

    @GetMapping("/edit")
    public String displayUpdateProduct(@RequestParam("id") Long id, Model model){
        if(id == null){
            return "redirect:/products";
        }

        Optional<Product> productOptional = productRepository.findById(id);
        ProductDTO productDTO = new ProductDTO();
        Product product = new Product();
        if(productOptional.isPresent()){
            product = productOptional.get();
        }
        productDTO.setName(product.getName());
        productDTO.setBrand(product.getBrand());
        productDTO.setCategory(product.getCategory());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(product.getPrice());

        model.addAttribute("productDTO", productDTO);
        model.addAttribute("product", product);
        
        return "products/editProduct";
    }

    @PostMapping("/edit")
    public String updateNewProduct(@RequestParam("id") Long id,@Valid @ModelAttribute ProductDTO productDTO,  BindingResult result, Model model){


        Optional<Product> productOptional = productRepository.findById(id);
        Product product = new Product();
        if(productOptional.isPresent()){
            product = productOptional.get();
        }
        model.addAttribute("product", product);

        if (result.hasErrors()){
            return "products/editProduct";
        }

        if(!productDTO.getFile().isEmpty()) {
            MultipartFile imageFile = productDTO.getFile();
            String imageName = product.getImageFileName();
            try {
                Path path = Paths.get(DIRECTORY);
                InputStream inputStream = imageFile.getInputStream();
                Files.copy(inputStream, path.resolve(imageName), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                System.out.println("Exception" + e.getMessage());
            }
        }

        product.setName(productDTO.getName());
        product.setBrand(productDTO.getBrand());
        product.setCategory(productDTO.getCategory());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        productRepository.save(product);


        return "redirect:/products";
    }
}
