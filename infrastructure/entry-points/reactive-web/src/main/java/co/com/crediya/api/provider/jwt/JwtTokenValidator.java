package co.com.crediya.api.provider.jwt;

import co.com.crediya.api.dto.common.ClaimsDTO;
import co.com.crediya.api.contract.TokenValidator;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import reactor.core.publisher.Mono;

import java.util.Date;

@Component
public class JwtTokenValidator implements TokenValidator {

    private final JwtProperties properties;

    public JwtTokenValidator(JwtProperties properties) {
        this.properties = properties;
    }

    public Mono<ClaimsDTO> validateTokenAndGetClaims(String token) {
        try {
            var claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(properties.getSecret().getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            if (claims.getExpiration().before(new Date())) {
                return Mono.empty();
            }

            return Mono.just(new ClaimsDTO(
                    claims.get("userId", String.class),
                    claims.getSubject(),
                    claims.get("roleId", String.class)
            ));
        } catch (Exception e) {
            return Mono.empty();
        }
    }
}
