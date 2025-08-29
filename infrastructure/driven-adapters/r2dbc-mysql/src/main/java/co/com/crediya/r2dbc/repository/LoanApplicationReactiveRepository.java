package co.com.crediya.r2dbc.repository;

import co.com.crediya.r2dbc.entity.LoanApplicationEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface LoanApplicationReactiveRepository extends
        ReactiveCrudRepository<LoanApplicationEntity, UUID>,
        ReactiveQueryByExampleExecutor<LoanApplicationEntity> {
}
