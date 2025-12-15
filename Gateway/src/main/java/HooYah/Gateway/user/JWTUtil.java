package HooYah.Gateway.user;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import jakarta.annotation.PostConstruct;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class JWTUtil {

    private static String SECRET_KEY;
    private static long EXPIRATION_SECOND;

    @Value("${token.SECRET_KEY}")
    private String secretKey;

    @Value("${token.EXPIRATION_SECOND}")
    private long expirationSecond;

    @PostConstruct
    public void init() {
        JWTUtil.SECRET_KEY = secretKey;
        JWTUtil.EXPIRATION_SECOND = expirationSecond;
    }

    public static String generateToken(Long userId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_SECOND))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public static Long decodeToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    public static String generateCookie(String token) {
        ResponseCookie cookie = ResponseCookie.from("token", token)
                .path("/")
                .sameSite("None")
                .httpOnly(true)
                .secure(true)
                .maxAge(EXPIRATION_SECOND)
                .build();

        return cookie.toString();
    }
}
