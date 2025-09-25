package com.smarterp.system.service;

import java.util.List;

import com.smarterp.common.core.web.domain.AjaxResult;
import com.smarterp.system.domain.MercadoUpc;
import com.smarterp.system.domain.MercadoUpcItem;
import com.smarterp.system.domain.dto.MercadoUpcDTO;
import com.smarterp.system.domain.vo.MercadoUpcVO;

/**
 * UPC信息Service接口
 *
 * @author smarterp
 * @date 2023-04-16
 */
public interface IMercadoUpcService {
    /**
     * 查询UPC信息
     *
     * @param id UPC信息主键
     * @return UPC信息
     */
    public MercadoUpc selectMercadoUpcById(Long id);

    /**
     * 查询UPC信息列表
     *
     * @param mercadoUpc UPC信息
     * @return UPC信息集合
     */
    public List<MercadoUpcVO> selectMercadoUpcList(MercadoUpc mercadoUpc);

    /**
     * 新增UPC信息
     *
     * @param mercadoUpcDTO UPC信息
     * @return 结果
     */
    public AjaxResult insertMercadoUpc(MercadoUpcDTO mercadoUpcDTO);

    /**
     * 修改UPC信息
     *
     * @param mercadoUpc UPC信息
     * @return 结果
     */
    public int updateMercadoUpc(MercadoUpc mercadoUpc);

    /**
     * 批量删除UPC信息
     *
     * @param ids 需要删除的UPC信息主键集合
     * @return 结果
     */
    public int deleteMercadoUpcByIds(Long[] ids);

    /**
     * 删除UPC信息信息
     *
     * @param id UPC信息主键
     * @return 结果
     */
    public int deleteMercadoUpcById(Long id);

    /**
     * 查看upc详情
     * @param mercadoUpcItem
     * @return
     */
    public List<MercadoUpcItem> selectMercadoUpcItemList(MercadoUpcItem mercadoUpcItem);
}
