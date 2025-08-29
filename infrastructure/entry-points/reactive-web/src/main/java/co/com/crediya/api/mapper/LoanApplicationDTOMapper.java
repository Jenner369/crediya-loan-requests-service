package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.loanapplication.RegisterLoanApplicationDTO;
import co.com.crediya.api.dto.loanapplication.ShortLoanApplicationDTO;
import co.com.crediya.model.loanapplication.LoanApplication;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoanApplicationDTOMapper {
    LoanApplication toModelFromRegisterDTO(RegisterLoanApplicationDTO registerLoanApplicationDTO);
    ShortLoanApplicationDTO toShortDTOFromModel(LoanApplication loanApplication);
}
