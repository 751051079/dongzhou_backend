package com.smarterp.order.domain.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

@JsonSerialize
public class BatchCollectionLinkToShop implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "店铺id")
    private String shopId;

    @ApiModelProperty(value = "采集的链接")
    private String linkId;

    @ApiModelProperty(value = "upc编码")
    private String upcCode;

    @ApiModelProperty(value = "SKU")
    private String preSku;

    @ApiModelProperty(value = "采集的链接")
    private List<String> linkIdList;

    @ApiModelProperty(value = "部门ID")
    private Long deptId;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public String getUpcCode() {
        return upcCode;
    }

    public void setUpcCode(String upcCode) {
        this.upcCode = upcCode;
    }

    public String getPreSku() {
        return preSku;
    }

    public void setPreSku(String preSku) {
        this.preSku = preSku;
    }

    public List<String> getLinkIdList() {
        return linkIdList;
    }

    public void setLinkIdList(List<String> linkIdList) {
        this.linkIdList = linkIdList;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
