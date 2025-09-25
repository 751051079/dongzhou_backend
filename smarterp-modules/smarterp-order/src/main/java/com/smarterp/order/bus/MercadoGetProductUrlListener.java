package com.smarterp.order.bus;

import com.smarterp.order.constant.RabbitMQConstant;
import com.smarterp.order.domain.MercadoBatchCollectionLink;
import com.smarterp.order.domain.dto.MercadoBatchCollectionLinkDTO;
import com.smarterp.order.tools.CrawlingUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.Set;


@Component
public class MercadoGetProductUrlListener {

    private static final Logger logger = LoggerFactory.getLogger(MercadoGetProductUrlListener.class);

    @Resource
    private AmqpTemplate amqpTemplate;

    /**
     * 通过店铺链接采集数据，例如MLM2073374699
     * @param mercadoBatchCollectionLinkDTO
     */
    @RabbitListener(bindings = @QueueBinding(exchange = @Exchange(
            name = RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
            type = ExchangeTypes.TOPIC),
            value = @Queue(name = RabbitMQConstant.MercadoQueue.GET_PRODUCT_URL_BY_SHOP_URL_QUEUE),
            key = RabbitMQConstant.RoutingKey.GET_PRODUCT_URL_BY_SHOP_URL))
    public void batchCollectionLink(MercadoBatchCollectionLinkDTO mercadoBatchCollectionLinkDTO) {
        try {
            if (mercadoBatchCollectionLinkDTO != null) {
                if (StringUtils.isNotEmpty(mercadoBatchCollectionLinkDTO.getShopUrl())) {
                    // 根据首页链接获取所有产品链接
                    Set<String> urlList = CrawlingUtil.getMercadoProductUrl(mercadoBatchCollectionLinkDTO.getShopUrl());
                    if (!urlList.isEmpty()) {
                        Iterator<String> iterator = urlList.iterator();
                        while (iterator.hasNext()) {
                            String itemId = iterator.next();
                            MercadoBatchCollectionLink mercadoBatchCollectionLink = new MercadoBatchCollectionLink();
                            mercadoBatchCollectionLink.setDeptId(mercadoBatchCollectionLinkDTO.getDeptId());
                            mercadoBatchCollectionLink.setUserId(mercadoBatchCollectionLinkDTO.getUserId());
                            mercadoBatchCollectionLink.setItemId(itemId);
                            amqpTemplate.convertAndSend(RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
                                    RabbitMQConstant.RoutingKey.CLOUD_PULL_MERCADO_BATCH_COLLECTION_LINK, mercadoBatchCollectionLink);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("采集链接到店铺异常 {}", e.getMessage());
        }
    }
}
