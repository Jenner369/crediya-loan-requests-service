package co.com.crediya.r2dbc.entity;

import co.com.crediya.r2dbc.contract.HasUUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Table("loan_applications")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoanApplicationEntity implements HasUUID {
    @Id
    private UUID id;
    private BigDecimal amount;
    private Integer term;
    private String identityDocument;
    private String email;
    private String statusCode;
    private String loanTypeCode;
    private LocalDateTime createdAt;
}
