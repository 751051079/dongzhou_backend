package com.smarterp.order.service;

import com.smarterp.order.domain.MercadoCallBack;

import java.util.List;

/**
 * 美客多回调信息Service接口
 *
 * @author smarterp
 * @date 2023-05-30
 */
public interface IMercadoSyncRemoteOrderService {

    /**
     * 同步自发货订单
     * @return
     */
    public List<Long> syncRemoteOrderLijuntao();

    /**
     * 查询订单表中状态是未发布状态的30个产品id
     * @return
     */
    public List<Long> pullProductLijuntao();



    /**
     * 同步自发货订单
     * @return
     */
    //public int   syncRemoteOrderLyh(int page,int size);

    /**
     * 查询订单表中状态是未发布状态的30个产品id
     * @return
     */
    public Long pullProductLyh();

    int pullProductIdLyh(int i, int size);

    int deleteProByLyh(int i, int size);

    List<Long> deleteProductLijuntao();

    List<Long> syncRemoteOrderBack(Integer limit);
}
