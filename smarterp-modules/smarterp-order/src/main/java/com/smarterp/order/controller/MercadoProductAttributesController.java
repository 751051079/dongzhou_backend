package com.smarterp.order.controller;

import com.smarterp.common.core.utils.poi.ExcelUtil;
import com.smarterp.common.core.web.controller.BaseController;
import com.smarterp.common.core.web.domain.AjaxResult;
import com.smarterp.common.core.web.page.TableDataInfo;
import com.smarterp.common.log.annotation.Log;
import com.smarterp.common.log.enums.BusinessType;
import com.smarterp.common.security.annotation.RequiresPermissions;
import com.smarterp.order.domain.MercadoProductAttributes;
import com.smarterp.order.service.IMercadoProductAttributesService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * 产品全局属性Controller
 *
 * @author smarterp
 * @date 2023-04-28
 */
@RestController
@RequestMapping("/attributes")
public class MercadoProductAttributesController extends BaseController {

    @Resource
    private IMercadoProductAttributesService mercadoProductAttributesService;

    /**
     * 查询产品全局属性列表
     */
    @RequiresPermissions("order:attributes:list")
    @GetMapping("/list")
    public TableDataInfo list(MercadoProductAttributes mercadoProductAttributes) {
        startPage();
        List<MercadoProductAttributes> list = mercadoProductAttributesService.selectMercadoProductAttributesList(mercadoProductAttributes);
        return getDataTable(list);
    }

    /**
     * 导出产品全局属性列表
     */
    @RequiresPermissions("order:attributes:export")
    @Log(title = "产品全局属性", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, MercadoProductAttributes mercadoProductAttributes) {
        List<MercadoProductAttributes> list = mercadoProductAttributesService.selectMercadoProductAttributesList(mercadoProductAttributes);
        ExcelUtil<MercadoProductAttributes> util = new ExcelUtil<MercadoProductAttributes>(MercadoProductAttributes.class);
        util.exportExcel(response, list, "产品全局属性数据");
    }

    /**
     * 获取产品全局属性详细信息
     */
    @RequiresPermissions("order:attributes:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(mercadoProductAttributesService.selectMercadoProductAttributesById(id));
    }

    /**
     * 新增产品全局属性
     */
    @RequiresPermissions("order:attributes:add")
    @Log(title = "产品全局属性", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MercadoProductAttributes mercadoProductAttributes) {
        return toAjax(mercadoProductAttributesService.insertMercadoProductAttributes(mercadoProductAttributes));
    }

    /**
     * 修改产品全局属性
     */
    @RequiresPermissions("order:attributes:edit")
    @Log(title = "产品全局属性", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MercadoProductAttributes mercadoProductAttributes) {
        return toAjax(mercadoProductAttributesService.updateMercadoProductAttributes(mercadoProductAttributes));
    }

    /**
     * 删除产品全局属性
     */
    @RequiresPermissions("order:attributes:remove")
    @Log(title = "产品全局属性", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(mercadoProductAttributesService.deleteMercadoProductAttributesByIds(ids));
    }
}
