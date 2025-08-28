package co.com.crediya.model.loanapplication;
import co.com.crediya.model.user.User;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanApplication {
    private UUID id;
    private BigDecimal amount;
    private Integer term;
    private String identityDocument;
    private String email;
    private String statusCode;
    private String loanTypeCode;

    public void setUserDetails(User user) {
        if (user != null) {
            this.identityDocument = user.getIdentityDocument();
            this.email = user.getEmail();
        }
    }
}
