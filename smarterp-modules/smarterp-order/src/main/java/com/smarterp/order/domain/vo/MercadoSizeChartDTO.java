package com.smarterp.order.domain.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;


public class MercadoSizeChartDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "店铺ID")
    private Long merchantId;

    @ApiModelProperty(value = "类目ID")
    private String domainId;

    @ApiModelProperty(value = "性别ID")
    private String genderId;

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    public String getGenderId() {
        return genderId;
    }

    public void setGenderId(String genderId) {
        this.genderId = genderId;
    }
}
