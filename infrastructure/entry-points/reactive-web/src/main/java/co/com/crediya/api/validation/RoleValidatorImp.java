package co.com.crediya.api.validation;

import co.com.crediya.api.contract.RoleValidator;
import co.com.crediya.api.validation.exception.ForbiddenException;
import co.com.crediya.model.role.enums.Roles;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class RoleValidatorImp implements RoleValidator {

    @Override
    public Mono<Void> validateRole(Roles... roles) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(auth -> {
                    boolean hasRole = Arrays.stream(roles)
                            .anyMatch(role -> auth.getAuthorities().stream()
                                    .anyMatch(granted -> granted.getAuthority().equals(role.getId().toString())));
                    if (!hasRole) {

                        return Mono.error(new ForbiddenException("No tiene el rol necesario para realizar esta acción"));
                    }

                    return Mono.empty();
                });
    }
}
