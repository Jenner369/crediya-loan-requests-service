package co.com.crediya.provider.jwt;

import co.com.crediya.api.provider.jwt.JwtProperties;
import co.com.crediya.api.provider.jwt.JwtTokenValidator;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtTokenValidatorTest {

    private JwtTokenValidator validator;
    private JwtProperties properties;

    @BeforeEach
    void setup() {
        properties = new JwtProperties();

        properties.setSecret("my-super-secret-key-that-is-long-enough!");
        validator = new JwtTokenValidator(properties);
    }

    @Test
    void validateTokenSuccess() {
        String userId = UUID.randomUUID().toString();
        String roleId = UUID.randomUUID().toString();
        String subject = "jenner@crediya.com";

        String token = Jwts.builder()
                .setSubject(subject)
                .claim("userId", userId)
                .claim("roleId", roleId)
                .setExpiration(new Date(System.currentTimeMillis() + 60_000)) // 1 min
                .signWith(Keys.hmacShaKeyFor(properties.getSecret().getBytes()), SignatureAlgorithm.HS256)
                .compact();

        StepVerifier.create(validator.validateTokenAndGetClaims(token))
                .assertNext(claims -> {
                    assertEquals(userId, claims.userId());
                    assertEquals(subject, claims.username());
                    assertEquals(roleId, claims.roleId());
                })
                .verifyComplete();
    }

    @Test
    void validateTokenExpiredTokenReturnsEmpty() {
        String token = Jwts.builder()
                .setSubject("jenner@crediya.com")
                .setExpiration(new Date(System.currentTimeMillis() - 1000)) // expirado
                .signWith(Keys.hmacShaKeyFor(properties.getSecret().getBytes()), SignatureAlgorithm.HS256)
                .compact();

        StepVerifier.create(validator.validateTokenAndGetClaims(token))
                .verifyComplete();
    }

    @Test
    void validateTokenInvalidTokenReturnsEmpty() {
        String invalidToken = "this.is.not.a.valid.token";

        StepVerifier.create(validator.validateTokenAndGetClaims(invalidToken))
                .verifyComplete();
    }
}
