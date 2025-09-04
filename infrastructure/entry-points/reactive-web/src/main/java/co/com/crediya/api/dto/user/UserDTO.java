package co.com.crediya.api.dto.user;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    String id;
    String name;
    String lastName;
    String email;
    String identityDocument;
    BigDecimal baseSalary;
}
