package co.com.crediya.validation;

import co.com.crediya.api.authentication.filter.AuthUserDetails;
import co.com.crediya.api.validation.RoleValidatorImp;
import co.com.crediya.api.validation.exception.ForbiddenException;
import co.com.crediya.model.role.enums.Roles;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RoleValidatorImpTest {

    private final RoleValidatorImp roleValidator = new RoleValidatorImp();

    private AuthUserDetails buildUserWithRole() {
        return new AuthUserDetails(
                UUID.randomUUID().toString(),
                "user@test.com",
                "password",
                List.of(() -> Roles.CLIENT.getId().toString()) // GrantedAuthority
        );
    }

    @Test
    void successWhenUserHasRole() {
        var userDetails = buildUserWithRole();
        var auth = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        var context = new SecurityContextImpl(auth);

        var result = roleValidator.validateRole(Roles.CLIENT)
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void errorWhenUserDoesNotHaveRole() {
        var userDetails = buildUserWithRole();
        var auth = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        var context = new SecurityContextImpl(auth);

        var result = roleValidator.validateRole(Roles.ADMIN)
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));

        StepVerifier.create(result)
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(ForbiddenException.class);
                })
                .verify();
    }
}
