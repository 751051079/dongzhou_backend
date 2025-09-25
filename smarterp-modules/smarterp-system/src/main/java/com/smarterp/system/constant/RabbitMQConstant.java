package com.smarterp.system.constant;

/**
 * @author juntao.li
 * @ClassName RabbitMQConstant
 * @description
 * @date 2023/3/24 08:17
 * @Version 1.0
 */
public interface RabbitMQConstant {

    String MQ_PULL_MERCADO_PRODUCT_EXCHANGE = "pull.mercado.product.exchange";

    interface RoutingKey {

        String GET_MERCADO_SHOP_ITEM_INFO = "get.mercado.shop.item.info";

        String PUSH_SHOP_ACCESS_TOKEN_TO_CACHE = "push.shop.access.token.to.cache";

        String SYNC_MERCADO_SIZE_CHARTS_DATA = "sync.mercado.size.charts.data";

    }

    interface MercadoQueue {

        String GET_MERCADO_SHOP_ITEM_INFO_QUEUE = "get.mercado.shop.item.info.queue";

        String PUSH_SHOP_ACCESS_TOKEN_TO_CACHE_QUEUE = "push.shop.access.token.to.cache.queue";

        String SYNC_MERCADO_SIZE_CHARTS_DATA_QUEUE = "sync.mercado.size.charts.data.queue";

    }


}
