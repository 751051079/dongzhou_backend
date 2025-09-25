package com.smarterp.order.controller;

import com.smarterp.common.core.web.controller.BaseController;
import com.smarterp.common.core.web.domain.AjaxResult;
import com.smarterp.common.core.web.page.TableDataInfo;
import com.smarterp.common.log.annotation.Log;
import com.smarterp.common.log.enums.BusinessType;
import com.smarterp.common.security.annotation.RequiresPermissions;
import com.smarterp.order.domain.MercadoShopItem;
import com.smarterp.order.domain.dto.BatchCollectionLinkToShop;
import com.smarterp.order.service.IMercadoShopItemService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 店铺产品Controller
 *
 * @author smarterp
 * @date 2024-06-09
 */
@RestController
@RequestMapping("/shop/item")
public class MercadoShopItemController extends BaseController {

    @Resource
    private IMercadoShopItemService mercadoShopItemService;

    /**
     * 查询店铺产品列表
     */
    @RequiresPermissions("system:shop:item:list")
    @GetMapping("/list")
    public TableDataInfo list(MercadoShopItem mercadoShopItem, Integer pageNum, Integer pageSize) {
        List<MercadoShopItem> list = mercadoShopItemService.selectMercadoShopItemList(mercadoShopItem, pageNum, pageSize);
        return getDataTable(list);
    }

    /**
     * 删除店铺产品
     */
    @RequiresPermissions("system:shop:item:remove")
    @Log(title = "店铺产品", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        return toAjax(mercadoShopItemService.deleteMercadoShopItemByIds(ids));
    }

    /**
     * 更新单个店铺的产品
     */
    @RequiresPermissions("system:shop:item:updateShopItemInfo")
    @GetMapping("/updateShopItemInfo/{merchantId}")
    public AjaxResult updateShopItemInfo(@PathVariable("merchantId") String merchantId) {
        return mercadoShopItemService.updateShopItemInfo(merchantId);
    }

    /**
     * 批量采集链接到店铺
     */
    @RequiresPermissions("item:batch:collection:to:shop")
    @PostMapping("/batch/collection/toShop")
    public AjaxResult batchCollectionToShop(@RequestBody BatchCollectionLinkToShop batchCollectionLinkToShop) {
        return mercadoShopItemService.batchCollectionToShop(batchCollectionLinkToShop);
    }

    /**
     * 批量更新产品信息
     */
    @RequiresPermissions("system:shop:item:batchEdit")
    @Log(title = "店铺产品", businessType = BusinessType.UPDATE)
    @GetMapping("/batchEdit/{ids}")
    public AjaxResult batchEdit(@PathVariable String[] ids) {
        return mercadoShopItemService.batchEdit(ids);
    }

}
