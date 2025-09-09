package co.com.crediya.config;

import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import reactor.netty.http.client.HttpClient;

import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Configuration
public class AuthRestConsumerConfig {

    private final String url;
    private final int timeout;

    public AuthRestConsumerConfig(@Value("${adapter.authrestconsumer.url}") String url,
                                  @Value("${adapter.authrestconsumer.timeout}") int timeout) {
        this.url = url;
        this.timeout = timeout;
    }

    @Bean(name = "authWebClient")
    public WebClient authWebClient(WebClient.Builder builder) {
        return builder
            .baseUrl(url)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
            .clientConnector(getClientHttpConnector())
            .filter(authorizationHeaderFilter())
            .build();
    }

    private ExchangeFilterFunction authorizationHeaderFilter() {
        return (request, next) ->
                ReactiveSecurityContextHolder.getContext()
                        .map(SecurityContext::getAuthentication)
                        .filter(Authentication::isAuthenticated)
                        .flatMap(auth -> {
                            Object credentials = auth.getCredentials();
                            if (credentials instanceof String token && !token.isBlank()) {
                                var newRequest = ClientRequest.from(request)
                                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                        .build();

                                return next.exchange(newRequest);
                            }

                            return next.exchange(request);
                        })
                        .switchIfEmpty(next.exchange(request));
    }

    private ClientHttpConnector getClientHttpConnector() {
        return new ReactorClientHttpConnector(HttpClient.create()
                .compress(true)
                .keepAlive(true)
                .option(CONNECT_TIMEOUT_MILLIS, timeout)
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(timeout, MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(timeout, MILLISECONDS));
                }));
    }

}
