package com.smarterp.order.service;

import com.smarterp.common.core.web.domain.AjaxResult;
import com.smarterp.order.domain.MercadoProductItem;
import com.smarterp.order.domain.dto.MercadoInfoDto;

import java.util.List;

/**
 * 产品，店铺和站点信息关联Service接口
 *
 * @author smarterp
 * @date 2023-04-26
 */
public interface IMercadoProductItemService {
    /**
     * 查询产品，店铺和站点信息关联
     *
     * @param id 产品，店铺和站点信息关联主键
     * @return 产品，店铺和站点信息关联
     */
    public MercadoProductItem selectMercadoProductItemById(Long id);

    /**
     * 查询产品，店铺和站点信息关联列表
     *
     * @param mercadoProductItem 产品，店铺和站点信息关联
     * @return 产品，店铺和站点信息关联集合
     */
    public List<MercadoProductItem> selectMercadoProductItemList(MercadoProductItem mercadoProductItem);

    /**
     * 新增产品，店铺和站点信息关联
     *
     * @param mercadoProductItem 产品，店铺和站点信息关联
     * @return 结果
     */
    public int insertMercadoProductItem(MercadoProductItem mercadoProductItem);

    /**
     * 修改产品，店铺和站点信息关联
     *
     * @param mercadoProductItem 产品，店铺和站点信息关联
     * @return 结果
     */
    public int updateMercadoProductItem(MercadoProductItem mercadoProductItem);

    /**
     * 批量删除产品，店铺和站点信息关联
     *
     * @param ids 需要删除的产品，店铺和站点信息关联主键集合
     * @return 结果
     */
    public int deleteMercadoProductItemByIds(Long[] ids);

    /**
     * 删除产品，店铺和站点信息关联信息
     *
     * @param id 产品，店铺和站点信息关联主键
     * @return 结果
     */
    public int deleteMercadoProductItemById(Long id);

    AjaxResult releaseProductHandle(MercadoInfoDto mercadoInfoDto);
}
