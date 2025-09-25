package com.smarterp.common.core.utils.mercado.vo;
//刷新token实体
public class MilRefreshTokeRqtVo {

    // 'authorization_code'or 'refresh_token' if you need get one new token
    private String grant_type = "refresh_token";

    //程序的appid
    private String client_id;

    //程序的秘钥
    private String client_secret;

    private String refresh_token;

    public String getGrant_type() {
        return grant_type;
    }

    public void setGrant_type(String grant_type) {
        this.grant_type = grant_type;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }
}
