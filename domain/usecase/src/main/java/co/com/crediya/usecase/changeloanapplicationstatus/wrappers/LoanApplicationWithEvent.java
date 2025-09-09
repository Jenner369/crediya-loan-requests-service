package co.com.crediya.usecase.changeloanapplicationstatus.wrappers;

import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.events.LoanApplicationChangedEvent;

public record LoanApplicationWithEvent(
        LoanApplication loanApplication,
        LoanApplicationChangedEvent event) {
}
