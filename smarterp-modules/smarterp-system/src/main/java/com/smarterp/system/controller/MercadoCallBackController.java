package com.smarterp.system.controller;


import com.alibaba.fastjson2.JSONObject;
import com.smarterp.common.core.web.controller.BaseController;
import com.smarterp.common.core.web.domain.AjaxResult;
import com.smarterp.system.service.IMercadoProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message")
public class MercadoCallBackController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(MercadoCallBackController.class);

    @Autowired
    private IMercadoProductService mercadoProductService;

    /**
     * 美客多回调接口
     *
     * @return
     */
    @PostMapping("/callback")
    public AjaxResult callBack(@RequestBody JSONObject message) {
        String merchantId = message.getString("merchantId");
        String accessToken = mercadoProductService.getAccessToken(merchantId);
        return AjaxResult.success(accessToken);
    }

}
