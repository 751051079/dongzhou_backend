package com.smarterp.order.service;

import java.util.List;

import com.smarterp.common.core.web.domain.AjaxResult;
import com.smarterp.order.domain.MercadoShopItem;
import com.smarterp.order.domain.dto.BatchCollectionLinkToShop;

/**
 * 店铺产品Service接口
 *
 * @author smarterp
 * @date 2024-06-09
 */
public interface IMercadoShopItemService {
    /**
     * 查询店铺产品
     *
     * @param id 店铺产品主键
     * @return 店铺产品
     */
    public MercadoShopItem selectMercadoShopItemById(String id);

    /**
     * 查询店铺产品列表
     *
     * @param mercadoShopItem 店铺产品
     * @return 店铺产品集合
     */
    public List<MercadoShopItem> selectMercadoShopItemList(MercadoShopItem mercadoShopItem, Integer pageNum, Integer pageSize);

    /**
     * 新增店铺产品
     *
     * @param mercadoShopItem 店铺产品
     * @return 结果
     */
    public int insertMercadoShopItem(MercadoShopItem mercadoShopItem);

    /**
     * 修改店铺产品
     *
     * @param mercadoShopItem 店铺产品
     * @return 结果
     */
    public int updateMercadoShopItem(MercadoShopItem mercadoShopItem);

    /**
     * 批量删除店铺产品
     *
     * @param ids 需要删除的店铺产品主键集合
     * @return 结果
     */
    public int deleteMercadoShopItemByIds(String[] ids);

    /**
     * 删除店铺产品信息
     *
     * @param id 店铺产品主键
     * @return 结果
     */
    public int deleteMercadoShopItemById(String id);

    /**
     * 获取店铺产品详情
     *
     * @param merchantId
     * @return
     */
    public AjaxResult updateShopItemInfo(String merchantId);

    /**
     * 获取所有店铺详情
     *
     * @return
     */
    public AjaxResult updateAllShopItemInfo();

    /**
     * 批量采集链接到店铺
     * @param batchCollectionLinkToShop
     * @return
     */
    public AjaxResult batchCollectionToShop(BatchCollectionLinkToShop batchCollectionLinkToShop);

    /**
     * 批量更新产品信息
     * @param ids
     * @return
     */
    public AjaxResult batchEdit(String[] ids);
}
