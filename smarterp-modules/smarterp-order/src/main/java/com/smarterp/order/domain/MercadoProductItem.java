package com.smarterp.order.domain;

import com.smarterp.common.core.annotation.Excel;
import com.smarterp.common.core.web.domain.BaseEntity;

import java.math.BigDecimal;

/**
 * 产品，店铺和站点信息关联对象 mercado_product_item
 *
 * @author smarterp
 * @date 2023-04-26
 */
public class MercadoProductItem extends BaseEntity {
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
     * 站点类型 MLM代表墨西哥 MLB代表巴西 MLC代表智利 MCO代表哥伦比亚
     */
    @Excel(name = "站点类型 MLM代表墨西哥 MLB代表巴西 MLC代表智利 MCO代表哥伦比亚")
    private String siteId;

    /**
     * 物流类型 remote：代表自发货 fulfillment:代表海外仓
     */
    @Excel(name = "物流类型 remote：代表自发货 fulfillment:代表海外仓")
    private String logisticType;

    /**
     * 站点售价
     */
    @Excel(name = "站点售价")
    private BigDecimal siteSalePrice;

    /**
     * 站点标题
     */
    @Excel(name = "站点标题")
    private String siteProductTitle;

    /**
     * 站点描述
     */
    @Excel(name = "站点描述")
    private String siteProductDescription;

    /**
     * 站点类目
     */
    @Excel(name = "站点类目")
    private String siteCategory;

    /**
     * 站点发布状态
     * 未发布：NoRelease
     * 发布成功：ReleaseSuccess
     * 发布失败：ReleaseFail
     */
    @Excel(name = "站点发布状态 未发布：NoRelease 发布成功：ReleaseSuccess 发布失败：ReleaseFail 发布中：ReleaseIng")
    private String siteReleaseStatus;

    /**
     * 站点描述状态
     * 成功：Success
     * 失败：Fail
     */
    @Excel(name = "站点描述状态 未发布：NoRelease 发布成功：ReleaseSuccess 发布失败：ReleaseFail 发布中：ReleaseIng")
    private String siteDescriptionStatus;

    /**
     * 站点产品id
     */
    @Excel(name = "站点产品id")
    private String siteItemId;

    /**
     * 店铺id
     */
    @Excel(name = "店铺id")
    private String merchantId;

    /**
     * 是否删除标识，0表示未删除，1表示已删除
     */
    @Excel(name = "是否删除标识，0表示未删除，1表示已删除")
    private Boolean isDeleted;
    //重试次数
    private int retryNum;

    private String permalink;

    public int getRetryNum() {
        return retryNum;
    }

    public void setRetryNum(int retryNum) {
        this.retryNum = retryNum;
    }

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

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public void setLogisticType(String logisticType) {
        this.logisticType = logisticType;
    }

    public String getLogisticType() {
        return logisticType;
    }

    public void setSiteSalePrice(BigDecimal siteSalePrice) {
        this.siteSalePrice = siteSalePrice;
    }

    public BigDecimal getSiteSalePrice() {
        return siteSalePrice;
    }

    public void setSiteProductTitle(String siteProductTitle) {
        this.siteProductTitle = siteProductTitle;
    }

    public String getSiteProductTitle() {
        return siteProductTitle;
    }

    public void setSiteProductDescription(String siteProductDescription) {
        this.siteProductDescription = siteProductDescription;
    }

    public String getSiteProductDescription() {
        return siteProductDescription;
    }

    public void setSiteCategory(String siteCategory) {
        this.siteCategory = siteCategory;
    }

    public String getSiteCategory() {
        return siteCategory;
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

    public void setSiteItemId(String siteItemId) {
        this.siteItemId = siteItemId;
    }

    public String getSiteItemId() {
        return siteItemId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }
}
