package com.smarterp.order.domain.dto;

import java.io.Serializable;

/**
 * UPC信息详情对象 mercado_upc_item
 *
 * @author smarterp
 * @date 2023-04-16
 */
public class MercadoUpcItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long upcId;
    private String upcCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUpcId() {
        return upcId;
    }

    public void setUpcId(Long upcId) {
        this.upcId = upcId;
    }

    public String getUpcCode() {
        return upcCode;
    }

    public void setUpcCode(String upcCode) {
        this.upcCode = upcCode;
    }

}
