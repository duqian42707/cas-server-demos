package com.dqv5.cas.client.controller;

import com.dqv5.cas.client.config.SpringCasConfigProperties;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * @author duqian
 * @date 2019/4/29
 */
@Controller
public class IndexController {
    @Resource
    private SpringCasConfigProperties configProp;

    @RequestMapping("/")
    public String index2() {
        return "forward:/index";
    }


    @RequestMapping("/index")
    public String index(HttpServletRequest request, ModelMap modelMap) {
        String account = request.getRemoteUser();
        PrintStream out = System.out;
        out.println("account:" + account);
        modelMap.put("account", account);
        AttributePrincipal principal = (AttributePrincipal) request.getUserPrincipal();
        if (principal != null) {
            modelMap.put("principal", principal);
            final Map attributes = principal.getAttributes();
            if (attributes != null) {
                out.println("attributes:" + attributes);
                modelMap.put("attributes", attributes);
            }
        }
        return "index";
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) throws UnsupportedEncodingException {
        session.invalidate();
        // service 后面的参数为CAS客户端应用的回调地址，需要使用 URLEncoder 进行编码
        String redirectURL = configProp.getCasServerUrlPrefix() + "/logout?service=" + URLEncoder.encode(configProp.getServerName(), "UTF-8");
        return "redirect:" + redirectURL;
    }

}
