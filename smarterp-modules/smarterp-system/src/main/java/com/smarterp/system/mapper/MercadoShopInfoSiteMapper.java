package com.smarterp.system.mapper;

import com.smarterp.system.domain.MercadoShopInfoSite;

import java.util.List;

/**
 * 店铺站点信息Mapper接口
 *
 * @author smarterp
 * @date 2024-06-06
 */
public interface MercadoShopInfoSiteMapper {
    /**
     * 查询店铺站点信息
     *
     * @param id 店铺站点信息主键
     * @return 店铺站点信息
     */
    public MercadoShopInfoSite selectMercadoShopInfoSiteById(Long id);

    /**
     * 查询店铺站点信息列表
     *
     * @param mercadoShopInfoSite 店铺站点信息
     * @return 店铺站点信息集合
     */
    public List<MercadoShopInfoSite> selectMercadoShopInfoSiteList(MercadoShopInfoSite mercadoShopInfoSite);

    /**
     * 新增店铺站点信息
     *
     * @param mercadoShopInfoSite 店铺站点信息
     * @return 结果
     */
    public int insertMercadoShopInfoSite(MercadoShopInfoSite mercadoShopInfoSite);

    /**
     * 修改店铺站点信息
     *
     * @param mercadoShopInfoSite 店铺站点信息
     * @return 结果
     */
    public int updateMercadoShopInfoSite(MercadoShopInfoSite mercadoShopInfoSite);

    /**
     * 删除店铺站点信息
     *
     * @param id 店铺站点信息主键
     * @return 结果
     */
    public int deleteMercadoShopInfoSiteById(Long id);

    /**
     * 批量删除店铺站点信息
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteMercadoShopInfoSiteByIds(Long[] ids);

    /**
     * 根据merchantId删除站点信息
     * @param merchantId
     * @return
     */
    public int deleteSiteByMerchantId(String merchantId);

    /**
     * 根据merchantId查询站点信息
     * @param merchantId
     * @return
     */
    public List<MercadoShopInfoSite> selectMercadoShopInfoSiteByMerchantId(String merchantId);
}
