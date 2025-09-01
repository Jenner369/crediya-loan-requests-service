package co.com.crediya.api.v1;

import co.com.crediya.api.authentication.filter.AuthUserDetails;
import co.com.crediya.api.contract.DTOValidator;
import co.com.crediya.api.contract.RoleValidator;
import co.com.crediya.api.dto.loanapplication.RegisterLoanApplicationDTO;
import co.com.crediya.api.dto.loanapplication.ShortLoanApplicationDTO;
import co.com.crediya.api.exception.GlobalErrorAttributes;
import co.com.crediya.api.exception.GlobalExceptionHandler;
import co.com.crediya.api.mapper.LoanApplicationDTOMapper;
import co.com.crediya.api.presentation.loanapplication.v1.LoanApplicationRouterV1;
import co.com.crediya.api.presentation.loanapplication.v1.handler.RegisterLoanApplicationHandlerV1;
import co.com.crediya.api.validation.exception.ForbiddenException;
import co.com.crediya.exception.UserNotFoundException;
import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loantype.enums.LoanTypes;
import co.com.crediya.model.role.enums.Roles;
import co.com.crediya.model.status.enums.Statuses;
import co.com.crediya.model.user.User;
import co.com.crediya.usecase.getuserbyidentitydocument.GetUserByIdentityDocumentUseCase;
import co.com.crediya.usecase.registerloanapplication.RegisterLoanApplicationUseCase;
import co.com.crediya.usecase.registerloanapplication.RegisterLoanApplicationUseCaseInput;
import jakarta.validation.ConstraintViolationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockAuthentication;

@ContextConfiguration(classes = {
        GlobalExceptionHandler.class,
        GlobalErrorAttributes.class,
        LoanApplicationRouterV1.class,
        RegisterLoanApplicationHandlerV1.class,
        LoanApplicationRouterV1Test.TestConfig.class,
        LoanApplicationRouterV1Test.TestSecurityConfig.class
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
    private RoleValidator roleValidator;

    @MockitoBean
    private LoanApplicationDTOMapper mapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public WebProperties.Resources webPropertiesResources() {
            return new WebProperties.Resources();
        }
    }

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
            return http
                    .csrf(ServerHttpSecurity.CsrfSpec::disable)
                    .authorizeExchange(ex -> ex.anyExchange().permitAll())
                    .build();
        }
    }

    private AuthUserDetails authUser;

    @BeforeEach
    void setupAuthUser() {
        authUser = new AuthUserDetails(
                UUID.randomUUID().toString(),
                "jenner@crediya.com",
                "fake-password",
                List.of(new SimpleGrantedAuthority(Roles.CLIENT.getId().toString()))
        );
    }

    @Test
    void testRegisterLoanApplicationSuccess() {
        var sampleId = UUID.randomUUID();
        var dto = new RegisterLoanApplicationDTO(new BigDecimal("2000000"), 12, LoanTypes.PERSONAL_LOAN.getCode(), "12345678");
        var user = new User(UUID.randomUUID(), "Jenner", "Durand", "jennerjose369@gmail.com", "12345678", new BigDecimal("5000000"), null);
        var loanApplication = new LoanApplication(sampleId, new BigDecimal("2000000"), 12, "12345678", user.getEmail(), Statuses.PENDING.getCode(), LoanTypes.PERSONAL_LOAN.getCode());
        var responseDTO = new ShortLoanApplicationDTO(sampleId.toString(), new BigDecimal("2000000"), 12, "12345678");

        when(dtoValidator.validate(dto)).thenReturn(Mono.just(dto));
        when(getUserByIdentityDocumentUseCase.execute(dto.identityDocument())).thenReturn(Mono.just(user));
        when(mapper.toModelFromRegisterDTO(dto)).thenReturn(loanApplication);
        when(roleValidator.validateRole(any())).thenReturn(Mono.empty());
        when(registerLoanApplicationUseCase.execute(any(RegisterLoanApplicationUseCaseInput.class))).thenReturn(Mono.just(loanApplication));
        when(mapper.toShortDTOFromModel(loanApplication)).thenReturn(responseDTO);

        webTestClient.mutateWith(mockAuthentication(
                        new UsernamePasswordAuthenticationToken(authUser, authUser.getPassword(), authUser.getAuthorities())
                ))
                .post()
                .uri("/api/v1/solicitud")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ShortLoanApplicationDTO.class)
                .value(userResponse -> Assertions.assertThat(userResponse.identityDocument()).isEqualTo(user.getIdentityDocument()));
    }

    @Test
    void testRegisterLoanApplicationUserNotFound() {
        var dto = new RegisterLoanApplicationDTO(new BigDecimal("2000000"), 12, LoanTypes.PERSONAL_LOAN.getCode(), "99999999");
        var loanApplication = new LoanApplication(UUID.randomUUID(), new BigDecimal("2000000"), 12, "99999999", "jenner@crediya.com", Statuses.PENDING.getCode(), LoanTypes.PERSONAL_LOAN.getCode());

        when(mapper.toModelFromRegisterDTO(dto)).thenReturn(loanApplication);
        when(dtoValidator.validate(dto)).thenReturn(Mono.just(dto));
        when(roleValidator.validateRole(any())).thenReturn(Mono.empty());
        when(getUserByIdentityDocumentUseCase.execute(anyString())).thenReturn(Mono.error(new UserNotFoundException()));

        webTestClient.mutateWith(mockAuthentication(
                        new UsernamePasswordAuthenticationToken(authUser, authUser.getPassword(), authUser.getAuthorities())
                ))
                .post()
                .uri("/api/v1/solicitud")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404);
    }

    @Test
    void testRegisterLoanApplicationValidationFail() {
        var dto = new RegisterLoanApplicationDTO(new BigDecimal("2000000"), 12, LoanTypes.PERSONAL_LOAN.getCode(), "12345678");

        when(dtoValidator.validate(dto))
                .thenReturn(Mono.error(
                        new ConstraintViolationException(
                                "Validation failed",
                                new HashSet<>()
                        )
                ));
        when(roleValidator.validateRole(any())).thenReturn(Mono.empty());

        webTestClient.mutateWith(mockAuthentication(
                        new UsernamePasswordAuthenticationToken(authUser, authUser.getPassword(), authUser.getAuthorities())
                ))
                .post()
                .uri("/api/v1/solicitud")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isEqualTo(422);
    }

    @Test
    void testRegisterLoanApplicationForbidden() {
        var dto = new RegisterLoanApplicationDTO(new BigDecimal("2000000"), 12, LoanTypes.PERSONAL_LOAN.getCode(), "12345678");

        when(roleValidator.validateRole(any())).thenReturn(Mono.error(new ForbiddenException("Access denied")));

        webTestClient.mutateWith(mockAuthentication(
                        new UsernamePasswordAuthenticationToken(authUser, authUser.getPassword(), authUser.getAuthorities())
                ))
                .post()
                .uri("/api/v1/solicitud")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isForbidden();
    }
}
