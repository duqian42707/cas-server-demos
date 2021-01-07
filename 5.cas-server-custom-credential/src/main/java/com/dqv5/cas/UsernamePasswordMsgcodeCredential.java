package com.dqv5.cas;

import org.apereo.cas.authentication.UsernamePasswordCredential;

public class UsernamePasswordMsgcodeCredential extends UsernamePasswordCredential {

    private String msgcode;

    public String getMsgcode() {
        return msgcode;
    }

    public void setMsgcode(String msgcode) {
        this.msgcode = msgcode;
    }
}
