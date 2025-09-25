package com.smarterp.order.bus;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.smarterp.common.core.utils.mercado.MercadoHttpUtil;
import com.smarterp.common.core.utils.mercado.MercadoNewHttpUtil;
import com.smarterp.common.redis.service.RedisService;
import com.smarterp.order.constant.RabbitMQConstant;
import com.smarterp.order.domain.MercadoOrderInfo;
import com.smarterp.order.domain.MercadoOrderInfoPayment;
import com.smarterp.order.mapper.MercadoOrderInfoMapper;
import com.smarterp.order.mapper.MercadoOrderInfoPaymentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.locks.Lock;


@Component
public class SyncMercadoOrderPageNextDetailListener {

    private static final Logger logger = LoggerFactory.getLogger(SyncMercadoOrderPageNextDetailListener.class);

    @Resource
    private MercadoOrderInfoMapper mercadoOrderInfoMapper;

    @Resource
    private MercadoOrderInfoPaymentMapper mercadoOrderInfoPaymentMapper;

    @Resource
    private RedisService redisService;

    @Resource
    private RedisLockRegistry redisLockRegistry;

    /**
     * 处理订单详情数据
     *
     * @param detail
     */
    @RabbitListener(bindings = @QueueBinding(exchange = @Exchange(
            name = RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
            type = ExchangeTypes.TOPIC),
            value = @Queue(name = RabbitMQConstant.MercadoQueue.SYNC_MERCADO_ORDER_PAGE_INFO_NEXT_DETAIL_QUEUE),
            key = RabbitMQConstant.RoutingKey.SYNC_MERCADO_ORDER_PAGE_INFO_NEXT_DETAIL))
    public void syncMercadoOrderPageNextDetailListener(MercadoOrderInfo detail) {
        // 开始时间
        long startTime = System.currentTimeMillis();
        Lock lock = redisLockRegistry.obtain("SYNC_MERCADO_ORDER_PAGE_INFO_NEXT_DETAIL:" + detail.getId());
        boolean b = lock.tryLock();
        try {
            if (b) {
                logger.info("队列中收到的订单消息 {}", JSON.toJSONString(detail));
                // 首先根据merchanId查询店铺信息
                String accessToken = redisService.getCacheObject("ACCESS_TOKEN_SHOP:" + detail.getMerchantId());
                logger.info("获取的accessToken数据 {}", accessToken);
                if (accessToken != null) {
                    // 根据订单id获取订单详情
                    JSONObject jsonObject = MercadoHttpUtil.getMercadoInfoByOrderId(accessToken, detail.getId());
                    logger.info("获取到的订单详情 {}", jsonObject.toJSONString());
                    if (jsonObject != null) {
                        String status = jsonObject.getString("status");
                        if (!"400".equals(status) && !"401".equals(status) && !"403".equals(status) && !"404".equals(status)
                                && !"451".equals(status) && !"500".equals(status)) {
                            // 根据userId查询站点信息
                            String siteInfo = redisService.getCacheObject("MERCADO_SHOP_INFO:SITE:" + detail.getUserId());
                            logger.info("获取到的站点信息 {}", JSON.toJSONString(siteInfo));
                            if (siteInfo != null) {
                                JSONObject siteJosnObject = JSON.parseObject(siteInfo);
                                MercadoOrderInfo mercadoOrderInfo = new MercadoOrderInfo();
                                mercadoOrderInfo.setId(detail.getId());
                                mercadoOrderInfo.setParentId(detail.getParentId());
                                mercadoOrderInfo.setUserId(detail.getUserId());
                                mercadoOrderInfo.setSiteId(siteJosnObject.getString("site_id"));
                                mercadoOrderInfo.setLogisticType(siteJosnObject.getString("logistic_type"));
                                mercadoOrderInfo.setMerchantId(detail.getMerchantId());
                                JSONObject shipping = jsonObject.getJSONObject("shipping");
                                if (shipping != null) {
                                    mercadoOrderInfo.setShipmentId(shipping.getString("id"));
                                }
                                JSONObject buyer = jsonObject.getJSONObject("buyer");
                                if (buyer != null) {
                                    mercadoOrderInfo.setBuyerId(buyer.getString("id"));
                                    mercadoOrderInfo.setBuyerNickName(buyer.getString("nickname"));
                                    mercadoOrderInfo.setBuyerLastName(buyer.getString("last_name"));
                                    mercadoOrderInfo.setBuyerFirstName(buyer.getString("first_name"));
                                }
                                mercadoOrderInfo.setDateCreated(jsonObject.getDate("date_created"));
                                mercadoOrderInfo.setDateClosed(jsonObject.getDate("date_closed"));
                                mercadoOrderInfo.setLastUpdated(jsonObject.getDate("last_updated"));
                                mercadoOrderInfo.setExpirationDate(jsonObject.getDate("expiration_date"));
                                // 物流信息
                                JSONObject shipJson = MercadoHttpUtil.getShipDetailByShipId(accessToken, mercadoOrderInfo.getShipmentId());
                                if (shipJson != null) {
                                    String shipStatus = shipJson.getString("status");
                                    if (!"400".equals(shipStatus) && !"401".equals(shipStatus) && !"403".equals(shipStatus) && !"404".equals(shipStatus)
                                            && !"451".equals(shipStatus) && !"500".equals(shipStatus)) {
                                        mercadoOrderInfo.setShipStatus(shipJson.getString("status"));
                                    }
                                    JSONObject lead_time = shipJson.getJSONObject("lead_time");
                                    if (lead_time != null) {
                                        JSONObject estimated_delivery_time = lead_time.getJSONObject("estimated_delivery_time");
                                        mercadoOrderInfo.setDateShipping(estimated_delivery_time.getDate("date"));
                                    }
                                }
                                mercadoOrderInfo.setOrderStatus(jsonObject.getString("status"));
                                // 处理订单产品详情
                                processOrderItems(mercadoOrderInfo, jsonObject.getJSONArray("order_items"), accessToken);
                                // 处理订单付款信息
                                processOrderPatments(mercadoOrderInfo, jsonObject.getJSONArray("payments"));
                                // 插入订单数据
                                logger.error("插入订单数据 {}",JSON.toJSONString(mercadoOrderInfo));
                                mercadoOrderInfoMapper.insertMercadoOrderInfo(mercadoOrderInfo);
                            }
                        }
                    }
                }
            } else {
                logger.error("处理订单详情数据没有拿到锁 订单ID {}", detail.getId());
            }
        } catch (Exception e) {
            logger.error("处理订单信息详情异常 {} 订单ID {}", e, detail.getId());
        } finally {
            long end = System.currentTimeMillis();
            logger.error("处理订单信息详情耗费时间 {} 订单ID {}", (end - startTime), detail.getId());
            try {
                if (b) {
                    lock.unlock();
                }
            } catch (Exception e) {
                logger.error("处理订单信息详情释放锁异常 {} 订单ID {}", e, detail.getId());
            }
        }
    }

