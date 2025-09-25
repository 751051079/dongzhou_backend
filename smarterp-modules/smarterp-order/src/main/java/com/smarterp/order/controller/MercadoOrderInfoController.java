package com.smarterp.order.controller;

import com.smarterp.common.core.utils.poi.ExcelUtil;
import com.smarterp.common.core.web.controller.BaseController;
import com.smarterp.common.core.web.domain.AjaxResult;
import com.smarterp.common.core.web.page.TableDataInfo;
import com.smarterp.common.log.annotation.Log;
import com.smarterp.common.log.enums.BusinessType;
import com.smarterp.common.security.annotation.RequiresPermissions;
import com.smarterp.order.domain.MercadoOrderInfo;
import com.smarterp.order.domain.dto.MercadoOrderInfoDetail;
import com.smarterp.order.service.IMercadoOrderInfoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 订单信息Controller
 *
 * @author smarterp
 * @date 2024-06-07
 */
@RestController
@RequestMapping("/info")
public class MercadoOrderInfoController extends BaseController {

    @Resource
    private IMercadoOrderInfoService mercadoOrderInfoService;

    /**
     * 查询订单信息列表
     */
    @RequiresPermissions("system:info:list")
    @GetMapping("/list")
    public TableDataInfo list(MercadoOrderInfo mercadoOrderInfo, Integer pageNum, Integer pageSize) {
        List<MercadoOrderInfoDetail> list = mercadoOrderInfoService.selectMercadoOrderInfoList(mercadoOrderInfo, pageNum, pageSize);
        return getDataTable(list);
    }

    /**
     * 获取订单信息详细信息
     */
    @RequiresPermissions("system:info:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        return success(mercadoOrderInfoService.selectMercadoOrderInfoById(id));
    }

    /**
     * 新增订单信息
     */
    @RequiresPermissions("system:info:add")
    @Log(title = "订单信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MercadoOrderInfo mercadoOrderInfo) {
        return toAjax(mercadoOrderInfoService.insertMercadoOrderInfo(mercadoOrderInfo));
    }

    /**
     * 修改订单信息
     */
    @RequiresPermissions("system:info:edit")
    @Log(title = "订单信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MercadoOrderInfo mercadoOrderInfo) {
        return toAjax(mercadoOrderInfoService.updateMercadoOrderInfo(mercadoOrderInfo));
    }

    /**
     * 删除订单信息
     */
    @RequiresPermissions("system:order:info:remove")
    @Log(title = "订单信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        return toAjax(mercadoOrderInfoService.deleteMercadoOrderInfoByIds(ids));
    }

    /**
     * 同步单店铺订单数据
     */
    @RequiresPermissions("system:order:info:sync")
    @Log(title = "订单信息", businessType = BusinessType.DELETE)
    @GetMapping("/syncShopOrderInfo/{merchantId}")
    public AjaxResult syncShopOrderInfo(@PathVariable("merchantId") String merchantId) {
        return mercadoOrderInfoService.syncShopOrderInfo(merchantId);
    }

    /**
     * 同步订单信息
     */
    @RequiresPermissions("system:order:info:edit")
    @Log(title = "订单信息", businessType = BusinessType.UPDATE)
    @GetMapping("/updateOrderInfo/{ids}")
    public AjaxResult updateOrderInfo(@PathVariable String[] ids) {
        return toAjax(mercadoOrderInfoService.updateOrderInfo(ids));
    }
}
