package com.smarterp.order.domain.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;


public class MercadoProductItemDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "itemId")
    private Long itemId;

    @ApiModelProperty(value = "产品id")
    private Long productId;

    @ApiModelProperty(value = "站点类型 MLM代表墨西哥 MLB代表巴西 MLC代表智利 MCO代表哥伦比亚")
    private String siteId;

    @ApiModelProperty(value = "物流类型 remote：代表自发货 fulfillment:代表海外仓")
    private String logisticType;

    @ApiModelProperty(value = "站点售价")
    private BigDecimal siteSalePrice;

    @ApiModelProperty(value = "站点发布状态 未发布：NoRelease 发布成功：ReleaseSuccess 发布失败：ReleaseFail")
    private String siteReleaseStatus;

    @ApiModelProperty(value = "站点描述状态 成功：Success 失败：Fail")
    private String siteDescriptionStatus;

    @ApiModelProperty(value = "站点产品id")
    private String siteItemId;

    private String permalink;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getLogisticType() {
        return logisticType;
    }

    public void setLogisticType(String logisticType) {
        this.logisticType = logisticType;
    }

    public BigDecimal getSiteSalePrice() {
        return siteSalePrice;
    }

    public void setSiteSalePrice(BigDecimal siteSalePrice) {
        this.siteSalePrice = siteSalePrice;
    }

    public String getSiteReleaseStatus() {
        return siteReleaseStatus;
    }

    public void setSiteReleaseStatus(String siteReleaseStatus) {
        this.siteReleaseStatus = siteReleaseStatus;
    }

    public String getSiteDescriptionStatus() {
        return siteDescriptionStatus;
    }

    public void setSiteDescriptionStatus(String siteDescriptionStatus) {
        this.siteDescriptionStatus = siteDescriptionStatus;
    }

    public String getSiteItemId() {
        return siteItemId;
    }

    public void setSiteItemId(String siteItemId) {
        this.siteItemId = siteItemId;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }
}
