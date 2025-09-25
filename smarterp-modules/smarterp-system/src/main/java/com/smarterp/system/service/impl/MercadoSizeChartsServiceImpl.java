package com.smarterp.system.service.impl;

import com.smarterp.system.domain.MercadoSizeCharts;
import com.smarterp.system.mapper.MercadoSizeChartsMapper;
import com.smarterp.system.service.IMercadoSizeChartsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * 尺码Service业务层处理
 *
 * @author smarterp
 * @date 2024-12-08
 */
@Service
public class MercadoSizeChartsServiceImpl implements IMercadoSizeChartsService {

    @Resource
    private MercadoSizeChartsMapper mercadoSizeChartsMapper;

    /**
     * 查询尺码
     *
     * @param id 尺码主键
     * @return 尺码
     */
    @Override
    public MercadoSizeCharts selectMercadoSizeChartsById(String id) {
        return mercadoSizeChartsMapper.selectMercadoSizeChartsById(id);
    }

    /**
     * 查询尺码列表
     *
     * @param mercadoSizeCharts 尺码
     * @return 尺码
     */
    @Override
    public List<MercadoSizeCharts> selectMercadoSizeChartsList(MercadoSizeCharts mercadoSizeCharts) {
        return mercadoSizeChartsMapper.selectMercadoSizeChartsList(mercadoSizeCharts);
    }

    /**
     * 新增尺码
     *
     * @param mercadoSizeCharts 尺码
     * @return 结果
     */
    @Override
    public int insertMercadoSizeCharts(MercadoSizeCharts mercadoSizeCharts) {
        return mercadoSizeChartsMapper.insertMercadoSizeCharts(mercadoSizeCharts);
    }

    /**
     * 修改尺码
     *
     * @param mercadoSizeCharts 尺码
     * @return 结果
     */
    @Override
    public int updateMercadoSizeCharts(MercadoSizeCharts mercadoSizeCharts) {
        return mercadoSizeChartsMapper.updateMercadoSizeCharts(mercadoSizeCharts);
    }

    /**
     * 批量删除尺码
     *
     * @param ids 需要删除的尺码主键
     * @return 结果
     */
    @Override
    public int deleteMercadoSizeChartsByIds(String[] ids) {
        return mercadoSizeChartsMapper.deleteMercadoSizeChartsByIds(ids);
    }

    /**
     * 删除尺码信息
     *
     * @param id 尺码主键
     * @return 结果
     */
    @Override
    public int deleteMercadoSizeChartsById(String id) {
        return mercadoSizeChartsMapper.deleteMercadoSizeChartsById(id);
    }
}
