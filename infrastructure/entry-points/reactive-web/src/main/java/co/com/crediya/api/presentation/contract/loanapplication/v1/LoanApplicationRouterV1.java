package co.com.crediya.api.presentation.contract.loanapplication.v1;

import co.com.crediya.api.presentation.contract.loanapplication.v1.handler.RegisterLoanApplicationHandler;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class LoanApplicationRouterV1 {
    @Bean
    @RouterOperations({
        @RouterOperation(
            path = "/api/v1/solicitud",
            beanClass = RegisterLoanApplicationHandler.class,
            beanMethod = "handle",
            method = RequestMethod.POST
        )
    })
    public RouterFunction<ServerResponse> routerFunction(RegisterLoanApplicationHandler registerLoanApplicationHandler) {
        return RouterFunctions
            .route()
            .path("/api/v1/solicitud", builder -> {
                builder.POST("", registerLoanApplicationHandler::handle);
            })
            .build();
        }
}
