package com.smarterp.order.domain.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

public class MercadoProductDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "全局产品id")
    private String cbtItemId;

    @ApiModelProperty(value = "部门id")
    private Long deptId;

    @ApiModelProperty(value = "商家id")
    private List<String> merchantIdList;

    public String getCbtItemId() {
        return cbtItemId;
    }

    public void setCbtItemId(String cbtItemId) {
        this.cbtItemId = cbtItemId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public List<String> getMerchantIdList() {
        return merchantIdList;
    }

    public void setMerchantIdList(List<String> merchantIdList) {
        this.merchantIdList = merchantIdList;
    }
}
