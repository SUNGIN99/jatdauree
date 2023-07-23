package com.example.jatdauree.src;

import com.example.jatdauree.utils.jwt.JwtAuthenticateFilter;
import com.example.jatdauree.utils.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class
WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;

    //https://velog.io/@cminmins/Spring-Status-401-error
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                // 권한 테스트 URI ( 나중에삭제)
                .antMatchers("/jat/sellers/test").hasRole("SELLER")
                // app API (전부 요청 허락 23.07.24)
                .antMatchers("/jat/app/**").permitAll()
                // 관리자 요청 필터
                .antMatchers("/jat/stores/admin/**").hasRole("ADMIN")
                .antMatchers("/jat/reviews/admin").hasRole("ADMIN")
                // 판매자 회원관련 API는 비밀번호 재설정빼고 토큰 필요 X
                .antMatchers("/jat/sellers/pw-restore").hasRole("SELLER")
                .antMatchers("/jat/sellers/**").permitAll()
                // 그외 웹으로 들어오는 요청모두 권한 확인
                .antMatchers("/jat/**").hasRole("SELLER")
                // 말고 그냥 들어오는 모든 요청 거절절
               .anyRequest().denyAll()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(new JwtAuthenticateFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);

        //http.cors();
        //http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}
