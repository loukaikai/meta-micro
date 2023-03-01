package com.micro.meta.auth.config;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

/**
 * SpringSecurity配置 使用了WebSecurityConfigurerAdapter来配置安全设置
 * Created by loukaikai on 2020/6/19.
 */
@Configuration
@EnableWebSecurity(debug = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 这里的configure()方法配置了安全策略，
     * 规定了哪些URL是不需要进行安全认证的，哪些需要进行认证。这里的配置允许端点请求，
     * 允许RSA公钥请求，允许Swagger的API文档请求，允许OAuth2的令牌请求，
     * 并且要求其他所有请求都必须进行身份认证。
     * **/
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll()
                .antMatchers("/rsa/publicKey").permitAll()
                .antMatchers("/v2/api-docs", "doc.html").permitAll()
                .antMatchers("/doc.html", "/doc.html/**", "/webjars/**", "/v2/**", "/swagger-resources",
                        "/swagger-resources/**", "/swagger-ui.html", "/swagger-ui.html/**").permitAll()
                .antMatchers("/oauth/token").permitAll()
                .anyRequest().authenticated().and().csrf().disable();
    }

    /**
     * 用于获取 AuthenticationManager 实例
     * **/
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * PasswordEncoder用于加密和解密用户密码。
     * 在这个配置类中，passwordEncoder()方法被@Bean注解标记，
     * 以便可以创建PasswordEncoder实例并在其他地方使用
     * **/
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
