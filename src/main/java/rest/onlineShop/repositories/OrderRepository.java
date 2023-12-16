package rest.onlineShop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rest.onlineShop.models.Order;
import rest.onlineShop.models.OrderStatus;
import java.util.List;

import java.time.LocalDateTime;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findOrdersByStatusAndCreatedAtBefore(OrderStatus status, LocalDateTime createdAtBefore);
    List<Order> getOrdersByPersonId(int id);
    List<Order> findAllByStatus(OrderStatus status);
}
