package com.smarterp.order.domain.dto;

import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * @author juntao.li
 * @ClassName MercadoSiteInfo
 * @description 站点类型
 * @date 2023/4/26 22:50
 * @Version 1.0
 */
public class MercadoSiteInfo {

    @ApiModelProperty(value = "站点id MLM-墨西哥 MLB-巴西 MLC-智利 MCO-哥伦比亚  MLMFULL-墨西哥海外仓" )
    private String siteId;

    @ApiModelProperty(value = "物流类型 remote-自发货 fulfillment-海外仓")
    private String logisticType;

    @ApiModelProperty("站点标题")
    private String title;

    @ApiModelProperty(value = "站点售价")
    private BigDecimal price;

    @ApiModelProperty(value = "站点描述")
    private String des;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }
}
