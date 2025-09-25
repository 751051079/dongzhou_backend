package com.smarterp.order.mapper;

import java.util.List;

import com.smarterp.order.domain.MercadoProductErrorLog;


/**
 * 错误日志Mapper接口
 *
 * @author smarterp
 * @date 2023-07-03
 */
public interface MercadoProductErrorLogMapper {
    /**
     * 查询错误日志
     *
     * @param id 错误日志主键
     * @return 错误日志
     */
    public MercadoProductErrorLog selectMercadoProductErrorLogById(Long id);

    /**
     * 查询错误日志列表
     *
     * @param mercadoProductErrorLog 错误日志
     * @return 错误日志集合
     */
    public List<MercadoProductErrorLog> selectMercadoProductErrorLogList(MercadoProductErrorLog mercadoProductErrorLog);

    /**
     * 新增错误日志
     *
     * @param mercadoProductErrorLog 错误日志
     * @return 结果
     */
    public int insertMercadoProductErrorLog(MercadoProductErrorLog mercadoProductErrorLog);

    /**
     * 修改错误日志
     *
     * @param mercadoProductErrorLog 错误日志
     * @return 结果
     */
    public int updateMercadoProductErrorLog(MercadoProductErrorLog mercadoProductErrorLog);

    /**
     * 删除错误日志
     *
     * @param id 错误日志主键
     * @return 结果
     */
    public int deleteMercadoProductErrorLogById(Long id);

    /**
     * 批量删除错误日志
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteMercadoProductErrorLogByIds(Long[] ids);
}
