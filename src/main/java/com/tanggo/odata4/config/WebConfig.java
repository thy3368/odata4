package com.tanggo.odata4.config;

import com.tanggo.odata4.servlet.ODataServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Bean
    public ServletRegistrationBean<ODataServlet> odataServlet(ODataServlet servlet) {
        ServletRegistrationBean<ODataServlet> registration = new ServletRegistrationBean<>(
            servlet, 
            "/odata/*"  // OData 服务的基础路径
        );
        registration.setLoadOnStartup(1);
        return registration;
    }
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/odata/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("OData-Version");
    }
} 