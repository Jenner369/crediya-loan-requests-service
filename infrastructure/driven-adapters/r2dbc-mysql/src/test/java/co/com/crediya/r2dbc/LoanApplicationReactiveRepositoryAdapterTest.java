package co.com.crediya.r2dbc;

import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loantype.enums.LoanTypes;
import co.com.crediya.model.status.enums.Statuses;
import co.com.crediya.r2dbc.adapter.LoanApplicationReactiveRepositoryAdapter;
import co.com.crediya.r2dbc.entity.LoanApplicationEntity;
import co.com.crediya.r2dbc.repository.LoanApplicationReactiveRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanApplicationReactiveRepositoryAdapterTest {
    @InjectMocks
    LoanApplicationReactiveRepositoryAdapter repositoryAdapter;

    @Mock
    LoanApplicationReactiveRepository repository;

    @Mock
    ObjectMapper mapper;

    private final UUID sampleId = UUID.randomUUID();
    private final LoanApplicationEntity sampleEntity =
            new LoanApplicationEntity(
                    sampleId,
                    new BigDecimal("1000000"),
                    12,
                    "12345678",
                    "jennerjose369@gmail.com",
                    Statuses.PENDING.getCode(),
                    LoanTypes.BUSINESS_LOAN.getCode()
            );

    private final LoanApplication sampleLoanApplication =
            new LoanApplication(
                    sampleId,
                    new BigDecimal("1000000"),
                    12,
                    "12345678",
                    "jennerjose369@gmail.com",
                    Statuses.PENDING.getCode(),
                    LoanTypes.BUSINESS_LOAN.getCode()
            );

    @Test
    void mustSaveLoanApplication() {
        when(repository.save(any(LoanApplicationEntity.class))).thenReturn(Mono.just(sampleEntity));
        when(mapper.map(any(LoanApplication.class), eq(LoanApplicationEntity.class))).thenReturn(sampleEntity);
        when(mapper.map(sampleEntity, LoanApplication.class)).thenReturn(sampleLoanApplication);

        StepVerifier.create(repositoryAdapter.save(sampleLoanApplication))
                .expectNext(sampleLoanApplication)
                .verifyComplete();
    }

    @Test
    void mustFindById() {
        when(repository.findById(sampleId)).thenReturn(Mono.just(sampleEntity));
        when(mapper.map(sampleEntity, LoanApplication.class)).thenReturn(sampleLoanApplication);

        var result = repositoryAdapter.findById(sampleId);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.equals(sampleLoanApplication))
                .verifyComplete();
    }
}
