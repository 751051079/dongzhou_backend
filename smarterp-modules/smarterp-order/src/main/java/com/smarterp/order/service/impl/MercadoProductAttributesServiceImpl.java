package com.smarterp.order.service.impl;

import com.smarterp.common.core.utils.DateUtils;
import com.smarterp.order.domain.MercadoProductAttributes;
import com.smarterp.order.mapper.MercadoProductAttributesMapper;
import com.smarterp.order.service.IMercadoProductAttributesService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 产品全局属性Service业务层处理
 *
 * @author smarterp
 * @date 2023-04-28
 */
@Service
public class MercadoProductAttributesServiceImpl implements IMercadoProductAttributesService {

    @Resource
    private MercadoProductAttributesMapper mercadoProductAttributesMapper;

    /**
     * 查询产品全局属性
     *
     * @param id 产品全局属性主键
     * @return 产品全局属性
     */
    @Override
    public MercadoProductAttributes selectMercadoProductAttributesById(Long id) {
        return mercadoProductAttributesMapper.selectMercadoProductAttributesById(id);
    }

    /**
     * 查询产品全局属性列表
     *
     * @param mercadoProductAttributes 产品全局属性
     * @return 产品全局属性
     */
    @Override
    public List<MercadoProductAttributes> selectMercadoProductAttributesList(MercadoProductAttributes mercadoProductAttributes) {
        return mercadoProductAttributesMapper.selectMercadoProductAttributesList(mercadoProductAttributes);
    }

    /**
     * 新增产品全局属性
     *
     * @param mercadoProductAttributes 产品全局属性
     * @return 结果
     */
    @Override
    public int insertMercadoProductAttributes(MercadoProductAttributes mercadoProductAttributes) {
        mercadoProductAttributes.setCreateTime(DateUtils.getNowDate());
        return mercadoProductAttributesMapper.insertMercadoProductAttributes(mercadoProductAttributes);
    }

    /**
     * 修改产品全局属性
     *
     * @param mercadoProductAttributes 产品全局属性
     * @return 结果
     */
    @Override
    public int updateMercadoProductAttributes(MercadoProductAttributes mercadoProductAttributes) {
        mercadoProductAttributes.setUpdateTime(DateUtils.getNowDate());
        return mercadoProductAttributesMapper.updateMercadoProductAttributes(mercadoProductAttributes);
    }

    /**
     * 批量删除产品全局属性
     *
     * @param ids 需要删除的产品全局属性主键
     * @return 结果
     */
    @Override
    public int deleteMercadoProductAttributesByIds(Long[] ids) {
        return mercadoProductAttributesMapper.deleteMercadoProductAttributesByIds(ids);
    }

    /**
     * 删除产品全局属性信息
     *
     * @param id 产品全局属性主键
     * @return 结果
     */
    @Override
    public int deleteMercadoProductAttributesById(Long id) {
        return mercadoProductAttributesMapper.deleteMercadoProductAttributesById(id);
    }
}
