package com.smarterp.system.mapper;

import java.util.List;
import com.smarterp.system.domain.MercaodoDeptcombo;

/**
 * 部门与套餐关联Mapper接口
 * 
 * @author smarterp
 * @date 2023-04-20
 */
public interface MercaodoDeptcomboMapper 
{
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
    public int insertMercaodoDeptcombo(MercaodoDeptcombo mercaodoDeptcombo);

    /**
     * 修改部门与套餐关联
     * 
     * @param mercaodoDeptcombo 部门与套餐关联
     * @return 结果
     */
    public int updateMercaodoDeptcombo(MercaodoDeptcombo mercaodoDeptcombo);

    /**
     * 删除部门与套餐关联
     * 
     * @param id 部门与套餐关联主键
     * @return 结果
     */
    public int deleteMercaodoDeptcomboById(Long id);

    /**
     * 批量删除部门与套餐关联
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteMercaodoDeptcomboByIds(Long[] ids);
}
