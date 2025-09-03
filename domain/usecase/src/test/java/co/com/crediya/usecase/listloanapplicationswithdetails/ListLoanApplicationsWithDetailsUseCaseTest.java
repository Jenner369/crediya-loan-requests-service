package co.com.crediya.usecase.listloanapplicationswithdetails;

import co.com.crediya.common.PageRequest;
import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListLoanApplicationsWithDetailsUseCaseTest {

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ListLoanApplicationsWithDetailsUseCase useCase;

    private LoanApplication loanApplication;
    private User user;

    @BeforeEach
    void setUp() {
        loanApplication = LoanApplication.builder()
                .id(UUID.randomUUID())
                .amount(new BigDecimal("10000"))
                .term(12)
                .identityDocument("12345678")
                .email("jennerjose369@gmail.com")
                .statusCode("approved")
                .loanTypeCode("personal_loan")
                .build();

        user = User.builder()
                .id(UUID.randomUUID())
                .name("Jenner")
                .lastName("Durand")
                .email("jennerjose369@gmail.com")
                .identityDocument("12345678")
                .baseSalary(new BigDecimal("5000000"))
                .build();
    }

    @Test
    void shouldReturnPagedLoanApplications() {
        var input = new ListLoanApplicationsWithDetailsUseCaseInput(
                "12345678",
                "approved",
                "personal_loan",
                true,
                new PageRequest(0, 10)
        );

        when(loanApplicationRepository.findAllWithDetails(
                input.identityDocument(),
                input.statusCode(),
                input.loanTypeCode(),
                input.autoApproval(),
                input.page().pageNumber(),
                input.page().pageSize()
        )).thenReturn(Flux.just(loanApplication));

        when(userRepository.findAllByIdentityDocuments(List.of("12345678")))
                .thenReturn(Flux.just(user));

        when(loanApplicationRepository.countByFilters(
                input.identityDocument(),
                input.statusCode(),
                input.loanTypeCode(),
                input.autoApproval()
        )).thenReturn(Mono.just(1L));

        StepVerifier.create(useCase.execute(input))
                .assertNext(page -> {
                    StepVerifier.create(page.getTotalElements())
                            .expectNext(1L)
                            .verifyComplete();

                    StepVerifier.create(page.getContent())
                            .expectNext(loanApplication)
                            .verifyComplete();

                })
                .verifyComplete();

        verify(loanApplicationRepository).findAllWithDetails(
                input.identityDocument(),
                input.statusCode(),
                input.loanTypeCode(),
                input.autoApproval(),
                input.page().pageNumber(),
                input.page().pageSize()
        );
        verify(loanApplicationRepository).countByFilters(
                input.identityDocument(),
                input.statusCode(),
                input.loanTypeCode(),
                input.autoApproval()
        );
    }
}
