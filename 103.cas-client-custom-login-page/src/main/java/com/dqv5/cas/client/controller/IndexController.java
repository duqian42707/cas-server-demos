package com.dqv5.cas.client.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author duqian
 * @date 2019/4/29
 */
@Controller
public class IndexController {
    @Resource
    private RestTemplate restTemplate;

    @Value("${custom.cas.server.url}")
    private String casServerUrl;

    /**
     * 调用cas的restful接口，通过用户名、密码获取tgt
     * 然后构造出重定向的地址返回给页面
     *
     * @param username
     * @param password
     * @param service
     * @return
     * @throws UnsupportedEncodingException
     */
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, String service) throws UnsupportedEncodingException {
        String api = casServerUrl + "/v1/tickets";
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("username", username);
        map.add("password", password);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
        String tgt = this.restTemplate.postForObject(api, request, String.class);
        String url = casServerUrl + "/auto-login?tgt=" + tgt;
        if (!StringUtils.isEmpty(service)) {
            url += "&service=" + URLEncoder.encode(service, "UTF-8");
        }
        return "redirect:" + url;
    }


}
