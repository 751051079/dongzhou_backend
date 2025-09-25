package com.smarterp.order.controller;

import com.smarterp.common.core.web.controller.BaseController;
import com.smarterp.common.core.web.domain.AjaxResult;
import com.smarterp.order.bus.DelaySend;
import com.smarterp.order.constant.RabbitMQConstant;
import com.smarterp.order.domain.dto.MercaSendProDto;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author juntao.li
 * @ClassName RabbitMqController
 * @description rabbitmq测试
 * @date 2023/4/17 09:26
 * @Version 1.0
 */
@RestController
@RequestMapping("/rabbitmq")
public class RabbitMqController extends BaseController {

    @Resource
    private AmqpTemplate amqpTemplate;

    @Resource
    private DelaySend delaySend;

    /**
     * rabbitmq测试
     *
     * @param dto
     * @return
     */
    @PostMapping("/sendMessage")
    public AjaxResult sendMessage(@RequestBody MercaSendProDto dto) {
        amqpTemplate.convertAndSend(RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
                RabbitMQConstant.RoutingKey.CLOUD_PULL_MERCADO_PRODUCT_INFO, "我是消息");
        return AjaxResult.success("发送成功");
    }

}
