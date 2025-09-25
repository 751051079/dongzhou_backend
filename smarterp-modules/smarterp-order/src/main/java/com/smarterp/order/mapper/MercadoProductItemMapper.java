package com.smarterp.order.mapper;

import com.smarterp.order.domain.MercadoProductItem;
import com.smarterp.order.domain.vo.SysDeptVO;

import java.util.List;

/**
 * 产品，店铺和站点信息关联Mapper接口
 *
 * @author smarterp
 * @date 2023-04-26
 */
public interface MercadoProductItemMapper {
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
     * 删除产品，店铺和站点信息关联
     *
     * @param id 产品，店铺和站点信息关联主键
     * @return 结果
     */
    public int deleteMercadoProductItemById(Long id);

    /**
     * 批量删除产品，店铺和站点信息关联
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteMercadoProductItemByIds(Long[] ids);

    /**
     * 根据productId删除站点数据
     *
     * @param productId
     */
    public Integer deleteMercadoProductItemByProductId(Long productId);

    /**
     * 获取部门关联的套餐有效天数信息
     *
     * @return
     */
    public List<SysDeptVO> getAllDeptComboList();

    /**
     * 批量插入店铺站点信息
     *
     * @param mercadoProductItemList
     * @return
     */
    public Integer batchProductItemList(List<MercadoProductItem> mercadoProductItemList);

    /**
     * 批量删除
     * @param result
     * @return
     */
    public Integer deleteMercadoProductItemByProductIds(List<Long> result);
}
