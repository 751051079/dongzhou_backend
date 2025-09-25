package com.smarterp.message.constant;

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

        String MERCADO_CALL_BACK_INFO = "mercado.call.back.info";

        String MERCADO_CALL_BACK_INFO_MARKETPLACE_ITEMS = "mercado.call.back.info.marketplace.items";

        String MERCADO_CALL_BACK_INFO_MARKETPLACE_ORDERS = "mercado.call.back.info.marketplace.orders";

    }

    interface MercadoQueue {

        String MERCADO_CALL_BACK_INFO_QUEUE = "mercado.call.back.info.queue";

        String MERCADO_CALL_BACK_INFO_MARKETPLACE_ITEMS_QUEUE = "mercado.call.back.info.marketplace.items.queue";

        String MERCADO_CALL_BACK_INFO_MARKETPLACE_ORDERS_QUEUE = "mercado.call.back.info.marketplace.orders.queue";

    }


}
