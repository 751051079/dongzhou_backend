package com.smarterp.order.service.impl;

import com.smarterp.order.domain.MercadoProductErrorLog;
import com.smarterp.order.mapper.MercadoProductErrorLogMapper;
import com.smarterp.order.service.IMercadoProductErrorLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 错误日志Service业务层处理
 *
 * @author smarterp
 * @date 2023-07-03
 */
@Service
public class MercadoProductErrorLogServiceImpl implements IMercadoProductErrorLogService {

    @Resource
    private MercadoProductErrorLogMapper mercadoProductErrorLogMapper;

    /**
     * 查询错误日志
     *
     * @param id 错误日志主键
     * @return 错误日志
     */
    @Override
    public MercadoProductErrorLog selectMercadoProductErrorLogById(Long id) {
        return mercadoProductErrorLogMapper.selectMercadoProductErrorLogById(id);
    }

    /**
     * 查询错误日志列表
     *
     * @param mercadoProductErrorLog 错误日志
     * @return 错误日志
     */
    @Override
    public List<MercadoProductErrorLog> selectMercadoProductErrorLogList(MercadoProductErrorLog mercadoProductErrorLog) {
        return mercadoProductErrorLogMapper.selectMercadoProductErrorLogList(mercadoProductErrorLog);
    }

    /**
     * 新增错误日志
     *
     * @param mercadoProductErrorLog 错误日志
     * @return 结果
     */
    @Override
    public int insertMercadoProductErrorLog(MercadoProductErrorLog mercadoProductErrorLog) {
        return mercadoProductErrorLogMapper.insertMercadoProductErrorLog(mercadoProductErrorLog);
    }

    /**
     * 修改错误日志
     *
     * @param mercadoProductErrorLog 错误日志
     * @return 结果
     */
    @Override
    public int updateMercadoProductErrorLog(MercadoProductErrorLog mercadoProductErrorLog) {
        return mercadoProductErrorLogMapper.updateMercadoProductErrorLog(mercadoProductErrorLog);
    }

    /**
     * 批量删除错误日志
     *
     * @param ids 需要删除的错误日志主键
     * @return 结果
     */
    @Override
    public int deleteMercadoProductErrorLogByIds(Long[] ids) {
        return mercadoProductErrorLogMapper.deleteMercadoProductErrorLogByIds(ids);
    }

    /**
     * 删除错误日志信息
     *
     * @param id 错误日志主键
     * @return 结果
     */
    @Override
    public int deleteMercadoProductErrorLogById(Long id) {
        return mercadoProductErrorLogMapper.deleteMercadoProductErrorLogById(id);
    }
}
