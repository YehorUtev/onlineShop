package rest.onlineShop.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import rest.onlineShop.models.CartItem;
import rest.onlineShop.models.OrderStatus;
import rest.onlineShop.repositories.OrderRepository;
import rest.onlineShop.models.Order;
import rest.onlineShop.models.Product;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartItemService cartItemService;
    private final ProductService productService;

    @Autowired
    public OrderService(OrderRepository orderRepository, CartItemService cartItemService, ProductService productService) {
        this.orderRepository = orderRepository;
        this.cartItemService = cartItemService;
        this.productService = productService;
    }
    @Scheduled(fixedRate = 36000)
    public void cancelUnpaidOrder(){
        LocalDateTime localDateTime = LocalDateTime.now().minusMinutes(10);
        List<Order> unpaidOrder = orderRepository.findOrdersByStatusAndCreatedAtBefore(OrderStatus.PENDING_PAYMENT, localDateTime);
        unpaidOrder.forEach(order -> {
            List<CartItem> items = order.getCartItemList();
            items.forEach(item -> {
                int productId = item.getProduct().getId();
                Product product = productService.getOneById(productId).get();
                int countLeft = product.getCountLeft();
                int count = item.getQuantity();
                product.setCountLeft(countLeft + count);
                productService.update(product, productId);
                cartItemService.deleteItem(item.getId());
            });
            orderRepository.delete(order);
        });
    }
    public Order createOrder(Order order){
        return orderRepository.save(order);
    }
    public List<Order> getAllOrders(){
        return orderRepository.findAll();
    }
    public Optional<Order> getOrderById(int orderId){
        return orderRepository.findById(orderId);
    }
    public Optional<Order> updateOrderStatus(int orderId, OrderStatus orderStatus){
        Optional<Order> order = orderRepository.findById(orderId);
        order.ifPresent(o -> {
            o.setStatus(orderStatus);
            orderRepository.save(o);
        });
        return order;
    }
    public List<Order> getOrdersByStatus(OrderStatus orderStatus){
        return orderRepository.findAllByStatus(orderStatus);
    }
    public List<Order> getOrdersByPersonId(int personId){
        return orderRepository.getOrdersByPersonId(personId);
    }
}
