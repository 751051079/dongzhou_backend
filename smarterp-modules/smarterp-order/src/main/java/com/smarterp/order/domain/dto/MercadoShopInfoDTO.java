package com.smarterp.order.domain.dto;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author juntao.li
 * @ClassName ShopInfoDTO
 * @description 店铺站点信息
 * @date 2023/4/26 22:49
 * @Version 1.0
 */
public class MercadoShopInfoDTO {

    @ApiModelProperty(value = "店铺id")
    private String merchantId;

    @ApiModelProperty(value = "站点信息集合")
    private List<MercadoSiteInfo> mercadoSiteInfoList;

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public List<MercadoSiteInfo> getMercadoSiteInfoList() {
        return mercadoSiteInfoList;
    }

    public void setMercadoSiteInfoList(List<MercadoSiteInfo> mercadoSiteInfoList) {
        this.mercadoSiteInfoList = mercadoSiteInfoList;
    }
}
