package com.smarterp.system.mapper;

import java.util.List;

import com.smarterp.system.domain.MercadoComboInfo;
import com.smarterp.system.domain.vo.SysDeptVO;

/**
 * 套餐Mapper接口
 *
 * @author smarterp
 * @date 2023-04-16
 */
public interface MercadoComboInfoMapper {
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
     * 删除套餐
     *
     * @param id 套餐主键
     * @return 结果
     */
    public int deleteMercadoComboInfoById(Long id);

    /**
     * 批量删除套餐
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteMercadoComboInfoByIds(Long[] ids);

    /**
     * 查询所有部门绑定套餐的剩余有效天数
     *
     * @return
     */
    public List<SysDeptVO> getAllDeptComboList();
}
