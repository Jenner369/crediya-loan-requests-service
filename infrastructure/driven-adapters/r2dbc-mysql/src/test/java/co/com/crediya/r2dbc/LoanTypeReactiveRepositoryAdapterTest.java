package co.com.crediya.r2dbc;

import co.com.crediya.model.loantype.LoanType;
import co.com.crediya.model.loantype.enums.LoanTypes;
import co.com.crediya.r2dbc.adapter.LoanTypeReactiveRepositoryAdapter;
import co.com.crediya.r2dbc.entity.LoanTypeEntity;
import co.com.crediya.r2dbc.repository.LoanTypeReactiveRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanTypeReactiveRepositoryAdapterTest {
    @InjectMocks
    LoanTypeReactiveRepositoryAdapter repositoryAdapter;

    @Mock
    LoanTypeReactiveRepository repository;

    @Mock
    ObjectMapper mapper;

    private final UUID sampleId = LoanTypes.BUSINESS_LOAN.getId();
    private final LoanTypeEntity sampleEntity =
            new LoanTypeEntity(
                    sampleId,
                    LoanTypes.BUSINESS_LOAN.getName(),
                    LoanTypes.BUSINESS_LOAN.getMinAmount(),
                    LoanTypes.BUSINESS_LOAN.getMaxAmount(),
                    LoanTypes.BUSINESS_LOAN.getInterestRate(),
                    LoanTypes.BUSINESS_LOAN.getAutoApproval()
            );

    private final LoanType sampleLoanType = LoanTypes.BUSINESS_LOAN.toModel();

    @Test
    void mustSaveLoanType() {
        when(repository.save(any(LoanTypeEntity.class))).thenReturn(Mono.just(sampleEntity));
        when(mapper.map(any(LoanType.class), eq(LoanTypeEntity.class))).thenReturn(sampleEntity);
        when(mapper.map(sampleEntity, LoanType.class)).thenReturn(sampleLoanType);

        StepVerifier.create(repositoryAdapter.save(sampleLoanType))
                .expectNext(sampleLoanType)
                .verifyComplete();
    }

    @Test
    void mustFindById() {
        when(repository.findById(sampleId)).thenReturn(Mono.just(sampleEntity));
        when(mapper.map(sampleEntity, LoanType.class)).thenReturn(sampleLoanType);

        var result = repositoryAdapter.findById(sampleId);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.equals(sampleLoanType))
                .verifyComplete();
    }

    @Test
    void mustExistById() {
        when(repository.existsById(sampleId)).thenReturn(Mono.just(true));

        var result = repositoryAdapter.existsById(sampleId);

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }
}
