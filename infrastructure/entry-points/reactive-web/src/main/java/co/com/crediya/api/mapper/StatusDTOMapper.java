package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.status.StatusDTO;
import co.com.crediya.model.status.Status;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StatusDTOMapper {
    StatusDTO toDTO(Status status);
}
