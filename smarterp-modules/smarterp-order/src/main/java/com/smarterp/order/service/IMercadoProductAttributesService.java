package com.smarterp.order.service;

import java.util.List;

import com.smarterp.order.domain.MercadoProductAttributes;

/**
 * 产品全局属性Service接口
 *
 * @author smarterp
 * @date 2023-04-28
 */
public interface IMercadoProductAttributesService {
    /**
     * 查询产品全局属性
     *
     * @param id 产品全局属性主键
     * @return 产品全局属性
     */
    public MercadoProductAttributes selectMercadoProductAttributesById(Long id);

    /**
     * 查询产品全局属性列表
     *
     * @param mercadoProductAttributes 产品全局属性
     * @return 产品全局属性集合
     */
    public List<MercadoProductAttributes> selectMercadoProductAttributesList(MercadoProductAttributes mercadoProductAttributes);

    /**
     * 新增产品全局属性
     *
     * @param mercadoProductAttributes 产品全局属性
     * @return 结果
     */
    public int insertMercadoProductAttributes(MercadoProductAttributes mercadoProductAttributes);

    /**
     * 修改产品全局属性
     *
     * @param mercadoProductAttributes 产品全局属性
     * @return 结果
     */
    public int updateMercadoProductAttributes(MercadoProductAttributes mercadoProductAttributes);

    /**
     * 批量删除产品全局属性
     *
     * @param ids 需要删除的产品全局属性主键集合
     * @return 结果
     */
    public int deleteMercadoProductAttributesByIds(Long[] ids);

    /**
     * 删除产品全局属性信息
     *
     * @param id 产品全局属性主键
     * @return 结果
     */
    public int deleteMercadoProductAttributesById(Long id);
}
