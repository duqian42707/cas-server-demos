package com.dqv5.cas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 自定义密码验证器
 *
 * @author duq
 * @date 2021/1/7
 */
public class CustomPasswordEncoder implements PasswordEncoder {
    private final Logger log = LoggerFactory.getLogger(CustomPasswordEncoder.class);

    @Override
    public String encode(CharSequence rawPassword) {
        String encodedPassword = rawPassword + "_1234abcd";
        log.info("进行密码加密操作，加密前：{}, 加密后：{}", rawPassword, encodedPassword);
        return encodedPassword;
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String pass = this.encode(rawPassword);
        boolean match = pass.equals(encodedPassword);
        log.info("进行密码比对操作，请求密码：{}，加密后的请求密码：{}，正确的密码：{}，是否匹配：{}", rawPassword, pass, encodedPassword, match);
        return match;
    }
}
