package co.com.crediya.api.dto.loantype;

import co.com.crediya.model.loantype.enums.LoanTypes;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanTypeDTO {
    String code;
    String name;
    BigDecimal interestRate;

    public static LoanTypeDTO fromEnum(LoanTypes type) {
        return LoanTypeDTO.builder()
                .code(type.getCode())
                .name(type.getName())
                .interestRate(type.getInterestRate())
                .build();
    }
}