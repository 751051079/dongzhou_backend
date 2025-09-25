package com.smarterp.system.domain;

import com.smarterp.common.core.annotation.Excel;
import com.smarterp.common.core.web.domain.BaseEntity;

import java.math.BigDecimal;


/**
 * 套餐对象 mercado_combo_info
 *
 * @author smarterp
 * @date 2023-04-16
 */
public class MercadoComboInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 绑定最大店铺数
     */
    @Excel(name = "绑定最大店铺数")
    private Long maxShopCount;

    /**
     * 绑定最多子账号数量
     */
    @Excel(name = "绑定最多子账号数量")
    private Long maxChildAccount;

    /**
     * 套餐名称
     */
    @Excel(name = "套餐名称")
    private String comboName;

    /**
     * 套餐有效天数
     */
    @Excel(name = "套餐有效天数")
    private Long comboEfficientDays;

    /**
     * 套餐价格
     */
    @Excel(name = "套餐价格")
    private BigDecimal comboSalePrice;

    /**
     * 每天最大采集数量
     */
    @Excel(name = "每天最大采集数量")
    private Long collectionQuantityDays;

    /**
     * 套餐类型
     */
    @Excel(name = "套餐类型")
    private String comboType;

    /**
     * 套餐描述
     */
    @Excel(name = "套餐描述")
    private String comboDescription;

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
     * 更新人名称
     */
    @Excel(name = "更新人名称")
    private String updateName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMaxShopCount() {
        return maxShopCount;
    }

    public void setMaxShopCount(Long maxShopCount) {
        this.maxShopCount = maxShopCount;
    }

    public Long getMaxChildAccount() {
        return maxChildAccount;
    }

    public void setMaxChildAccount(Long maxChildAccount) {
        this.maxChildAccount = maxChildAccount;
    }

    public String getComboName() {
        return comboName;
    }

    public void setComboName(String comboName) {
        this.comboName = comboName;
    }

    public Long getComboEfficientDays() {
        return comboEfficientDays;
    }

    public void setComboEfficientDays(Long comboEfficientDays) {
        this.comboEfficientDays = comboEfficientDays;
    }

    public BigDecimal getComboSalePrice() {
        return comboSalePrice;
    }

    public void setComboSalePrice(BigDecimal comboSalePrice) {
        this.comboSalePrice = comboSalePrice;
    }

    public Long getCollectionQuantityDays() {
        return collectionQuantityDays;
    }

    public void setCollectionQuantityDays(Long collectionQuantityDays) {
        this.collectionQuantityDays = collectionQuantityDays;
    }

    public String getComboType() {
        return comboType;
    }

    public void setComboType(String comboType) {
        this.comboType = comboType;
    }

    public String getComboDescription() {
        return comboDescription;
    }

    public void setComboDescription(String comboDescription) {
        this.comboDescription = comboDescription;
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

    public String getUpdateName() {
        return updateName;
    }

    public void setUpdateName(String updateName) {
        this.updateName = updateName;
    }
}
