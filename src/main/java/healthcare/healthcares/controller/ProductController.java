package healthcare.healthcares.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import healthcare.healthcares.model.Product;
import healthcare.healthcares.service.ProductService;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<Product> createProduct(
        @RequestParam("name") String name,
        @RequestParam("sku") String sku,
        @RequestParam("description") String description,
        @RequestParam("price") Double price,
        @RequestParam("image") MultipartFile image) throws IOException {
    log.info("Received Image: {}", image.getOriginalFilename());
    
    // Validasi ukuran file atau tipe file jika diperlukan
    if (image.isEmpty()) {
        throw new IllegalArgumentException("Image file cannot be empty");
    }
    
    // Buat instance Product menggunakan data dari parameter
    Product product = new Product();
    product.setName(name);
    product.setSku(sku);
 
    product.setDescription(description);
    product.setPrice(price);
    
    // Simpan produk dan file menggunakan service
    Product savedProduct = productService.saveProduct(product, image);
    
    return ResponseEntity.ok(savedProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
        @PathVariable Long id,
        @RequestParam("name") String name,
        @RequestParam("sku") String sku,
        @RequestParam("description") String description,
        @RequestParam("price") Double price,
        @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {
    
    Product updatedProduct = new Product();
    updatedProduct.setName(name);
    updatedProduct.setSku(sku);
    updatedProduct.setDescription(description);
    updatedProduct.setPrice(price);

    Product product = productService.updateProduct(id, updatedProduct, image);
    return ResponseEntity.ok(product);
    }


    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
