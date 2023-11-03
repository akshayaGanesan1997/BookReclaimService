/**
 * User.java:
 * 
 * Defines the "User" entity class for a book marketplace application.
 * It represents users within the marketplace and includes essential user information such as
 * a unique identifier, username, email, password, first name, last name, phone number, and available funds.
 *
 * Key Features:
 * - Annotated with @Entity to indicate it's a persistent entity in a database.
 * - Utilizes Lombok annotations for generating common methods like getters, setters, and constructors.
 * - Defines validation constraints using Jakarta Bean Validation annotations for various user fields.
 * - Establishes a one-to-many relationship with the "Transaction" entity, representing user transaction history.
 *
 * The class serves as the blueprint for storing and managing user data in the application's database.
 */

package com.books.bookmarketplace.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity class representing a User in the book marketplace application.
 */
@Entity // Indicates that this class is an entity that can be persisted in a database.
@Data // Lombok annotation that generates getters, setters, equals, hashCode, and toString methods.
@NoArgsConstructor // Lombok annotation for generating a no-argument constructor.
@AllArgsConstructor // Lombok annotation for generating an all-argument constructor.
@Builder // Lombok annotation for generating a builder pattern for creating instances of this class.
@Table(name = "Users") // Specifies the name of the table in the database that corresponds to this entity.
public class User {
    @Id // Indicates that the following field is the primary key for the table.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Specifies the strategy for generating primary key values.
    private Long userId; // Unique identifier for each user.

    @NotBlank(message = "Username is required") // Specifies that the username field must not be blank.
    private String username; // The username of the user.

    @Email(message = "Invalid email format") // Specifies that the email field must be in a valid email format.
    @NotBlank(message = "Email is required") // Specifies that the email field must not be blank.
    private String email; // The email address of the user.

    @Size(min = 6, message = "Password must be at least 6 characters") // Specifies the minimum size constraint for the password.
    @NotBlank(message = "Password is required") // Specifies that the password field must not be blank.
    private String password; // The password for the user.

    @NotBlank(message = "First name is required") // Specifies that the first name field must not be blank.
    private String firstName; // The first name of the user.

    @NotBlank(message = "Last name is required") // Specifies that the last name field must not be blank.
    private String lastName; // The last name of the user.

    @Pattern(regexp = "\\d{10}", message = "Phone number must be a 10-digit number") // Specifies a regular expression pattern for the phone number.
    private String phoneNumber; // The phone number of the user.

    @NotNull
    @DecimalMin(value = "0.0", message = "Funds must be a non-negative value") // Specifies that the funds field must be a non-negative decimal.
    private Double funds; // Represents the amount of funds the user has.

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL) // Indicates a one-to-many relationship with the Transaction entity.
    private List < Transaction > transactions = new ArrayList < > (); // Represents the list of transactions associated with the user.
}