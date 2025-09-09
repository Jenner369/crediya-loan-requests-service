package co.com.crediya.model.loanapplication;
import co.com.crediya.model.loanapplication.events.LoanApplicationChangedEvent;
import co.com.crediya.model.loanapplication.exceptions.CannotChangeLoanApplicationStatusException;
import co.com.crediya.model.loanapplication.exceptions.InvalidStatusForLoanApplicationException;
import co.com.crediya.model.loantype.LoanType;
import co.com.crediya.model.status.Status;
import co.com.crediya.model.status.enums.Statuses;
import co.com.crediya.model.user.User;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanApplication {
    private UUID id;
    private BigDecimal amount;
    private Integer term;
    private String identityDocument;
    private String email;
    private String statusCode;
    private String loanTypeCode;

    // Read only
    private LoanType loanType;
    private Status status;
    private User user;
    private BigDecimal totalMonthlyDebtApproved;

    public void setUserDetails(User user) {
        if (user != null) {
            this.identityDocument = user.getIdentityDocument();
            this.email = user.getEmail();
        }
    }

    public void approve() {
        this.statusCode = Statuses.APPROVED.getCode();
    }

    public void reject() {
        this.statusCode = Statuses.REJECTED.getCode();
    }

    public void markAsManualReview() {
        this.statusCode = Statuses.MANUAL_REVIEW.getCode();
    }

    // From PENDING to APPROVED or REJECTED (Advisor Process)
    private Boolean canChangeStatus(String newStatusCode) {
        return (Statuses.isApproved(newStatusCode)
                || Statuses.isRejected(newStatusCode))
                && Statuses.PENDING.getCode().equals(statusCode);
    }

    public void validateCanChangeStatus(String newStatusCode) {
        if (Boolean.FALSE.equals(canChangeStatus(newStatusCode))) {
            throw new CannotChangeLoanApplicationStatusException();
        }
    }

    public LoanApplicationChangedEvent changeStatus(String newStatusCode) {
        if (Boolean.TRUE.equals(Statuses.isApproved(newStatusCode))) {
            approve();
        } else if (Boolean.TRUE.equals(Statuses.isRejected(newStatusCode))) {
            reject();
        } else {
            throw new InvalidStatusForLoanApplicationException();
        }

        return new LoanApplicationChangedEvent(
                id.toString(),
                amount,
                term,
                loanType.getName(),
                status.getName(),
                user.getEmail(),
                user.getName(),
                user.getLastName()
        );
    }

    // From PENDING to APPROVED, REJECTED or MANUAL_REVIEW (Auto Approval Process)
    private Boolean canChangeStatusFromDecision(String newStatusCode) {
        return (Statuses.isApproved(newStatusCode)
                || Statuses.isRejected(newStatusCode)
                || Statuses.isManualReview(newStatusCode))
                && Statuses.PENDING.getCode().equals(statusCode);
    }

    public void validateCanChangeStatusFromDecision(String newStatusCode) {
        if (Boolean.FALSE.equals(canChangeStatusFromDecision(newStatusCode))) {
            throw new CannotChangeLoanApplicationStatusException();
        }
    }

    public void changeStatusFromDecision(String newStatusCode) {
        if (Boolean.TRUE.equals(Statuses.isApproved(newStatusCode))) {
            approve();
        } else if (Boolean.TRUE.equals(Statuses.isRejected(newStatusCode))) {
            reject();
        } else if (Boolean.TRUE.equals(Statuses.isManualReview(newStatusCode))) {
            markAsManualReview();
        } else {
            throw new InvalidStatusForLoanApplicationException();
        }
    }
}
