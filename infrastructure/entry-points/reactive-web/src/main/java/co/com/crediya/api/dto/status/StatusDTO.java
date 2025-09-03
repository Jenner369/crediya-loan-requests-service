package co.com.crediya.api.dto.status;

import co.com.crediya.model.status.enums.Statuses;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusDTO {
    String code;
    String name;
    String description;

    public static StatusDTO fromEnum(Statuses status) {
        return StatusDTO.builder()
                .code(status.getCode())
                .name(status.getName())
                .description(status.getDescription())
                .build();
    }
}