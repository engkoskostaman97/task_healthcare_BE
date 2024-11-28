package healthcare.healthcares.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import healthcare.healthcares.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}


