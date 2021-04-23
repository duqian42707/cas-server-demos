package com.dqv5.cas.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @author duqian
 * @date 2019/4/29
 */
@SpringBootApplication
public class CasClientApplication extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(CasClientApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(CasClientApplication.class, args);
    }

}
