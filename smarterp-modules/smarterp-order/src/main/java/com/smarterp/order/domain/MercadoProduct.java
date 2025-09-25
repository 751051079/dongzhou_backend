package com.smarterp.order.domain;

import com.smarterp.common.core.annotation.Excel;
import com.smarterp.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * 产品对象 mercado_product
 *
 * @author smarterp
 * @date 2023-04-26
 */
public class MercadoProduct extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 用户id
     */
    @Excel(name = "用户id")
    private Long userId;

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
     * 采集的url
     */
    @Excel(name = "采集的url")
    private String mercadoProductUrl;

    /**
     * 全局产品标题
     */
    @Excel(name = "全局产品标题")
    private String productTitle;

    /**
     * 全局产品描述
     */
    @Excel(name = "全局产品描述")
    private String productDescription;

    /**
     * 售价
     */
    @Excel(name = "售价")
    private BigDecimal salePrice;

    /**
     * 采集链接的全局产品id
     */
    @Excel(name = "采集链接的全局产品id")
    private String cbtItemId;

    /**
     * 上传成功返回的全局产品id
     */
    @Excel(name = "上传成功返回的全局产品id")
    private String cbtProId;

    /**
     * 全局类目
     */
    @Excel(name = "全局类目")
    private String cbtCategory;

    /**
     * upc编码
     */
    @Excel(name = "upc编码")
    private String upcCode;

    /**
     * sku前缀
     */
    @Excel(name = "sku前缀")
    private String skuPre;

    /**
     * 快递档位
     */
    @Excel(name = "快递档位")
    private Long shipType;

    /**
     * 发布状态
     * 未发布：NoRelease
     * 发布成功：ReleaseSuccess
     * 发布失败：ReleaseFail
     */
    @Excel(name = "发布状态 未发布：NoRelease 发布成功：ReleaseSuccess 发布失败：ReleaseFail 发布中：ReleaseIng")
    private String releaseStatus;

    /**
     * 全局产品图片
     */
    @Excel(name = "全局产品图片")
    private String pictures;

    /**
     * 库存
     */
    @ApiModelProperty(value = "库存")
    private Integer availableQuantity;

    /**
     * 是否删除标识，0表示未删除，1表示已删除
     */
    @Excel(name = "是否删除标识，0表示未删除，1表示已删除")
    private Boolean isDeleted;

    /**
     * 产品类别
     */
    @Excel(name = "产品类别")
    private String domainId;

    /**
     * 性别
     */
    @Excel(name = "性别")
    private String genderId;

    /**
     * 尺寸和重量信息
     */
    @Excel(name = "尺寸和重量信息")
    private String shipInfo;

    /**
     * 侵权信息
     */
    @Excel(name = "侵权信息")
    private String infringementInfo;

    /**
     * 侵权状态
     */
    @Excel(name = "侵权状态")
    private String infringementStatus;

    public String getSkuPre() {
        return skuPre;
    }

    public void setSkuPre(String skuPre) {
        this.skuPre = skuPre;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public void setMercadoProductUrl(String mercadoProductUrl) {
        this.mercadoProductUrl = mercadoProductUrl;
    }

    public String getMercadoProductUrl() {
        return mercadoProductUrl;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setCbtItemId(String cbtItemId) {
        this.cbtItemId = cbtItemId;
    }

    public String getCbtItemId() {
        return cbtItemId;
    }

    public String getCbtProId() {
        return cbtProId;
    }

    public void setCbtProId(String cbtProId) {
        this.cbtProId = cbtProId;
    }

    public void setCbtCategory(String cbtCategory) {
        this.cbtCategory = cbtCategory;
    }

    public String getCbtCategory() {
        return cbtCategory;
    }

    public void setUpcCode(String upcCode) {
        this.upcCode = upcCode;
    }

    public String getUpcCode() {
        return upcCode;
    }

    public void setShipType(Long shipType) {
        this.shipType = shipType;
    }

    public Long getShipType() {
        return shipType;
    }

    public String getReleaseStatus() {
        return releaseStatus;
    }

    public void setReleaseStatus(String releaseStatus) {
        this.releaseStatus = releaseStatus;
    }

    public void setPictures(String pictures) {
        this.pictures = pictures;
    }

    public String getPictures() {
        return pictures;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
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

    public String getShipInfo() {
        return shipInfo;
    }

    public void setShipInfo(String shipInfo) {
        this.shipInfo = shipInfo;
    }

    public String getInfringementInfo() {
        return infringementInfo;
    }

    public void setInfringementInfo(String infringementInfo) {
        this.infringementInfo = infringementInfo;
    }

    public String getInfringementStatus() {
        return infringementStatus;
    }

    public void setInfringementStatus(String infringementStatus) {
        this.infringementStatus = infringementStatus;
    }
}
