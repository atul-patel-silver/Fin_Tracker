package org.api.gateway.config;//package com.gateway.sever.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;

import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.config.EnableWebFlux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {


    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity serverHttpSecurity) {
        serverHttpSecurity.authorizeExchange(exchanges -> exchanges
                        .pathMatchers(
                                  "/",
                                "/user-service/api/auth/**",
                                "/user-service/api/role/**",
                                "/user-service/api/home/**").permitAll()
                        .pathMatchers(
                                "/user-service/api/users/**").hasRole("ADMIN")
                        .pathMatchers("/user/api/update-user/",
                                "/user/api/fetch/",
                                "/user/api/delete/",
                                "/address/user/**").hasRole("USER").anyExchange().authenticated());
        serverHttpSecurity.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {}));

        serverHttpSecurity.csrf(ServerHttpSecurity.CsrfSpec::disable);
        return serverHttpSecurity.build();
    }


}
