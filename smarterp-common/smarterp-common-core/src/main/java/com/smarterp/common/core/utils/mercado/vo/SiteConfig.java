package com.smarterp.common.core.utils.mercado.vo;

import java.math.BigDecimal;

public class SiteConfig {

    private String site_id;

    private String logistic_type;

    private BigDecimal price;

    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSite_id() {
        return site_id;
    }

    public void setSite_id(String site_id) {
        this.site_id = site_id;
    }

    public String getLogistic_type() {
        return logistic_type;
    }

    public void setLogistic_type(String logistic_type) {
        this.logistic_type = logistic_type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
