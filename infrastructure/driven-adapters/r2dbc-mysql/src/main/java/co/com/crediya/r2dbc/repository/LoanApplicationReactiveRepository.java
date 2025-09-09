package co.com.crediya.r2dbc.repository;

import co.com.crediya.r2dbc.entity.LoanApplicationEntity;
import co.com.crediya.r2dbc.projection.LoanApplicationWithDetails;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

public interface LoanApplicationReactiveRepository extends
        ReactiveCrudRepository<LoanApplicationEntity, UUID>,
        ReactiveQueryByExampleExecutor<LoanApplicationEntity> {

    @Query("""
    SELECT
        la.id AS id,
        la.amount AS amount,
        la.term AS term,
        la.identity_document AS identity_document,
        la.email AS email,
        la.status_code AS status_code,
        s.name as status_name,
        s.description AS status_description,
        la.loan_type_code AS loan_type_code,
        lt.name AS loan_type_name,
        lt.interest_rate AS loan_type_interest_rate,
        (
            SELECT COALESCE(
                SUM(
                    sub.amount * (
                        ( (sub_lt.interest_rate/12) * POWER(1 + (sub_lt.interest_rate/12), sub.term) )
                        /
                        (POWER(1 + (sub_lt.interest_rate/12), sub.term) - 1)
                    )
                ),
            0)
            FROM loan_applications sub
            INNER JOIN loan_types sub_lt ON sub.loan_type_code = sub_lt.code
            WHERE sub.identity_document = la.identity_document
              AND sub.status_code = :approvedStatus
              AND sub.id <> la.id
              AND sub.created_at < la.created_at
            ) AS total_monthly_debt_approved
    FROM loan_applications la
    INNER JOIN statuses s ON la.status_code = s.code
    INNER JOIN loan_types lt ON la.loan_type_code = lt.code
    WHERE (:identityDocument IS NULL OR la.identity_document = :identityDocument)
      AND (:loanTypeCode IS NULL OR la.loan_type_code = :loanTypeCode)
      AND (:statusCode IS NULL OR la.status_code = :statusCode)
      AND (:autoApproval IS NULL OR (lt.auto_approval = :autoApproval))
    ORDER BY la.created_at DESC
    LIMIT :limit OFFSET :offset
""")
    Flux<LoanApplicationWithDetails> findAllWithDetails(
            @Param("identityDocument") String identityDocument,
            @Param("statusCode") String statusCode,
            @Param("loanTypeCode") String loanTypeCode,
            @Param("approvedStatus") String approvedStatus,
            @Param("autoApproval") Boolean autoApproval,
            @Param("limit") Integer limit,
            @Param("offset") Integer offset
    );

    @Query("""
    SELECT COUNT(*)
    FROM loan_applications la
    INNER JOIN loan_types lt ON la.loan_type_code = lt.code
    WHERE (:identityDocument IS NULL OR la.identity_document = :identityDocument)
      AND (:statusCode IS NULL OR la.status_code = :statusCode)
      AND (:loanTypeCode IS NULL OR la.loan_type_code = :loanTypeCode)
      AND (:autoApproval IS NULL OR (lt.auto_approval = :autoApproval))
""")
    Mono<Long> countByFilters(
            @Param("identityDocument") String identityDocument,
            @Param("statusCode") String statusCode,
            @Param("loanTypeCode") String loanTypeCode,
            @Param("autoApproval") Boolean autoApproval
    );

    @Query("""
SELECT COALESCE(
    SUM(
        la.amount * (
            ( (lt.interest_rate/12) * POWER(1 + (lt.interest_rate/12), la.term) )
            /
            (POWER(1 + (lt.interest_rate/12), la.term) - 1)
        )
    ), 0)
FROM loan_applications la
INNER JOIN loan_types lt ON la.loan_type_code = lt.code
INNER JOIN loan_applications lau ON lau.id = :id
WHERE la.identity_document = lau.identity_document
  AND la.status_code = :approvedStatus
  AND la.id <> :id
""")
    Mono<BigDecimal> getTotalMonthlyDebtApprovedFromLoanApplicationById(
            @Param("id") UUID id,
            @Param("approvedStatus") String approvedStatus
    );
}
