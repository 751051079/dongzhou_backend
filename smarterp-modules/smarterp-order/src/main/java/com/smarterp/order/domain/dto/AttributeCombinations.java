package com.smarterp.order.domain.dto;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class AttributeCombinations {

    @ApiModelProperty(value = "属性id")
    private String id;

    @ApiModelProperty(value = "属性名称")
    private String name;

    @ApiModelProperty(value = "值id")
    private String valueId;

    @ApiModelProperty(value = "值名称")
    private String valueName;

    private List<AttributeCombinations> values;

    @ApiModelProperty(value = "值类型")
    private String valueType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValueId() {
        return valueId;
    }

    public void setValueId(String valueId) {
        this.valueId = valueId;
    }

    public String getValueName() {
        return valueName;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    public List<AttributeCombinations> getValues() {
        return values;
    }

    public void setValues(List<AttributeCombinations> values) {
        this.values = values;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }
}
