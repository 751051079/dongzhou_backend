package com.smarterp.order.domain;

import com.smarterp.common.core.annotation.Excel;
import com.smarterp.common.core.web.domain.BaseEntity;

/**
 * 订单产品对象 mercado_order_info_item
 *
 * @author smarterp
 * @date 2024-06-07
 */
public class MercadoOrderInfoItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private String id;

    /**
     * 订单id
     */
    @Excel(name = "订单id")
    private String orderId;

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

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setVariationId(String variationId) {
        this.variationId = variationId;
    }

    public String getVariationId() {
        return variationId;
    }

    public void setVariationAttributes(String variationAttributes) {
        this.variationAttributes = variationAttributes;
    }

    public String getVariationAttributes() {
        return variationAttributes;
    }

    public void setWarranty(String warranty) {
        this.warranty = warranty;
    }

    public String getWarranty() {
        return warranty;
    }

    public void setSellerSku(String sellerSku) {
        this.sellerSku = sellerSku;
    }

    public String getSellerSku() {
        return sellerSku;
    }

    public void setParentItemId(String parentItemId) {
        this.parentItemId = parentItemId;
    }

    public String getParentItemId() {
        return parentItemId;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setFullUnitPrice(String fullUnitPrice) {
        this.fullUnitPrice = fullUnitPrice;
    }

    public String getFullUnitPrice() {
        return fullUnitPrice;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public void setSaleFee(String saleFee) {
        this.saleFee = saleFee;
    }

    public String getSaleFee() {
        return saleFee;
    }

    public void setBaseExchangeRate(String baseExchangeRate) {
        this.baseExchangeRate = baseExchangeRate;
    }

    public String getBaseExchangeRate() {
        return baseExchangeRate;
    }

}
