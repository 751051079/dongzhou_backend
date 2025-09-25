package com.smarterp.order.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.smarterp.common.core.annotation.Excel;
import com.smarterp.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

/**
 * 订单信息对象 mercado_order_info
 *
 * @author smarterp
 * @date 2024-06-07
 */
public class MercadoOrderInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 订单id
     */
    private String id;

    /**
     * 父订单id
     */
    @Excel(name = "父订单id")
    private String parentId;

    /**
     * 部门id
     */
    @Excel(name = "部门id")
    private Long deptId;

    /**
     * 商家id
     */
    @Excel(name = "商家id")
    private String merchantId;

    /**
     * 卖家id
     */
    @Excel(name = "卖家id")
    private String userId;

    /**
     * 站点类型
     * MLM代表墨西哥
     * MLB代表巴西
     * MLC代表智利
     * MCO代表哥伦比亚
     */
    @Excel(name = "站点类型 MLM代表墨西哥 MLB代表巴西 MLC代表智利 MCO代表哥伦比亚")
    private String siteId;

    /**
     * 物流类型
     * remote：代表自发货
     * fulfillment:代表海外仓
     */
    @Excel(name = "物流类型 remote：代表自发货 fulfillment:代表海外仓")
    private String logisticType;

    /**
     * 物流id
     */
    @Excel(name = "物流id")
    private String shipmentId;

    /**
     * 买家id
     */
    @Excel(name = "买家id")
    private String buyerId;

    /**
     * 买家昵称
     */
    @Excel(name = "买家昵称")
    private String buyerNickName;

    /**
     * 买家姓
     */
    @Excel(name = "买家姓")
    private String buyerLastName;

    /**
     * 买家名
     */
    @Excel(name = "买家名")
    private String buyerFirstName;

    /**
     * 订单图片
     */
    @Excel(name = "订单图片")
    private String orderPictures;

    /**
     * 订单创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "订单创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date dateCreated;

    /**
     * 订单关闭时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "订单关闭时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date dateClosed;

    /**
     * 最后更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "最后更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date lastUpdated;

    /**
     * 截止日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "截止日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date expirationDate;

    /**
     * 付款时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "付款时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date datePayed;

    /**
     * 发货时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "发货时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date dateShipping;

    /**
     * 订单状态
     */
    @Excel(name = "订单状态")
    private String orderStatus;

    /**
     * 物流状态
     */
    @Excel(name = "物流状态")
    private String shipStatus;

    /**
     * 付款状态
     */
    @Excel(name = "付款状态")
    private String payStatus;

    /**
     * 创建时间开始
     */
    @ApiModelProperty("创建时间开始")
    private String createBeginTime;

    /**
     * 创建时间结束
     */
    @ApiModelProperty("创建时间结束")
    private String createEndTime;

    /**
     * 产品id
     */
    @Excel(name = "产品id")
    private String itemId;

    /**
     * 产品标题
     */
    @Excel(name = "产品标题")
    private String title;

    /**
     * 类目id
     */
    @Excel(name = "类目id")
    private String categoryId;

    /**
     * 变体id
     */
    @Excel(name = "变体id")
    private String variationId;

    /**
     * 变体信息
     */
    @Excel(name = "变体信息")
    private String variationAttributes;

    /**
     * 产品质保信息
     */
    @Excel(name = "产品质保信息")
    private String warranty;

    /**
     * 卖家sku
     */
    @Excel(name = "卖家sku")
    private String sellerSku;

    /**
     * 全局产品id
     */
    @Excel(name = "全局产品id")
    private String parentItemId;

    /**
     * 数量
     */
    @Excel(name = "数量")
    private String quantity;

    /**
     * 单位售价
     */
    @Excel(name = "单位售价")
    private String unitPrice;

    /**
     * 单位售价
     */
    @Excel(name = "单位售价")
    private String fullUnitPrice;

    /**
     * 单位
     */
    @Excel(name = "单位")
    private String currencyId;

    /**
     * 折扣信息
     */
    @Excel(name = "折扣信息")
    private String saleFee;

    /**
     * 汇率
     */
    @Excel(name = "汇率")
    private String baseExchangeRate;

    /**
     * 产品链接
     */
    @Excel(name = "产品链接")
    private String permalink;

    private List<String> merchantIds;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setLogisticType(String logisticType) {
        this.logisticType = logisticType;
    }

    public String getLogisticType() {
        return logisticType;
    }

    public void setShipmentId(String shipmentId) {
        this.shipmentId = shipmentId;
    }

    public String getShipmentId() {
        return shipmentId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerNickName(String buyerNickName) {
        this.buyerNickName = buyerNickName;
    }

    public String getBuyerNickName() {
        return buyerNickName;
    }

    public void setBuyerLastName(String buyerLastName) {
        this.buyerLastName = buyerLastName;
    }

    public String getBuyerLastName() {
        return buyerLastName;
    }

    public void setBuyerFirstName(String buyerFirstName) {
        this.buyerFirstName = buyerFirstName;
    }

    public String getBuyerFirstName() {
        return buyerFirstName;
    }

    public void setOrderPictures(String orderPictures) {
        this.orderPictures = orderPictures;
    }

    public String getOrderPictures() {
        return orderPictures;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateClosed(Date dateClosed) {
        this.dateClosed = dateClosed;
    }

    public Date getDateClosed() {
        return dateClosed;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setDatePayed(Date datePayed) {
        this.datePayed = datePayed;
    }

    public Date getDatePayed() {
        return datePayed;
    }

    public void setDateShipping(Date dateShipping) {
        this.dateShipping = dateShipping;
    }

    public Date getDateShipping() {
        return dateShipping;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setShipStatus(String shipStatus) {
        this.shipStatus = shipStatus;
    }

    public String getShipStatus() {
        return shipStatus;
    }

    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    public String getCreateBeginTime() {
        return createBeginTime;
    }

    public void setCreateBeginTime(String createBeginTime) {
        this.createBeginTime = createBeginTime;
    }

    public String getCreateEndTime() {
        return createEndTime;
    }

    public void setCreateEndTime(String createEndTime) {
        this.createEndTime = createEndTime;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getVariationId() {
        return variationId;
    }

    public void setVariationId(String variationId) {
        this.variationId = variationId;
    }

    public String getVariationAttributes() {
        return variationAttributes;
    }

    public void setVariationAttributes(String variationAttributes) {
        this.variationAttributes = variationAttributes;
    }

    public String getWarranty() {
        return warranty;
    }

    public void setWarranty(String warranty) {
        this.warranty = warranty;
    }

    public String getSellerSku() {
        return sellerSku;
    }

    public void setSellerSku(String sellerSku) {
        this.sellerSku = sellerSku;
    }

    public String getParentItemId() {
        return parentItemId;
    }

    public void setParentItemId(String parentItemId) {
        this.parentItemId = parentItemId;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getFullUnitPrice() {
        return fullUnitPrice;
    }

    public void setFullUnitPrice(String fullUnitPrice) {
        this.fullUnitPrice = fullUnitPrice;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }

    public String getSaleFee() {
        return saleFee;
    }

    public void setSaleFee(String saleFee) {
        this.saleFee = saleFee;
    }

    public String getBaseExchangeRate() {
        return baseExchangeRate;
    }

    public void setBaseExchangeRate(String baseExchangeRate) {
        this.baseExchangeRate = baseExchangeRate;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public List<String> getMerchantIds() {
        return merchantIds;
    }

    public void setMerchantIds(List<String> merchantIds) {
        this.merchantIds = merchantIds;
    }
}
