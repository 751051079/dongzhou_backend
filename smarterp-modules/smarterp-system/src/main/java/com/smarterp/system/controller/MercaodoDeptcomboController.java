package com.smarterp.system.controller;

import com.smarterp.common.core.web.controller.BaseController;
import com.smarterp.common.core.web.domain.AjaxResult;
import com.smarterp.common.core.web.page.TableDataInfo;
import com.smarterp.common.log.annotation.Log;
import com.smarterp.common.log.enums.BusinessType;
import com.smarterp.common.security.annotation.RequiresPermissions;
import com.smarterp.system.domain.MercaodoDeptcombo;
import com.smarterp.system.service.IMercaodoDeptcomboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门与套餐关联Controller
 *
 * @author smarterp
 * @date 2023-04-20
 */
@RestController
@RequestMapping("/deptcombo")
public class MercaodoDeptcomboController extends BaseController {

    @Autowired
    private IMercaodoDeptcomboService mercaodoDeptcomboService;

    /**
     * 查询部门与套餐关联列表
     */
    @RequiresPermissions("system:deptcombo:list")
    @GetMapping("/list")
    public TableDataInfo list(MercaodoDeptcombo mercaodoDeptcombo) {
        startPage();
        List<MercaodoDeptcombo> list = mercaodoDeptcomboService.selectMercaodoDeptcomboList(mercaodoDeptcombo);
        return getDataTable(list);
    }

    /**
     * 获取部门与套餐关联详细信息
     */
    @RequiresPermissions("system:deptcombo:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(mercaodoDeptcomboService.selectMercaodoDeptcomboById(id));
    }

    /**
     * 新增部门与套餐关联
     */
    @RequiresPermissions("system:deptcombo:add")
    @Log(title = "部门与套餐关联", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MercaodoDeptcombo mercaodoDeptcombo) {
        return mercaodoDeptcomboService.insertMercaodoDeptcombo(mercaodoDeptcombo);
    }

    /**
     * 修改部门与套餐关联
     */
    @RequiresPermissions("system:deptcombo:edit")
    @Log(title = "部门与套餐关联", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MercaodoDeptcombo mercaodoDeptcombo) {
        return toAjax(mercaodoDeptcomboService.updateMercaodoDeptcombo(mercaodoDeptcombo));
    }

    /**
     * 删除部门与套餐关联
     */
    @RequiresPermissions("system:deptcombo:remove")
    @Log(title = "部门与套餐关联", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id) {
        return toAjax(mercaodoDeptcomboService.deleteMercaodoDeptcomboById(id));
    }
}
