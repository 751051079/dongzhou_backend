package com.smarterp.order.mapper;

import java.util.List;

import com.smarterp.order.domain.MercadoShopGlobal;
import com.smarterp.order.domain.MercadoShopItem;
import org.apache.ibatis.annotations.Param;

/**
 * 店铺产品Mapper接口
 *
 * @author smarterp
 * @date 2024-06-09
 */
public interface MercadoShopItemMapper {
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
    public List<MercadoShopItem> selectMercadoShopItemList(MercadoShopItem mercadoShopItem);

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
     * 删除店铺产品
     *
     * @param id 店铺产品主键
     * @return 结果
     */
    public int deleteMercadoShopItemById(String id);

    /**
     * 批量删除店铺产品
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteMercadoShopItemByIds(String[] ids);

    /**
     * 批量插入或更新
     *
     * @param mercadoShopItemList
     * @return
     */
    public int replaceIntoItemList(List<MercadoShopItem> mercadoShopItemList);

    /**
     * 批量插入全局产品信息
     *
     * @param mercadoShopGlobalList
     * @return
     */
    public Integer batchInsertShopGlobal(List<MercadoShopGlobal> mercadoShopGlobalList);

    /**
     * 根据主键id集合查询
     *
     * @param itemIdList
     * @return
     */
    public List<MercadoShopItem> selectMercadoShopItemByIdList(@Param("itemIdList") List<String> itemIdList);
}
