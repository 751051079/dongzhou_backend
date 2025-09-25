package com.smarterp.order.domain.dto.batch;


import java.math.BigDecimal;

public class SitesPrice {

    private String value;

    private String label;

    private BigDecimal price;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
