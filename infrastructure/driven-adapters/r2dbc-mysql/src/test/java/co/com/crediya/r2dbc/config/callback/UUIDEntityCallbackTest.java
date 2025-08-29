package co.com.crediya.r2dbc.config.callback;

import co.com.crediya.r2dbc.entity.LoanApplicationEntity;
import org.junit.jupiter.api.Test;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UUIDEntityCallbackTest {

    private final UUIDEntityCallback callback = new UUIDEntityCallback();

    @Test
    void shouldGenerateUuidIfNull() {
        var entity = new LoanApplicationEntity();

        StepVerifier.create(callback.onBeforeConvert(entity, SqlIdentifier.unquoted("test_table")))
                .assertNext(result -> {
                    assertThat(result.getId()).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    void shouldKeepExistingUuid() {
        var entity = new LoanApplicationEntity();
        UUID existingId = UUID.randomUUID();
        entity.setId(existingId);

        StepVerifier.create(callback.onBeforeConvert(entity, SqlIdentifier.unquoted("test_table")))
                .assertNext(result -> {
                    assertThat(result.getId()).isEqualTo(existingId);
                })
                .verifyComplete();
    }
}
