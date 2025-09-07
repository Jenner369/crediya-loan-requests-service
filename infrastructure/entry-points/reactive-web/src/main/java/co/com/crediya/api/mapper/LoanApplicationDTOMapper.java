package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.loanapplication.*;
import co.com.crediya.common.PageRequest;
import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.usecase.changeloanapplicationstatus.ChangeLoanApplicationStatusUseCaseInput;
import co.com.crediya.usecase.listloanapplicationswithdetails.ListLoanApplicationsWithDetailsUseCaseInput;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {StatusDTOMapper.class, LoanTypeDTOMapper.class, UserDTOMapper.class})
public interface LoanApplicationDTOMapper {
    LoanApplication toModelFromRegisterDTO(RegisterLoanApplicationDTO registerLoanApplicationDTO);

    ShortLoanApplicationDTO toShortDTOFromModel(LoanApplication loanApplication);

    @Mapping(target = "page", source = "dto", qualifiedByName = "toPageRequestFromSearchDTO")
    ListLoanApplicationsWithDetailsUseCaseInput toListInputFromSearchDTO(SearchLoanApplicationDTO dto);

    LoanApplicationDTO toDTOFromModel(LoanApplication loanApplication);

    ChangeLoanApplicationStatusUseCaseInput toChangeStatusInputFromDTO(ChangeLoanApplicationStatusDTO dto);

    // Utilities
    @Named("toPageRequestFromSearchDTO")
    default PageRequest toPageRequestFromSearchDTO(SearchLoanApplicationDTO dto) {
        return new PageRequest(dto.page(), dto.size());
    }
}
