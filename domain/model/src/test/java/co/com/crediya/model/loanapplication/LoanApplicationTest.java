package co.com.crediya.model.loanapplication;

import co.com.crediya.model.loanapplication.exceptions.CannotChangeLoanApplicationStatusException;
import co.com.crediya.model.loanapplication.exceptions.InvalidStatusForLoanApplicationException;
import co.com.crediya.model.loantype.enums.LoanTypes;
import co.com.crediya.model.status.enums.Statuses;
import co.com.crediya.model.user.User;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoanApplicationTest {
    @Test
    void shouldSetIdentityDocumentAndEmail() {
        var user = new User();

        user.setIdentityDocument("12345678");
        user.setEmail("test@example.com");

        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setIdentityDocument("old-doc");
        loanApplication.setEmail("old@example.com");

        loanApplication.setUserDetails(user);

        assertThat(loanApplication.getIdentityDocument()).isEqualTo("12345678");
        assertThat(loanApplication.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void shouldNotFailWhenUserIsNull() {
        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setIdentityDocument("doc");
        loanApplication.setEmail("email@example.com");

        loanApplication.setUserDetails(null);

        assertThat(loanApplication.getIdentityDocument()).isEqualTo("doc");
        assertThat(loanApplication.getEmail()).isEqualTo("email@example.com");
    }

    @Test
    void shouldChangeStatus() {
        var loanApplication = LoanApplication
                .builder()
                .id(UUID.randomUUID())
                .loanType(LoanTypes.BUSINESS_LOAN.toModel())
                .status(Statuses.PENDING.toModel())
                .user(User.builder()
                        .id(UUID.randomUUID())
                        .name("Jenner")
                        .lastName("Durand")
                        .email("jennerjose369@gmail.com")
                        .build()
                )
                .statusCode(Statuses.PENDING.getCode())
                .build();

        loanApplication.changeStatus(Statuses.APPROVED.getCode());

        assertThat(loanApplication.getStatusCode()).isEqualTo(Statuses.APPROVED.getCode());
    }

    @Test
    void shouldThrowExceptionWhenChangingToInvalidStatus() {
        var loanApplication = LoanApplication
                .builder()
                .statusCode(Statuses.PENDING.getCode())
                .build();

        try {
            loanApplication.changeStatus("invalid_status");
        } catch (Exception e) {
            assertThat(e).isInstanceOf(InvalidStatusForLoanApplicationException.class);
        }
    }

    @Test
    void shouldThrowExceptionWhenChangingFromNonPendingStatus() {
        var loanApplication = LoanApplication
                .builder()
                .statusCode(Statuses.APPROVED.getCode())
                .build();

        try {
            loanApplication.validateCanChangeStatus(Statuses.REJECTED.getCode());
        } catch (Exception e) {
            assertThat(e).isInstanceOf(CannotChangeLoanApplicationStatusException.class);
        }
    }

    @Test
    void shouldApproveLoan() {
        LoanApplication loan = new LoanApplication();
        loan.setStatusCode(Statuses.PENDING.getCode());

        loan.approve();

        assertThat(loan.getStatusCode()).isEqualTo(Statuses.APPROVED.getCode());
    }

    @Test
    void shouldRejectLoan() {
        LoanApplication loan = new LoanApplication();
        loan.setStatusCode(Statuses.PENDING.getCode());

        loan.reject();

        assertThat(loan.getStatusCode()).isEqualTo(Statuses.REJECTED.getCode());
    }

    @Test
    void shouldMarkAsManualReview() {
        LoanApplication loan = new LoanApplication();
        loan.setStatusCode(Statuses.PENDING.getCode());

        loan.markAsManualReview();

        assertThat(loan.getStatusCode()).isEqualTo(Statuses.MANUAL_REVIEW.getCode());
    }

    @Test
    void shouldChangeStatusFromDecisionToManualReview() {
        LoanApplication loan = new LoanApplication();
        loan.setStatusCode(Statuses.PENDING.getCode());

        loan.changeStatusFromDecision(Statuses.MANUAL_REVIEW.getCode());

        assertThat(loan.getStatusCode()).isEqualTo(Statuses.MANUAL_REVIEW.getCode());
    }

    @Test
    void shouldNotAllowChangeFromDecisionIfStatusIsNotPending() {
        LoanApplication loan = new LoanApplication();
        loan.setStatusCode(Statuses.APPROVED.getCode());
        var manualReviewStatusCode = Statuses.MANUAL_REVIEW.getCode();

        assertThatThrownBy(() ->
                loan.validateCanChangeStatusFromDecision(manualReviewStatusCode)
        ).isInstanceOf(CannotChangeLoanApplicationStatusException.class);
    }
}
