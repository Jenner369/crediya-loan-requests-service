package co.com.crediya.api.v1;

import co.com.crediya.api.authentication.filter.AuthUserDetails;
import co.com.crediya.api.contract.DTOValidator;
import co.com.crediya.api.contract.RoleValidator;
import co.com.crediya.api.dto.common.PaginationHeaders;
import co.com.crediya.api.dto.loanapplication.LoanApplicationDTO;
import co.com.crediya.api.dto.loanapplication.RegisterLoanApplicationDTO;
import co.com.crediya.api.dto.loanapplication.SearchLoanApplicationDTO;
import co.com.crediya.api.dto.loanapplication.ShortLoanApplicationDTO;
import co.com.crediya.api.dto.loantype.LoanTypeDTO;
import co.com.crediya.api.dto.status.StatusDTO;
import co.com.crediya.api.dto.user.UserDTO;
import co.com.crediya.api.exception.GlobalErrorAttributes;
import co.com.crediya.api.exception.GlobalExceptionHandler;
import co.com.crediya.api.mapper.LoanApplicationDTOMapper;
import co.com.crediya.api.presentation.loanapplication.v1.LoanApplicationRouterV1;
import co.com.crediya.api.presentation.loanapplication.v1.handler.ListLoanApplicationsWithDetailsHandlerV1;
import co.com.crediya.api.presentation.loanapplication.v1.handler.RegisterLoanApplicationHandlerV1;
import co.com.crediya.api.validation.exception.ForbiddenException;
import co.com.crediya.common.PageRequest;
import co.com.crediya.common.PageResponse;
import co.com.crediya.exception.UserNotFoundException;
import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loantype.enums.LoanTypes;
import co.com.crediya.model.role.enums.Roles;
import co.com.crediya.model.status.enums.Statuses;
import co.com.crediya.model.user.User;
import co.com.crediya.usecase.getuserbyidentitydocument.GetUserByIdentityDocumentUseCase;
import co.com.crediya.usecase.listloanapplicationswithdetails.ListLoanApplicationsWithDetailsUseCase;
import co.com.crediya.usecase.listloanapplicationswithdetails.ListLoanApplicationsWithDetailsUseCaseInput;
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
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
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
        ListLoanApplicationsWithDetailsHandlerV1.class,
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
    private ListLoanApplicationsWithDetailsUseCase listLoanApplicationsWithDetailsUseCase;

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
        var dto = new RegisterLoanApplicationDTO(
                new BigDecimal("2000000"),
                12,
                LoanTypes.PERSONAL_LOAN.getCode(),
                "12345678"
        );

        var user = User.builder()
                .id(UUID.randomUUID())
                .name("Jenner")
                .lastName("Durand")
                .email("jennerjose369@gmail.com")
                .identityDocument("12345678")
                .baseSalary(new BigDecimal("5000000"))
                .build();

        var loanApplication = LoanApplication.builder()
                .id(sampleId)
                .amount(new BigDecimal("2000000"))
                .term(12)
                .identityDocument("12345678")
                .email(user.getEmail())
                .statusCode(Statuses.PENDING.getCode())
                .loanTypeCode(LoanTypes.PERSONAL_LOAN.getCode())
                .build();

        var responseDTO = new ShortLoanApplicationDTO(
                sampleId.toString(),
                new BigDecimal("2000000"),
                12,
                "12345678"
        );

        when(dtoValidator.validate(dto)).thenReturn(Mono.just(dto));
        when(getUserByIdentityDocumentUseCase.execute(dto.identityDocument()))
                .thenReturn(Mono.just(user));
        when(mapper.toModelFromRegisterDTO(dto)).thenReturn(loanApplication);
        when(roleValidator.validateRole(any())).thenReturn(Mono.empty());
        when(registerLoanApplicationUseCase.execute(any(RegisterLoanApplicationUseCaseInput.class)))
                .thenReturn(Mono.just(loanApplication));
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
                .value(userResponse ->
                        Assertions.assertThat(userResponse.identityDocument())
                                .isEqualTo(user.getIdentityDocument())
                );
    }

    @Test
    void testRegisterLoanApplicationUserNotFound() {
        var dto = new RegisterLoanApplicationDTO(
                new BigDecimal("2000000"),
                12,
                LoanTypes.PERSONAL_LOAN.getCode(),
                "99999999"
        );

        var loanApplication = LoanApplication.builder()
                .amount(new BigDecimal("2000000"))
                .term(12)
                .identityDocument("99999999")
                .statusCode(Statuses.PENDING.getCode())
                .loanTypeCode(LoanTypes.PERSONAL_LOAN.getCode())
                .build();

        when(mapper.toModelFromRegisterDTO(dto)).thenReturn(loanApplication);
        when(dtoValidator.validate(dto)).thenReturn(Mono.just(dto));
        when(roleValidator.validateRole(any())).thenReturn(Mono.empty());
        when(getUserByIdentityDocumentUseCase.execute(anyString()))
                .thenReturn(Mono.error(new UserNotFoundException()));

        webTestClient.mutateWith(mockAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                authUser,
                                authUser.getPassword(),
                                authUser.getAuthorities()
                        )
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
        var dto = new RegisterLoanApplicationDTO(
                new BigDecimal("2000000"),
                12,
                LoanTypes.PERSONAL_LOAN.getCode(),
                "12345678"
        );

        when(dtoValidator.validate(dto))
                .thenReturn(Mono.error(
                        new ConstraintViolationException(
                                "Validation failed",
                                new HashSet<>()
                        )
                ));
        when(roleValidator.validateRole(any())).thenReturn(Mono.empty());

        webTestClient.mutateWith(mockAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                authUser,
                                authUser.getPassword(),
                                authUser.getAuthorities()
                        )
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
        var dto = new RegisterLoanApplicationDTO(
                new BigDecimal("2000000"),
                12,
                LoanTypes.PERSONAL_LOAN.getCode(),
                "12345678"
        );

        when(roleValidator.validateRole(any()))
                .thenReturn(Mono.error(new ForbiddenException("Access denied")));

        webTestClient.mutateWith(mockAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                authUser,
                                authUser.getPassword(),
                                authUser.getAuthorities()
                        )
                ))
                .post()
                .uri("/api/v1/solicitud")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void testListLoanApplicationsWithDetailsSuccess() {
        var identityDocument = "12345678";
        var statusCode = Statuses.PENDING.getCode();
        var loanTypeCode = LoanTypes.PERSONAL_LOAN.getCode();
        var page = 1;
        var size = 10;

        var dto = new SearchLoanApplicationDTO(
                identityDocument,
                statusCode,
                loanTypeCode,
                null,
                page,
                size
        );
        var pagination = new PageRequest(dto.page(), dto.size());

        var loanApplication = LoanApplication.builder()
                .id(UUID.randomUUID())
                .amount(new BigDecimal("2000000"))
                .term(12)
                .identityDocument(identityDocument)
                .email("jenner@crediya.com")
                .statusCode(statusCode)
                .loanTypeCode(loanTypeCode)
                .build();

        var userDTO = UserDTO.builder()
                .name("Jenner")
                .lastName("Durand")
                .email("jenner@crediya.com")
                .identityDocument(identityDocument)
                .baseSalary(new BigDecimal("5000000"))
                .build();

        var loanApplicationDTO = new LoanApplicationDTO(
                loanApplication.getId().toString(),
                loanApplication.getAmount(),
                loanApplication.getTerm(),
                loanApplication.getIdentityDocument(),
                loanApplication.getEmail(),
                StatusDTO.fromEnum(Statuses.PENDING),
                LoanTypeDTO.fromEnum(LoanTypes.PERSONAL_LOAN),
                userDTO,
                null
        );

        when(dtoValidator.validate(any(SearchLoanApplicationDTO.class)))
                .thenReturn(Mono.just(dto));
        when(roleValidator.validateRole(Roles.ADVISOR)).thenReturn(Mono.empty());
        when(mapper.toListInputFromSearchDTO(dto)).thenReturn(
                new ListLoanApplicationsWithDetailsUseCaseInput(
                        dto.identityDocument(),
                        dto.statusCode(),
                        dto.loanTypeCode(),
                        dto.autoApproval(),
                        pagination
                )
        );
        when(listLoanApplicationsWithDetailsUseCase.execute(any()))
                .thenReturn(Mono.just(
                        PageResponse.of(
                                Flux.just(loanApplication),
                                Mono.just(1L),
                                pagination
                        )
                ));
        when(mapper.toDTOFromModel(loanApplication)).thenReturn(loanApplicationDTO);

        var uri = UriComponentsBuilder.fromPath("/api/v1/solicitud")
                .queryParam(SearchLoanApplicationDTO.PARAM_IDENTITY_DOCUMENT, identityDocument)
                .queryParam(SearchLoanApplicationDTO.PARAM_STATUS_CODE, statusCode)
                .queryParam(SearchLoanApplicationDTO.PARAM_LOAN_TYPE_CODE, loanTypeCode)
                .queryParam(SearchLoanApplicationDTO.PARAM_PAGE, page)
                .queryParam(SearchLoanApplicationDTO.PARAM_SIZE, size)
                .toUriString();

        webTestClient.mutateWith(mockAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                "advisor",
                                "password",
                                List.of(new SimpleGrantedAuthority(Roles.ADVISOR.getId().toString()))
                        )
                ))
                .get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(PaginationHeaders.TOTAL_COUNT, "1")
                .expectHeader().valueEquals(PaginationHeaders.PAGE_NUMBER, String.valueOf(page))
                .expectHeader().valueEquals(PaginationHeaders.PAGE_SIZE, String.valueOf(size))
                .expectBodyList(LoanApplicationDTO.class)
                .value(list -> Assertions.assertThat(list).hasSize(1)
                        .first()
                        .extracting(LoanApplicationDTO::getIdentityDocument)
                        .isEqualTo(identityDocument));
    }

}
