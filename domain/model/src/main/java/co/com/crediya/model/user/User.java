package co.com.crediya.model.user;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private UUID id;
    private String name;
    private String lastName;
    private String email;
    private String identityDocument;
    private BigDecimal baseSalary;
    private String roleId;
}
