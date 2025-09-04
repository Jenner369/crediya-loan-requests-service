package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.loantype.LoanTypeDTO;
import co.com.crediya.model.loantype.LoanType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoanTypeDTOMapper {
    LoanTypeDTO toDTO(LoanType loanType);
}
