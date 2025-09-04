package co.com.crediya.api.presentation.loanapplication.v1.handler;

import co.com.crediya.api.authentication.filter.AuthUserDetails;
import co.com.crediya.api.dto.common.ErrorResponseDTO;
import co.com.crediya.api.dto.loanapplication.RegisterLoanApplicationDTO;
import co.com.crediya.api.dto.loanapplication.ShortLoanApplicationDTO;
import co.com.crediya.api.mapper.LoanApplicationDTOMapper;
import co.com.crediya.api.contract.DTOValidator;
import co.com.crediya.api.contract.RoleValidator;
import co.com.crediya.api.contract.RouteHandler;
import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.role.enums.Roles;
import co.com.crediya.model.user.User;
import co.com.crediya.usecase.getuserbyidentitydocument.GetUserByIdentityDocumentUseCase;
import co.com.crediya.usecase.registerloanapplication.RegisterLoanApplicationUseCase;
import co.com.crediya.usecase.registerloanapplication.RegisterLoanApplicationUseCaseInput;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class RegisterLoanApplicationHandlerV1 implements RouteHandler {

    private final RegisterLoanApplicationUseCase registerLoanApplicationUseCase;
    private final GetUserByIdentityDocumentUseCase getUserByIdentityDocumentUseCase;
    private final DTOValidator dtoValidator;
    private final LoanApplicationDTOMapper mapper;
    private final RoleValidator roleValidator;

    @Override
    @Operation(
            tags = {"Loan Application API"},
            summary = "Registrar una nueva solicitud de préstamo",
            description = "Permite registrar una nueva solicitud de préstamo en el sistema.",
            requestBody = @RequestBody(
                    description = "Datos de la solicitud de préstamo",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RegisterLoanApplicationDTO.class))
            )
    )
    @ApiResponse(responseCode = "200", description = "Solicitud de préstamo registrada exitosamente",
            content = @Content(schema = @Schema(implementation = ShortLoanApplicationDTO.class)))
    @ApiResponse(responseCode = "400", description = "Error de dominio",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "401", description = "No autorizado",
            content = @Content(schema = @Schema))
    @ApiResponse(responseCode = "403", description = "Prohibido",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Recurso no encontrados",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "422", description = "Error de validación",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    public Mono<ServerResponse> handle(ServerRequest request) {
        return getAuthenticatedUser()
                .flatMap(authUser -> processRequest(request, authUser));
    }

    private Mono<User> getAuthenticatedUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(auth -> {
                    AuthUserDetails principal = (AuthUserDetails) auth.getPrincipal();
                    return User.builder()
                            .id(UUID.fromString(principal.getId()))
                            .email(principal.getUsername())
                            .roleId(principal.getRoleId())
                            .build();
                });
    }

    private Mono<ServerResponse> processRequest(ServerRequest request, User authUser) {
        return request.bodyToMono(RegisterLoanApplicationDTO.class)
                .flatMap(dto -> roleValidator.validateRole(Roles.CLIENT).thenReturn(dto))
                .flatMap(dtoValidator::validate)
                .doOnNext(user -> {
                    var rid = request.exchange().getRequest().getId();
                    log.info("[{}] POST /api/v1/solicitud - Intento de registro", rid);
                })
                .map(mapper::toModelFromRegisterDTO)
                .flatMap(loanApp -> enrichLoanApplication(loanApp, authUser))
                .flatMap(registerLoanApplicationUseCase::execute)
                .doOnNext(loanApplication -> {
                    var rid = request.exchange().getRequest().getId();
                    log.info("[{}] POST /api/v1/solicitud - Registro exitoso para la solicitud con ID: {}", rid, loanApplication.getId());
                })
                .map(mapper::toShortDTOFromModel)
                .flatMap(shortDTO -> ServerResponse.ok().bodyValue(shortDTO));
    }

    private Mono<RegisterLoanApplicationUseCaseInput> enrichLoanApplication(
            LoanApplication loanApplication,
            User authUser
    ) {
        return getUserByIdentityDocumentUseCase.execute(loanApplication.getIdentityDocument())
                .map(user -> {
                    loanApplication.setUserDetails(user);
                    return new RegisterLoanApplicationUseCaseInput(
                            loanApplication,
                            user,
                            authUser
                    );
                });
    }
}