    /**
     * 处理订单产品信息
     *
     * @param mercadoOrderInfo
     * @param orderItems
     */
    private void processOrderItems(MercadoOrderInfo mercadoOrderInfo, JSONArray orderItems, String accessToken) {
        try {
            if (orderItems != null && orderItems.size() > 0) {
                JSONObject jsonObject = orderItems.getJSONObject(0);
                JSONObject item = jsonObject.getJSONObject("item");
                if (item != null) {
                    String itemId = item.getString("id");
                    if (itemId != null) {
                        mercadoOrderInfo.setItemId(itemId);
                        mercadoOrderInfo.setTitle(item.getString("title"));
                        mercadoOrderInfo.setCategoryId(item.getString("category_id"));
                        mercadoOrderInfo.setVariationId(item.getString("variation_id"));
                        JSONArray variation_attributes = item.getJSONArray("variation_attributes");
                        if (variation_attributes != null && variation_attributes.size() > 0) {
                            mercadoOrderInfo.setVariationAttributes(variation_attributes.toJSONString());
                        }
                        mercadoOrderInfo.setWarranty(item.getString("warranty"));
                        mercadoOrderInfo.setSellerSku(item.getString("seller_sku"));
                        mercadoOrderInfo.setParentItemId(item.getString("parent_item_id"));
                        mercadoOrderInfo.setQuantity(jsonObject.getString("quantity"));
                        mercadoOrderInfo.setUnitPrice(jsonObject.getString("unit_price"));
                        mercadoOrderInfo.setUnitPrice(jsonObject.getString("full_unit_price"));
                        mercadoOrderInfo.setCurrencyId(jsonObject.getString("currency_id"));
                        mercadoOrderInfo.setSaleFee(jsonObject.getString("sale_fee"));
                        mercadoOrderInfo.setBaseExchangeRate(jsonObject.getString("base_exchange_rate"));

                        try {
                            String variation_id = item.getString("variation_id");
                            // 根据变体id查询产品首图
                            JSONObject itemInfo = MercadoNewHttpUtil.getSiteProductInfo(itemId, accessToken);
                            logger.error("查询订单产品详情的数据 {}",JSON.toJSONString(itemInfo));
                            if (itemInfo != null) {
                                mercadoOrderInfo.setPermalink(itemInfo.getString("permalink"));
                                // 如果变体id不为空
                                if (variation_id != null && !"".equals(variation_id)) {
                                    JSONArray variations = itemInfo.getJSONArray("variations");
                                    if (variations != null && variations.size() > 0) {
                                        String picture_id = "";
                                        for (int index = 0; index < variations.size(); index++) {
                                            JSONObject object = variations.getJSONObject(index);
                                            if (object.getString("id").equals(variation_id)) {
                                                JSONArray picture_ids = object.getJSONArray("picture_ids");
                                                picture_id = picture_ids.getString(0);
                                                break;
                                            }
                                        }
                                        if (!picture_id.equals("")) {
                                            JSONArray pictures = itemInfo.getJSONArray("pictures");
                                            if (pictures != null && pictures.size() > 0) {
                                                for (int index = 0; index < pictures.size(); index++) {
                                                    JSONObject object = pictures.getJSONObject(index);
                                                    if (object.getString("id").equals(picture_id)) {
                                                        String url = object.getString("url");
                                                        mercadoOrderInfo.setOrderPictures(url);
                                                        break;
                                                    }
                                                }

                                            }
                                        }
                                    }
                                } else {
                                    JSONArray pictures = itemInfo.getJSONArray("pictures");
                                    if (pictures != null && pictures.size() > 0) {
                                        JSONObject object = pictures.getJSONObject(0);
                                        String url = object.getString("url");
                                        mercadoOrderInfo.setOrderPictures(url);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            logger.error("查询产品首图异常 {}", e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("处理订单产品异常 {}", orderItems.toJSONString());
        }
    }


    /**
     * 处理订单付款信息
     *
     * @param mercadoOrderInfo
     * @param payments
     */
    private void processOrderPatments(MercadoOrderInfo mercadoOrderInfo, JSONArray payments) {
        try {
            if (payments != null && payments.size() > 0) {
                for (int index = 0; index < payments.size(); index++) {
                    JSONObject payment = payments.getJSONObject(index);
                    MercadoOrderInfoPayment mercadoOrderInfoPayment = new MercadoOrderInfoPayment();
                    mercadoOrderInfoPayment.setId(payment.getString("order_id") + "_" + payment.getString("payer_id"));
                    mercadoOrderInfoPayment.setOrderId(payment.getString("order_id"));
                    mercadoOrderInfoPayment.setPayerId(payment.getString("payer_id"));
                    JSONObject collector = payment.getJSONObject("collector");
                    if (collector != null) {
                        mercadoOrderInfoPayment.setCollectorId(collector.getString("id"));
                    }
                    mercadoOrderInfoPayment.setCardId(payment.getString("card_id"));
                    mercadoOrderInfoPayment.setSiteId(payment.getString("site_id"));
                    mercadoOrderInfoPayment.setReason(payment.getString("reason"));
                    mercadoOrderInfoPayment.setPaymentMethodId(payment.getString("payment_method_id"));
                    mercadoOrderInfoPayment.setCurrencyId(payment.getString("currency_id"));
                    mercadoOrderInfoPayment.setInstallments(payment.getString("installments"));
                    JSONObject atm_transfer_reference = payment.getJSONObject("atm_transfer_reference");
                    if (atm_transfer_reference != null) {
                        mercadoOrderInfoPayment.setTransactionId(atm_transfer_reference.getString("transaction_id"));
                    }
                    mercadoOrderInfoPayment.setOperationType(payment.getString("operation_type"));
                    mercadoOrderInfoPayment.setPaymentType(payment.getString("digital_currency"));
                    JSONArray available_actions = payment.getJSONArray("available_actions");
                    if (available_actions != null && available_actions.size() > 0) {
                        mercadoOrderInfoPayment.setAvailableActions(available_actions.toJSONString());
                    }
                    mercadoOrderInfoPayment.setStatus(payment.getString("status"));
                    mercadoOrderInfoPayment.setStatusDetail(payment.getString("status_detail"));
                    mercadoOrderInfoPayment.setTransactionAmount(payment.getString("transaction_amount"));
                    mercadoOrderInfoPayment.setTaxesAmount(payment.getString("taxes_amount"));
                    mercadoOrderInfoPayment.setShippingCost(payment.getString("shipping_cost"));
                    mercadoOrderInfoPayment.setCouponAmount(payment.getString("coupon_amount"));
                    mercadoOrderInfoPayment.setOverpaidAmount(payment.getString("overpaid_amount"));
                    mercadoOrderInfoPayment.setTotalPaidAmount(payment.getString("total_paid_amount"));
                    mercadoOrderInfoPayment.setInstallmentAmount(payment.getString("installment_amount"));
                    mercadoOrderInfoPayment.setDateApproved(payment.getDate("date_approved"));
                    mercadoOrderInfoPayment.setDateCreated(payment.getDate("date_created"));
                    mercadoOrderInfoPayment.setDateLastModified(payment.getDate("date_last_modified"));
                    mercadoOrderInfoPaymentMapper.insertMercadoOrderInfoPayment(mercadoOrderInfoPayment);
                }
            }
        } catch (Exception e) {
            logger.error("处理订单付款信息异常 {}", payments.toJSONString());
        }
    }

}
