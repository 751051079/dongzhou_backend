package com.smarterp.order.service.impl;

import com.smarterp.common.core.utils.DateUtils;
import com.smarterp.common.core.web.domain.AjaxResult;
import com.smarterp.order.constant.RabbitMQConstant;
import com.smarterp.order.domain.MercadoProductItem;
import com.smarterp.order.domain.dto.MercadoInfoDto;
import com.smarterp.order.domain.vo.MercadoReleaseProductDTO;
import com.smarterp.order.mapper.MercadoProductItemMapper;
import com.smarterp.order.service.IMercadoProductItemService;
import com.smarterp.order.service.IMercadoProductService;
import com.smarterp.order.service.IMercadoProductTraService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 产品，店铺和站点信息关联Service业务层处理
 *
 * @author smarterp
 * @date 2023-04-26
 */
@Service
public class MercadoProductItemServiceImpl implements IMercadoProductItemService {

    private static final Logger logger = LoggerFactory.getLogger(MercadoProductItemServiceImpl.class);

    @Resource
    private MercadoProductItemMapper mercadoProductItemMapper;

    @Autowired
    private IMercadoProductTraService mercadoProductTraService;

    @Autowired
    private IMercadoProductService mercadoProductService;

    @Resource
    private AmqpTemplate amqpTemplate;


    /**
     * 查询产品，店铺和站点信息关联
     *
     * @param id 产品，店铺和站点信息关联主键
     * @return 产品，店铺和站点信息关联
     */
    @Override
    public MercadoProductItem selectMercadoProductItemById(Long id) {
        return mercadoProductItemMapper.selectMercadoProductItemById(id);
    }

    /**
     * 查询产品，店铺和站点信息关联列表
     *
     * @param mercadoProductItem 产品，店铺和站点信息关联
     * @return 产品，店铺和站点信息关联
     */
    @Override
    public List<MercadoProductItem> selectMercadoProductItemList(MercadoProductItem mercadoProductItem) {
        return mercadoProductItemMapper.selectMercadoProductItemList(mercadoProductItem);
    }

    /**
     * 新增产品，店铺和站点信息关联
     *
     * @param mercadoProductItem 产品，店铺和站点信息关联
     * @return 结果
     */
    @Override
    public int insertMercadoProductItem(MercadoProductItem mercadoProductItem) {
        mercadoProductItem.setCreateTime(DateUtils.getNowDate());
        return mercadoProductItemMapper.insertMercadoProductItem(mercadoProductItem);
    }

    /**
     * 修改产品，店铺和站点信息关联
     *
     * @param mercadoProductItem 产品，店铺和站点信息关联
     * @return 结果
     */
    @Override
    public int updateMercadoProductItem(MercadoProductItem mercadoProductItem) {
        mercadoProductItem.setUpdateTime(DateUtils.getNowDate());
        return mercadoProductItemMapper.updateMercadoProductItem(mercadoProductItem);
    }

    /**
     * 批量删除产品，店铺和站点信息关联
     *
     * @param ids 需要删除的产品，店铺和站点信息关联主键
     * @return 结果
     */
    @Override
    public int deleteMercadoProductItemByIds(Long[] ids) {
        return mercadoProductItemMapper.deleteMercadoProductItemByIds(ids);
    }

    /**
     * 删除产品，店铺和站点信息关联信息
     *
     * @param id 产品，店铺和站点信息关联主键
     * @return 结果
     */
    @Override
    public int deleteMercadoProductItemById(Long id) {
        return mercadoProductItemMapper.deleteMercadoProductItemById(id);
    }


    @Override
    public AjaxResult releaseProductHandle(MercadoInfoDto mercadoInfoDto) {
        if (null != mercadoInfoDto.getProductId()) {
            //说明是编辑发布或者编辑保存
            String operateType = mercadoInfoDto.getOperateType();
            if ("add".equals(operateType)) {
                //纯编辑
                return mercadoProductService.updateMercadoProduct(mercadoInfoDto);
            } else {
                //编辑后在发布
                AjaxResult ajaxResult = mercadoProductService.updateMercadoProduct(mercadoInfoDto);
                if (200 == (int) ajaxResult.get("code")) {
                    //再发布
                    MercadoReleaseProductDTO mercadoReleaseProductDTO = new MercadoReleaseProductDTO();
                    mercadoReleaseProductDTO.setProductId(mercadoInfoDto.getProductId());
                    mercadoReleaseProductDTO.setReleaseType("cbt");
                    return mercadoProductService.releaseProduct(mercadoReleaseProductDTO);
                } else {
                    return ajaxResult;
                }
            }
        } else {
            //说明是第一次发布
            List<Long> productIds = mercadoProductTraService.releaseProduct(mercadoInfoDto);
            this.mqHandle(mercadoInfoDto.getOperateType(), productIds);
            this.processInfringementInfo(productIds);
            return AjaxResult.success("产品发布中");
        }
    }

    private void mqHandle(String operateType, List<Long> productIds) {
        if (operateType.equals("release")) {
            if (!productIds.isEmpty()) {
                for (Long productId : productIds) {
                    // 将需要发布的产品id放到消息队列中
                    amqpTemplate.convertAndSend(RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
                            RabbitMQConstant.RoutingKey.CLOUD_PULL_MERCADO_PRODUCT_INFO, productId);

                }
            }
        }
    }

    /**
     * 处理侵权信息
     *
     * @param productIds
     */
    private void processInfringementInfo(List<Long> productIds) {
        if (!productIds.isEmpty()) {
            for (Long productId : productIds) {
                // 将需要发布的产品id放到消息队列中
                amqpTemplate.convertAndSend(RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
                        RabbitMQConstant.RoutingKey.CLOUD_PROCESS_INFRINGEMENT_INFORMATION, productId);

            }
        }
    }


}
