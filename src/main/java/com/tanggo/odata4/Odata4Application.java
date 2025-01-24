package com.tanggo.odata4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ServletComponentScan
@EnableJpaRepositories
@ComponentScan(basePackages = {
    "com.tanggo.odata4.config",
    "com.tanggo.odata4.service",
    "com.tanggo.odata4.processor",
    "com.tanggo.odata4.repository",
    "com.tanggo.odata4.model",
    "com.tanggo.odata4.aspect",
    "com.tanggo.odata4.servlet"
})
public class Odata4Application {

    public static void main(String[] args) {
        SpringApplication.run(Odata4Application.class, args);
    }

}
