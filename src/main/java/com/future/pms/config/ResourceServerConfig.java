package com.future.pms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;

import static com.future.pms.Constants.RESOURCE_ID;

@Configuration @EnableResourceServer public class ResourceServerConfig
    extends ResourceServerConfigurerAdapter {

    @Override public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId(RESOURCE_ID).stateless(false);
    }

    @Override public void configure(HttpSecurity http) throws Exception {
        http.
            anonymous().disable().authorizeRequests().antMatchers("/api/**")
            .access("hasRole('ROLE_CUSTOMER')").and().
            anonymous().disable().authorizeRequests().antMatchers("/api2/**")
            .access("hasRole('ROLE_ADMIN')").and().
            anonymous().disable().authorizeRequests().antMatchers("/api3/**")
            .access("hasRole('ROLE_SUPER_ADMIN')").and().exceptionHandling()
            .accessDeniedHandler(new OAuth2AccessDeniedHandler());
    }

}
