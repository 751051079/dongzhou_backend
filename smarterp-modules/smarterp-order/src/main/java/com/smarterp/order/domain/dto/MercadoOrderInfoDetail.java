package com.smarterp.order.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


public class MercadoOrderInfoDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("订单ID")
    private String orderId;

    @ApiModelProperty("产品ID")
    private String itemId;

    @ApiModelProperty("sellerSku")
    private String sellerSku;

    @ApiModelProperty("单价")
    private String unitPrice;

    @ApiModelProperty("数量")
    private String quantity;

    @ApiModelProperty("变体信息")
    private String variationAttributes;

    @ApiModelProperty("站点")
    private String siteId;

    @ApiModelProperty("发货类型")
    private String logisticType;

    @ApiModelProperty("店铺ID")
    private String mercadoShopId;

    @ApiModelProperty("店铺名称")
    private String mercadoShopName;

    @ApiModelProperty("买家昵称")
    private String buyerNickName;

    @ApiModelProperty("下单时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateCreated;

    @ApiModelProperty("发货时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateShipping;

    @ApiModelProperty("状态")
    private String shipStatus;

    @ApiModelProperty("产品首图")
    private String thumbnail;

    @ApiModelProperty("产品链接")
    private String permalink;

    @ApiModelProperty("订单SKU信息")
    private List<String> orderSkuInfo;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getSellerSku() {
        return sellerSku;
    }

    public void setSellerSku(String sellerSku) {
        this.sellerSku = sellerSku;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getVariationAttributes() {
        return variationAttributes;
    }

    public void setVariationAttributes(String variationAttributes) {
        this.variationAttributes = variationAttributes;
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

    public String getMercadoShopId() {
        return mercadoShopId;
    }

    public void setMercadoShopId(String mercadoShopId) {
        this.mercadoShopId = mercadoShopId;
    }

    public String getMercadoShopName() {
        return mercadoShopName;
    }

    public void setMercadoShopName(String mercadoShopName) {
        this.mercadoShopName = mercadoShopName;
    }

    public String getBuyerNickName() {
        return buyerNickName;
    }

    public void setBuyerNickName(String buyerNickName) {
        this.buyerNickName = buyerNickName;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateShipping() {
        return dateShipping;
    }

    public void setDateShipping(Date dateShipping) {
        this.dateShipping = dateShipping;
    }

    public String getShipStatus() {
        return shipStatus;
    }

    public void setShipStatus(String shipStatus) {
        this.shipStatus = shipStatus;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public List<String> getOrderSkuInfo() {
        return orderSkuInfo;
    }

    public void setOrderSkuInfo(List<String> orderSkuInfo) {
        this.orderSkuInfo = orderSkuInfo;
    }
}
