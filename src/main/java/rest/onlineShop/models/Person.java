package rest.onlineShop.models;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@AllArgsConstructor
@Table(name = "person")
public class Person implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "username")
    @NotEmpty(message = "Username should not be empty")
    @Size(min = 5, max = 30, message = "Username should be from 5 to 30 characters long")
    private String username;
    @Column(name = "first_name")
    @NotEmpty(message = "First name should not be empty")
    @Size(min = 2, max = 20, message = "First name should be from 2 to 20 characters long")
    private String firstName;
    @Column(name = "last_name")
    @NotEmpty(message = "Last name should not be empty")
    @Size(min = 2, max = 20, message = "Last name should be from 2 to 20 characters long")
    private String lastName;
    @Column(name = "password")
    private String password;
    @Column(name = "email")
    private String email;
    @Column(name = "role")
    private String role;
}
