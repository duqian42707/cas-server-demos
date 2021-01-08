package com.dqv5.cas.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author duqian
 * @date 2019/4/29
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.cas")
public class SpringCasConfigProperties {
    private String validateFilters;
    private String signOutFilters;
    private String authFilters;
    private String requestWrapperFilters;

    private String casServerUrlPrefix;
    private String casServerLoginUrl;
    private String serverName;

}
