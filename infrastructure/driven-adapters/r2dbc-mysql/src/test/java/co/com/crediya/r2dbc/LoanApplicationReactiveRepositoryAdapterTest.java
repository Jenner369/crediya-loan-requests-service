package co.com.crediya.r2dbc;

import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loantype.enums.LoanTypes;
import co.com.crediya.model.status.enums.Statuses;
import co.com.crediya.model.user.User;
import co.com.crediya.r2dbc.adapter.LoanApplicationReactiveRepositoryAdapter;
import co.com.crediya.r2dbc.entity.LoanApplicationEntity;
import co.com.crediya.r2dbc.projection.LoanApplicationWithDetails;
import co.com.crediya.r2dbc.repository.LoanApplicationReactiveRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    private final LoanApplicationWithDetails sampleLoanApplicationProjection =
            new LoanApplicationWithDetails(
                    sampleId,
                    new BigDecimal("1000000"),
                    12,
                    "12345678",
                    "jennerjose369@gmail.com",
                    Statuses.PENDING.getCode(),
                    Statuses.PENDING.getName(),
                    Statuses.PENDING.getDescription(),
                    LoanTypes.BUSINESS_LOAN.getCode(),
                    LoanTypes.BUSINESS_LOAN.getName(),
                    LoanTypes.BUSINESS_LOAN.getInterestRate(),
    null
            );


    private final LoanApplication sampleLoanApplication =
            new LoanApplication(
                    sampleId,
                    new BigDecimal("1000000"),
                    12,
                    "12345678",
                    "jennerjose369@gmail.com",
                    Statuses.PENDING.getCode(),
                    LoanTypes.BUSINESS_LOAN.getCode(),
                    LoanTypes.BUSINESS_LOAN.toModel(),
                    Statuses.PENDING.toModel(),
                    User.builder()
                            .name("Jenner")
                            .lastName("Durand")
                            .email("jennerjose369@gmail.com")
                            .identityDocument("jennerjose369@gmail.com")
                            .baseSalary(new BigDecimal("5000000"))
                            .build(),
                    null
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

    @Test
    void mustFindAllWithDetails() {
        when(repository.findAllWithDetails(
                any(),
                any(),
                any(),
                any(),
                any(),
                eq(1),
                eq(0)
        )).thenReturn(Flux.just(sampleLoanApplicationProjection));

        var result = repositoryAdapter.findAllWithDetails(
                "12345678",
                Statuses.PENDING.getCode(),
                LoanTypes.BUSINESS_LOAN.getCode(),
                false,
                1,
                1
        );

        StepVerifier.create(result)
                .expectNextMatches(value ->
                        value.getIdentityDocument()
                                .equals(sampleLoanApplication.getIdentityDocument())
                )
                .verifyComplete();
    }

    @Test
    void mustCountByFilters() {
        when(repository.countByFilters(any(), any(), any(), any()))
                .thenReturn(Mono.just(5L));

        var result = repositoryAdapter.countByFilters("12345678", null, null, false);

        StepVerifier.create(result)
                .expectNext(5L)
                .verifyComplete();
    }
}
