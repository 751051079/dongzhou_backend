package com.smarterp.system.domain;

import com.smarterp.common.core.annotation.Excel;
import com.smarterp.common.core.web.domain.BaseEntity;

/**
 * UPC信息详情对象 mercado_upc_item
 *
 * @author smarterp
 * @date 2023-04-16
 */
public class MercadoUpcItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * upc信息id
     */
    @Excel(name = "upc信息id")
    private Long upcId;

    /**
     * upc编码
     */
    @Excel(name = "upc编码")
    private String upcCode;

    /**
     * Upc状态，YES表示已使用，NO表示未使用
     */
    @Excel(name = "Upc状态，YES表示已使用，NO表示未使用")
    private String upcStatus;

    /**
     * 是否删除标识，0表示未删除，1表示已删除
     */
    @Excel(name = "是否删除标识，0表示未删除，1表示已删除")
    private Boolean isDeleted;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setUpcId(Long upcId) {
        this.upcId = upcId;
    }

    public Long getUpcId() {
        return upcId;
    }

    public void setUpcCode(String upcCode) {
        this.upcCode = upcCode;
    }

    public String getUpcCode() {
        return upcCode;
    }

    public void setUpcStatus(String upcStatus) {
        this.upcStatus = upcStatus;
    }

    public String getUpcStatus() {
        return upcStatus;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}
