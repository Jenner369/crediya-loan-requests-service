package co.com.crediya.r2dbc.config.callback;

import co.com.crediya.r2dbc.contract.HasUUID;
import org.reactivestreams.Publisher;
import org.springframework.data.r2dbc.mapping.event.BeforeConvertCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class UUIDEntityCallback implements BeforeConvertCallback<HasUUID> {
    @Override
    public Publisher<HasUUID> onBeforeConvert(HasUUID entity, SqlIdentifier table) {
        if (entity.getId() == null) {
            entity.setId(java.util.UUID.randomUUID());
        }
        return Mono.just(entity);
    }
}
