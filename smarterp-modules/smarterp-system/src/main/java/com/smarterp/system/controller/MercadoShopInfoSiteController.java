package com.smarterp.system.controller;

import com.smarterp.common.core.utils.poi.ExcelUtil;
import com.smarterp.common.core.web.controller.BaseController;
import com.smarterp.common.core.web.domain.AjaxResult;
import com.smarterp.common.core.web.page.TableDataInfo;
import com.smarterp.common.log.annotation.Log;
import com.smarterp.common.log.enums.BusinessType;
import com.smarterp.common.security.annotation.RequiresPermissions;
import com.smarterp.system.domain.MercadoShopInfoSite;
import com.smarterp.system.service.IMercadoShopInfoSiteService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 店铺站点信息Controller
 *
 * @author smarterp
 * @date 2024-06-06
 */
@RestController
@RequestMapping("/site")
public class MercadoShopInfoSiteController extends BaseController {

    @Resource
    private IMercadoShopInfoSiteService mercadoShopInfoSiteService;

    /**
     * 查询店铺站点信息列表
     */
    @RequiresPermissions("system:site:list")
    @GetMapping("/list")
    public TableDataInfo list(MercadoShopInfoSite mercadoShopInfoSite) {
        startPage();
        List<MercadoShopInfoSite> list = mercadoShopInfoSiteService.selectMercadoShopInfoSiteList(mercadoShopInfoSite);
        return getDataTable(list);
    }

    /**
     * 导出店铺站点信息列表
     */
    @RequiresPermissions("system:site:export")
    @Log(title = "店铺站点信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, MercadoShopInfoSite mercadoShopInfoSite) {
        List<MercadoShopInfoSite> list = mercadoShopInfoSiteService.selectMercadoShopInfoSiteList(mercadoShopInfoSite);
        ExcelUtil<MercadoShopInfoSite> util = new ExcelUtil<MercadoShopInfoSite>(MercadoShopInfoSite.class);
        util.exportExcel(response, list, "店铺站点信息数据");
    }

    /**
     * 获取店铺站点信息详细信息
     */
    @RequiresPermissions("system:site:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(mercadoShopInfoSiteService.selectMercadoShopInfoSiteById(id));
    }

    /**
     * 新增店铺站点信息
     */
    @RequiresPermissions("system:site:add")
    @Log(title = "店铺站点信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MercadoShopInfoSite mercadoShopInfoSite) {
        return toAjax(mercadoShopInfoSiteService.insertMercadoShopInfoSite(mercadoShopInfoSite));
    }

    /**
     * 修改店铺站点信息
     */
    @RequiresPermissions("system:site:edit")
    @Log(title = "店铺站点信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MercadoShopInfoSite mercadoShopInfoSite) {
        return toAjax(mercadoShopInfoSiteService.updateMercadoShopInfoSite(mercadoShopInfoSite));
    }

    /**
     * 删除店铺站点信息
     */
    @RequiresPermissions("system:site:remove")
    @Log(title = "店铺站点信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(mercadoShopInfoSiteService.deleteMercadoShopInfoSiteByIds(ids));
    }
}
