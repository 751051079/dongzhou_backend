package com.smarterp.order.mapper;

import com.smarterp.order.domain.MercadoProductCombination;

import java.util.List;

/**
 * 变体信息Mapper接口
 *
 * @author smarterp
 * @date 2023-05-05
 */
public interface MercadoProductCombinationMapper {

    /**
     * 查询变体信息
     *
     * @param id 变体信息主键
     * @return 变体信息
     */
    public MercadoProductCombination selectMercadoProductCombinationById(Long id);

    /**
     * 查询变体信息列表
     *
     * @param mercadoProductCombination 变体信息
     * @return 变体信息集合
     */
    public List<MercadoProductCombination> selectMercadoProductCombinationList(MercadoProductCombination mercadoProductCombination);

    /**
     * 新增变体信息
     *
     * @param mercadoProductCombination 变体信息
     * @return 结果
     */
    public int insertMercadoProductCombination(MercadoProductCombination mercadoProductCombination);

    /**
     * 修改变体信息
     *
     * @param mercadoProductCombination 变体信息
     * @return 结果
     */
    public int updateMercadoProductCombination(MercadoProductCombination mercadoProductCombination);

    /**
     * 删除变体信息
     *
     * @param id 变体信息主键
     * @return 结果
     */
    public int deleteMercadoProductCombinationById(Long id);

    /**
     * 批量删除变体信息
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteMercadoProductCombinationByIds(Long[] ids);

    /**
     * 根据productId删除变体数据
     * @param productId
     * @return
     */
    public Integer deleteMercadoProductCombinationByProductId(Long productId);

    /**
     * 批量插入变体信息
     * @param insertCombinationList
     * @return
     */
    public Integer batchMercadoProductCombination(List<MercadoProductCombination> insertCombinationList);

    /**
     * 批量删除
     * @param result
     */
    public Integer deleteMercadoProductCombinationByProductIds(List<Long> result);
}
