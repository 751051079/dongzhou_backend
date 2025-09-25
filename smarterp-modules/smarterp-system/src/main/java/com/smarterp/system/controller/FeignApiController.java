package com.smarterp.system.controller;


import com.smarterp.common.core.domain.R;
import com.smarterp.common.core.web.controller.BaseController;
import com.smarterp.system.service.IMercadoProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feignApi")
public class FeignApiController extends BaseController {

    @Autowired
    private IMercadoProductService mercadoProductService;

    /**
     * 定时更新店铺token
     * @return
     */
    @PostMapping("/updateMercadoToken")
    public R<?> updateMercadoToken() {
        mercadoProductService.updateMercadoToken();
        return R.ok();
    }

    /**
     * 定时更新所属部门绑定套餐的有效天数
     * @return
     */
    @PostMapping("/updateComboValidDate")
    public R<?> updateComboValidDate() {
        mercadoProductService.updateComboValidDate();
        return R.ok();
    }
}
