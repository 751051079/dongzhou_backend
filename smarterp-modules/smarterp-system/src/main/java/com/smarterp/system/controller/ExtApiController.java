package com.smarterp.system.controller;

import com.alibaba.fastjson2.JSONObject;
import com.smarterp.common.core.web.domain.AjaxResult;
import com.smarterp.common.core.web.page.TableDataInfo;
import com.smarterp.common.security.annotation.RequiresPermissions;
import com.smarterp.system.domain.MercadoComboInfo;
import com.smarterp.system.service.IExtApService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ext")
@Api(tags = "套餐管理")
public class ExtApiController {
    @Autowired
    private IExtApService extApService;

    /**
     * 查询汇率
     */
    @GetMapping("/getRateInfo")
    public AjaxResult getRateInfo(@RequestParam(name = "fromSate") String fromSate, @RequestParam(name = "toSate") String toSate) {
        JSONObject json = extApService.getRateInfo(fromSate,toSate);
        return AjaxResult.success(json);
    }
}
