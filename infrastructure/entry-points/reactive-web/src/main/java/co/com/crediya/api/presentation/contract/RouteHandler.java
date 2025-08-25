package co.com.crediya.api.presentation.contract;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface RouteHandler {
    Mono<ServerResponse> handle(ServerRequest request);
}
