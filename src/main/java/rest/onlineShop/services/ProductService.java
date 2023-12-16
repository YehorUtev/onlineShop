package rest.onlineShop.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rest.onlineShop.models.Product;
import rest.onlineShop.repositories.ProductRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAll(){
        return productRepository.findAll();
    }
    public Optional<Product> getOneById(int id){
        return productRepository.findById(id);
    }
    public Product save(Product product){
        return productRepository.save(product);
    }
    public Product update(Product product, int id){
        Optional<Product> product1 = getOneById(id);
        if(product1.isEmpty()){
            throw new NoSuchElementException("No product found with id " + id);
        }
        product.setId(id);
        return productRepository.save(product);
    }
    public void delete(int id){
        productRepository.deleteById(id);
    }
}
