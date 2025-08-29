package co.com.crediya.r2dbc.adapter;

import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.r2dbc.entity.LoanApplicationEntity;
import co.com.crediya.r2dbc.repository.LoanApplicationReactiveRepository;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

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

}
