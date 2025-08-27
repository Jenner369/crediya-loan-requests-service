package co.com.crediya.model.loanapplication;

import co.com.crediya.model.user.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
}
