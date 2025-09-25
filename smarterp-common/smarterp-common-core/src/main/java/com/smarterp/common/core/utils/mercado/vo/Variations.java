package com.smarterp.common.core.utils.mercado.vo;

import java.math.BigDecimal;
import java.util.List;

public class Variations {

    private BigDecimal price;

    private List<VariationsAttributeCombinations> attribute_combinations = null;

    private List<Attributes> attributes = null;

    private Integer available_quantity;

    private Integer sold_quantity;

    private List<String> picture_ids = null;

    public List<Attributes> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attributes> attributes) {
        this.attributes = attributes;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public List<VariationsAttributeCombinations> getAttribute_combinations() {
        return attribute_combinations;
    }

    public void setAttribute_combinations(List<VariationsAttributeCombinations> attribute_combinations) {
        this.attribute_combinations = attribute_combinations;
    }

    public Integer getAvailable_quantity() {
        return available_quantity;
    }

    public void setAvailable_quantity(Integer available_quantity) {
        this.available_quantity = available_quantity;
    }

    public Integer getSold_quantity() {
        return sold_quantity;
    }

    public void setSold_quantity(Integer sold_quantity) {
        this.sold_quantity = sold_quantity;
    }

    public List<String> getPicture_ids() {
        return picture_ids;
    }

    public void setPicture_ids(List<String> picture_ids) {
        this.picture_ids = picture_ids;
    }
}
