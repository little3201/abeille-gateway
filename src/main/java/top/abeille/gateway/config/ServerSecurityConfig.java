/*
 * Copyright (c) 2019. Abeille All Right Reserved.
 */
package top.abeille.gateway.config;


import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.HttpStatusReturningServerLogoutSuccessHandler;
import top.abeille.gateway.api.HypervisorApi;
import top.abeille.gateway.handler.AbeilleFailureHandler;
import top.abeille.gateway.handler.AbeilleSuccessHandler;
import top.abeille.gateway.service.AbeilleUserDetailsService;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;

/**
 * spring security 配置
 *
 * @author liwenqiang 2019/7/12 17:51
 */
@EnableWebFluxSecurity
public class ServerSecurityConfig {

    private static final String KEY_STORE = "jwt/abeille-top-jwt.jks";
    private static final String KEY_PASS = "abeille-top";
    private static final String ALIAS = "abeille-top-jwt";

    private final HypervisorApi hypervisorApi;

    public ServerSecurityConfig(HypervisorApi hypervisorApi) {
        this.hypervisorApi = hypervisorApi;
    }

    /**
     * 密码配置，使用BCryptPasswordEncoder
     */
    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 用户数据加载
     */
    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        return new AbeilleUserDetailsService(hypervisorApi);
    }

    /**
     * 安全配置
     */
    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.oauth2ResourceServer(o -> o.jwt().jwtDecoder(jwtDecoder()))
                .formLogin(f -> f.authenticationSuccessHandler(authenticationSuccessHandler())
                        .authenticationFailureHandler(authenticationFailureHandler()))
                .logout(l -> l.logoutSuccessHandler(new HttpStatusReturningServerLogoutSuccessHandler()))
                .csrf(c -> c.disable())
                .authorizeExchange(a -> a.pathMatchers(HttpMethod.OPTIONS).permitAll()
                        .pathMatchers(HttpMethod.GET, "/assets/**").permitAll()
                        .anyExchange().authenticated())
                .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)));
        return http.build();
    }

    /**
     * 登陆成功后执行的处理器
     */
    private ServerAuthenticationSuccessHandler authenticationSuccessHandler() {
        return new AbeilleSuccessHandler();
    }

    /**
     * 登陆失败后执行的处理器
     */
    private ServerAuthenticationFailureHandler authenticationFailureHandler() {
        return new AbeilleFailureHandler();
    }

    /**
     * JWT 解码器
     */
    private ReactiveJwtDecoder jwtDecoder() {
        return new NimbusReactiveJwtDecoder(readRSAPublicKey());
    }

    /**
     * 获取RSAPublicKey
     */
    private RSAPublicKey readRSAPublicKey() {
        ClassPathResource ksFile = new ClassPathResource(KEY_STORE);
        KeyStoreKeyFactory ksFactory = new KeyStoreKeyFactory(ksFile, KEY_PASS.toCharArray());
        KeyPair keyPair = ksFactory.getKeyPair(ALIAS);
        return (RSAPublicKey) keyPair.getPublic();
    }
}