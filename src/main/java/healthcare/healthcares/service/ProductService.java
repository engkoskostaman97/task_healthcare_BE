package healthcare.healthcares.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import healthcare.healthcares.model.Product;
import healthcare.healthcares.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Slf4j
@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    private final String uploadDir;

    public ProductService() {
        this.uploadDir = System.getProperty("file.upload-dir", "D:/healthcare/uploads");
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
            System.out.println("Created upload directory at: " + uploadDir);
        }
    }

    public Product saveProduct(Product product, MultipartFile imageFile) throws IOException {
        if (!imageFile.isEmpty()) {
            String fileName = imageFile.getOriginalFilename();
            String filePath = Paths.get(uploadDir, fileName).toString();
            System.out.println("Saving file to: " + filePath);
            imageFile.transferTo(new File(filePath)); // Simpan file di folder absolut
            
            // Simpan hanya nama file ke dalam imageUrl
            product.setImageUrl(fileName);
        }
        return productRepository.save(product);
    }
    

    public Product updateProduct(Long id, Product updatedProduct, MultipartFile imageFile) throws IOException {
    // Cari produk berdasarkan ID
    Product existingProduct = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product with ID " + id + " not found"));

    // Update atribut produk
    existingProduct.setName(updatedProduct.getName());
    existingProduct.setSku(updatedProduct.getSku());
    existingProduct.setDescription(updatedProduct.getDescription());
    existingProduct.setPrice(updatedProduct.getPrice());

    // Validasi direktori upload
    Path uploadDirectory = Paths.get(uploadDir).toAbsolutePath();
    if (!Files.exists(uploadDirectory)) {
        Files.createDirectories(uploadDirectory);
        log.info("Created upload directory at: {}", uploadDirectory);
    }

    // Jika ada file gambar baru
    if (imageFile != null && !imageFile.isEmpty()) {
        String originalFilename = imageFile.getOriginalFilename();
        if (originalFilename != null) {
            // Hapus file lama jika ada
            String oldImagePath = existingProduct.getImageUrl();
            if (oldImagePath != null) {
                Path oldFilePath = uploadDirectory.resolve(oldImagePath);
                try {
                    Files.deleteIfExists(oldFilePath);
                    log.info("Deleted old image: {}", oldFilePath);
                } catch (IOException ex) {
                    log.error("Failed to delete old image: {}", oldFilePath, ex);
                }
            }

            // Simpan file baru
            String sanitizedFilename = Paths.get(originalFilename).getFileName().toString(); // Sanitasi nama file
            Path newFilePath = uploadDirectory.resolve(sanitizedFilename);
            try {
                Files.copy(imageFile.getInputStream(), newFilePath, StandardCopyOption.REPLACE_EXISTING);
                log.info("Saved new image to: {}", newFilePath);

                // Update URL gambar di database
                existingProduct.setImageUrl(sanitizedFilename);
            } catch (IOException ex) {
                log.error("Failed to save new image: {}", newFilePath, ex);
                throw new IOException("Error saving new image file", ex);
            }
        }
    }

    // Simpan perubahan ke database
    return productRepository.save(existingProduct);
}




    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
