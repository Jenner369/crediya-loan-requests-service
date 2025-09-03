package co.com.crediya.api.dto.loanapplication;

import co.com.crediya.api.dto.loantype.LoanTypeDTO;
import co.com.crediya.api.dto.status.StatusDTO;
import co.com.crediya.api.dto.user.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoanApplicationDTO {
    private String id;
    private BigDecimal amount;
    private Integer term;
    private String identityDocument;
    private String email;

    private StatusDTO status;
    private LoanTypeDTO loanType;
    private UserDTO user;

    private BigDecimal totalMonthlyDebtApproved;
}
