package com.smarterp.order.constant;

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

        String CLOUD_PULL_MERCADO_PRODUCT_INFO = "cloud.pull.mercado.product.info";

        String CLOUD_PULL_MERCADO_BATCH_COLLECTION_LINK = "cloud.pull.mercado.batch.collection.link";

        String CLOUD_PULL_MERCADO_BATCH_COLLECTION_LINK_TO_SHOP = "cloud.pull.mercado.batch.collection.link.to.shop";

        String CLOUD_PULL_MERCADO_BATCH_COLLECTION_ITEM_TO_SHOP = "cloud.pull.mercado.batch.collection.item.to.shop";

        String CLOUD_PULL_MERCADO_BATCH_COPY_LINK_TO_SHOP = "cloud.pull.mercado.batch.copy.link.to.shop";

        String UPDATE_UPC_CODE_INFO = "update.upc.code.info";

        String GET_PRODUCT_URL_BY_SHOP_URL = "get.product.url.by.shop.url";

        String SYNC_MERCADO_ORDER_PAGE_INFO = "sync.mercado.order.page.info";

        String SYNC_MERCADO_ORDER_PAGE_INFO_NEXT = "sync.mercado.order.page.info.next";

        String SYNC_MERCADO_ORDER_PAGE_INFO_NEXT_DETAIL = "sync.mercado.order.page.info.next.detail";

        String GET_MERCADO_SHOP_ITEM_INFO_PAGE = "get.mercado.shop.item.info.page";

        String GET_MERCADO_SHOP_ITEM_INFO_PAGE_MARKETPLACE_ITEM = "get.mercado.shop.item.info.page.marketplace.item";

        String GET_MERCADO_SHOP_ITEM_INFO_PAGE_MARKETPLACE_ITEM_NEXT = "get.mercado.shop.item.info.page.marketplace.item.next";

        String MERCADO_CALL_BACK_INFO_MARKETPLACE_ITEMS = "mercado.call.back.info.marketplace.items";

        String MERCADO_CALL_BACK_INFO_MARKETPLACE_ORDERS = "mercado.call.back.info.marketplace.orders";

        String CLOUD_PROCESS_INFRINGEMENT_INFORMATION = "cloud.process.infringement.information";

    }

    interface MercadoQueue {

        String CLOUD_PULL_MERCADO_PRODUCT_INFO_QUEUE = "cloud.pull.mercado.product.info.queue";

        String CLOUD_PULL_MERCADO_BATCH_COLLECTION_LINK_QUEUE = "cloud.pull.mercado.batch.collection.link.queue";

        String CLOUD_PULL_MERCADO_BATCH_COLLECTION_LINK_TO_SHOP_QUEUE = "cloud.pull.mercado.batch.collection.link.to.shop.queue";

        String CLOUD_PULL_MERCADO_BATCH_COLLECTION_ITEM_TO_SHOP_QUEUE = "cloud.pull.mercado.batch.collection.item.to.shop.queue";

        String CLOUD_PULL_MERCADO_BATCH_COPY_LINK_TO_SHOP_QUEUE = "cloud.pull.mercado.batch.copy.link.to.shop.queue";

        String UPDATE_UPC_CODE_INFO_QUEUE = "update.upc.code.info.queue";

        String GET_PRODUCT_URL_BY_SHOP_URL_QUEUE = "get.product.url.by.shop.url.queue";

        String SYNC_MERCADO_ORDER_PAGE_INFO_QUEUE = "sync.mercado.order.page.info.queue";

        String SYNC_MERCADO_ORDER_PAGE_INFO_NEXT_QUEUE = "sync.mercado.order.page.info.next.queue";

        String SYNC_MERCADO_ORDER_PAGE_INFO_NEXT_DETAIL_QUEUE = "sync.mercado.order.page.info.next.detail.queue";

        String GET_MERCADO_SHOP_ITEM_INFO_PAGE_QUEUE = "get.mercado.shop.item.info.page.queue";

        String GET_MERCADO_SHOP_ITEM_INFO_PAGE_MARKETPLACE_ITEM_QUEUE = "get.mercado.shop.item.info.page.marketplace.item.queue";

        String GET_MERCADO_SHOP_ITEM_INFO_PAGE_MARKETPLACE_ITEM_NEXT_QUEUE = "get.mercado.shop.item.info.page.marketplace.item.next.queue";

        String MERCADO_CALL_BACK_INFO_MARKETPLACE_ITEMS_QUEUE = "mercado.call.back.info.marketplace.items.queue";

        String MERCADO_CALL_BACK_INFO_MARKETPLACE_ORDERS_QUEUE = "mercado.call.back.info.marketplace.orders.queue";

        String CLOUD_PROCESS_INFRINGEMENT_INFORMATION_QUEUE = "cloud.process.infringement.information.queue";

    }


}
