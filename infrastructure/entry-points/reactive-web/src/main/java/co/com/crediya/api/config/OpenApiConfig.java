package co.com.crediya.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "CrediYA Loan Requests API",
                version = "1.0.0",
                description = "Loan Requests API for CrediYA Application",
                contact = @Contact(
                        name = "Pragma Team",
                        email = "support@pragma.co",
                        url = "https://pragma.co"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                )
        )
)
public class OpenApiConfig {
}
