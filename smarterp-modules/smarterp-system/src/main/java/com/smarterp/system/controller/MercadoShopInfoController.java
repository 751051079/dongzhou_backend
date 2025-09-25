package com.smarterp.system.controller;

import com.smarterp.common.core.web.controller.BaseController;
import com.smarterp.common.core.web.domain.AjaxResult;
import com.smarterp.common.core.web.page.TableDataInfo;
import com.smarterp.common.log.annotation.Log;
import com.smarterp.common.log.enums.BusinessType;
import com.smarterp.common.security.annotation.RequiresPermissions;
import com.smarterp.system.domain.MercadoShopInfo;
import com.smarterp.system.domain.MercadoShopInfoSite;
import com.smarterp.system.domain.vo.MercadoShopInfoVO;
import com.smarterp.system.service.IMercadoShopInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 店铺Controller
 *
 * @author smarterp
 * @date 2023-04-19
 */
@RestController
@RequestMapping("/shop")
public class MercadoShopInfoController extends BaseController {

    @Autowired
    private IMercadoShopInfoService mercadoShopInfoService;

    /**
     * 查询店铺列表
     */
    @RequiresPermissions("system:shop:list")
    @GetMapping("/list")
    public TableDataInfo list(MercadoShopInfo mercadoShopInfo) {
        startPage();
        List<MercadoShopInfo> list = mercadoShopInfoService.selectMercadoShopInfoList(mercadoShopInfo);
        return getDataTable(list);
    }

    /**
     * 查询店铺站点
     */
    @GetMapping("/site/list")
    public TableDataInfo list(MercadoShopInfoSite mercadoShopInfoSite) {
        List<MercadoShopInfoSite> list = mercadoShopInfoService.selectMercadoShopInfoSiteList(mercadoShopInfoSite);
        return getDataTable(list);
    }

    /**
     * 获取店铺详细信息
     */
    @RequiresPermissions("system:shop:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(mercadoShopInfoService.selectMercadoShopInfoById(id));
    }

    /**
     * 绑定店铺
     */
    @RequiresPermissions("system:shop:add")
    @Log(title = "绑定店铺", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MercadoShopInfoVO mercadoShopInfoVO) {
        return mercadoShopInfoService.insertMercadoShopInfo(mercadoShopInfoVO);
    }

    /**
     * 修改店铺
     */
    @RequiresPermissions("system:shop:edit")
    @Log(title = "修改店铺", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MercadoShopInfo mercadoShopInfo) {
        return toAjax(mercadoShopInfoService.updateMercadoShopInfo(mercadoShopInfo));
    }

    /**
     * 删除店铺
     */
    @RequiresPermissions("system:shop:remove")
    @Log(title = "删除店铺", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id) {
        return toAjax(mercadoShopInfoService.deleteMercadoShopInfoById(id));
    }

    /**
     * 修改价格系数
     */
    @Log(title = "修改价格系数", businessType = BusinessType.UPDATE)
    @PostMapping("/editPriceRatio")
    public AjaxResult editPriceRatio(@RequestBody MercadoShopInfo mercadoShopInfo) {
        return toAjax(mercadoShopInfoService.editPriceRatio(mercadoShopInfo));
    }
}
