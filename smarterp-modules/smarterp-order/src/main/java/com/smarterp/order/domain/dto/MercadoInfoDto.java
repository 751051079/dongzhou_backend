package com.smarterp.order.domain.dto;


import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.List;

public class MercadoInfoDto {

    @ApiModelProperty(value = "产品表id")
    private Long productId;

    @ApiModelProperty(value = "全局产品id")
    private String id;

    @ApiModelProperty(value = "采集的url")
    private String mercadoProductUrl;

    @ApiModelProperty(value = "英文标题")
    private String title;

    @ApiModelProperty(value = "类目id")
    private String categoryId;

    @ApiModelProperty(value = "库存")
    private Integer availableQuantity;

    @ApiModelProperty(value = "全局产品价格，单位USD")
    private BigDecimal price;

    @ApiModelProperty(value = "操作类型 保存:add 发布:release")
    private String operateType;

    @ApiModelProperty(value = "UPC")
    private String upcCode;

    @ApiModelProperty(value = "快递档位")
    private Long shipType;

    @ApiModelProperty(value = "sku前缀")
    private String skuPre;

    @ApiModelProperty(value = "店铺 站点信息")
    private List<MercadoShopInfoDTO> mercadoShopInfoDTOList;

    @ApiModelProperty(value = "图片对象")
    private List<MercadoInfoPictureDto> mercadoInfoPictureDtos;

    @ApiModelProperty(value = "卖家条款")
    private List<MercadoAttrInfo> mercadoSaleTerms;

    @ApiModelProperty(value = "属性")
    private List<MercadoAttrInfo> mercadoAttrInfos;

    @ApiModelProperty(value = "变体")
    private List<MercadoVarInfo> mercadoVarInfos;

    @ApiModelProperty(value = "尺寸和重量等信息")
    private String shipInfo;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getSkuPre() {
        return skuPre;
    }

    public void setSkuPre(String skuPre) {
        this.skuPre = skuPre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMercadoProductUrl() {
        return mercadoProductUrl;
    }

    public void setMercadoProductUrl(String mercadoProductUrl) {
        this.mercadoProductUrl = mercadoProductUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getUpcCode() {
        return upcCode;
    }

    public void setUpcCode(String upcCode) {
        this.upcCode = upcCode;
    }

    public Long getShipType() {
        return shipType;
    }

    public void setShipType(Long shipType) {
        this.shipType = shipType;
    }

    public List<MercadoShopInfoDTO> getMercadoShopInfoDTOList() {
        return mercadoShopInfoDTOList;
    }

    public void setMercadoShopInfoDTOList(List<MercadoShopInfoDTO> mercadoShopInfoDTOList) {
        this.mercadoShopInfoDTOList = mercadoShopInfoDTOList;
    }

    public List<MercadoInfoPictureDto> getMercadoInfoPictureDtos() {
        return mercadoInfoPictureDtos;
    }

    public List<MercadoAttrInfo> getMercadoSaleTerms() {
        return mercadoSaleTerms;
    }

    public void setMercadoSaleTerms(List<MercadoAttrInfo> mercadoSaleTerms) {
        this.mercadoSaleTerms = mercadoSaleTerms;
    }

    public void setMercadoInfoPictureDtos(List<MercadoInfoPictureDto> mercadoInfoPictureDtos) {
        this.mercadoInfoPictureDtos = mercadoInfoPictureDtos;
    }

    public List<MercadoAttrInfo> getMercadoAttrInfos() {
        return mercadoAttrInfos;
    }

    public void setMercadoAttrInfos(List<MercadoAttrInfo> mercadoAttrInfos) {
        this.mercadoAttrInfos = mercadoAttrInfos;
    }

    public List<MercadoVarInfo> getMercadoVarInfos() {
        return mercadoVarInfos;
    }

    public void setMercadoVarInfos(List<MercadoVarInfo> mercadoVarInfos) {
        this.mercadoVarInfos = mercadoVarInfos;
    }

    public String getShipInfo() {
        return shipInfo;
    }

    public void setShipInfo(String shipInfo) {
        this.shipInfo = shipInfo;
    }
}
