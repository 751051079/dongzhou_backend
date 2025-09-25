package com.smarterp.order.domain.dto;

import java.util.List;

public class BatchCollectionDTO {

    private String itemId;

    private String mercadoUrl;

    private List<String> shopIds;

    private String skuPre;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getMercadoUrl() {
        return mercadoUrl;
    }

    public void setMercadoUrl(String mercadoUrl) {
        this.mercadoUrl = mercadoUrl;
    }

    public List<String> getShopIds() {
        return shopIds;
    }

    public void setShopIds(List<String> shopIds) {
        this.shopIds = shopIds;
    }

    public String getSkuPre() {
        return skuPre;
    }

    public void setSkuPre(String skuPre) {
        this.skuPre = skuPre;
    }
}
