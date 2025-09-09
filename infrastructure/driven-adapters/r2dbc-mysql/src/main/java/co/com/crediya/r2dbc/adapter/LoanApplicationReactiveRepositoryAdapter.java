package co.com.crediya.r2dbc.adapter;

import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.model.loantype.LoanType;
import co.com.crediya.model.status.Status;
import co.com.crediya.model.status.enums.Statuses;
import co.com.crediya.r2dbc.entity.LoanApplicationEntity;
import co.com.crediya.r2dbc.projection.LoanApplicationWithDetails;
import co.com.crediya.r2dbc.repository.LoanApplicationReactiveRepository;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public class LoanApplicationReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        LoanApplication,
        LoanApplicationEntity,
        UUID,
        LoanApplicationReactiveRepository
> implements LoanApplicationRepository {

    public LoanApplicationReactiveRepositoryAdapter(LoanApplicationReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, LoanApplication.class));
    }

    @Override
    public Flux<LoanApplication> findAllWithDetails(
            String identityDocument,
            String statusCode,
            String loanTypeCode,
            Boolean autoApproval,
            Integer pageNumber,
            Integer pageSize
    ) {
        var offset = (pageNumber - 1) * pageSize;

        return repository.findAllWithDetails(
                        identityDocument,
                        statusCode,
                        loanTypeCode,
                        Statuses.APPROVED.getCode(),
                        autoApproval,
                        pageSize,
                        offset
                )
                .map(LoanApplicationWithDetails::toModel);
    }

    @Override
    public Mono<Long> countByFilters(
            String identityDocument,
            String statusCode,
            String loanTypeCode,
            Boolean autoApproval
    ) {
        return repository.countByFilters(
                identityDocument,
                statusCode,
                loanTypeCode,
                autoApproval
        );
    }

    @Override
    public Mono<BigDecimal> getTotalMonthlyDebtApprovedFromLoanApplicationById(UUID id, String approvedStatus) {
        return repository.getTotalMonthlyDebtApprovedFromLoanApplicationById(
                id,
                approvedStatus
        );
    }
}
