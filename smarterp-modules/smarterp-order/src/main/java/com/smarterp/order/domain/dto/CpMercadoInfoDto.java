package com.smarterp.order.domain.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CpMercadoInfoDto {
    //产品表id
    private String productId;
    //SKU前缀
    private String skuPre;
    //快递档位
    private Long gears;
    //产品url
    private String mercadoUrl;
    //平台全局产品id
    private String id;
    //全局产品价格
    private BigDecimal price;
    //类目id
    private String categoryId;
    //各个站点的价格
    private List<Map<String, Object>> sitesPrice = new ArrayList<>();
    //英语标题
    private String englishTitle;
    //西语标题
    private String spanishTitle;
    //葡萄牙语标题
    private String portugueseTitle;
    //基础属性
    private List<Map<String, Object>> infoList;
    //变体颜色
    private Map<String,Object> colorMap;
    //变体尺寸
    private Map<String,Object> sizeMap;
    //图片
    private List<Map<String,Object>> colorImagesList;
    //西语说明
    private String spanishMsg;
    //葡萄牙语说明
    private String portugueseMsg;




    // 类别ID
    private String domainId;

    // 性别ID
    private String genderId;

    // 性别名称
    private String genderName;

    private List<String> shopIds;

    public String getMercadoUrl() {
        return mercadoUrl;
    }

    public void setMercadoUrl(String mercadoUrl) {
        this.mercadoUrl = mercadoUrl;
    }

    public Long getGears() {
        return gears;
    }

    public void setGears(Long gears) {
        this.gears = gears;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public List<Map<String, Object>> getSitesPrice() {
        return sitesPrice;
    }

    public void setSitesPrice(List<Map<String, Object>> sitesPrice) {
        this.sitesPrice = sitesPrice;
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

    public List<Map<String, Object>> getInfoList() {
        return infoList;
    }

    public void setInfoList(List<Map<String, Object>> infoList) {
        this.infoList = infoList;
    }

    public Map<String, Object> getColorMap() {
        return colorMap;
    }

    public void setColorMap(Map<String, Object> colorMap) {
        this.colorMap = colorMap;
    }

    public Map<String, Object> getSizeMap() {
        return sizeMap;
    }

    public void setSizeMap(Map<String, Object> sizeMap) {
        this.sizeMap = sizeMap;
    }

    public List<Map<String, Object>> getColorImagesList() {
        return colorImagesList;
    }

    public void setColorImagesList(List<Map<String, Object>> colorImagesList) {
        this.colorImagesList = colorImagesList;
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

    public List<String> getShopIds() {
        return shopIds;
    }

    public void setShopIds(List<String> shopIds) {
        this.shopIds = shopIds;
    }
}
