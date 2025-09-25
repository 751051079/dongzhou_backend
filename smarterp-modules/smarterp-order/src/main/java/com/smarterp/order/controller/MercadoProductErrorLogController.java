package com.smarterp.order.controller;

import com.smarterp.common.core.utils.poi.ExcelUtil;
import com.smarterp.common.core.web.controller.BaseController;
import com.smarterp.common.core.web.domain.AjaxResult;
import com.smarterp.common.core.web.page.TableDataInfo;
import com.smarterp.common.log.annotation.Log;
import com.smarterp.common.log.enums.BusinessType;
import com.smarterp.common.security.annotation.RequiresPermissions;
import com.smarterp.order.domain.MercadoProductErrorLog;
import com.smarterp.order.service.IMercadoProductErrorLogService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 错误日志Controller
 *
 * @author smarterp
 * @date 2023-07-03
 */
@RestController
@RequestMapping("/log")
public class MercadoProductErrorLogController extends BaseController {

    @Resource
    private IMercadoProductErrorLogService mercadoProductErrorLogService;

    /**
     * 查询错误日志列表
     */
    @RequiresPermissions("order:log:list")
    @GetMapping("/list")
    public TableDataInfo list(MercadoProductErrorLog mercadoProductErrorLog) {
        startPage();
        List<MercadoProductErrorLog> list = mercadoProductErrorLogService.selectMercadoProductErrorLogList(mercadoProductErrorLog);
        return getDataTable(list);
    }

    /**
     * 导出错误日志列表
     */
    @RequiresPermissions("order:log:export")
    @Log(title = "错误日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, MercadoProductErrorLog mercadoProductErrorLog) {
        List<MercadoProductErrorLog> list = mercadoProductErrorLogService.selectMercadoProductErrorLogList(mercadoProductErrorLog);
        ExcelUtil<MercadoProductErrorLog> util = new ExcelUtil<MercadoProductErrorLog>(MercadoProductErrorLog.class);
        util.exportExcel(response, list, "错误日志数据");
    }

    /**
     * 获取错误日志详细信息
     */
    @RequiresPermissions("order:log:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(mercadoProductErrorLogService.selectMercadoProductErrorLogById(id));
    }

    /**
     * 新增错误日志
     */
    @RequiresPermissions("order:log:add")
    @Log(title = "错误日志", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MercadoProductErrorLog mercadoProductErrorLog) {
        return toAjax(mercadoProductErrorLogService.insertMercadoProductErrorLog(mercadoProductErrorLog));
    }

    /**
     * 修改错误日志
     */
    @RequiresPermissions("order:log:edit")
    @Log(title = "错误日志", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MercadoProductErrorLog mercadoProductErrorLog) {
        return toAjax(mercadoProductErrorLogService.updateMercadoProductErrorLog(mercadoProductErrorLog));
    }

    /**
     * 删除错误日志
     */
    @RequiresPermissions("order:log:remove")
    @Log(title = "错误日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(mercadoProductErrorLogService.deleteMercadoProductErrorLogByIds(ids));
    }
}
