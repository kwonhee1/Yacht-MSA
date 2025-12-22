package HooYah.Gateway.user;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import java.util.Date;

public class JWTService {

    private final String secretKey;

    private final long expirationSecond;

    public JWTService(String secretKey,  long expirationSecond) {
        this.secretKey = secretKey;
        this.expirationSecond = expirationSecond;
    }

    public String generateToken(Long userId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationSecond))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Long decodeToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

}
