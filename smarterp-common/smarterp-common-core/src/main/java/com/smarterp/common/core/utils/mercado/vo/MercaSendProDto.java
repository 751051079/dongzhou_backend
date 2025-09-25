package com.smarterp.common.core.utils.mercado.vo;

import java.math.BigDecimal;

public class MercaSendProDto {

    private String merceProUrl;

    private String upc;

    private BigDecimal price;

    private int wgType;

    private String sku;

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public int getWgType() {
        return wgType;
    }

    public void setWgType(int wgType) {
        this.wgType = wgType;
    }

    public String getMerceProUrl() {
        return merceProUrl;
    }

    public void setMerceProUrl(String merceProUrl) {
        this.merceProUrl = merceProUrl;
    }
}
