package rest.onlineShop.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rest.onlineShop.dto.CartItemDto2;
import rest.onlineShop.exceptions.AccessDeniedException;
import rest.onlineShop.exceptions.CartItemNotFoundException;
import rest.onlineShop.models.Person;
import rest.onlineShop.repositories.CartItemRepository;
import rest.onlineShop.models.CartItem;
import rest.onlineShop.models.Product;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final PersonDetailService personDetailService;
    private final ProductService productService;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CartItemService(CartItemRepository cartItemRepository, PersonDetailService personDetailService, ProductService productService, JdbcTemplate jdbcTemplate) {
        this.cartItemRepository = cartItemRepository;
        this.personDetailService = personDetailService;
        this.productService = productService;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CartItem> getAllBy(String username) {
        return cartItemRepository.findByPersonUsername(username);
    }

    @Transactional
    public void deleteCartItem(int cartItemId, Person person) {
        Optional<CartItem> cartItemOptional = cartItemRepository.findById(cartItemId);
        if (cartItemOptional.isPresent()) {
            CartItem cartItem = cartItemOptional.get();
            if (cartItem.getPerson().getUsername().equals(person.getUsername())) {
                cartItemRepository.delete(cartItem);
                return;
            } else {
                throw new AccessDeniedException("Access denied");
            }
        }
        throw new CartItemNotFoundException("No such item");
    }

    public void deleteItem(int cartItemId){
        CartItem cartItem = cartItemRepository.findById(cartItemId).stream().findAny().orElse(null);
        cartItemRepository.delete(cartItem);
    }
    public List<CartItemDto2> getAllNotTaken(String username){
        return jdbcTemplate.query("SELECT * FROM cart_item WHERE username = ? AND order_id IS NULL", new Object[]{username}, new BeanPropertyRowMapper<>(CartItemDto2.class));
    }
    public CartItem addCartItem(Person person, int productId, int quantity){
        Product product = productService.getOneById(productId).stream().findAny().orElse(null);
        if(product == null){
            throw new NoSuchElementException();
        } else if (product.getCountLeft() - quantity < 0) {
            throw new IllegalArgumentException();
        }
        CartItem cartItem = new CartItem();
        cartItem.setPerson(person);
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);
    }
    public Optional<CartItem> getCartItemById(int id){
        return cartItemRepository.findById(id);
    }
    public CartItem updateCartItemQuantity(int cartItemId, int quantity){
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElse(null);
        if(cartItem == null){
            throw new NoSuchElementException();
        } else if (cartItem.getProduct().getCountLeft() - quantity < 0) {
            throw new IllegalArgumentException();
        }
        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);
    }
}
