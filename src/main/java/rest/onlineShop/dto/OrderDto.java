package rest.onlineShop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private int id;
    private int personId;
    private List<CartItemDto> cartItems;
    private String status;
    private LocalDateTime createdAt;
    private String shippingAddress;
}
