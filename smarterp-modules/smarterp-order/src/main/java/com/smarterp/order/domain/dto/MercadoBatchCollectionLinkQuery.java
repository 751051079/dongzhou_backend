package com.smarterp.order.domain.dto;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 批量采集的链接对象 mercado_batch_collection_link
 *
 * @author smarterp
 * @date 2024-05-27
 */
public class MercadoBatchCollectionLinkQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("部门id")
    private Long deptId;

    @ApiModelProperty("用户id")
    private String merchantId;

    @ApiModelProperty("创建时间开始")
    private String createBeginTime;

    @ApiModelProperty("创建时间结束")
    private String createEndTime;

    @ApiModelProperty("更新时间开始")
    private String updateBeginTime;

    @ApiModelProperty("更新时间结束")
    private String updateEndTime;

    @ApiModelProperty("系统创建时间开始")
    private String systemBeginTime;

    @ApiModelProperty("系统创建时间结束")
    private String systemEndTime;

    @ApiModelProperty("排序字段名称")
    private String orderByColumn;

    @ApiModelProperty("顺序标记 ascending or descending")
    private String isAsc;

    @ApiModelProperty("站点")
    private String siteId;

    @ApiModelProperty("创建人姓名")
    private String createdName;

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
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

    public String getUpdateBeginTime() {
        return updateBeginTime;
    }

    public void setUpdateBeginTime(String updateBeginTime) {
        this.updateBeginTime = updateBeginTime;
    }

    public String getUpdateEndTime() {
        return updateEndTime;
    }

    public void setUpdateEndTime(String updateEndTime) {
        this.updateEndTime = updateEndTime;
    }

    public String getSystemBeginTime() {
        return systemBeginTime;
    }

    public void setSystemBeginTime(String systemBeginTime) {
        this.systemBeginTime = systemBeginTime;
    }

    public String getSystemEndTime() {
        return systemEndTime;
    }

    public void setSystemEndTime(String systemEndTime) {
        this.systemEndTime = systemEndTime;
    }

    public String getOrderByColumn() {
        return orderByColumn;
    }

    public void setOrderByColumn(String orderByColumn) {
        this.orderByColumn = orderByColumn;
    }

    public String getIsAsc() {
        return isAsc;
    }

    public void setIsAsc(String isAsc) {
        this.isAsc = isAsc;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getCreatedName() {
        return createdName;
    }

    public void setCreatedName(String createdName) {
        this.createdName = createdName;
    }
}
