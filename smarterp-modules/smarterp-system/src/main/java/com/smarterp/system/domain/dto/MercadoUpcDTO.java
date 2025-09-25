package com.smarterp.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class MercadoUpcDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "号码池")
    private String upcContent;

    @ApiModelProperty(value = "号码名称")
    private String codeName;

    @ApiModelProperty(value = "号码池类型")
    private String codeType;

    public String getUpcContent() {
        return upcContent;
    }

    public void setUpcContent(String upcContent) {
        this.upcContent = upcContent;
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public String getCodeType() {
        return codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }
}
