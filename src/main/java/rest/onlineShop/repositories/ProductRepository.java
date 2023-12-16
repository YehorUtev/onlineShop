package rest.onlineShop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rest.onlineShop.models.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
}
