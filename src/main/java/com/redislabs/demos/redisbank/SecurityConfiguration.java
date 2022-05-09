package com.redislabs.demos.redisbank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
	private Config config;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
        .csrf().disable()
        .authorizeRequests()
        .antMatchers("/auth-login.html").permitAll()
        .antMatchers("/assets/**").permitAll()
        .antMatchers("/ws/info", "/ws/**", "/websocket") .permitAll()
        .anyRequest().authenticated()
        .and()
        .formLogin()
        .loginPage("/auth-login.html")
        .loginProcessingUrl("/perform_login")
        .defaultSuccessUrl("/index.html")
        .failureUrl("/auth-login.html?error=true");

    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception   {
        auth.inMemoryAuthentication()
        .withUser("redis")
        .password("{noop}redisPass")
        .roles("USER");
    }

}
