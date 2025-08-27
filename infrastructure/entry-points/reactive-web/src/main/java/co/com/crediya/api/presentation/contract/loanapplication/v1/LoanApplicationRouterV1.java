package co.com.crediya.api.presentation.contract.loanapplication.v1;

import co.com.crediya.api.presentation.contract.loanapplication.v1.handler.RegisterLoanApplicationHandlerV1;
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
            beanClass = RegisterLoanApplicationHandlerV1.class,
            beanMethod = "handle",
            method = RequestMethod.POST
        )
    })
    public RouterFunction<ServerResponse> routerFunction(RegisterLoanApplicationHandlerV1 registerLoanApplicationHandlerV1) {
        return RouterFunctions
            .route()
            .path("/api/v1/solicitud", builder -> {
                builder.POST("", registerLoanApplicationHandlerV1::handle);
            })
            .build();
        }
}
