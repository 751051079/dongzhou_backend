package com.smarterp.system.service;

import java.util.List;

import com.smarterp.system.domain.MercadoComboInfo;

/**
 * 套餐Service接口
 *
 * @author smarterp
 * @date 2023-04-16
 */
public interface IMercadoComboInfoService {
    /**
     * 查询套餐
     *
     * @param id 套餐主键
     * @return 套餐
     */
    public MercadoComboInfo selectMercadoComboInfoById(Long id);

    /**
     * 查询套餐列表
     *
     * @param mercadoComboInfo 套餐
     * @return 套餐集合
     */
    public List<MercadoComboInfo> selectMercadoComboInfoList(MercadoComboInfo mercadoComboInfo);

    /**
     * 新增套餐
     *
     * @param mercadoComboInfo 套餐
     * @return 结果
     */
    public int insertMercadoComboInfo(MercadoComboInfo mercadoComboInfo);

    /**
     * 修改套餐
     *
     * @param mercadoComboInfo 套餐
     * @return 结果
     */
    public int updateMercadoComboInfo(MercadoComboInfo mercadoComboInfo);

    /**
     * 批量删除套餐
     *
     * @param ids 需要删除的套餐主键集合
     * @return 结果
     */
    public int deleteMercadoComboInfoByIds(Long[] ids);

    /**
     * 删除套餐信息
     *
     * @param id 套餐主键
     * @return 结果
     */
    public int deleteMercadoComboInfoById(Long id);
}
