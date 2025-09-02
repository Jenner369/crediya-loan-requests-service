package co.com.crediya.api.contract;

import co.com.crediya.model.role.enums.Roles;
import reactor.core.publisher.Mono;

public interface RoleValidator {
    Mono<Void> validateRole(Roles... roles);
}
