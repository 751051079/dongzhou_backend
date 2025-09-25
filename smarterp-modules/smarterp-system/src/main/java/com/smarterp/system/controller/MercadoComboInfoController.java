package com.smarterp.system.controller;

import com.smarterp.common.core.utils.poi.ExcelUtil;
import com.smarterp.common.core.web.controller.BaseController;
import com.smarterp.common.core.web.domain.AjaxResult;
import com.smarterp.common.core.web.page.TableDataInfo;
import com.smarterp.common.log.annotation.Log;
import com.smarterp.common.log.enums.BusinessType;
import com.smarterp.common.security.annotation.RequiresPermissions;
import com.smarterp.system.domain.MercadoComboInfo;
import com.smarterp.system.service.IMercadoComboInfoService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 套餐Controller
 *
 * @author smarterp
 * @date 2023-04-16
 */
@RestController
@RequestMapping("/combo")
@Api(tags = "套餐管理")
public class MercadoComboInfoController extends BaseController {
    
    @Autowired
    private IMercadoComboInfoService mercadoComboInfoService;

    /**
     * 查询套餐列表
     */
    @RequiresPermissions("system:combo:list")
    @GetMapping("/list")
    public TableDataInfo list(MercadoComboInfo mercadoComboInfo) {
        startPage();
        List<MercadoComboInfo> list = mercadoComboInfoService.selectMercadoComboInfoList(mercadoComboInfo);
        return getDataTable(list);
    }

    /**
     * 导出套餐列表
     */
    @RequiresPermissions("system:combo:export")
    @Log(title = "套餐", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, MercadoComboInfo mercadoComboInfo) {
        List<MercadoComboInfo> list = mercadoComboInfoService.selectMercadoComboInfoList(mercadoComboInfo);
        ExcelUtil<MercadoComboInfo> util = new ExcelUtil<MercadoComboInfo>(MercadoComboInfo.class);
        util.exportExcel(response, list, "套餐数据");
    }

    /**
     * 获取套餐详细信息
     */
    @RequiresPermissions("system:combo:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(mercadoComboInfoService.selectMercadoComboInfoById(id));
    }

    /**
     * 新增套餐
     */
    @RequiresPermissions("system:combo:add")
    @Log(title = "套餐", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MercadoComboInfo mercadoComboInfo) {
        return toAjax(mercadoComboInfoService.insertMercadoComboInfo(mercadoComboInfo));
    }

    /**
     * 修改套餐
     */
    @RequiresPermissions("system:combo:edit")
    @Log(title = "套餐", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MercadoComboInfo mercadoComboInfo) {
        return toAjax(mercadoComboInfoService.updateMercadoComboInfo(mercadoComboInfo));
    }

    /**
     * 删除套餐
     */
    @RequiresPermissions("system:combo:remove")
    @Log(title = "套餐", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id) {
        return toAjax(mercadoComboInfoService.deleteMercadoComboInfoById(id));
    }
}
