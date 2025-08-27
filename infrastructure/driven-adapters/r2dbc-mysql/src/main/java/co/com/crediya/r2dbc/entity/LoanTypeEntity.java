package co.com.crediya.r2dbc.entity;

import co.com.crediya.r2dbc.contract.HasUUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Table("loan_types")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoanTypeEntity implements HasUUID {
    @Id
    private UUID id;
    private String name;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private BigDecimal interestRate;
    private Boolean autoApproval;
}
