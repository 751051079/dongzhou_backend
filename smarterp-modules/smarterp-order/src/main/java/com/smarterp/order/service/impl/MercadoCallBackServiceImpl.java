package com.smarterp.order.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSONObject;
import com.smarterp.order.domain.MercadoCallBack;
import com.smarterp.order.mapper.MercadoCallBackMapper;
import com.smarterp.order.service.IMercadoCallBackService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 美客多回调信息Service业务层处理
 *
 * @author smarterp
 * @date 2023-05-30
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

    /**
     * 插入数据
     *
     * @param message
     * @return
     */
    @Override
    public Integer insertMercadoMessage(JSONObject message) {
        MercadoCallBack mercadoCallBack = new MercadoCallBack();
        mercadoCallBack.setId(IdUtil.getSnowflake(1, 1).nextId());
        mercadoCallBack.setResponseId(message.getString("_id"));
        mercadoCallBack.setResource(message.getString("resource"));
        mercadoCallBack.setUserId(message.getString("user_id"));
        mercadoCallBack.setTopic(message.getString("topic"));
        mercadoCallBack.setApplicationId(message.getLong("application_id"));
        mercadoCallBack.setAttempts(message.getLong("attempts"));
        mercadoCallBack.setSent(message.getDate("sent"));
        mercadoCallBack.setReceived(message.getDate("received"));
        return mercadoCallBackMapper.insertMercadoCallBack(mercadoCallBack);
    }
}
