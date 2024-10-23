package com.tanppoppo.testore.testore.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    private static final String[] PUBLIC_URLS = {
            "/"
            , "/common/**"
            , "/member/js/**"
            , "/member/loginForm"
            , "/member/joinForm"
            , "/member/join"
            , "/member/verify-email"
            , "/member/resend-verification"
    };

    @Bean
    protected SecurityFilterChain config(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(author -> author
                        .requestMatchers(PUBLIC_URLS).permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin
                        .loginPage("/member/loginForm")
                        .usernameParameter("email")
                        .passwordParameter("memberPassword")
                        .loginProcessingUrl("/member/login")
                        .failureHandler((request, response, exception) -> {
                            if (exception instanceof InternalAuthenticationServiceException) {
                                response.sendRedirect("/member/loginForm?error=unverified");
                            } else if (exception instanceof BadCredentialsException) {
                                response.sendRedirect("/member/loginForm?error=badcredential");
                            } else {
                                response.sendRedirect("/member/loginForm?error=true");
                            }
                        })
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/member/logout")
                        .logoutSuccessUrl("/")
                );

        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable);

        http    
                .requiresChannel()
                .anyRequest()
                .requiresSecure();

        return http.build();

    }

    @Bean
    public BCryptPasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
