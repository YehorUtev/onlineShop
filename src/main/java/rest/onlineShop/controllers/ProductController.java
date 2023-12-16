package rest.onlineShop.controllers;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import rest.onlineShop.dto.ProductDto;
import rest.onlineShop.dto.ProductListDto;
import rest.onlineShop.models.Product;
import rest.onlineShop.security.PersonDetails;
import rest.onlineShop.services.ProductService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping()
    public ResponseEntity<?> getAllProducts(@AuthenticationPrincipal PersonDetails personDetails) {
        List<Product> products = productService.getAll();
        if (personDetails.getPerson().getRole().equals("ROLE_ADMIN")) {
            return new ResponseEntity<>(new ProductListDto(products, true), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ProductListDto(products, false), HttpStatus.OK);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") int productId, @AuthenticationPrincipal PersonDetails personDetails) {
        Optional<Product> product = productService.getOneById(productId);
        if (product.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (personDetails.getPerson().getRole().equals("ROLE_ADMIN")) {
            return new ResponseEntity<>(new ProductDto(product.get(), true), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ProductDto(product.get(), false), HttpStatus.OK);
        }
    }
    @PostMapping("/add")
    public ResponseEntity<?> saveProduct(@Valid @RequestBody Product product, BindingResult br){
        if(br.hasErrors()){
            return new ResponseEntity<>(br.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(productService.save(product), HttpStatus.CREATED);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") int id){
        try{
            productService.delete(id);
        }catch (EmptyResultDataAccessException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PatchMapping("/edit/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable("id") int id, @Valid @RequestBody Product product, BindingResult br){
        if(br.hasErrors()){
            return new ResponseEntity<>(br.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        try{
            productService.update(product, id);
        }catch (NoSuchElementException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
