package com.future.pms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.*;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

@Configuration @EnableSwagger2 public class SwaggerConfig implements WebMvcConfigurer {
    @Override public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("swagger-ui.html")
            .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
            .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Bean public Docket Api() {
        return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.any())
            .paths(PathSelectors.any()).build();
    }

    @Override public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/api/v2/api-docs", "/v2/api-docs");
        registry.addRedirectViewController("/api/swagger-resources/configuration/ui",
            "/swagger-resources/configuration/ui");
        registry.addRedirectViewController("/api/swagger-resources/configuration/security",
            "/swagger-resources/configuration/security");
        registry.addRedirectViewController("/api/swagger-resources", "/swagger-resources");
    }

    @Override public void configureViewResolvers(ViewResolverRegistry registry) {
        //No implementation required
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        //No implementation required
    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        //No implementation required
    }

    @Override public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        //No implementation required
    }

    @Override public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //No implementation required
    }

    @Override public void configureHandlerExceptionResolvers(
        List<HandlerExceptionResolver> exceptionResolvers) {
    }

    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        //No implementation required
    }

    @Override public Validator getValidator() {
        return null;
    }

    @Override public MessageCodesResolver getMessageCodesResolver() {
        return null;
    }

    @Override public void configurePathMatch(PathMatchConfigurer configurer) {
        //No implementation required
    }

    @Override public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        //No implementation required
    }

    @Override public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        //No implementation required
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        //No implementation required
    }

    @Override public void addFormatters(FormatterRegistry registry) {
        //No implementation required
    }

    @Override public void addInterceptors(InterceptorRegistry registry) {
        //No implementation required
    }

    @Override public void addCorsMappings(CorsRegistry registry) {
        //No implementation required
    }
}
