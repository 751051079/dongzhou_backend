package com.smarterp.order.mapper;

import com.smarterp.order.domain.MercadoProductAttributes;

import java.util.List;

/**
 * 产品全局属性Mapper接口
 *
 * @author smarterp
 * @date 2023-04-28
 */
public interface MercadoProductAttributesMapper {
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
     * 删除产品全局属性
     *
     * @param id 产品全局属性主键
     * @return 结果
     */
    public int deleteMercadoProductAttributesById(Long id);

    /**
     * 批量删除产品全局属性
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteMercadoProductAttributesByIds(Long[] ids);

    /**
     * 根据productId删除属性数据
     *
     * @param productId
     */
    public Integer deleteMercadoProductAttributesByProductId(Long productId);

    /**
     * 批量插入属性
     *
     * @param inserAttributesList
     * @return
     */
    public Integer batchMercadoProductAttributes(List<MercadoProductAttributes> inserAttributesList);

    /**
     * 批量删除
     * @param result
     * @return
     */
    public Integer deleteMercadoProductAttributesByProductIds(List<Long> result);
}
