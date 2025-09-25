package com.smarterp.order.domain.dto;

import com.smarterp.common.core.annotation.Excel;
import com.smarterp.common.core.web.domain.BaseEntity;

/**
 * 店铺站点信息对象 mercado_shop_info_site
 *
 * @author smarterp
 * @date 2024-06-06
 */
public class MercadoShopInfoSite extends BaseEntity {
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
     * 商家id
     */
    @Excel(name = "商家id")
    private String merchantId;

    /**
     * 店铺站点id
     */
    @Excel(name = "店铺站点id")
    private String userId;

    /**
     * 站点类型 MLM代表墨西哥 MLB代表巴西 MLC代表智利 MCO代表哥伦比亚
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
     * 站点链接限制数
     */
    @Excel(name = "站点链接限制数")
    private String quota;

    /**
     * 站点所有链接数
     */
    @Excel(name = "站点所有链接数")
    private String totalItems;

    private Integer offset;
    private Integer limit;

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

    public String getQuota() {
        return quota;
    }

    public void setQuota(String quota) {
        this.quota = quota;
    }

    public String getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(String totalItems) {
        this.totalItems = totalItems;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
