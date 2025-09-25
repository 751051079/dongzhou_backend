package com.smarterp.message.controller;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.smarterp.common.core.web.controller.BaseController;
import com.smarterp.common.core.web.domain.AjaxResult;
import com.smarterp.common.redis.service.RedisService;
import com.smarterp.message.constant.RabbitMQConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/info")
public class MercadoCallBackController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(MercadoCallBackController.class);

    @Resource
    private AmqpTemplate amqpTemplate;

    @Resource
    private RedisService redisService;


    /**
     * 美客多回调接口
     *
     * @return
     */
    @PostMapping("/callback")
    public AjaxResult callBack(@RequestBody JSONObject message) {
        String enabled = redisService.getCacheObject("sys_config:mercado.call.back.enable");
        if ("true".equals(enabled)) {
            logger.info("美客多回调消息 {}", JSON.toJSONString(message));
            amqpTemplate.convertAndSend(RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
                    RabbitMQConstant.RoutingKey.MERCADO_CALL_BACK_INFO, message);
        }
        return AjaxResult.success("Success");
    }

}
