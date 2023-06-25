package com.example.jatdauree.src;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class


WebSecurityConfig extends WebSecurityConfigurerAdapter {
    //https://velog.io/@cminmins/Spring-Status-401-error
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
    }
}
