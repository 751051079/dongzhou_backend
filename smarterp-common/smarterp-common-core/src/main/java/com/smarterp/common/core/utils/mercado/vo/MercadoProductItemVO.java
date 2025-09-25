package com.smarterp.common.core.utils.mercado.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 产品，店铺和站点信息关联对象 mercado_product_item
 *
 * @author smarterp
 * @date 2023-04-26
 */
public class MercadoProductItemVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "站点类型 MLM代表墨西哥 MLB代表巴西 MLC代表智利 MCO代表哥伦比亚")
    private String siteId;

    @ApiModelProperty(value = "物流类型 remote：代表自发货 fulfillment:代表海外仓")
    private String logisticType;

    @ApiModelProperty(value = "站点标题")
    private String siteProductTitle;

    @ApiModelProperty(value = "站点描述")
    private String siteProductDescription;

    @ApiModelProperty(value = "全局产品id")
    private String cbtId;

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

    public String getSiteProductTitle() {
        return siteProductTitle;
    }

    public void setSiteProductTitle(String siteProductTitle) {
        this.siteProductTitle = siteProductTitle;
    }

    public String getSiteProductDescription() {
        return siteProductDescription;
    }

    public void setSiteProductDescription(String siteProductDescription) {
        this.siteProductDescription = siteProductDescription;
    }

    public String getCbtId() {
        return cbtId;
    }

    public void setCbtId(String cbtId) {
        this.cbtId = cbtId;
    }
}
