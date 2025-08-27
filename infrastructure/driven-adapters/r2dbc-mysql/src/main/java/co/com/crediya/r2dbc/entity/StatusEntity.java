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

@Table("statuses")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StatusEntity implements HasUUID {
    @Id
    private UUID id;
    private String name;
    private String description;
}
