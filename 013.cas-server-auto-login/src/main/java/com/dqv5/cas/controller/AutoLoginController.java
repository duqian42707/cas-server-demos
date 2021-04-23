package com.dqv5.cas.controller;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.web.support.CookieRetrievingCookieGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.security.auth.login.FailedLoginException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;

/**
 * @author duqian
 * @date 2021/4/23
 */
@Controller
public class AutoLoginController {

    @Autowired
    @Qualifier("ticketGrantingTicketCookieGenerator")
    private CookieRetrievingCookieGenerator cookieRetrievingCookieGenerator;

    @GetMapping("/auto-login")
    public String login(@RequestParam String tgt, String service, HttpServletRequest request, HttpServletResponse response) throws FailedLoginException {
        try {
            cookieRetrievingCookieGenerator.addCookie(request, response, tgt);
            String url = "redirect:login";
            if (StringUtils.isNotBlank(service)) {
                url = url + "?service=" + URLEncoder.encode(service);
            }
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            throw new FailedLoginException();
        }
    }
}
