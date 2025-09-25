package com.smarterp.message.service.impl;

import com.smarterp.message.domain.MercadoCallBack;
import com.smarterp.message.mapper.MercadoCallBackMapper;
import com.smarterp.message.service.IMercadoCallBackService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 美客多回调信息Service业务层处理
 *
 * @author smarterp
 * @date 2024-06-23
 */
@Service
public class MercadoCallBackServiceImpl implements IMercadoCallBackService {

    @Resource
    private MercadoCallBackMapper mercadoCallBackMapper;

    /**
     * 查询美客多回调信息
     *
     * @param id 美客多回调信息主键
     * @return 美客多回调信息
     */
    @Override
    public MercadoCallBack selectMercadoCallBackById(Long id) {
        return mercadoCallBackMapper.selectMercadoCallBackById(id);
    }

    /**
     * 查询美客多回调信息列表
     *
     * @param mercadoCallBack 美客多回调信息
     * @return 美客多回调信息
     */
    @Override
    public List<MercadoCallBack> selectMercadoCallBackList(MercadoCallBack mercadoCallBack) {
        return mercadoCallBackMapper.selectMercadoCallBackList(mercadoCallBack);
    }

    /**
     * 新增美客多回调信息
     *
     * @param mercadoCallBack 美客多回调信息
     * @return 结果
     */
    @Override
    public int insertMercadoCallBack(MercadoCallBack mercadoCallBack) {
        return mercadoCallBackMapper.insertMercadoCallBack(mercadoCallBack);
    }

    /**
     * 修改美客多回调信息
     *
     * @param mercadoCallBack 美客多回调信息
     * @return 结果
     */
    @Override
    public int updateMercadoCallBack(MercadoCallBack mercadoCallBack) {
        return mercadoCallBackMapper.updateMercadoCallBack(mercadoCallBack);
    }

    /**
     * 批量删除美客多回调信息
     *
     * @param ids 需要删除的美客多回调信息主键
     * @return 结果
     */
    @Override
    public int deleteMercadoCallBackByIds(Long[] ids) {
        return mercadoCallBackMapper.deleteMercadoCallBackByIds(ids);
    }

    /**
     * 删除美客多回调信息信息
     *
     * @param id 美客多回调信息主键
     * @return 结果
     */
    @Override
    public int deleteMercadoCallBackById(Long id) {
        return mercadoCallBackMapper.deleteMercadoCallBackById(id);
    }
}
