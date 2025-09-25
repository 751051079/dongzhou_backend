package com.smarterp.order.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.smarterp.common.core.annotation.Excel;
import com.smarterp.common.core.web.domain.BaseEntity;

import java.util.Date;

/**
 * 错误日志对象 mercado_product_error_log
 *
 * @author smarterp
 * @date 2023-07-03
 */
public class MercadoProductErrorLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 全局/站点
     */
    @Excel(name = "全局/站点")
    private String errorType;

    /**
     * 产品id
     */
    @Excel(name = "产品id")
    private Long productId;

    /**
     * 产品站点id
     */
    @Excel(name = "产品站点id")
    private Long productItemId;

    /**
     * 错误日志
     */
    @Excel(name = "错误日志")
    private String errorInfo;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getProductItemId() {
        return productItemId;
    }

    public void setProductItemId(Long productItemId) {
        this.productItemId = productItemId;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
