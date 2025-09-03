package co.com.crediya.api.dto.loanapplication;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Filtros para buscar solicitudes de préstamo con paginación")
public record SearchLoanApplicationDTO(

        @Schema(description = "Documento de identidad del solicitante", example = "123456789")
        String identityDocument,

        @Schema(description = "Código del estado de la solicitud de préstamo", example = "approved")
        String statusCode,

        @Schema(description = "Código del tipo de préstamo", example = "personal_loan")
        String loanTypeCode,

        @Schema(description = "Indica si la solicitud fue aprobada automáticamente", example = "true")
        Boolean autoApproval,

        @Schema(description = "Número de página ", example = "1")
        @NotNull
        @Min(value = 1, message = "El número de página debe ser 1 o mayor")
        Integer page,

        @Schema(description = "Cantidad de elementos por página", example = "10")
        @NotNull
        @Min(value = 1, message = "El tamaño de página debe ser al menos 1")
        Integer size

) {
        public static final String PARAM_IDENTITY_DOCUMENT = "identityDocument";
        public static final String PARAM_STATUS_CODE = "statusCode";
        public static final String PARAM_LOAN_TYPE_CODE = "loanTypeCode";
        public static final String PARAM_AUTO_APPROVAL = "autoApproval";
        public static final String PARAM_PAGE = "page";
        public static final String PARAM_SIZE = "size";
}
