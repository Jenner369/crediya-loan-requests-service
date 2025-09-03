package co.com.crediya.api.presentation.loanapplication.v1.handler;

import co.com.crediya.api.contract.DTOValidator;
import co.com.crediya.api.contract.RoleValidator;
import co.com.crediya.api.contract.RouteHandler;
import co.com.crediya.api.dto.common.ErrorResponseDTO;
import co.com.crediya.api.dto.common.PaginationDefaults;
import co.com.crediya.api.dto.common.PaginationHeaders;
import co.com.crediya.api.dto.loanapplication.LoanApplicationDTO;
import co.com.crediya.api.dto.loanapplication.SearchLoanApplicationDTO;
import co.com.crediya.api.mapper.LoanApplicationDTOMapper;
import co.com.crediya.model.role.enums.Roles;
import co.com.crediya.usecase.listloanapplicationswithdetails.ListLoanApplicationsWithDetailsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class ListLoanApplicationsWithDetailsHandlerV1 implements RouteHandler {

    private final ListLoanApplicationsWithDetailsUseCase listLoanApplicationsWithDetailsUseCase;
    private final DTOValidator dtoValidator;
    private final LoanApplicationDTOMapper mapper;
    private final RoleValidator roleValidator;

    @Override
    @Operation(
            tags = {"Loan Application API"},
            summary = "Listar solicitudes de préstamo con detalles",
            description = "Permite listar las solicitudes de préstamo con detalles, aplicando filtros y paginación",
            parameters = {
                    @Parameter(name = "identityDocument", description = "Documento de identidad del solicitante", example = "123456789"),
                    @Parameter(name = "statusCode", description = "Código del estado de la solicitud", example = "approved"),
                    @Parameter(name = "loanTypeCode", description = "Código del tipo de préstamo", example = "personal_loan"),
                    @Parameter(name = "autoApproval", description = "Indica si la solicitud fue aprobada automáticamente", example = "true"),
                    @Parameter(name = "page", description = "Número de página", example = "1"),
                    @Parameter(name = "size", description = "Cantidad de elementos por página", example = "10")
            }
    )
    @ApiResponse(responseCode = "200", description = "Lista de solicitudes de préstamo obtenida correctamente",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = LoanApplicationDTO.class))))
    @ApiResponse(responseCode = "401", description = "No autorizado",
            content = @Content(schema = @Schema))
    @ApiResponse(responseCode = "422", description = "Error de validación",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    public Mono<ServerResponse> handle(ServerRequest request) {
        return dtoValidator.validate(mapDTOFromRequest(request))
                .flatMap(dto -> roleValidator.validateRole(Roles.ADVISOR).thenReturn(dto))
                .doOnNext(user -> {
                    var rid = request.exchange().getRequest().getId();
                    log.info("[{}] GET /api/v1/solicitud - Consulta de solicitudes de préstamo", rid);
                })
                .map(mapper::toListInputFromSearchDTO)
                .flatMap(listLoanApplicationsWithDetailsUseCase::execute)
                .map(paginated -> paginated.mapTo(mapper::toDTOFromModel))
                .flatMap(list -> list.getTotalElements()
                        .flatMap(total -> ServerResponse.ok()
                                .header(PaginationHeaders.TOTAL_COUNT, String.valueOf(total))
                                .header(PaginationHeaders.PAGE_NUMBER, String.valueOf(list.getPageNumber()))
                                .header(PaginationHeaders.PAGE_SIZE, String.valueOf(list.getPageSize()))
                                .body(list.getContent(), LoanApplicationDTO.class)
                        )
                );
    }

    private SearchLoanApplicationDTO mapDTOFromRequest(ServerRequest request) {
        var params = request.queryParams();

        return new SearchLoanApplicationDTO(
                params.getFirst(SearchLoanApplicationDTO.PARAM_IDENTITY_DOCUMENT),
                params.getFirst(SearchLoanApplicationDTO.PARAM_STATUS_CODE),
                params.getFirst(SearchLoanApplicationDTO.PARAM_LOAN_TYPE_CODE),
                request.queryParam(SearchLoanApplicationDTO.PARAM_AUTO_APPROVAL).map(Boolean::valueOf)
                        .orElse(null),
                request.queryParam(SearchLoanApplicationDTO.PARAM_PAGE).map(Integer::valueOf)
                        .orElse(PaginationDefaults.DEFAULT_PAGE_NUMBER),
                request.queryParam(SearchLoanApplicationDTO.PARAM_SIZE).map(Integer::valueOf).
                        orElse(PaginationDefaults.DEFAULT_PAGE_SIZE)
        );
    }
}
