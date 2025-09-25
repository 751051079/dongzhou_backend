package com.smarterp.system.controller;

import com.smarterp.common.core.web.controller.BaseController;
import com.smarterp.common.core.web.domain.AjaxResult;
import com.smarterp.common.core.web.page.TableDataInfo;
import com.smarterp.common.log.annotation.Log;
import com.smarterp.common.log.enums.BusinessType;
import com.smarterp.common.security.annotation.RequiresPermissions;
import com.smarterp.system.domain.MercadoUpc;
import com.smarterp.system.domain.MercadoUpcItem;
import com.smarterp.system.domain.dto.MercadoUpcDTO;
import com.smarterp.system.domain.vo.MercadoUpcVO;
import com.smarterp.system.service.IMercadoUpcService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * UPC信息Controller
 *
 * @author smarterp
 * @date 2023-04-16
 */
@RestController
@RequestMapping("/upc")
@Api(tags = "UPC管理")
public class MercadoUpcController extends BaseController {

    @Autowired
    private IMercadoUpcService mercadoUpcService;

    /**
     * 查询UPC信息详情列表
     */
    @RequiresPermissions("system:upc:item:list")
    @GetMapping("/item/list")
    public TableDataInfo itemList(MercadoUpcItem mercadoUpcItem) {
        startPage();
        List<MercadoUpcItem> list = mercadoUpcService.selectMercadoUpcItemList(mercadoUpcItem);
        return getDataTable(list);
    }

    /**
     * 查询UPC信息列表
     */
    @RequiresPermissions("system:upc:list")
    @GetMapping("/list")
    public TableDataInfo list(MercadoUpc mercadoUpc) {
        startPage();
        List<MercadoUpcVO> list = mercadoUpcService.selectMercadoUpcList(mercadoUpc);
        return getDataTable(list);
    }

    /**
     * 获取UPC信息详细信息
     */
    @RequiresPermissions("system:upc:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(mercadoUpcService.selectMercadoUpcById(id));
    }

    /**
     * 新增UPC信息
     */
    @RequiresPermissions("system:upc:add")
    @Log(title = "UPC信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MercadoUpcDTO mercadoUpcDTO) {
        return mercadoUpcService.insertMercadoUpc(mercadoUpcDTO);
    }

    /**
     * 修改UPC信息
     */
    @RequiresPermissions("system:upc:edit")
    @Log(title = "UPC信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MercadoUpc mercadoUpc) {
        return toAjax(mercadoUpcService.updateMercadoUpc(mercadoUpc));
    }

    /**
     * 删除UPC信息
     */
    @RequiresPermissions("system:upc:remove")
    @Log(title = "UPC信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id) {
        return toAjax(mercadoUpcService.deleteMercadoUpcById(id));
    }
}
