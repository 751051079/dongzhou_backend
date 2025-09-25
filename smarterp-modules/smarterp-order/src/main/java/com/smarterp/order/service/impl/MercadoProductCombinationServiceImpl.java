package com.smarterp.order.service.impl;

import com.smarterp.common.core.utils.DateUtils;
import com.smarterp.order.domain.MercadoProductCombination;
import com.smarterp.order.mapper.MercadoProductCombinationMapper;
import com.smarterp.order.service.IMercadoProductCombinationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 变体信息Service业务层处理
 *
 * @author smarterp
 * @date 2023-05-05
 */
@Service
public class MercadoProductCombinationServiceImpl implements IMercadoProductCombinationService {

    @Resource
    private MercadoProductCombinationMapper mercadoProductCombinationMapper;

    /**
     * 查询变体信息
     *
     * @param id 变体信息主键
     * @return 变体信息
     */
    @Override
    public MercadoProductCombination selectMercadoProductCombinationById(Long id) {
        return mercadoProductCombinationMapper.selectMercadoProductCombinationById(id);
    }

    /**
     * 查询变体信息列表
     *
     * @param mercadoProductCombination 变体信息
     * @return 变体信息
     */
    @Override
    public List<MercadoProductCombination> selectMercadoProductCombinationList(MercadoProductCombination mercadoProductCombination) {
        return mercadoProductCombinationMapper.selectMercadoProductCombinationList(mercadoProductCombination);
    }

    /**
     * 新增变体信息
     *
     * @param mercadoProductCombination 变体信息
     * @return 结果
     */
    @Override
    public int insertMercadoProductCombination(MercadoProductCombination mercadoProductCombination) {
        mercadoProductCombination.setCreateTime(DateUtils.getNowDate());
        return mercadoProductCombinationMapper.insertMercadoProductCombination(mercadoProductCombination);
    }

    /**
     * 修改变体信息
     *
     * @param mercadoProductCombination 变体信息
     * @return 结果
     */
    @Override
    public int updateMercadoProductCombination(MercadoProductCombination mercadoProductCombination) {
        mercadoProductCombination.setUpdateTime(DateUtils.getNowDate());
        return mercadoProductCombinationMapper.updateMercadoProductCombination(mercadoProductCombination);
    }

    /**
     * 批量删除变体信息
     *
     * @param ids 需要删除的变体信息主键
     * @return 结果
     */
    @Override
    public int deleteMercadoProductCombinationByIds(Long[] ids) {
        return mercadoProductCombinationMapper.deleteMercadoProductCombinationByIds(ids);
    }

    /**
     * 删除变体信息信息
     *
     * @param id 变体信息主键
     * @return 结果
     */
    @Override
    public int deleteMercadoProductCombinationById(Long id) {
        return mercadoProductCombinationMapper.deleteMercadoProductCombinationById(id);
    }
}
