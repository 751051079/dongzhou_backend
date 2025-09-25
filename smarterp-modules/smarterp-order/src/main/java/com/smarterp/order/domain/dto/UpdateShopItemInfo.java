package com.smarterp.order.domain.dto;


import java.io.Serializable;

public class UpdateShopItemInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String merchantId;

    private String scrollId;

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getScrollId() {
        return scrollId;
    }

    public void setScrollId(String scrollId) {
        this.scrollId = scrollId;
    }
}
