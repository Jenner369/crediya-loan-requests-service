package co.com.crediya.usecase.getuserbyidentitydocument;

import co.com.crediya.contract.ReactiveUseCase;
import co.com.crediya.exception.UserNotFoundException;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GetUserByIdentityDocumentUseCase implements ReactiveUseCase<String, Mono<User>> {

    private final UserRepository userRepository;

    @Override
    public Mono<User> execute(String identityDocument) {
        return userRepository.findByIdentityDocument(identityDocument)
                .switchIfEmpty(Mono.error(new UserNotFoundException()));
    }
}
