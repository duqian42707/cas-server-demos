package com.dqv5.cas.client.config;

import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.jasig.cas.client.util.HttpServletRequestWrapperFilter;
import org.jasig.cas.client.validation.Cas20ProxyReceivingTicketValidationFilter;
import org.jasig.cas.client.validation.Cas30ProxyReceivingTicketValidationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author duqian
 * @date 2019/4/29
 */
@Configuration
public class AppConfig {

    @Resource
    private SpringCasConfigProperties autoconfig;

    @Bean
    public ServletListenerRegistrationBean servletListenerRegistrationBean() {
        return new ServletListenerRegistrationBean<>(new SingleSignOutHttpSessionListener());
    }

    @Bean
    public FilterRegistrationBean filterSingleRegistration() {
        FilterRegistrationBean<SingleSignOutFilter> registrationBean = new FilterRegistrationBean<>();
        Map<String, String> initParameters = new HashMap<>(1);
        initParameters.put("casServerUrlPrefix", autoconfig.getCasServerUrlPrefix());
        registrationBean.setFilter(new SingleSignOutFilter());
        registrationBean.addUrlPatterns(autoconfig.getSignOutFilters());
        registrationBean.setInitParameters(initParameters);
        registrationBean.setOrder(1);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean authenticationFilterRegistrationBean() {
        FilterRegistrationBean<AuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
        Map<String, String> initParameters = new HashMap<>(2);
        initParameters.put("casServerLoginUrl", autoconfig.getCasServerLoginUrl());
        initParameters.put("serverName", autoconfig.getServerName());
        registrationBean.setFilter(new AuthenticationFilter());
        registrationBean.addUrlPatterns(autoconfig.getAuthFilters());
        registrationBean.setInitParameters(initParameters);
        registrationBean.setOrder(2);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean validationFilterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean<>();
        Map<String, String> initParameters = new HashMap<>(2);
        initParameters.put("casServerUrlPrefix", autoconfig.getCasServerUrlPrefix());
        initParameters.put("serverName", autoconfig.getServerName());
        initParameters.put("redirectAfterValidation", "true");
        initParameters.put("useSession", "true");
        initParameters.put("authn_method", "mfa-duo");
//        registrationBean.setFilter(new Cas20ProxyReceivingTicketValidationFilter());
        registrationBean.setFilter(new Cas30ProxyReceivingTicketValidationFilter());
        registrationBean.addUrlPatterns(autoconfig.getValidateFilters());
        registrationBean.setInitParameters(initParameters);
        registrationBean.setOrder(3);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean casHttpServletRequestWrapperFilter() {
        FilterRegistrationBean<HttpServletRequestWrapperFilter> registrationBean = new FilterRegistrationBean<>();
        List<String> urlPatterns = new ArrayList<>();
        urlPatterns.add(autoconfig.getRequestWrapperFilters());
        registrationBean.setFilter(new HttpServletRequestWrapperFilter());
        registrationBean.setUrlPatterns(urlPatterns);
        registrationBean.setOrder(4);
        return registrationBean;
    }

}
