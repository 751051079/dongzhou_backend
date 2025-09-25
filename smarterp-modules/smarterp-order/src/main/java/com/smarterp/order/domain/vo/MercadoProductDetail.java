package com.smarterp.order.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.smarterp.common.core.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class MercadoProductDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "产品首图")
    private String pictureUrl;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "站点信息")
    private List<MercadoProductItemDTO> mercadoProductItemList;

    @ApiModelProperty(value = "店铺名称")
    private String shopName;

    @ApiModelProperty(value = "全局产品发布状态")
    private String releaseStatus;

    @ApiModelProperty(value = "全局产品id")
    private String cbtProId;

    @ApiModelProperty(value = "采集链接全局产品id")
    private String cbtItemId;

    @ApiModelProperty(value = "创建人")
    private String createName;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "SKU前缀")
    private String skuPre;

    @ApiModelProperty(value = "UPC编码")
    private String upcCode;

    @ApiModelProperty(value = "采集的链接")
    private String mercadoProductUrl;

    @ApiModelProperty(value = "尺寸和重量信息")
    private String shipInfo;

    @Excel(name = "侵权信息")
    private String infringementInfo;

    @Excel(name = "侵权状态")
    private String infringementStatus;

    private String packageHeight;
    private String packageLength;
    private String packageWeight;
    private String packageWidth;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<MercadoProductItemDTO> getMercadoProductItemList() {
        return mercadoProductItemList;
    }

    public void setMercadoProductItemList(List<MercadoProductItemDTO> mercadoProductItemList) {
        this.mercadoProductItemList = mercadoProductItemList;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getReleaseStatus() {
        return releaseStatus;
    }

    public void setReleaseStatus(String releaseStatus) {
        this.releaseStatus = releaseStatus;
    }

    public String getCbtProId() {
        return cbtProId;
    }

    public void setCbtProId(String cbtProId) {
        this.cbtProId = cbtProId;
    }

    public String getCbtItemId() {
        return cbtItemId;
    }

    public void setCbtItemId(String cbtItemId) {
        this.cbtItemId = cbtItemId;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getSkuPre() {
        return skuPre;
    }

    public void setSkuPre(String skuPre) {
        this.skuPre = skuPre;
    }

    public String getUpcCode() {
        return upcCode;
    }

    public void setUpcCode(String upcCode) {
        this.upcCode = upcCode;
    }

    public String getMercadoProductUrl() {
        return mercadoProductUrl;
    }

    public void setMercadoProductUrl(String mercadoProductUrl) {
        this.mercadoProductUrl = mercadoProductUrl;
    }

    public String getShipInfo() {
        return shipInfo;
    }

    public void setShipInfo(String shipInfo) {
        this.shipInfo = shipInfo;
    }

    public String getInfringementInfo() {
        return infringementInfo;
    }

    public void setInfringementInfo(String infringementInfo) {
        this.infringementInfo = infringementInfo;
    }

    public String getInfringementStatus() {
        return infringementStatus;
    }

    public void setInfringementStatus(String infringementStatus) {
        this.infringementStatus = infringementStatus;
    }

    public String getPackageHeight() {
        return packageHeight;
    }

    public void setPackageHeight(String packageHeight) {
        this.packageHeight = packageHeight;
    }

    public String getPackageLength() {
        return packageLength;
    }

    public void setPackageLength(String packageLength) {
        this.packageLength = packageLength;
    }

    public String getPackageWeight() {
        return packageWeight;
    }

    public void setPackageWeight(String packageWeight) {
        this.packageWeight = packageWeight;
    }

    public String getPackageWidth() {
        return packageWidth;
    }

    public void setPackageWidth(String packageWidth) {
        this.packageWidth = packageWidth;
    }
}
