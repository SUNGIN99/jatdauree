package com.example.jatdauree.utils.jwt;

import com.example.jatdauree.config.secret.JwtSecret;
import com.example.jatdauree.src.domain.web.seller.service.SellerService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 23.06.27 JWT 적용 확인
 * 기본 필드 및 createJwt(), getUserId(), getAuthentication(), resolveToken(), validateToken() 등 기본함수 구현
 */

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    private final SellerService sellerService;
    // 23.06.27 나중에 토큰 유효시간 바꿀것
    private long tokenValidTime = 1*(1000*60*60*24*365);

    /*
    //https://superbono-2020.tistory.com/186
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }*/

    // 23.06.27 나중에 구매자, 판매자에 따른 Role 설정 해줘야함
    public String createJwt(int userIdx){
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam("type", "jwt")
                .claim("userIdx", userIdx) // jwt 토큰 내의 payload 정보 (key, value)
                .setIssuedAt(now) //토큰 발행 시간 정보
                .setExpiration(new Date(System.currentTimeMillis()+ 1*tokenValidTime)) // 토큰 유효기간
                .signWith(SignatureAlgorithm.HS256, JwtSecret.JWT_SECRET_KEY) // 사용할 암호화 알고리즘, signature에 들어갈 secret값 세팅
                .compact();
    }

    // JWT 토큰에서 인증정보 조회
    public Authentication getAuthentication(String token){
        UserDetails userDetails = sellerService.loadUserByUserIdx(this.getUserId(token));
        log.info("JwtTokenProvider : {}", userDetails.getUsername());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    //토큰에서 회원정보 추출
    public Long getUserId(String token){
        return new Long(Jwts.parser().setSigningKey(JwtSecret.JWT_SECRET_KEY).parseClaimsJws(token).getBody().get("userIdx", Integer.class));
    }

    // Request의 Header에서 token 값을 가져옴 "X-ACCESS-TOKEN" : "TOKEN 값"
    public String resolveToken(HttpServletRequest request){
        return request.getHeader("X-ACCESS-TOKEN");
    }

    // 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(JwtSecret.JWT_SECRET_KEY).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

}


