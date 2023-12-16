package rest.onlineShop.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import rest.onlineShop.dto.CartItemDto;
import rest.onlineShop.dto.CartItemDto2;
import rest.onlineShop.exceptions.AccessDeniedException;
import rest.onlineShop.exceptions.CartItemNotFoundException;
import rest.onlineShop.models.CartItem;
import rest.onlineShop.security.PersonDetails;
import rest.onlineShop.services.CartItemService;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/items")
public class CartItemController {
    private final CartItemService cartItemService;

    @Autowired
    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<CartItemDto2>> getAll(@AuthenticationPrincipal PersonDetails personDetails) {
        List<CartItemDto2> cartItems = cartItemService.getAllNotTaken(personDetails.getUsername());
        return ResponseEntity.ok(cartItems);
    }

    private CartItemDto convertToDto(CartItem cartItem) {
        return new CartItemDto(cartItem.getId(), cartItem.getPerson().getUsername(), cartItem.getProduct().getId(), cartItem.getQuantity());
    }

    @PostMapping("/add")
    public ResponseEntity<CartItemDto> addCartItem(@RequestBody CartItemDto cartItemDto, @AuthenticationPrincipal PersonDetails personDetails) {
        CartItem cartItem = cartItemService.addCartItem(personDetails.getPerson(), cartItemDto.getProductId(), cartItemDto.getQuantity());
        CartItemDto responseDto = convertToDto(cartItem);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/quantity")
    public ResponseEntity<CartItemDto> updateCartItemQuantity(@PathVariable("id") int cartItemId,
                                                              @RequestParam int quantity) {
        CartItem cartItem = null;
        try {
            cartItem = cartItemService.updateCartItemQuantity(cartItemId, quantity);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        CartItemDto responseDto = convertToDto(cartItem);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable("id") int id, @AuthenticationPrincipal PersonDetails personDetails){
        try{
            cartItemService.deleteCartItem(id, personDetails.getPerson());
        }catch (CartItemNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (AccessDeniedException e){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok().build();
    }
}
