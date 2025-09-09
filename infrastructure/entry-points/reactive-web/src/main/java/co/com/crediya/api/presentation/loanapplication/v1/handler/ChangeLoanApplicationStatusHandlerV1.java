package co.com.crediya.api.presentation.loanapplication.v1.handler;

import co.com.crediya.api.contract.DTOValidator;
import co.com.crediya.api.contract.RoleValidator;
import co.com.crediya.api.contract.RouteHandler;
import co.com.crediya.api.contract.UUIDValidator;
import co.com.crediya.api.dto.common.ErrorResponseDTO;
import co.com.crediya.api.dto.loanapplication.ChangeLoanApplicationStatusDTO;
import co.com.crediya.api.mapper.LoanApplicationDTOMapper;
import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.model.role.enums.Roles;
import co.com.crediya.usecase.changeloanapplicationstatus.ChangeLoanApplicationStatusUseCase;
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
public class ChangeLoanApplicationStatusHandlerV1 implements RouteHandler {
    private final ChangeLoanApplicationStatusUseCase changeLoanApplicationStatusUseCase;
    private final LoanApplicationDTOMapper mapper;
    private final RoleValidator roleValidator;
    private final UUIDValidator uuidValidator;
    private final DTOValidator dtoValidator;

    @Override
    @Operation(
            tags = {"Loan Application API"},
            summary = "Cambiar el estado de una solicitud de préstamo",
            description = "Permite cambiar el estado de una solicitud de préstamo existente en el sistema.",
            requestBody = @RequestBody(
                    description = "Datos para cambiar el estado de la solicitud de préstamo",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ChangeLoanApplicationStatusDTO.class))
            )
    )
    @ApiResponse(responseCode = "200", description = "Estado de la solicitud de préstamo cambiado exitosamente",
            content = @Content(schema = @Schema))
    @ApiResponse(responseCode = "400", description = "Error de dominio",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "401", description = "No autorizado",
            content = @Content(schema = @Schema))
    @ApiResponse(responseCode = "403", description = "Prohibido",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Recurso no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "422", description = "Error de validación",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    public Mono<ServerResponse> handle(ServerRequest request) {
        return request.bodyToMono(ChangeLoanApplicationStatusDTO.class)
                .flatMap(dtoValidator::validate)
                .flatMap(dto -> uuidValidator.validate(dto.loanApplicationId())
                        .thenReturn(dto))
                .flatMap(dto -> roleValidator.validateRole(Roles.ADVISOR)
                        .thenReturn(dto))
                .map(mapper::toChangeStatusInputFromDTO)
                .doOnNext(dto -> {
                    var rid = request.exchange().getRequest().getId();

                    log.info("[{}] PUT /api/v1/solicitud - Intento de cambio de estado de solicitud de préstamo {}-{}",
                            rid,
                            dto.loanApplicationId(), dto.newStatusCode()
                    );
                })
                .flatMap(changeLoanApplicationStatusUseCase::execute)
                .doOnNext(loanApplication -> {
                    var rid = request.exchange().getRequest().getId();

                    log.info("[{}] PUT /api/v1/solicitud - Cambio de estado de solicitud de préstamo exitoso {}-{}",
                            rid,
                            loanApplication.getId(), loanApplication.getStatusCode()
                    );
                })
                .then(ServerResponse.ok().build());
    }
}
