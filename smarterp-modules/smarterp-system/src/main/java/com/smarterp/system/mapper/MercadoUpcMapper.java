package com.smarterp.system.mapper;

import java.util.List;

import com.smarterp.system.domain.MercadoUpc;
import com.smarterp.system.domain.vo.MercadoUpcVO;

/**
 * UPC信息Mapper接口
 *
 * @author smarterp
 * @date 2023-04-16
 */
public interface MercadoUpcMapper {
    /**
     * 查询UPC信息
     *
     * @param id UPC信息主键
     * @return UPC信息
     */
    public MercadoUpc selectMercadoUpcById(Long id);

    /**
     * 查询UPC信息列表
     *
     * @param mercadoUpc UPC信息
     * @return UPC信息集合
     */
    public List<MercadoUpc> selectMercadoUpcList(MercadoUpc mercadoUpc);

    /**
     * 查询UPC信息列表（包括部门信息）
     *
     * @param mercadoUpc UPC信息
     * @return UPC信息集合
     */
    public List<MercadoUpcVO> selectMercadoUpcDeptList(MercadoUpc mercadoUpc);

    /**
     * 新增UPC信息
     *
     * @param mercadoUpc UPC信息
     * @return 结果
     */
    public int insertMercadoUpc(MercadoUpc mercadoUpc);

    /**
     * 修改UPC信息
     *
     * @param mercadoUpc UPC信息
     * @return 结果
     */
    public int updateMercadoUpc(MercadoUpc mercadoUpc);

    /**
     * 删除UPC信息
     *
     * @param id UPC信息主键
     * @return 结果
     */
    public int deleteMercadoUpcById(Long id);

    /**
     * 批量删除UPC信息
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteMercadoUpcByIds(Long[] ids);
}
