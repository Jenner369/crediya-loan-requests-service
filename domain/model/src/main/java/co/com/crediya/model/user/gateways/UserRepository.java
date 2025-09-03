package co.com.crediya.model.user.gateways;

import co.com.crediya.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserRepository {
    Mono<User> findByIdentityDocument(String identityDocument);
    Flux<User> findAllByIdentityDocuments(List<String> identityDocuments);
}
