package com.smarterp.order.domain.dto;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class MercadoVarInfo {

    @ApiModelProperty(value = "变体图片，集合中是图片id")
    private List<String> pictureIds;

    @ApiModelProperty(value = "变体属性")
    private List<AttributeCombinations> attributeCombinations;

    @ApiModelProperty(value = "变体库存")
    private Integer availableQuantity;

    @ApiModelProperty(value = "sku")
    private String sku;

    @ApiModelProperty(value = "尺码表ID")
    private String sizeChartId;

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public List<String> getPictureIds() {
        return pictureIds;
    }

    public void setPictureIds(List<String> pictureIds) {
        this.pictureIds = pictureIds;
    }

    public List<AttributeCombinations> getAttributeCombinations() {
        return attributeCombinations;
    }

    public void setAttributeCombinations(List<AttributeCombinations> attributeCombinations) {
        this.attributeCombinations = attributeCombinations;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public String getSizeChartId() {
        return sizeChartId;
    }

    public void setSizeChartId(String sizeChartId) {
        this.sizeChartId = sizeChartId;
    }
}
