package com.smarterp.order.service;

import com.alibaba.fastjson2.JSONObject;
import com.smarterp.order.domain.MercadoCallBack;

import java.util.List;

/**
 * 美客多回调信息Service接口
 *
 * @author smarterp
 * @date 2023-05-30
 */
public interface IMercadoCallBackService {
    /**
     * 查询美客多回调信息
     *
     * @param id 美客多回调信息主键
     * @return 美客多回调信息
     */
    public MercadoCallBack selectMercadoCallBackById(Long id);

    /**
     * 查询美客多回调信息列表
     *
     * @param mercadoCallBack 美客多回调信息
     * @return 美客多回调信息集合
     */
    public List<MercadoCallBack> selectMercadoCallBackList(MercadoCallBack mercadoCallBack);

    /**
     * 新增美客多回调信息
     *
     * @param mercadoCallBack 美客多回调信息
     * @return 结果
     */
    public int insertMercadoCallBack(MercadoCallBack mercadoCallBack);

    /**
     * 修改美客多回调信息
     *
     * @param mercadoCallBack 美客多回调信息
     * @return 结果
     */
    public int updateMercadoCallBack(MercadoCallBack mercadoCallBack);

    /**
     * 批量删除美客多回调信息
     *
     * @param ids 需要删除的美客多回调信息主键集合
     * @return 结果
     */
    public int deleteMercadoCallBackByIds(Long[] ids);

    /**
     * 删除美客多回调信息信息
     *
     * @param id 美客多回调信息主键
     * @return 结果
     */
    public int deleteMercadoCallBackById(Long id);

    /**
     * 插入数据
     *
     * @param message
     * @return
     */
    public Integer insertMercadoMessage(JSONObject message);
}
