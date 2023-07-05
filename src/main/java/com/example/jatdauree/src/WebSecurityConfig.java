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
                .antMatchers("/jat/sellers/test").hasRole("SELLER")
                .anyRequest().permitAll()
                .and()
                .addFilterBefore(new JwtAuthenticateFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);

        http.cors();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}
