package com.smarterp.order.domain.dto;

import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

public class MercadoInfoSiteDto {

    @ApiModelProperty("站点id")
    private String id;

    @ApiModelProperty("站点标题")
    private String title;

    @ApiModelProperty(value = "售价")
    private BigDecimal price;

    @ApiModelProperty(value = "站点描述")
    private String des;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }
}
