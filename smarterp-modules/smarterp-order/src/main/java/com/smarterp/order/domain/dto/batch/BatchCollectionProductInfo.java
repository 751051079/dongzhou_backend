package com.smarterp.order.domain.dto.batch;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.List;

public class BatchCollectionProductInfo {

    @ApiModelProperty(value = "全局产品id")
    private String cpProdutId;

    @ApiModelProperty(value = "全局产品价格")
    private BigDecimal price;

    @ApiModelProperty(value = "类目id")
    private String categoryId;

    @ApiModelProperty(value = "采集链接地址")
    private String mercadoUrl;

    @ApiModelProperty(value = "需求上架的店铺id")
    private List<String> shopIds;

    @ApiModelProperty(value = "需求上架的站点id")
    private List<String> siteIds;

    @ApiModelProperty(value = "快递档次")
    private String gears;

    @ApiModelProperty(value = "站点价格信息")
    List<SitesPrice> SitesPrice;

    @ApiModelProperty(value = "英语标题")
    private String englishTitle;

    @ApiModelProperty(value = "西班牙语标题")
    private String spanishTitle;

    @ApiModelProperty(value = "葡萄牙语标题")
    private String portugueseTitle;

    @ApiModelProperty(value = "西班牙语描述")
    private String spanishMsg;

    @ApiModelProperty(value = "葡萄牙语描述")
    private String portugueseMsg;

    @ApiModelProperty(value = "属性信息")
    private List<ProductInfo> ProductInfo;

    @ApiModelProperty(value = "SKU")
    private List<String> skuList;

    @ApiModelProperty(value = "各个颜色对应的图片")
    private JSONArray fileList;

    @ApiModelProperty(value = "sizeId")
    private String sizeId;

    @ApiModelProperty(value = "colorId")
    private String colorId;

    @ApiModelProperty(value = "sku前缀")
    private String skuPre;

    @ApiModelProperty(value = "产品id")
    private String productId;

    @ApiModelProperty(value = "操作标识 release 发布")
    private String operateType;

    public String getCpProdutId() {
        return cpProdutId;
    }

    public void setCpProdutId(String cpProdutId) {
        this.cpProdutId = cpProdutId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getMercadoUrl() {
        return mercadoUrl;
    }

    public void setMercadoUrl(String mercadoUrl) {
        this.mercadoUrl = mercadoUrl;
    }

    public List<String> getShopIds() {
        return shopIds;
    }

    public void setShopIds(List<String> shopIds) {
        this.shopIds = shopIds;
    }

    public List<String> getSiteIds() {
        return siteIds;
    }

    public void setSiteIds(List<String> siteIds) {
        this.siteIds = siteIds;
    }

    public String getGears() {
        return gears;
    }

    public void setGears(String gears) {
        this.gears = gears;
    }

    public List<com.smarterp.order.domain.dto.batch.SitesPrice> getSitesPrice() {
        return SitesPrice;
    }

    public void setSitesPrice(List<com.smarterp.order.domain.dto.batch.SitesPrice> sitesPrice) {
        SitesPrice = sitesPrice;
    }

    public String getEnglishTitle() {
        return englishTitle;
    }

    public void setEnglishTitle(String englishTitle) {
        this.englishTitle = englishTitle;
    }

    public String getSpanishTitle() {
        return spanishTitle;
    }

    public void setSpanishTitle(String spanishTitle) {
        this.spanishTitle = spanishTitle;
    }

    public String getPortugueseTitle() {
        return portugueseTitle;
    }

    public void setPortugueseTitle(String portugueseTitle) {
        this.portugueseTitle = portugueseTitle;
    }

    public String getSpanishMsg() {
        return spanishMsg;
    }

    public void setSpanishMsg(String spanishMsg) {
        this.spanishMsg = spanishMsg;
    }

    public String getPortugueseMsg() {
        return portugueseMsg;
    }

    public void setPortugueseMsg(String portugueseMsg) {
        this.portugueseMsg = portugueseMsg;
    }

    public List<com.smarterp.order.domain.dto.batch.ProductInfo> getProductInfo() {
        return ProductInfo;
    }

    public void setProductInfo(List<com.smarterp.order.domain.dto.batch.ProductInfo> productInfo) {
        ProductInfo = productInfo;
    }

    public List<String> getSkuList() {
        return skuList;
    }

    public void setSkuList(List<String> skuList) {
        this.skuList = skuList;
    }

    public JSONArray getFileList() {
        return fileList;
    }

    public void setFileList(JSONArray fileList) {
        this.fileList = fileList;
    }

    public String getSizeId() {
        return sizeId;
    }

    public void setSizeId(String sizeId) {
        this.sizeId = sizeId;
    }

    public String getColorId() {
        return colorId;
    }

    public void setColorId(String colorId) {
        this.colorId = colorId;
    }

    public String getSkuPre() {
        return skuPre;
    }

    public void setSkuPre(String skuPre) {
        this.skuPre = skuPre;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }
}
