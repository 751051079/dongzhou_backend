package com.smarterp.system.service;

import java.util.List;

import com.smarterp.common.core.web.domain.AjaxResult;
import com.smarterp.system.domain.MercaodoDeptcombo;

/**
 * 部门与套餐关联Service接口
 *
 * @author smarterp
 * @date 2023-04-20
 */
public interface IMercaodoDeptcomboService {
    /**
     * 查询部门与套餐关联
     *
     * @param id 部门与套餐关联主键
     * @return 部门与套餐关联
     */
    public MercaodoDeptcombo selectMercaodoDeptcomboById(Long id);

    /**
     * 查询部门与套餐关联列表
     *
     * @param mercaodoDeptcombo 部门与套餐关联
     * @return 部门与套餐关联集合
     */
    public List<MercaodoDeptcombo> selectMercaodoDeptcomboList(MercaodoDeptcombo mercaodoDeptcombo);

    /**
     * 新增部门与套餐关联
     *
     * @param mercaodoDeptcombo 部门与套餐关联
     * @return 结果
     */
    public AjaxResult insertMercaodoDeptcombo(MercaodoDeptcombo mercaodoDeptcombo);

    /**
     * 修改部门与套餐关联
     *
     * @param mercaodoDeptcombo 部门与套餐关联
     * @return 结果
     */
    public int updateMercaodoDeptcombo(MercaodoDeptcombo mercaodoDeptcombo);

    /**
     * 批量删除部门与套餐关联
     *
     * @param ids 需要删除的部门与套餐关联主键集合
     * @return 结果
     */
    public int deleteMercaodoDeptcomboByIds(Long[] ids);

    /**
     * 删除部门与套餐关联信息
     *
     * @param id 部门与套餐关联主键
     * @return 结果
     */
    public int deleteMercaodoDeptcomboById(Long id);
}
