package com.practice.authentication.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
public class JwtTokenGenerator {
    private final long EXPIRY_TIME = 30*60*1000;
    private String JWT_SECRET = "practice";

    public String generateJwtToken(Map<String,Object> claims,String subject){
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() +EXPIRY_TIME)).compact();
    }

    private Claims getAllClaimsFromToken(String token){
        return Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJwt(token).getBody();
    }

    private <T> T getClaimResolverFromToken(String token, Function<Claims,T> claimResolver){
        Claims claims = getAllClaimsFromToken(token);
        return claimResolver.apply(claims);
    }

    public Date getIssueTimeFromToken(String token){
        return getClaimResolverFromToken(token,Claims::getIssuedAt);
    }

    public Date getExpiryTimeFromToken(String token){
        return getClaimResolverFromToken(token,Claims::getExpiration);
    }
}
