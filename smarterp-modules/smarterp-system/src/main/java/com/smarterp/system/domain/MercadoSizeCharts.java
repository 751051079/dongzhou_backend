package com.smarterp.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * 尺码对象 mercado_size_charts
 *
 * @author smarterp
 * @date 2024-12-08
 */
public class MercadoSizeCharts implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "尺码id")
    private String id;

    @ApiModelProperty(value = "名称")
    private String names;

    @ApiModelProperty(value = "类别")
    private String domainId;

    @ApiModelProperty(value = "性别")
    private String genderId;

    @ApiModelProperty(value = "性别")
    private String genderName;

    private String siteId;

    private String type;

    @ApiModelProperty(value = "卖家id")
    private String sellerId;

    private String measureType;

    private String mainAttributeId;

    private String templateId;

    private String sizeChartInfo;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }


    public String getGenderId() {
        return genderId;
    }

    public void setGenderId(String genderId) {
        this.genderId = genderId;
    }

    public String getGenderName() {
        return genderName;
    }

    public void setGenderName(String genderName) {
        this.genderName = genderName;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getMeasureType() {
        return measureType;
    }

    public void setMeasureType(String measureType) {
        this.measureType = measureType;
    }

    public String getMainAttributeId() {
        return mainAttributeId;
    }

    public void setMainAttributeId(String mainAttributeId) {
        this.mainAttributeId = mainAttributeId;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getSizeChartInfo() {
        return sizeChartInfo;
    }

    public void setSizeChartInfo(String sizeChartInfo) {
        this.sizeChartInfo = sizeChartInfo;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
