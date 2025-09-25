package com.smarterp.message.service;

import java.util.List;

import com.smarterp.message.domain.MercadoCallBack;

/**
 * 美客多回调信息Service接口
 *
 * @author smarterp
 * @date 2024-06-23
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
}
