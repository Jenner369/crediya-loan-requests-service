package co.com.crediya.api.presentation.contract.loanapplication.v1.handler;

import co.com.crediya.api.dto.common.ErrorResponseDTO;
import co.com.crediya.api.dto.loanapplication.RegisterLoanApplicationDTO;
import co.com.crediya.api.dto.loanapplication.ShortLoanApplicationDTO;
import co.com.crediya.api.mapper.LoanApplicationDTOMapper;
import co.com.crediya.api.presentation.contract.DTOValidator;
import co.com.crediya.api.presentation.contract.RouteHandler;
import co.com.crediya.api.presentation.contract.UUIDValidator;
import co.com.crediya.usecase.getuserbyidentitydocument.GetUserByIdentityDocumentUseCase;
import co.com.crediya.usecase.registerloanapplication.RegisterLoanApplicationUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class RegisterLoanApplicationHandlerV1 implements RouteHandler {

    private final RegisterLoanApplicationUseCase registerLoanApplicationUseCase;
    private final GetUserByIdentityDocumentUseCase getUserByIdentityDocumentUseCase;
    private final DTOValidator dtoValidator;
    private final UUIDValidator uuidValidator;
    private final LoanApplicationDTOMapper mapper;

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
    @ApiResponse(responseCode = "400", description = "Error de validación",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Recurso no encontrados",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    public Mono<ServerResponse> handle(ServerRequest request) {
        return request
                .bodyToMono(RegisterLoanApplicationDTO.class)
                .doOnNext(user -> {
                    var rid = request.exchange().getRequest().getId();
                    log.info("[{}] POST /api/v1/solicitud - Intento de registro", rid);
                })
                .flatMap(dtoValidator::validate)
                .flatMap(dto ->
                        uuidValidator
                                .validateExists(dto.loanTypeId())
                                .thenReturn(dto)
                )
                .map(mapper::toModelFromRegisterDTO)
                .flatMap(loanApplication ->
                        getUserByIdentityDocumentUseCase
                                .execute(loanApplication.getIdentityDocument())
                                .map(user -> {
                                    loanApplication.setUserDetails(user);

                                    return loanApplication;
                                })
                )
                .flatMap(registerLoanApplicationUseCase::execute)
                .doOnNext(loanApplication -> {
                    var rid = request.exchange().getRequest().getId();
                    log.info("[{}] POST /api/v1/solicitud - Registro exitoso para la solicitud con ID: {}",
                            rid,
                            loanApplication.getId()
                    );
                })
                .map(mapper::toShortDTOFromModel)
                .flatMap(shortDTO -> ServerResponse.ok().bodyValue(shortDTO));
    }
}
