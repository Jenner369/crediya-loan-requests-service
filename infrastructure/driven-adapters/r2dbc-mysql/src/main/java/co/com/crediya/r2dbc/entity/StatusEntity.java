package co.com.crediya.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("statuses")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StatusEntity {
    @Id
    private String code;
    private String name;
    private String description;
}
