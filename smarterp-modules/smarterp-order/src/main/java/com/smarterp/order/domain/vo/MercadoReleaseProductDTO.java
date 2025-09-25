package com.smarterp.order.domain.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;


public class MercadoReleaseProductDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "站点id")
    private Long siteId;

    @ApiModelProperty(value = "产品id")
    private Long productId;

    @ApiModelProperty(value = "发布类型 cbt：表示发布全局产品  site：表示发布站点产品  siteDes：表示发布站点描述")
    private String releaseType;

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getReleaseType() {
        return releaseType;
    }

    public void setReleaseType(String releaseType) {
        this.releaseType = releaseType;
    }
}
