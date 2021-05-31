package com.dqv5.cas.client.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author duqian
 * @date 2019/4/29
 */
@Controller
@Slf4j
public class IndexController {

    @Resource
    private RestTemplate restTemplate;

    @Value("${cas.server.url}")
    private String casServerUrl;
    @Value("${cas.client.clientId}")
    private String clientId;
    @Value("${cas.client.clientSecret}")
    private String clientSecret;
    @Value("${cas.client.redirectUrl}")
    private String clientUrl;

    @RequestMapping("/")
    public String index() {
        return "index";
    }


    @RequestMapping("/authorize")
    public String authorize() {
        String redirectURI = URLEncoder.encode(clientUrl);
        String url = casServerUrl + "/oauth2.0/authorize?response_type=code&client_id=" + clientId + "&redirect_uri=" + redirectURI;
        return "redirect:" + url;
    }

    @RequestMapping("/auth")
    public String auth(ModelMap modelMap, String code) {
        String url = casServerUrl + "/oauth2.0/accessToken?grant_type=authorization_code&client_id={clientId}&client_secret={clientSecret}&code={code}&redirect_uri={redirectURI}";
        // 请求参数
        Map<String, Object> param = new HashMap<>();
        param.put("clientId", clientId);
        param.put("clientSecret", clientSecret);
        param.put("code", code);
        param.put("redirectURI", clientUrl);
        log.info(code);
        String res1 = restTemplate.getForObject(url, String.class, param);
        log.info(res1);
        String[] split = res1.split("&");
        String accessToken = null;
        for (String pair : split) {
            if (pair.startsWith("access_token=")) {
                accessToken = pair.substring("access_token=".length());
            }
        }

        String profileUrl = casServerUrl + "/oauth2.0/profile?access_token=" + accessToken;
        log.info(profileUrl);
        String res2 = restTemplate.getForObject(profileUrl, String.class);
        log.info(res2);
        modelMap.put("userInfo", res2);
        return "index";
    }


}
