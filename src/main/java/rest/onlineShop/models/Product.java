package rest.onlineShop.models;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    @NotEmpty(message = "Name should not be empty")
    @Size(min = 2, max = 20, message = "Name should be 2 to 20 characters long")
    private String name;
    @Column(name = "description")
    @NotEmpty(message = "Description should not be empty")
    private String description;
    @Column(name = "count_Left")
    @Min(value = 0, message = "Count should not be less than 0")
    private int countLeft;
    @Column(name = "price")
    @Min(value = 0, message = "Price should not be less than 0")
    private double price;
    @Column(name = "photo", columnDefinition = "bytea")
    private byte[] photo;
}
