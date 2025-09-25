package com.smarterp.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.smarterp.common.core.annotation.Excel;
import com.smarterp.common.core.web.domain.BaseEntity;

import java.util.Date;

/**
 * 店铺对象 mercado_shop_info
 *
 * @author smarterp
 * @date 2023-04-19
 */
public class MercadoShopInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 部门id
     */
    @Excel(name = "部门id")
    private Long deptId;

    /**
     * 店铺名称
     */
    @Excel(name = "店铺名称")
    private String mercadoShopName;

    /**
     * 店铺别名
     */
    @Excel(name = "店铺别名")
    private String aliasName;

    /**
     * 商家id
     */
    @Excel(name = "商家id")
    private String merchantId;

    /**
     * Yes or No
     */
    @Excel(name = "Yes or No")
    private String authorizeStatus;

    /**
     * 授权时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "授权时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date authorizeTime;

    /**
     * 店铺token
     */
    @Excel(name = "店铺token")
    private String accessToken;

    /**
     * 刷新token
     */
    @Excel(name = "刷新token")
    private String refreshToken;

    /**
     * 是否删除标识，0表示未删除，1表示已删除
     */
    @Excel(name = "是否删除标识，0表示未删除，1表示已删除")
    private Boolean isDeleted;

    /**
     * 创建人名称
     */
    @Excel(name = "创建人名称")
    private String createName;

    /**
     * 公司名称
     */
    @Excel(name = "公司名称")
    private String deptName;

    /**
     * 价格系数
     */
    @Excel(name = "价格系数")
    private String priceRatio;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setMercadoShopName(String mercadoShopName) {
        this.mercadoShopName = mercadoShopName;
    }

    public String getMercadoShopName() {
        return mercadoShopName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setAuthorizeStatus(String authorizeStatus) {
        this.authorizeStatus = authorizeStatus;
    }

    public String getAuthorizeStatus() {
        return authorizeStatus;
    }

    public void setAuthorizeTime(Date authorizeTime) {
        this.authorizeTime = authorizeTime;
    }

    public Date getAuthorizeTime() {
        return authorizeTime;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getPriceRatio() {
        return priceRatio;
    }

    public void setPriceRatio(String priceRatio) {
        this.priceRatio = priceRatio;
    }
}
