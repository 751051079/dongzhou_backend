package com.smarterp.system.service;

import java.util.List;

import com.smarterp.common.core.web.domain.AjaxResult;
import com.smarterp.system.domain.MercadoShopInfo;
import com.smarterp.system.domain.MercadoShopInfoSite;
import com.smarterp.system.domain.vo.MercadoShopInfoVO;

/**
 * 店铺Service接口
 *
 * @author smarterp
 * @date 2023-04-19
 */
public interface IMercadoShopInfoService {
    /**
     * 查询店铺
     *
     * @param id 店铺主键
     * @return 店铺
     */
    public MercadoShopInfo selectMercadoShopInfoById(Long id);

    /**
     * 查询店铺列表
     *
     * @param mercadoShopInfo 店铺
     * @return 店铺集合
     */
    public List<MercadoShopInfo> selectMercadoShopInfoList(MercadoShopInfo mercadoShopInfo);

    /**
     * 新增店铺
     *
     * @param mercadoShopInfoVO 店铺
     * @return 结果
     */
    public AjaxResult insertMercadoShopInfo(MercadoShopInfoVO mercadoShopInfoVO);

    /**
     * 修改店铺
     *
     * @param mercadoShopInfo 店铺
     * @return 结果
     */
    public int updateMercadoShopInfo(MercadoShopInfo mercadoShopInfo);

    /**
     * 批量删除店铺
     *
     * @param ids 需要删除的店铺主键集合
     * @return 结果
     */
    public int deleteMercadoShopInfoByIds(Long[] ids);

    /**
     * 删除店铺信息
     *
     * @param id 店铺主键
     * @return 结果
     */
    public int deleteMercadoShopInfoById(Long id);

    /**
     * 查询店铺站点
     * @param mercadoShopInfoSite
     * @return
     */
    public List<MercadoShopInfoSite> selectMercadoShopInfoSiteList(MercadoShopInfoSite mercadoShopInfoSite);

    /**
     * 修改价格系数
     * @param mercadoShopInfo
     * @return
     */
    public int editPriceRatio(MercadoShopInfo mercadoShopInfo);
}
