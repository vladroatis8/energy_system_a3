package ro.utcluj.ds.auth_service.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ro.utcluj.ds.auth_service.entities.AuthUser;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // Injectăm cheia secretă din application.properties
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    // Generează un token doar pe baza datelor utilizatorului (username și rol)
    public String generateToken(AuthUser authUser) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", authUser.getId());
        claims.put("role", authUser.getRole());
        return createToken(claims, authUser.getUsername());
    }

    // Creează token-ul cu o valabilitate (ex: 10 ore)
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims) // Informațiile suplimentare (rol, id)
                .setSubject(subject) // Subiectul token-ului (username-ul)
                .setIssuedAt(new Date(System.currentTimeMillis())) // Când a fost creat
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Valabil 10 ore
                .signWith(getSignKey(), SignatureAlgorithm.HS256) // Semnăm cu cheia secretă
                .compact();
    }

    // Metodă helper pentru a obține cheia de semnare din textul secret
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // --- Metode de validare a token-ului ---
    // (Acestea vor fi folosite mai târziu de API Gateway sau de alte servicii)

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}