package co.com.crediya.model.status.gateways;

import co.com.crediya.model.status.Status;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

public interface StatusRepository {
    Mono<Status> findById(UUID id);
    Mono<Optional<Status>> findByName(String name);
}
