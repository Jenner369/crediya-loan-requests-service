package co.com.crediya.api.presentation.loanapplication.v1;

import co.com.crediya.api.presentation.loanapplication.v1.handler.ChangeLoanApplicationStatusHandlerV1;
import co.com.crediya.api.presentation.loanapplication.v1.handler.ListLoanApplicationsWithDetailsHandlerV1;
import co.com.crediya.api.presentation.loanapplication.v1.handler.RegisterLoanApplicationHandlerV1;
import co.com.crediya.usecase.listloanapplicationswithdetails.ListLoanApplicationsWithDetailsUseCaseInput;
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
        ),
        @RouterOperation(
            path = "/api/v1/solicitud",
            beanClass = ListLoanApplicationsWithDetailsHandlerV1.class,
            beanMethod = "handle",
            method = RequestMethod.GET
        ),
        @RouterOperation(
            path = "/api/v1/solicitud",
            beanClass = ChangeLoanApplicationStatusHandlerV1.class,
            beanMethod = "handle",
            method = RequestMethod.PUT
        )
    })
    public RouterFunction<ServerResponse> routerFunction(
            RegisterLoanApplicationHandlerV1 registerLoanApplicationHandlerV1,
            ListLoanApplicationsWithDetailsHandlerV1 listLoanApplicationsWithDetailsHandlerV1,
            ChangeLoanApplicationStatusHandlerV1 changeLoanApplicationStatusHandlerV1
    ) {
        return RouterFunctions
            .route()
            .path("/api/v1/solicitud", builder -> builder
                    .GET("", listLoanApplicationsWithDetailsHandlerV1::handle)
                    .POST("", registerLoanApplicationHandlerV1::handle)
                    .PUT("", changeLoanApplicationStatusHandlerV1::handle)
            )
            .build();
        }
}
