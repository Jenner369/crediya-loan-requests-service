package co.com.crediya.api.v1;

import co.com.crediya.api.dto.loanapplication.RegisterLoanApplicationDTO;
import co.com.crediya.api.dto.loanapplication.ShortLoanApplicationDTO;
import co.com.crediya.api.mapper.LoanApplicationDTOMapper;
import co.com.crediya.api.presentation.contract.DTOValidator;
import co.com.crediya.api.presentation.contract.UUIDValidator;
import co.com.crediya.api.presentation.loanapplication.v1.LoanApplicationRouterV1;
import co.com.crediya.api.presentation.loanapplication.v1.handler.RegisterLoanApplicationHandlerV1;
import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loantype.enums.LoanTypes;
import co.com.crediya.model.status.enums.Statuses;
import co.com.crediya.model.user.User;
import co.com.crediya.usecase.getuserbyidentitydocument.GetUserByIdentityDocumentUseCase;
import co.com.crediya.usecase.registerloanapplication.RegisterLoanApplicationUseCase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.UUID;

@ContextConfiguration(classes = {
        LoanApplicationRouterV1.class,
        RegisterLoanApplicationHandlerV1.class
})
@WebFluxTest
class LoanApplicationRouterV1Test {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private RegisterLoanApplicationUseCase registerLoanApplicationUseCase;

    @MockitoBean
    private GetUserByIdentityDocumentUseCase getUserByIdentityDocumentUseCase;

    @MockitoBean
    private DTOValidator dtoValidator;

    @MockitoBean
    private UUIDValidator uuidValidator;

    @MockitoBean
    private LoanApplicationDTOMapper mapper;

    @Test
    void testRegisterLoanApplication() {
        var sampleId = UUID.randomUUID();

        var dto = new RegisterLoanApplicationDTO(
                new BigDecimal("2000000"),
                12,
                LoanTypes.PERSONAL_LOAN.getId().toString(),
                "12345678"
        );

        var loanApplication = new LoanApplication(
                sampleId,
                new BigDecimal("2000000"),
                12,
                "12345678",
                "jennerjose369@gmail.com",
                Statuses.PENDING.getId(),
                LoanTypes.PERSONAL_LOAN.getId()
        );

        var user = new User(
                UUID.randomUUID(),
                "Jenner",
                "Durand",
                "jennerjose369@gmail.com",
                "12345678",
                new BigDecimal("5000000")
        );

        var responseDTO = new ShortLoanApplicationDTO(
                sampleId.toString(),
                new BigDecimal("2000000"),
                12,
                "12345678"
        );

        when(dtoValidator.validate(dto))
                .thenReturn(Mono.just(dto));
        when(uuidValidator.validateExists(dto.loanTypeId()))
                .thenReturn(Mono.empty());
        when(getUserByIdentityDocumentUseCase.execute(dto.identityDocument()))
                .thenReturn(Mono.just(user));
        when(mapper.toModelFromRegisterDTO(dto))
                .thenReturn(loanApplication);
        when(registerLoanApplicationUseCase.execute(loanApplication))
                .thenReturn(Mono.just(loanApplication));
        when(mapper.toShortDTOFromModel(loanApplication))
                .thenReturn(responseDTO);

        webTestClient.post()
                .uri("/api/v1/solicitud")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ShortLoanApplicationDTO.class)
                .value(userResponse -> {
                    Assertions
                            .assertThat(userResponse.identityDocument())
                            .isEqualTo(user.getIdentityDocument());
                });
    }
}
