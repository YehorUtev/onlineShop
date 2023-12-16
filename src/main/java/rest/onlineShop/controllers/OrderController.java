package rest.onlineShop.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rest.onlineShop.dto.CartItemDto;
import rest.onlineShop.dto.OrderDto;
import rest.onlineShop.exceptions.AccessDeniedException;
import rest.onlineShop.exceptions.CartItemNotFoundException;
import rest.onlineShop.models.CartItem;
import rest.onlineShop.models.Order;
import rest.onlineShop.models.OrderStatus;
import rest.onlineShop.models.Product;
import rest.onlineShop.security.PersonDetails;
import rest.onlineShop.services.CartItemService;
import rest.onlineShop.services.OrderService;
import rest.onlineShop.services.PersonDetailService;
import rest.onlineShop.services.ProductService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final PersonDetailService personDetailService;
    private final ProductService productService;
    private final CartItemService cartItemService;

    @Autowired
    public OrderController(OrderService orderService, PersonDetailService personDetailService, ProductService productService, CartItemService cartItemService) {
        this.orderService = orderService;
        this.personDetailService = personDetailService;
        this.productService = productService;
        this.cartItemService = cartItemService;
    }

    @PostMapping()
    public ResponseEntity<OrderDto> createOrder(@AuthenticationPrincipal PersonDetails personDetails,
                                                @RequestBody List<CartItemDto> cartItemDtos,
                                                @RequestParam("shippingAddress") String shippingAddress) {
        Order order = new Order();
        order.setPerson(personDetails.getPerson());
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setCreatedAt(LocalDateTime.now());
        order.setShippingAddress(shippingAddress);
        for (CartItemDto cartItemDto : cartItemDtos) {
            int productId = cartItemDto.getProductId();
            Optional<Product> productOptional = productService.getOneById(productId);
            if(productOptional.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            int countLeft = productOptional.get().getCountLeft();
            int count = cartItemDto.getQuantity();
            if(countLeft < count){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            Product product = productOptional.get();
            product.setCountLeft(countLeft - count);
            productService.update(product, productId);
            try{
                cartItemService.deleteCartItem(cartItemDto.getId(), personDetails.getPerson());
            }catch (CartItemNotFoundException e){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }catch (AccessDeniedException e){
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        List<CartItem> cartItems = cartItemDtos.stream()
                .map(dto -> {
                    Product product = productService.getOneById(dto.getProductId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found"));
                    CartItem cartItem = new CartItem();
                    cartItem.setProduct(product);
                    cartItem.setQuantity(dto.getQuantity());
                    cartItem.setPerson(personDetails.getPerson());
                    return cartItem;
                })
                .collect(Collectors.toList());
        order.setCartItemList(cartItems);
        Order savedOrder = orderService.createOrder(order);
        OrderDto orderDto = convertOrderToOrderDto(savedOrder);
        return new ResponseEntity<>(orderDto, HttpStatus.CREATED);
    }
    @GetMapping("/all")
    public ResponseEntity<?> getAll(){
        List<Order> orders = orderService.getAllOrders();
        List<OrderDto> orderDtos = orders.stream()
                .map(this::convertOrderToOrderDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(orderDtos, HttpStatus.OK);
    }
    @GetMapping()
    public ResponseEntity<?> getAllOrdersByUser(@AuthenticationPrincipal PersonDetails personDetails){
        List<Order> orders = orderService.getOrdersByPersonId(personDetails.getPerson().getId());
        List<OrderDto> orderDtos = orders.stream()
                .map(this::convertOrderToOrderDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(orderDtos, HttpStatus.OK);
    }
    @GetMapping("/all/status")
    public ResponseEntity<?> getAllByStatus(@RequestParam("status") OrderStatus orderStatus){
        List<Order> orders = orderService.getOrdersByStatus(orderStatus);
        List<OrderDto> orderDtos = orders.stream()
                .map(this::convertOrderToOrderDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(orderDtos, HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable("id") int id, @AuthenticationPrincipal PersonDetails personDetails){
        Optional<Order> orderOptional = orderService.getOrderById(id);
        if(orderOptional.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Order order = orderOptional.get();
        if(order.getPerson().getId() != personDetails.getPerson().getId() && !"ROLE_ADMIN".equals(personDetails.getPerson().getRole())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        OrderDto orderDto = convertOrderToOrderDto(order);
        return new ResponseEntity<>(orderDto, HttpStatus.OK);
    }
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateOrder(@PathVariable("id") int id, @RequestParam("status") OrderStatus status){
        Optional<Order> orderOptional = orderService.updateOrderStatus(id, status);
        if(orderOptional.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(convertOrderToOrderDto(orderOptional.get()), HttpStatus.OK);
    }
    private OrderDto convertOrderToOrderDto(Order order){
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setPersonId(order.getPerson().getId());
        orderDto.setCartItems(order.getCartItemList().stream()
                .map(this::convertCartItemToCartItemDto)
                .collect(Collectors.toList()));
        orderDto.setStatus(order.getStatus().toString());
        orderDto.setCreatedAt(order.getCreatedAt());
        orderDto.setShippingAddress(order.getShippingAddress());
        return orderDto;
    }
    private CartItemDto convertCartItemToCartItemDto(CartItem cartItem){
        return new CartItemDto(
                cartItem.getId(), cartItem.getPerson().getUsername(), cartItem.getProduct().getId(), cartItem.getQuantity()
        );
    }
}
