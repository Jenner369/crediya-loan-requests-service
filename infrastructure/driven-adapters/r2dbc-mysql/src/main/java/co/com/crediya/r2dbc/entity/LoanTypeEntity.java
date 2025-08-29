package co.com.crediya.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("loan_types")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoanTypeEntity {
    @Id
    private String code;
    private String name;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private BigDecimal interestRate;
    private Boolean autoApproval;
}
