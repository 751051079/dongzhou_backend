package com.smarterp.order.domain.dto;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class MercadoBatchCollectionLinkAddDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("产品url地址")
    private String productUrl;

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }
}
