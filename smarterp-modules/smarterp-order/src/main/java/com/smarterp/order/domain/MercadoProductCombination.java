package com.smarterp.order.domain;

import com.smarterp.common.core.annotation.Excel;
import com.smarterp.common.core.web.domain.BaseEntity;

import java.math.BigDecimal;

/**
 * 变体信息对象 mercado_product_combination
 *
 * @author smarterp
 * @date 2023-05-05
 */
public class MercadoProductCombination extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 产品id
     */
    @Excel(name = "产品id")
    private Long productId;

    /**
     * 变体SKU
     */
    @Excel(name = "变体SKU")
    private String combinationSku;

    /**
     * 变体价格
     */
    @Excel(name = "变体价格")
    private BigDecimal combinationPrice;

    /**
     * 变体图片信息
     */
    @Excel(name = "变体图片信息")
    private String pictures;

    /**
     * 变体详情
     */
    @Excel(name = "变体详情")
    private String combinationJson;

    /**
     * 变体库存
     */
    @Excel(name = "变体库存")
    private Integer availableQuantity;

    /**
     * 变体排序
     */
    @Excel(name = "变体排序")
    private Integer combinationSort;

    /**
     * 是否删除标识，0表示未删除，1表示已删除
     */
    @Excel(name = "是否删除标识，0表示未删除，1表示已删除")
    private Boolean isDeleted;

    private String sizeChartId;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setCombinationSku(String combinationSku) {
        this.combinationSku = combinationSku;
    }

    public String getCombinationSku() {
        return combinationSku;
    }

    public void setCombinationPrice(BigDecimal combinationPrice) {
        this.combinationPrice = combinationPrice;
    }

    public BigDecimal getCombinationPrice() {
        return combinationPrice;
    }

    public void setPictures(String pictures) {
        this.pictures = pictures;
    }

    public String getPictures() {
        return pictures;
    }

    public void setCombinationJson(String combinationJson) {
        this.combinationJson = combinationJson;
    }

    public String getCombinationJson() {
        return combinationJson;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public Integer getCombinationSort() {
        return combinationSort;
    }

    public void setCombinationSort(Integer combinationSort) {
        this.combinationSort = combinationSort;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public String getSizeChartId() {
        return sizeChartId;
    }

    public void setSizeChartId(String sizeChartId) {
        this.sizeChartId = sizeChartId;
    }
}
