package rest.onlineShop.jwt;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import rest.onlineShop.security.PersonDetails;

import java.util.Date;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.expiration}")
    private long jwtExpirationInMs;
    public String generateToken(Authentication authentication){
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        Date dateNow = new Date();
        Date expirationDate = new Date(dateNow.getTime() + jwtExpirationInMs);
        return Jwts.builder()
                .setSubject(personDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
    public String getUsernameFromJwt(String token){
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
    public boolean validateToken(String authToken){
        try{
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        }catch (MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException ex){
            System.out.println(ex.getMessage());
        }
        return false;
    }
}
