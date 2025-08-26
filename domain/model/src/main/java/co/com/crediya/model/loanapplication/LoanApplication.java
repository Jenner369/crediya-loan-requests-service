package co.com.crediya.model.loanapplication;
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
    private String term;
    private String identityDocument;
    private UUID statusId;
    private UUID loanTypeId;
}
