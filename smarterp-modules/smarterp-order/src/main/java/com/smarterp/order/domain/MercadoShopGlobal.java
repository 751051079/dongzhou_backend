package com.smarterp.order.domain;


import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class MercadoShopGlobal implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键ID")
    private String id;

    @ApiModelProperty("商家ID")
    private String merchantId;

    @ApiModelProperty("全局产品ID")
    private String globalId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getGlobalId() {
        return globalId;
    }

    public void setGlobalId(String globalId) {
        this.globalId = globalId;
    }
}
