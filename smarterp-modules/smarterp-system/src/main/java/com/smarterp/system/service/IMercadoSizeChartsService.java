package com.smarterp.system.service;

import com.smarterp.system.domain.MercadoSizeCharts;

import java.util.List;

/**
 * 尺码Service接口
 *
 * @author smarterp
 * @date 2024-12-08
 */
public interface IMercadoSizeChartsService {
    /**
     * 查询尺码
     *
     * @param id 尺码主键
     * @return 尺码
     */
    public MercadoSizeCharts selectMercadoSizeChartsById(String id);

    /**
     * 查询尺码列表
     *
     * @param mercadoSizeCharts 尺码
     * @return 尺码集合
     */
    public List<MercadoSizeCharts> selectMercadoSizeChartsList(MercadoSizeCharts mercadoSizeCharts);

    /**
     * 新增尺码
     *
     * @param mercadoSizeCharts 尺码
     * @return 结果
     */
    public int insertMercadoSizeCharts(MercadoSizeCharts mercadoSizeCharts);

    /**
     * 修改尺码
     *
     * @param mercadoSizeCharts 尺码
     * @return 结果
     */
    public int updateMercadoSizeCharts(MercadoSizeCharts mercadoSizeCharts);

    /**
     * 批量删除尺码
     *
     * @param ids 需要删除的尺码主键集合
     * @return 结果
     */
    public int deleteMercadoSizeChartsByIds(String[] ids);

    /**
     * 删除尺码信息
     *
     * @param id 尺码主键
     * @return 结果
     */
    public int deleteMercadoSizeChartsById(String id);
}
