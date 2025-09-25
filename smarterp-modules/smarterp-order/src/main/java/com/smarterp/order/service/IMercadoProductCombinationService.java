package com.smarterp.order.service;

import java.util.List;

import com.smarterp.order.domain.MercadoProductCombination;

/**
 * 变体信息Service接口
 *
 * @author smarterp
 * @date 2023-05-05
 */
public interface IMercadoProductCombinationService {

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
     * 批量删除变体信息
     *
     * @param ids 需要删除的变体信息主键集合
     * @return 结果
     */
    public int deleteMercadoProductCombinationByIds(Long[] ids);

    /**
     * 删除变体信息信息
     *
     * @param id 变体信息主键
     * @return 结果
     */
    public int deleteMercadoProductCombinationById(Long id);
}
